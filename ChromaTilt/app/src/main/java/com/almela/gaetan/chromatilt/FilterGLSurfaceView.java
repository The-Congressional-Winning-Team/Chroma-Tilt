package com.almela.gaetan.chromatilt;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.almela.gaetan.chromatilt.menu.Button;

/**
 * Created by Willg on 1/4/2018.
 */

public class FilterGLSurfaceView extends GLSurfaceView {

    public FilterGLSurfaceView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return true;
    }
}
