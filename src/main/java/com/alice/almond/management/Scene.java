package com.alice.almond.management;

import java.util.HashMap;

import com.alice.almond.dots.Entity;
import com.alice.almond.dots.Component;
import com.alice.almond.dots.ComponentSystem;
import com.alice.almond.graphics.Camera;
import com.alice.almond.graphics.Window;
import com.alice.almond.utils.Event;
import com.alice.almond.utils.collections.SnapshotArray;

import org.lwjgl.glfw.GLFW;

public class Scene {

    private final SnapshotArray<Window> windows = new SnapshotArray<Window>();
    public PooledManager entityManager;

    private WindowPool windowPool = new WindowPool();

    public final Event<Float> init = new Event<Float>();
    public final Event<Float> update = new Event<Float>();

    public Scene(){
        entityManager = new PooledManager(this);
        update.Add("manager Update", x -> {

            for(Window window : windows){
                window.preUpdate.Broadcast(x);
                window.update.Broadcast(x);
            }
            entityManager.update(x);
            for(Window window : windows)
                window.postUpdate.Broadcast(x);
            

        });

    }  

    public Entity createEntity(){
        Entity en = entityManager.createEntity();
        entityManager.addEntity(en);
        return en;
    }

    public <T extends Component> T createComponent(Class<T> componentType){
        return entityManager.createComponent(componentType);
    }

    public ComponentSystem addSystem(ComponentSystem system){
        entityManager.addSystem(system);
        return system;
    }
    

    public Window createWindow(Camera camera, String title, int width, int height, boolean transparent){
        Window window = windowPool.obtain(this, camera, title, width, height, transparent);
        windows.add(window);
        window.postUpdate.Add("delete", x -> {
            if(GLFW.glfwWindowShouldClose(window.windowId)){
                windows.removeValue(window, false);
                windowPool.free(window);
            }
        });
        return window;
    }


    public Window getWindow(int key) {
        return windows.get(key);
    }

    public SnapshotArray<Window> getWindows(){
        return windows;
    }

}
