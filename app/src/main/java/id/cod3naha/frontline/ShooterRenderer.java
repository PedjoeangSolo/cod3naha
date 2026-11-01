package id.cod3naha.frontline;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ShooterRenderer implements GLSurfaceView.Renderer {
    public interface HudCallback { void onScore(int score); }
    private static final String VS="attribute vec3 aPos; uniform mat4 uMvp; void main(){gl_Position=uMvp*vec4(aPos,1.0);}";
    private static final String FS="precision mediump float; uniform vec4 uColor; void main(){gl_FragColor=uColor;}";
    private int program,posLoc,mvpLoc,colorLoc;
    private FloatBuffer vertices;
    private final float[] projection=new float[16],view=new float[16],model=new float[16],mvp=new float[16],temp=new float[16];
    private final float[] tx=new float[10],tz=new float[10];
    private float yaw=0,pitch=0,moveX=0,moveY=0,px=0,pz=0;
    private int score=0; private long last=System.nanoTime(); private HudCallback callback;
    public void setHudCallback(HudCallback cb){callback=cb;}

    @Override public void onSurfaceCreated(javax.microedition.khronos.egl.EGLConfig c){
        GLES20.glClearColor(0.42f,0.58f,0.68f,1f); GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        int v=shader(GLES20.GL_VERTEX_SHADER,VS),f=shader(GLES20.GL_FRAGMENT_SHADER,FS);
        program=GLES20.glCreateProgram();GLES20.glAttachShader(program,v);GLES20.glAttachShader(program,f);GLES20.glLinkProgram(program);
        posLoc=GLES20.glGetAttribLocation(program,"aPos");mvpLoc=GLES20.glGetUniformLocation(program,"uMvp");colorLoc=GLES20.glGetUniformLocation(program,"uColor");
        float[] data={-.5f,-.5f,-.5f,.5f,-.5f,-.5f,.5f,.5f,-.5f,-.5f,.5f,-.5f,-.5f,-.5f,.5f,.5f,-.5f,.5f,.5f,.5f,.5f,-.5f,.5f,.5f};
        vertices=ByteBuffer.allocateDirect(data.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();vertices.put(data).position(0);
        reset();
    }
    private int shader(int type,String src){int s=GLES20.glCreateShader(type);GLES20.glShaderSource(s,src);GLES20.glCompileShader(s);return s;}
    @Override public void onSurfaceChanged(javax.microedition.khronos.opengles.GL10 gl,int w,int h){
        GLES20.glViewport(0,0,w,h);Matrix.perspectiveM(projection,0,68f,(float)w/Math.max(1,h),0.1f,80f);
    }
    @Override public void onDrawFrame(javax.microedition.khronos.opengles.GL10 gl){
        long now=System.nanoTime();float dt=Math.min(.05f,(now-last)/1e9f);last=now;
        px+=(float)(Math.sin(yaw)*moveY+Math.cos(yaw)*moveX)*dt*3f;
        pz+=(float)(Math.cos(yaw)*moveY-Math.sin(yaw)*moveX)*dt*3f;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        float ex=px,ey=1.6f,ez=pz;
        Matrix.setLookAtM(view,0,ex,ey,ez,px+(float)Math.sin(yaw)*Math.cos(pitch),ey+(float)Math.sin(pitch),pz-(float)Math.cos(yaw)*Math.cos(pitch),0,1,0);
        box(0,-.55f,-8,18,.2f,26,.34f,.33f,.29f);
        box(-5,1,-9,.7f,2,12,.43f,.42f,.37f);box(5,1,-9,.7f,2,12,.43f,.42f,.37f);
        box(0,3,-15,12,.5f,.5f,.35f,.31f,.25f);
        for(int i=0;i<10;i++){
            float sway=(float)Math.sin(now/1e9+i)*.35f;
            box(tx[i]+sway,.65f,tz[i],.45f,1.3f,.35f,.34f,.28f,.21f);
            box(tx[i]+sway,1.45f,tz[i],.55f,.55f,.45f,.65f,.58f,.43f);
        }
    }
    private void box(float x,float y,float z,float sx,float sy,float sz,float r,float g,float b){
        Matrix.setIdentityM(model,0);Matrix.translateM(model,0,x,y,z);Matrix.scaleM(model,0,sx,sy,sz);
        Matrix.multiplyMM(temp,0,view,0,model,0);Matrix.multiplyMM(mvp,0,projection,0,temp,0);
        GLES20.glUseProgram(program);GLES20.glUniformMatrix4fv(mvpLoc,1,false,mvp,0);GLES20.glUniform4f(colorLoc,r,g,b,1);
        vertices.position(0);GLES20.glEnableVertexAttribArray(posLoc);GLES20.glVertexAttribPointer(posLoc,3,GLES20.GL_FLOAT,false,0,vertices);
        short[] ids={0,1,2,0,2,3,4,6,5,4,7,6,0,4,5,0,5,1,3,2,6,3,6,7,1,5,6,1,6,2,0,3,7,0,7,4};
        java.nio.ShortBuffer ib=ByteBuffer.allocateDirect(ids.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
        ib.put(ids).position(0);GLES20.glDrawElements(GLES20.GL_TRIANGLES,ids.length,GLES20.GL_UNSIGNED_SHORT,ib);
        GLES20.glDisableVertexAttribArray(posLoc);
    }
    public void look(float dx,float dy){yaw+=dx;pitch=Math.max(-.8f,Math.min(.8f,pitch+dy));}
    public void setMove(float x,float y){moveX=x;moveY=y;}
    public void fire(){
        int best=-1;float bestD=999;
        for(int i=0;i<10;i++){
            float dx=tx[i]-px,dz=tz[i]-pz;
            float target=(float)Math.atan2(dx,-dz);
            float diff=(float)Math.atan2(Math.sin(target-yaw),Math.cos(target-yaw));
            float dist=(float)Math.sqrt(dx*dx+dz*dz);
            if(Math.abs(diff)<.14f&&dist<bestD){best=i;bestD=dist;}
        }
        if(best>=0){tx[best]=100;score=Math.min(10,score+1);if(callback!=null)callback.onScore(score);}
    }
    public void reset(){
        score=0;px=0;pz=0;yaw=0;pitch=0;
        for(int i=0;i<10;i++){tx[i]=(i-4.5f)*1.4f;tz[i]=-7f-(i%3)*2.5f;}
        if(callback!=null)callback.onScore(0);
    }
}
