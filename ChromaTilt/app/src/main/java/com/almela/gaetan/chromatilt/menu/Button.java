package com.almela.gaetan.chromatilt.menu;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.DisplayMetrics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Willg on 1/15/2018.
 */

public class Button {

    Bitmap texture;

    int textureId;

    private FloatBuffer vertices;
    private ShortBuffer indices;

    private float[] verticesData;

    private final short[] indicesData =
            {
                    0, 1, 2, 0, 2, 3
            };

    public Runnable onPress;

    float xPos;
    float yPos;

    float width;
    float height;

    public boolean pointIsIn(int x, int y) {
        float[] coord = Menu.screenToOGL(x,y);
        return (coord[0] > xPos && coord[0] < (xPos + width)
                && coord[1] > yPos && coord[1] < (yPos + height));
    }

    public Button(float width, float height, float x, float y, String text, DisplayMetrics metrics, Runnable onPress) {
        this.onPress = onPress;
        this.width = width;
        this.height = height;

        float heightHalf = (metrics.heightPixels / 2);
        float widthHalf = (metrics.widthPixels / 2);

        yPos = ((y * metrics.heightPixels) - heightHalf) / heightHalf;
        xPos = ((x * metrics.widthPixels) - widthHalf) / widthHalf;

        this.height *= ((float) metrics.widthPixels / (float) metrics.heightPixels) * 2;
        this.width *= 2;

        int screenWidth = (int) ((this.height / 2f) * metrics.heightPixels);
        int screenHeight = (int) ((this.width / 2f) * metrics.widthPixels);

        Float scale = metrics.density;
        texture = Bitmap.createBitmap(metrics, screenHeight, screenWidth, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.argb(100, 0, 0, 0));
        Canvas canvas = new Canvas(texture);
        canvas.drawRect(0, 0, texture.getWidth(), texture.getHeight(), paint);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize((int) (40 * scale));

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int textX = (texture.getWidth() - bounds.width())/2;
        int textY = (texture.getHeight() + bounds.height())/2;

        canvas.drawText(text, textX, textY, paint);
        //canvas.drawRect(0, 0, texture.getWidth(), texture.getHeight() / 2, paint);

        int[] textureIds = new int[1];

        GLES20.glPixelStorei ( GLES20.GL_UNPACK_ALIGNMENT, 1 );

        GLES20.glGenTextures ( 1, textureIds, 0 );

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);

        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST );

        textureId = textureIds[0];

        verticesData = new float[] {
                xPos, yPos + this.height, 0f,
                0f, 0f,
                xPos, yPos, 0f,
                0f, 1f,
                xPos + this.width, yPos, 0f,
                1f, 1f,
                xPos + this.width, yPos + this.height, 0f,
                1f, 0f
        };

        indices = ByteBuffer.allocateDirect(indicesData.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        indices.put(indicesData).position(0);

        vertices = ByteBuffer.allocateDirect(verticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertices.put(verticesData).position(0);
    }

    public void draw() {
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        vertices.position(0);
        GLES20.glVertexAttribPointer(Menu.positionLoc, 3, GLES20.GL_FLOAT, false, 5*4, vertices);
        vertices.position(3);
        GLES20.glVertexAttribPointer(Menu.texCoordLoc, 2, GLES20.GL_FLOAT, false, 5*4, vertices);

        GLES20.glEnableVertexAttribArray(Menu.positionLoc);
        GLES20.glEnableVertexAttribArray(Menu.texCoordLoc);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glUniform1i(Menu.samplerLoc, 0);

        GLES20.glDrawElements ( GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indices );
    }

}
