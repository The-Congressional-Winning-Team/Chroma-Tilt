package com.almela.gaetan.chromatilt;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by Willg on 1/4/2018.
 */

public class FilterGLSurfaceView extends GLSurfaceView {

    MainActivity activity;

    GestureDetector gestureDetector;

    public FilterGLSurfaceView(Context context) {
        super(context);
        this.activity = (MainActivity) context;

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (!activity.renderer.menuOpen)
                    return false;

                int x = (int) e.getX();
                int y = (int) e.getY();

                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        for (Button b : activity.renderer.buttons) {
                            if (b.pointIsIn(x, y)) {
                                b.onPress.run();
                                activity.renderer.menuOpen = false;
                                return true;
                            }
                        }
                        break;
                }
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(velocityX < 0 || velocityY < 0) {
                    activity.renderer.menuOpen = true;
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return true;
    }
}
