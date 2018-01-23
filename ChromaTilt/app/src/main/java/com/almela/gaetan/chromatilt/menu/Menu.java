package com.almela.gaetan.chromatilt.menu;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.almela.gaetan.chromatilt.MainActivity;

import java.util.List;

/**
 * Created by Willg on 1/22/2018.
 */

public class Menu {

    List<Button> buttons;

    GestureDetector gestureDetector;

    MainActivity activity;

    public Menu(Context context) {
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
                                activity.renderer.opening = true;
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
                    activity.renderer.opening = true;
                    return true;
                }
                return false;
            }
        });
    }

    public boolean onTouchEvent(MotionEvent e) {
        gestureDetector.onTouchEvent(e);
    }

}
