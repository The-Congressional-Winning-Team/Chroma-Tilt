package com.almela.gaetan.chromatilt.menu;

import android.content.Context;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.almela.gaetan.chromatilt.GLRenderer;
import com.almela.gaetan.chromatilt.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Willg on 1/22/2018.
 */

public class Menu extends GestureDetector.SimpleOnGestureListener {

    List<Button> buttons;

    MainActivity activity;

    GLRenderer renderer;

    public Menu(Context context) {
        this.activity = (MainActivity) context;
        this.renderer = this.activity.renderer;
        buttons = new ArrayList<>();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.buttons.add(new Button(1f, 0.2f, 0, 0.1f, "Protanopia", displayMetrics, new Runnable() {
            @Override
            public void run() {
                renderer.LSlider = 1f;
                renderer.MSlider = 0f;
                renderer.SSlider = 0f;
            }
        }));

        this.buttons.add(new Button(1f, 0.2f, 0, 0.3f, "Deuteranopia", displayMetrics, new Runnable() {
            @Override
            public void run() {
                renderer.LSlider = 0f;
                renderer.MSlider = 1f;
                renderer.SSlider = 0f;
            }
        }));

        this.buttons.add(new Button(1f, 0.2f, 0, 0.5f, "Tritanopia", displayMetrics, new Runnable() {
            @Override
            public void run() {
                renderer.LSlider = 0f;
                renderer.MSlider = 0f;
                renderer.SSlider = 1f;
            }
        }));

        this.buttons.add(new Button(1f, 0.2f, 0, 0.7f, "Normal", displayMetrics, new Runnable() {
            @Override
            public void run() {
                renderer.LSlider = 0f;
                renderer.MSlider = 0f;
                renderer.SSlider = 0f;
            }
        }));
    }

    public void draw() {
        for (Button b : buttons) {
            b.draw();
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Toast.makeText(activity, "Touch", Toast.LENGTH_SHORT).show();
        for (Button b : buttons) {
            if (b.pointIsIn((int) e.getX(), (int) e.getY())) {
                b.onPress.run();
                return true;
            }
        }
        return false;
    }

}
