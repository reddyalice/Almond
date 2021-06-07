package com.alice.almond;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alice.almond.components.MaterialComponent;
import com.alice.almond.dots.Entity;
import com.alice.almond.graphics.Model2D;
import com.alice.almond.graphics.Model3D;
import com.alice.almond.graphics.OrthographicCamera;
import com.alice.almond.graphics.PerspectiveCamera;
import com.alice.almond.graphics.Texture;
import com.alice.almond.graphics.VAO;
import com.alice.almond.graphics.Window;
import com.alice.almond.graphics.materials.Basic2DMaterial;
import com.alice.almond.graphics.materials.Basic3DMaterial;
import com.alice.almond.management.OBJLoader;
import com.alice.almond.management.Scene;
import com.alice.almond.systems.RenderingSystem;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

public class App 
{

    private static final int CORES = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService EXECUTER = Executors.newFixedThreadPool(CORES);

    public static void main( String[] args ) throws IOException
    {
        GLFWErrorCallback.createPrint(System.err).set();
		boolean isInitialized  = GLFW.glfwInit();
		if(!isInitialized){
			System.err.println("Failed To initialized!");
			System.exit(1);
        }
       
        OrthographicCamera cam = new OrthographicCamera( 640, 480);
        Scene scene = new Scene();
        Window w = scene.createWindow(cam, "title", 640, 480, false);
   
        
        Model3D modelData = OBJLoader.loadOBJ("res/models/cactus.obj");
        


        
        //comp.model = VAO.loadToVAO(modelData);
        
        //comp.texture.GenTexture();
        //comp.material = new BasicMaterial();
        
      
       
        

       // Window w2 = scene.createWindow(cam, "title1", 640, 480, true);
       // Window w3 = scene.createWindow(cam, "title12", 640, 480, false);
        Entity en = scene.createEntity();
        MaterialComponent comp = scene.createComponent(MaterialComponent.class);
        comp.texture = new Texture("res/textures/cactus.png");
        comp.model = VAO.loadToVAO(modelData);
        comp.texture.GenTexture();
        comp.material = new Basic3DMaterial();
      
        en.add(comp);
        en.position.set(0,0,-1);
        en.scale.set(1, 1, 1);
       
        scene.init.Broadcast(1f/60f);

        scene.addSystem(new RenderingSystem());
        while(scene.getWindows().size > 0){

            cam.update();
            scene.update.Broadcast(1f/60f);
            GLFW.glfwPollEvents();


        }

        
        GLFW.glfwTerminate();


    }
}
