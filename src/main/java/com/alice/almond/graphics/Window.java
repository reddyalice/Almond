package com.alice.almond.graphics;

import com.alice.almond.utils.Event;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public class Window {

    public final long windowId;
    public Camera camera;
    private String title;
    private int width, height;

    public Window(String title, Camera camera, int width, int height, boolean transparent){
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, transparent ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        windowId = GLFW.glfwCreateWindow(width, height, title, 0, 0);
        this.width = width;
        this.height = height;
        this.title = title;
        this.camera = camera;
        preUpdate.Add("pull", x -> GLFW.glfwPollEvents());
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
