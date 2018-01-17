package com.almela.gaetan.chromatilt;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by Willg on 1/4/2018.
 */

public class FilterGLSurfaceView extends GLSurfaceView {

    public FilterGLSurfaceView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Toast.makeText(getContext(), "Touch", Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}
