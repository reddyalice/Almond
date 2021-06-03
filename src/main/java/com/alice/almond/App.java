package com.alice.almond;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class App 
{
    public static void main( String[] args )
    {
        GLFWErrorCallback.createPrint(System.err).set();
		boolean isInitialized  = GLFW.glfwInit();
		if(!isInitialized){
			System.err.println("Failed To initialized!");
			System.exit(1);
        }
    }
}
