package com.almela.gaetan.chromatilt;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.almela.gaetan.chromatilt.menu.Button;
import com.almela.gaetan.chromatilt.menu.Menu;

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
                Menu menu = activity.renderer.menu;
                for (Button b : menu.buttons) {
                    if (b.pointIsIn((int) e.getX(), (int) e.getY())) {
                        b.onPress.run();
                        return true;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return true;
    }

}
