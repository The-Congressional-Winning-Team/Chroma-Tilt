package com.almela.gaetan.chromatilt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorSpace;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.DisplayMetrics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Willg on 1/4/2018.
 */

public class GLRenderer implements GLSurfaceView.Renderer {

    private FloatBuffer vertexBuffer;
    private float vertices[] = {
            -1f, -1f, 0f,
            -1f, 1f, 0f,
            1f, -1f, 0f,
            -1f, 1f, 0f,
            1f, 1f, 0f,
            1f, -1f, 0f
    };
    private float color[] = new float[] { 0.0f, 0.6f, 1.0f, 1.0f };

    private int textures[] = new int[1];
    private float textureCoordinates[] = {0.0f, 0.5f,
            0.5f, 0.5f,
            0.0f, 0.0f,
            0.5f, 0.0f };
    private FloatBuffer textureBuffer;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
            "void main() {" +
            "   gl_Position = vPosition;" +
            "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "   gl_FragColor = vColor;" +
            "}";
    private int shaderProgram;

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glUseProgram(shaderProgram);

        int positionAttrib = GLES20.glGetAttribLocation(shaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionAttrib);

        GLES20.glVertexAttribPointer(positionAttrib, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        int colorUniform = GLES20.glGetUniformLocation(shaderProgram, "vColor");

        GLES20.glUniform4fv(colorUniform, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length / 3);
        GLES20.glDisableVertexAttribArray(positionAttrib);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);

        Bitmap bitmap = Bitmap.createBitmap((DisplayMetrics) null, 100,100, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                bitmap.setPixel(x,y,(int) (Math.random() * Math.pow(2,32)));
            }
        }

        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuf.asFloatBuffer();
        textureBuffer.put(textureCoordinates);
        textureBuffer.position(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

}
