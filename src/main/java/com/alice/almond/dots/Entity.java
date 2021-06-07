package com.alice.almond.dots;

import com.alice.almond.management.ComponentOperationHandler;
import com.alice.almond.utils.Signal;
import com.alice.almond.utils.collections.Array;
import com.alice.almond.utils.collections.Bag;
import com.alice.almond.utils.collections.Bits;
import com.alice.almond.utils.collections.ImmutableArray;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Entity {
	public int flags;
	public final Signal<Entity> componentAdded;
	public final Signal<Entity> componentRemoved;

	public final Vector3f position = new Vector3f();
    public final Quaternionf rotation = new Quaternionf();
    public final Vector3f scale = new Vector3f(100,100,100);

	public boolean scheduledForRemoval;
	public boolean removing;
	public ComponentOperationHandler componentOperationHandler;

	private Bag<Component> components;
	private Array<Component> componentsArray;
	private ImmutableArray<Component> immutableComponentsArray;
	private Bits componentBits;
	private Bits familyBits;

	public Entity () {
		components = new Bag<Component>();
		componentsArray = new Array<Component>(false, 16);
		immutableComponentsArray = new ImmutableArray<Component>(componentsArray);
		componentBits = new Bits();
		familyBits = new Bits();
		flags = 0;

		componentAdded = new Signal<Entity>();
		componentRemoved = new Signal<Entity>();
	}


	public Entity add (Component component) {
		if (addInternal(component)) {
			if (componentOperationHandler != null) {
				componentOperationHandler.add(this);
			}
			else {
				notifyComponentAdded();
			}
		}
		
		return this;
	}

	
	public <T extends Component> T addAndReturn(T component) {
		add(component);
		return component;
	}

	
	public <T extends Component> T remove (Class<T> componentClass) {
		ComponentType componentType = ComponentType.getFor(componentClass);
		int componentTypeIndex = componentType.getIndex();
		
		if(components.isIndexWithinBounds(componentTypeIndex)){
			Component removeComponent = components.get(componentTypeIndex);
	
			if (removeComponent != null && removeInternal(componentClass) != null) {
				if (componentOperationHandler != null) {
					componentOperationHandler.remove(this);
				}
				else {
					notifyComponentRemoved();
				}
			}
	
			return (T) removeComponent;
		}
		
		return null;
	}

	public void removeAll () {
		while (componentsArray.size > 0) {
			remove(componentsArray.get(0).getClass());
		}
	}

	public ImmutableArray<Component> getComponents () {
		return immutableComponentsArray;
	}

	
	public <T extends Component> T getComponent (Class<T> componentClass) {
		return getComponent(ComponentType.getFor(componentClass));
	}

	
	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent (ComponentType componentType) {
		int componentTypeIndex = componentType.getIndex();

		if (componentTypeIndex < components.getCapacity()) {
			return (T)components.get(componentType.getIndex());
		} else {
			return null;
		}
	}

    public <T extends Component> boolean hasComponent(Class<T> componentClass){
        return hasComponent(ComponentType.getFor(componentClass));
    } 

	
	public boolean hasComponent (ComponentType componentType) {
		return componentBits.get(componentType.getIndex());
	}

	
	public Bits getComponentBits () {
		return componentBits;
	}

	
	public Bits getFamilyBits () {
		return familyBits;
	}

	
	public boolean addInternal (Component component) {
		Class<? extends Component> componentClass = component.getClass();
		Component oldComponent = getComponent(componentClass);

		if (component == oldComponent) {
			return false;
		}

		if (oldComponent != null) {
			removeInternal(componentClass);
		}

		int componentTypeIndex = ComponentType.getIndexFor(componentClass);
		components.set(componentTypeIndex, component);
		componentsArray.add(component);
		componentBits.set(componentTypeIndex);
		
		return true;
	}

	public Component removeInternal (Class<? extends Component> componentClass) {
		ComponentType componentType = ComponentType.getFor(componentClass);
		int componentTypeIndex = componentType.getIndex();
		Component removeComponent = components.get(componentTypeIndex);

		if (removeComponent != null) {
			components.set(componentTypeIndex, null);
			componentsArray.removeValue(removeComponent, true);
			componentBits.clear(componentTypeIndex);
			
			return removeComponent;
		}

		return null;
	}
	
	public void notifyComponentAdded() {
		componentAdded.dispatch(this);
	}
	
	public void notifyComponentRemoved() {
		componentRemoved.dispatch(this);
	}

	
	public boolean isScheduledForRemoval () {
		return scheduledForRemoval;
	}

	
	public boolean isRemoving() {
		return removing;
	}
}
