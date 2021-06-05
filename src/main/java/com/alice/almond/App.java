package com.alice.almond;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class App 
{

    private static final int CORES = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService EXECUTER = Executors.newFixedThreadPool(CORES);

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
