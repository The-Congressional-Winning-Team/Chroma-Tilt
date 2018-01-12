package com.almela.gaetan.chromatilt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Willg on 1/4/2018.
 */

public class GLRenderer implements GLSurfaceView.Renderer, ImageReader.OnImageAvailableListener {

    private int mProgramObject;

    private int mPositionLoc;
    private int mTexCoordLoc;

    private int mSamplerLoc;

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

    public void setAspectRatio(DisplayMetrics displayMetrics, float aspectRatio) {
        float xOff = ((float)displayMetrics.heightPixels * aspectRatio) / (float) displayMetrics.widthPixels;
        mVerticesData =
                new float[] {
                        -xOff, 1f, 0.0f,
                        0.0f, 0.0f,
                        -xOff, -1f, 0.0f,
                        0.0f, 1.0f,
                        xOff, -1f, 0.0f,
                        1.0f, 1.0f,
                        xOff, 1f, 0.0f,
                        1.0f, 0.0f
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

        mProgramObject = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgramObject, vShader);
        GLES20.glAttachShader(mProgramObject, fShader);

        GLES20.glLinkProgram(mProgramObject);

        GLES20.glDeleteShader(vShader);
        GLES20.glDeleteShader(fShader);

        mPositionLoc = GLES20.glGetAttribLocation(mProgramObject, "a_position");
        mTexCoordLoc = GLES20.glGetAttribLocation(mProgramObject, "a_texCoord");

        mSamplerLoc = GLES20.glGetUniformLocation(mProgramObject, "s_texture");

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

        GLES20.glUniform1i ( mSamplerLoc, 0 );

        GLES20.glDrawElements ( GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mIndices );
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        mWidth = width;
        mHeight = height;
    }

}
