package com.almela.gaetan.chromatilt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;

import com.almela.gaetan.chromatilt.menu.Button;
import com.almela.gaetan.chromatilt.menu.Menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Willg on 1/4/2018.
 */

public class GLRenderer implements GLSurfaceView.Renderer, ImageReader.OnImageAvailableListener {

    public float LSlider = 1.0f;//Controls the amount of shifting in each cone. (Cool huh?)
    public float MSlider = 0.0f;
    public float SSlider = 0.0f;

    private int mProgramObject;

    private int mPositionLoc;
    private int mTexCoordLoc;

    private int mSamplerLoc;

    private int mLSliderLoc;
    private int mMSliderLoc;
    private int mSSliderLoc;

    private int mTextureId = -1;

    private int mWidth;
    private int mHeight;
    private FloatBuffer mVertices;
    private ShortBuffer mIndices;

    private float[] mVerticesData;

    private final short[] mIndicesData =
            {
                    0, 1, 2, 0, 2, 3
            };

    Bitmap cameraImage;

    MainActivity activity;

    Menu menu;

    boolean menuOpen = false;

    float xOff = 2f;

    boolean opening = false;

    public GLRenderer(MainActivity activity) {
        this.activity = activity;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (opening && xOff > 0f) {
                        xOff -= 0.05;
                        if (xOff < 0)
                            xOff = 0;
                        try {
                            Thread.sleep(50l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (!opening && xOff <= 2f) {
                        xOff += 0.05f;
                        if (xOff > 2f)
                            xOff = 2f;
                        try {
                            Thread.sleep(50l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public void setAspectRatio(DisplayMetrics displayMetrics, float aspectRatio) {
        float xOff = ((float) displayMetrics.heightPixels / aspectRatio) / displayMetrics.widthPixels;

        mVerticesData =
                new float[] {
                        -xOff, 1, 0.0f,
                        0.0f, 1.0f,
                        -xOff, -1, 0.0f,
                        1.0f, 1.0f,
                        xOff, -1, 0.0f,
                        1.0f, 0.0f,
                        xOff, 1, 0.0f,
                        0.0f, 0.0f
                };
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        mIndices = ByteBuffer.allocateDirect(mIndicesData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(mIndicesData).position(0);

        cameraImage = Bitmap.createBitmap((DisplayMetrics) null, 5, 5, Bitmap.Config.ARGB_8888);
    }


    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image;
        try {
            image = reader.acquireLatestImage();
        } catch (IllegalStateException e) {
            return;
        }
        if(image != null) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            cameraImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
            image.close();
        }
    }

    public void createTexture()
    {
        if (mTextureId != -1) {
            GLES20.glDeleteTextures(1, new int[] {mTextureId}, 0);
        }
        int[] textureId = new int[1];

        GLES20.glPixelStorei ( GLES20.GL_UNPACK_ALIGNMENT, 1 );

        GLES20.glGenTextures ( 1, textureId, 0 );

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, cameraImage, 0);

        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST );

        mTextureId = textureId[0];
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        menu = new Menu(activity);

        String vShaderStr = activity.readFile("shader.vert");
        String fShaderStr = activity.readFile("shader.frag");

        int vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vShader, vShaderStr);
        GLES20.glCompileShader(vShader);

        int fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fShader, fShaderStr);
        GLES20.glCompileShader(fShader);

        mProgramObject = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgramObject, vShader);
        GLES20.glAttachShader(mProgramObject, fShader);

        GLES20.glLinkProgram(mProgramObject);

        GLES20.glDeleteShader(vShader);
        GLES20.glDeleteShader(fShader);

        mPositionLoc = GLES20.glGetAttribLocation(mProgramObject, "a_position");
        mTexCoordLoc = GLES20.glGetAttribLocation(mProgramObject, "a_texCoord");

        mSamplerLoc = GLES20.glGetUniformLocation(mProgramObject, "s_texture");

        mLSliderLoc = GLES20.glGetUniformLocation(mProgramObject, "LShift");
        mMSliderLoc = GLES20.glGetUniformLocation(mProgramObject, "MShift");
        mSSliderLoc = GLES20.glGetUniformLocation(mProgramObject, "SShift");

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void onDrawFrame(GL10 glUnused)
    {
        createTexture();

        GLES20.glViewport(0, 0, mWidth, mHeight);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(mProgramObject);

        mVertices.position(0);
        GLES20.glVertexAttribPointer ( mPositionLoc, 3, GLES20.GL_FLOAT,
                false,
                5 * 4, mVertices );
        mVertices.position(3);
        GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT,
                false,
                5 * 4,
                mVertices );

        GLES20.glEnableVertexAttribArray ( mPositionLoc );
        GLES20.glEnableVertexAttribArray ( mTexCoordLoc );

        GLES20.glActiveTexture ( GLES20.GL_TEXTURE0 );
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_2D, mTextureId );

        GLES20.glUniform1f(mLSliderLoc, LSlider);//Bind that shit yo
        GLES20.glUniform1f(mMSliderLoc, MSlider);
        GLES20.glUniform1f(mSSliderLoc, SSlider);

        GLES20.glUniform1i ( mSamplerLoc, 0 );

        GLES20.glDrawElements ( GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mIndices );

        menu.draw();
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        mWidth = width;
        mHeight = height;
    }

}
