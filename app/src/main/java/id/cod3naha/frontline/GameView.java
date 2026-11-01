package id.cod3naha.frontline;
import android.content.Context;
import android.opengl.GLSurfaceView;

public class GameView extends GLSurfaceView {
    private final ShooterRenderer renderer;
    public GameView(Context context) {
        super(context); setEGLContextClientVersion(2);
        renderer=new ShooterRenderer(); setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
    public void fire(){queueEvent(renderer::fire);}
    public void reset(){queueEvent(renderer::reset);}
    public void look(float x,float y){queueEvent(()->renderer.look(x,y));}
    public void setMove(float x,float y){queueEvent(()->renderer.setMove(x,y));}
    public void setHudCallback(ShooterRenderer.HudCallback cb){renderer.setHudCallback(cb);}
}
