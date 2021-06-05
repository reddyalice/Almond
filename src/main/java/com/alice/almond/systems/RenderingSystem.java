package com.alice.almond.systems;

import com.alice.almond.components.MaterialComponent;
import com.alice.almond.dots.ComponentSystem;
import com.alice.almond.dots.Entity;
import com.alice.almond.dots.EntityManager;
import com.alice.almond.dots.ComponentFamily;
import com.alice.almond.utils.Event;
import com.alice.almond.utils.collections.ImmutableArray;

public class RenderingSystem extends ComponentSystem {
    
    private ImmutableArray<Entity> entities;
    private EntityManager manager;

    public final Event<Float> preRender = new Event<Float>();
    public final Event<Float> render = new Event<Float>();
    public final Event<Float> postRender = new Event<Float>();


    public RenderingSystem(){
        
        addedToManager.Add("loadManager", x -> {
            this.manager = x;
            this.entities = x.getEntitiesFor(ComponentFamily.all(MaterialComponent.class).get());
        });


        update.Add("generalUpdate", x -> {
            preRender.Broadcast(x);
            render.Broadcast(x);
            postRender.Broadcast(x);
        });



        
    
    
    }
}
