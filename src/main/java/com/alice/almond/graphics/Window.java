package com.alice.almond.graphics;

import java.nio.IntBuffer;

import com.alice.almond.management.Scene;
import com.alice.almond.utils.Event;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public class Window {

    public final long windowId;
    public Camera camera;
    private String title;
    private int width, height;
    private Vector2i position = new Vector2i();
    public Scene scene;
    public final boolean transparent;
    public Window(String title, Scene scene, Camera camera, int width, int height, boolean transparent){
        this.transparent = transparent;
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, transparent ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);

        windowId = GLFW.glfwCreateWindow(width, height, title, 0, scene.getWindows().size > 0 ? scene.getWindow(scene.getWindows().size - 1).windowId : 0);
        this.width = width;
        this.height = height;
        this.title = title;
        this.camera = camera;
        this.camera.update();
        update.Add("camera", x -> camera.update());
        GLFW.glfwMakeContextCurrent(windowId);
        GL.createCapabilities();
        
    }

    public Event<Float> preUpdate = new Event<Float>();
    public Event<Float> update = new Event<Float>();
    public Event<Float> postUpdate = new Event<Float>();

    public Event<Float> preRender = new Event<Float>();
    public Event<Float> render = new Event<Float>();
    public Event<Float> postRender = new Event<Float>();

    public void setTitle(String title){
        GLFW.glfwSetWindowTitle(windowId, title);
        this.title = title;
    }

    public void setPosition(int x, int y){
        GLFW.glfwSetWindowPos(windowId, x, y);
        position.set(x,y);
    }

    public Vector2i getPosition(){
        int[] x = new int[1], y = new int[1];
        GLFW.glfwGetWindowPos(windowId, x, y);
        position.set(x[0], y[0]);
        return position;
    }

    public void setWidth(int width){
        GLFW.glfwSetWindowSize(windowId, width, height);
        this.width = width;
    }

    public void setHeight(int height){
        GLFW.glfwSetWindowSize(windowId, width, height);
        this.height = height;
    }

    public String getTitle(){
        return title;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

}
