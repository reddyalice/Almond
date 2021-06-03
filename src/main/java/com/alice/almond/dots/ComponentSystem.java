package com.alice.almond.dots;

public abstract class ComponentSystem {
    public int priority;
    public boolean multiThreaded;
    private boolean processing;
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

    public void addedToManager (EntityManager manager) {
	}
	
	public void removedFromManager (EntityManager manager) {
	}

	
	public void update (float deltaTime) {
    }

    public boolean checkProcessing () {
		return processing;
	}

	
	public void setProcessing (boolean processing) {
		this.processing = processing;
	}
	

	public EntityManager getManager () {
		return manager;
	}
	
	public final void addedToManagerInternal(EntityManager manager) {
		this.manager = manager;
		addedToManager(manager);
	}
	
	public final void removedFromManagerInternal(EntityManager manager) {
		this.manager = null;
		removedFromManager(manager);
	}

}