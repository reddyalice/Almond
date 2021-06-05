package com.alice.almond;

import java.util.HashMap;

import com.alice.almond.dots.Entity;
import com.alice.almond.dots.PooledManager;
import com.alice.almond.dots.Component;
import com.alice.almond.dots.ComponentSystem;
import com.alice.almond.graphics.Camera;
import com.alice.almond.graphics.Window;
import com.alice.almond.utils.Event;

public class Scene {

    private final HashMap<String, Window> windows = new HashMap<String, Window>();
    public PooledManager entityManager;

    public final Event<Float> init = new Event<Float>();
    public final Event<Float> update = new Event<Float>();

    public Scene(){
        entityManager = new PooledManager(this);
        update.Add("manager Update", x -> {
            entityManager.update(x);
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
    

    public Window createWindow(String key, Camera camera, String title, int width, int height, boolean transparent){
        Window window = new Window(title, camera, width, height, transparent);
        windows.put(key, window);
        return windows.get(key);
    }


    public Window getWindow(String key) {
        return windows.get(key);
    }



}
