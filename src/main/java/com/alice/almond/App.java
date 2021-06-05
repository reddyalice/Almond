package com.alice.almond;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alice.almond.graphics.OrthographicCamera;
import com.alice.almond.graphics.Window;
import com.alice.almond.systems.RenderingSystem;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

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

       
        Scene scene = new Scene();
        scene.addSystem(new RenderingSystem());
        Window w = scene.createWindow("test", new OrthographicCamera(640, 480), "title", 640, 480, false);

        while(!GLFW.glfwWindowShouldClose(w.windowId)){

            w.preUpdate.Broadcast(1f/60f);
            scene.update.Broadcast(1f/60f);


        }

        



    }
}
