package com.alice.almond.dots;

import com.alice.almond.utils.Event;

public class ComponentSystem {
    public int priority;
    public boolean multiThreaded;
    public boolean processing;
	private EntityManager manager;

    public ComponentSystem () {
		this(0);
	}

	public ComponentSystem (int priority) {
		this(priority, false);
	}

    public ComponentSystem (int priority, boolean multiThreaded) {
		this.priority = priority;
		this.processing = true;
        this.multiThreaded = multiThreaded;
	}

	public final Event<EntityManager> addedToManager = new Event<EntityManager>();
	public final Event<EntityManager> removedFromManager = new Event<EntityManager>();
	public final Event<Float> update = new Event<Float>();
	

	public EntityManager getManager () {
		return manager;
	}
	
	final void addedToManagerInternal(EntityManager manager) {
		this.manager = manager;
		addedToManager.Broadcast(manager);
	}
	
	final void removedFromManagerInternal(EntityManager manager) {
		this.manager = null;
		removedFromManager.Broadcast(manager);
	}

}