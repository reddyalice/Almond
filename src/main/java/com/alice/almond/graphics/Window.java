package com.alice.almond.graphics;

import org.lwjgl.glfw.GLFW;

public class Window {

    public final long windowId;
    private String title;
    private int width, height;

    public Window(String title, int width, int height, boolean transparent){
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, transparent ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        windowId = GLFW.glfwCreateWindow(width, height, title, 0, 0);
        this.width = width;
        this.height = height;
        this.title = title;
    }


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
