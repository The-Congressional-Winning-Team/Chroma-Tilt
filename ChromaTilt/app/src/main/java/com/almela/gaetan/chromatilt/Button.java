package com.almela.gaetan.chromatilt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.DisplayMetrics;
import android.util.Size;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Willg on 1/15/2018.
 */

public class Button {

    Bitmap texture;

    int textureId;

    static int programObject;

    static int positionLoc;
    static int texCoordLoc;
    static int samplerLoc;

    private FloatBuffer vertices;
    private ShortBuffer indices;

    private float[] verticesData;

    private final short[] indicesData =
            {
                    0, 1, 2, 0, 2, 3
            };


    static {
        String vShaderStr =
                "attribute vec4 a_position;   \n"
                        + "attribute vec2 a_texCoord;   \n"
                        + "varying vec2 v_texCoord;     \n"
                        + "void main()                  \n"
                        + "{                            \n"
                        + "   gl_Position = a_position; \n"
                        + "   v_texCoord = a_texCoord;  \n"
                        + "}                            \n";

        String fShaderStr =
                "precision mediump float;                            \n"
                        + "varying vec2 v_texCoord;                            \n"
                        + "uniform sampler2D s_texture;                        \n"
                        + "void main()                                         \n"
                        + "{                                                   \n"
                        + "  gl_FragColor = texture2D( s_texture, v_texCoord );\n"
                        + "}                                                   \n";

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

        samplerLoc = GLES20.glGetUniformLocation(programObject, "s_texture");
    }

    public Button(Size size, Point position, String text, DisplayMetrics metrics) {
        //Float scale = metrics.density;
        texture = Bitmap.createBitmap(metrics, metrics.widthPixels, 100, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        Canvas canvas = new Canvas(texture);
        canvas.drawRect(0, 0, texture.getWidth(), texture.getHeight(), paint);

        int[] textureIds = new int[1];

        GLES20.glPixelStorei ( GLES20.GL_UNPACK_ALIGNMENT, 1 );

        GLES20.glGenTextures ( 1, textureIds, 0 );

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);

        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST );

        textureId = textureIds[0];

        float heightHalf = (metrics.heightPixels / 2);
        float widthHalf = (metrics.widthPixels / 2);

        float xPos = (position.y - heightHalf) / heightHalf;
        float yPos = (position.x - widthHalf) / widthHalf;
        float width = size.getHeight() / heightHalf;
        float height = size.getWidth() / widthHalf;

        verticesData = new float[] {

        };
    }

    public void draw() {

    }

}
