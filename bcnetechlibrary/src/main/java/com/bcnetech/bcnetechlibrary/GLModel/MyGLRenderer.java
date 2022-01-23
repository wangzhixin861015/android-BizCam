package com.bcnetech.bcnetechlibrary.GLModel;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private float x,y,z;
    Context context;   // Application's context
    private Cube cube;          // (NEW)
    Random r = new Random();
    // Rotational angle and speed (NEW)
    private float angleTriangle = 0.0f; // (NEW)
    private float angleQuad = 0.0f;     // (NEW)
    //		   private float speedTriangle = 0.5f; // (NEW)
//		   private float speedQuad = -0.4f;    // (NEW)
    private static float anglePyramid = 0; // Rotational angle in degree for pyramid (NEW)
    private static float angleCube = 0;
    private static float speedPyramid = 2.5f; // Rotational speed for pyramid (NEW)
    private static float speedCube = 5.0f;   // Rotational speed for cube (NEW)


    // Constructor with global application context
    public MyGLRenderer(Context context) {
        this.context = context;
        cube = new Cube();

    }

    public void onResume(){

    }

    public void onPause() {
        // 取消注册
    }

    // Call back when the surface is first created or re-created
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0,0,0, 0);  // Set color's clear-value to black
        gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
        gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
        gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
        gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance

        x=0;y=0;z=0;

    }

    // Call back after onSurfaceCreated() or whenever the window's size changes
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1;   // To prevent divide by zero
        float aspect = (float) width / height;

        // Set the viewport (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
        gl.glLoadIdentity();                 // Reset projection matrix
        // Use perspective projection
        GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
        gl.glLoadIdentity();                 // Reset

        // You OpenGL|ES display re-sizing code here
        // ......
    }

    // Call back to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear color and depth buffers using clear-value set earlier
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // You OpenGL|ES rendering code here
        /*gl.glLoadIdentity();                 // Reset model-view matrix ( NEW )
        gl.glTranslatef(-1.5f, 0.0f, -6.0f); // Translate left and into the screen ( NEW )
        gl.glRotatef(angleTriangle, 0.0f, 1.0f, 0.0f); // Rotate the triangle about the y-axis (NEW)
        //triangle.draw(gl);                   // Draw triangle
        gl.glRotatef(anglePyramid, 0.1f, 1.0f, -0.1f); // Rotate (NEW)
        pyramid.draw(gl);*/
        gl.glLoadIdentity();                 // Reset the mode-view matrix (NEW)
        gl.glTranslatef(0.0f, 0.0f, -3.0f);  // Translate right and into the screen (NEW)
        //gl.glRotatef(angleQuad, 1.0f, 0.0f, 0.0f); // Rotate the square about the x-axis (NEW)
        //quad.draw(gl);                       // Draw quad
        gl.glScalef(0.5f, 0.5f, 0.5f);      // Scale down (NEW)

        gl.glRotatef(x, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(y, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(z, 0.0f, 0.0f, 1.0f); // rotate about the axis (1,1,1) (NEW)
        cube.draw(gl);

        // Update the rotational angle after each refresh (NEW)
//		      angleTriangle += speedTriangle; // (NEW)
//		      angleQuad += speedQuad;         // (NEW)
       // anglePyramid += speedPyramid;   // (NEW)
        angleCube += speedCube;
        //angleCube += speedCube2 + speedCube;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}