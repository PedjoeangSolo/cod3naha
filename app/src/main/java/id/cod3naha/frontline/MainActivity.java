package id.cod3naha.frontline;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends Activity {
    private GameView game;
    private TextView hud;
    private float lastX, lastY;
    private boolean moving;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(4 | 1024 | 4096 | 512 | 2);
        FrameLayout root = new FrameLayout(this);
        game = new GameView(this);
        root.addView(game, new FrameLayout.LayoutParams(-1, -1));

        hud = new TextView(this);
        hud.setTextColor(Color.WHITE); hud.setTextSize(15);
        hud.setShadowLayer(3, 1, 1, Color.BLACK);
        hud.setText("FRONTLINE  •  TARGETS: 0/10");
        FrameLayout.LayoutParams hp = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        hp.topMargin = 16; root.addView(hud, hp);

        Button fire = new Button(this); fire.setText("FIRE");
        FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(150, 110, Gravity.RIGHT | Gravity.BOTTOM);
        fp.rightMargin = 30; fp.bottomMargin = 30; root.addView(fire, fp);
        fire.setOnTouchListener((v,e) -> { if(e.getAction()==MotionEvent.ACTION_DOWN) game.fire(); return true; });

        Button restart = new Button(this); restart.setText("RESTART");
        FrameLayout.LayoutParams rp = new FrameLayout.LayoutParams(130, 80, Gravity.RIGHT | Gravity.TOP);
        rp.rightMargin=20; rp.topMargin=50; root.addView(restart,rp);
        restart.setOnClickListener(v -> { game.reset(); updateHud(0); });

        root.setOnTouchListener((v,e) -> {
            float x=e.getX(), y=e.getY();
            if(e.getAction()==MotionEvent.ACTION_DOWN){lastX=x;lastY=y;moving=x<root.getWidth()*0.42f;return true;}
            if(e.getAction()==MotionEvent.ACTION_MOVE){
                float dx=x-lastX, dy=y-lastY;
                if(moving) game.setMove(clamp(dx/100f),clamp(-dy/100f));
                else game.look(dx*0.004f,dy*0.003f);
                lastX=x;lastY=y;return true;
            }
            if(e.getAction()==MotionEvent.ACTION_UP){if(moving)game.setMove(0,0);return true;}
            return true;
        });
        setContentView(root);
        game.setHudCallback(score -> runOnUiThread(() -> updateHud(score)));
    }
    private void updateHud(int score){hud.setText("FRONTLINE  •  TARGETS: "+score+"/10");}
    private float clamp(float v){return Math.max(-1f,Math.min(1f,v));}
    @Override protected void onPause(){super.onPause();if(game!=null)game.onPause();}
    @Override protected void onResume(){super.onResume();if(game!=null)game.onResume();}
}
