package com.almela.gaetan.chromatilt.menu;

import android.content.Context;
import android.opengl.GLES20;
import android.os.Looper;
import android.util.DisplayMetrics;

import com.almela.gaetan.chromatilt.GLRenderer;
import com.almela.gaetan.chromatilt.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Willg on 1/22/2018.
 */

public class Menu {

    public List<Button> buttons;

    MainActivity activity;

    GLRenderer renderer;

    static int programObject;

    static int positionLoc;
    static int texCoordLoc;
    static int samplerLoc;

    int xOffLoc;

    public Menu(Context context) {
        Looper.prepare();
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

        String vShaderStr = activity.readFile("menu.vert");
        String fShaderStr = activity.readFile("menu.frag");

        int vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vShader, vShaderStr);
        GLES20.glCompileShader(vShader);

        int fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fShader, fShaderStr);
        GLES20.glCompileShader(fShader);

        programObject = GLES20.glCreateProgram();

        GLES20.glAttachShader(programObject, vShader);
        GLES20.glAttachShader(programObject, fShader);

        GLES20.glLinkProgram(programObject);

        GLES20.glDeleteShader(vShader);
        GLES20.glDeleteShader(fShader);

        positionLoc = GLES20.glGetAttribLocation(programObject, "a_position");
        texCoordLoc = GLES20.glGetAttribLocation(programObject, "a_texCoord");

        xOffLoc = GLES20.glGetUniformLocation(programObject, "xOff");

        samplerLoc = GLES20.glGetUniformLocation(programObject, "s_texture");
    }

    public void draw() {
        for (Button b : buttons) {
            GLES20.glUseProgram(programObject);

            GLES20.glUniform1f(xOffLoc, 1);

            b.draw();
        }
    }

}
