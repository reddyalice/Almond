package com.alice.almond.dots;

import com.alice.almond.App;
import com.alice.almond.Scene;
import com.alice.almond.dots.ComponentOperationHandler.BooleanInformer;
import com.alice.almond.dots.SystemHandler.SystemListener;
import com.alice.almond.utils.Event;
import com.alice.almond.utils.Listener;
import com.alice.almond.utils.Signal;
import com.alice.almond.utils.collections.ImmutableArray;
import com.alice.almond.utils.reflections.ClassReflection;
import com.alice.almond.utils.reflections.ReflectionException;

public class EntityManager {

	private static ComponentFamily empty = ComponentFamily.all().get();
	public Scene scene;

	private final Listener<Entity> componentAdded = new ComponentListener();
	private final Listener<Entity> componentRemoved = new ComponentListener();

	private SystemHandler systemHandler = new SystemHandler(new ManagerSystemListener());
	private EntityHandler entityHandler = new EntityHandler(new ManagerEntityListener());
	private ComponentOperationHandler componentOperationHandler = new ComponentOperationHandler(
			new ManagerDelayedInformer());
	private FamilyHandler familyHandler = new FamilyHandler(entityHandler.getEntities());
	private boolean updating;

	public EntityManager(Scene scene) {
		this.scene = scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public Entity createEntity() {
		return new Entity();
	}

	public <T extends Component> T createComponent(Class<T> componentType) {
		try {
			return ClassReflection.newInstance(componentType);
		} catch (ReflectionException e) {
			return null;
		}
	}

	public void addEntity(Entity entity) {
		boolean delayed = updating || familyHandler.notifying();
		entityHandler.addEntity(entity, delayed);
	}

	public void removeEntity(Entity entity) {
		boolean delayed = updating || familyHandler.notifying();
		entityHandler.removeEntity(entity, delayed);
	}

	public void removeAllEntities(ComponentFamily family) {
		boolean delayed = updating || familyHandler.notifying();
		entityHandler.removeAllEntities(getEntitiesFor(family), delayed);
	}

	public void removeAllEntities() {
		boolean delayed = updating || familyHandler.notifying();
		entityHandler.removeAllEntities(delayed);
	}

	public ImmutableArray<Entity> getEntities() {
		return entityHandler.getEntities();
	}

	public void addSystem(ComponentSystem system) {
		systemHandler.addSystem(system);
	}

	public void removeSystem(ComponentSystem system) {
		systemHandler.removeSystem(system);
	}

	public void removeAllSystems() {
		systemHandler.removeAllSystems();
	}

	public <T extends ComponentSystem> T getSystem(Class<T> systemType) {
		return systemHandler.getSystem(systemType);
	}

	public ImmutableArray<ComponentSystem> getSystems() {
		return systemHandler.getSystems();
	}

	public ImmutableArray<Entity> getEntitiesFor(ComponentFamily family) {
		return familyHandler.getEntitiesFor(family);
	}

	public void addEntityListener(EntityListener listener) {
		addEntityListener(empty, 0, listener);
	}

	public void addEntityListener(int priority, EntityListener listener) {
		addEntityListener(empty, priority, listener);
	}

	public void addEntityListener(ComponentFamily family, EntityListener listener) {
		addEntityListener(family, 0, listener);
	}

	public void addEntityListener(ComponentFamily family, int priority, EntityListener listener) {
		familyHandler.addEntityListener(family, priority, listener);
	}

	public void removeEntityListener(EntityListener listener) {
		familyHandler.removeEntityListener(listener);
	}

	private final Event<Float> singleThreadUpdate = new Event<Float>();

	public void update(float deltaTime) {
		if (updating) {
			throw new IllegalStateException("Cannot call update() on an Engine that is already updating.");
		}

		singleThreadUpdate.Clear();
		updating = true;
		ImmutableArray<ComponentSystem> systems = systemHandler.getSystems();

		try {
			for (int i = 0; i < systems.size(); ++i) {
				ComponentSystem system = systems.get(i);

				if (system.multiThreaded) {
					if (system.processing) {
						App.EXECUTER.execute(() -> {
							system.update.Broadcast(deltaTime);
							while (componentOperationHandler.hasOperationsToProcess()
									|| entityHandler.hasPendingOperations()) {
								componentOperationHandler.processOperations();
								entityHandler.processPendingOperations();
							}
						});

					} else {
						App.EXECUTER.execute(() -> {
							while (componentOperationHandler.hasOperationsToProcess()
									|| entityHandler.hasPendingOperations()) {
								componentOperationHandler.processOperations();
								entityHandler.processPendingOperations();
							}
						});
					}
				} else {

					if (system.processing) {
						singleThreadUpdate.Add(i + "s", (x) -> {
							system.update.Broadcast(x);
							while (componentOperationHandler.hasOperationsToProcess()
									|| entityHandler.hasPendingOperations()) {
								componentOperationHandler.processOperations();
								entityHandler.processPendingOperations();
							}
							;
						});
					} else {
						singleThreadUpdate.Add(i + "s", (x) -> {
							while (componentOperationHandler.hasOperationsToProcess()
									|| entityHandler.hasPendingOperations()) {
								componentOperationHandler.processOperations();
								entityHandler.processPendingOperations();
							}
							;
						});
					}
				}

			}
			singleThreadUpdate.Broadcast(deltaTime);
			while(!App.EXECUTER.isTerminated())	{}


		
		} finally {
			updating = false;
		}
	}

	protected void addEntityInternal(Entity entity) {
		entity.componentAdded.add(componentAdded);
		entity.componentRemoved.add(componentRemoved);
		entity.componentOperationHandler = componentOperationHandler;

		familyHandler.updateFamilyMembership(entity);
	}

	protected void removeEntityInternal(Entity entity) {
		familyHandler.updateFamilyMembership(entity);

		entity.componentAdded.remove(componentAdded);
		entity.componentRemoved.remove(componentRemoved);
		entity.componentOperationHandler = null;
	}

	private class ComponentListener implements Listener<Entity> {
		@Override
		public void receive(Signal<Entity> signal, Entity object) {
			familyHandler.updateFamilyMembership(object);
		}
	}

	private class ManagerSystemListener implements SystemListener {
		@Override
		public void systemAdded(ComponentSystem system) {
			system.addedToManagerInternal(EntityManager.this);
		}

		@Override
		public void systemRemoved(ComponentSystem system) {
			system.removedFromManagerInternal(EntityManager.this);
		}
	}

	private class ManagerEntityListener implements EntityListener {
		@Override
		public void entityAdded(Entity entity) {
			addEntityInternal(entity);
		}

		@Override
		public void entityRemoved(Entity entity) {
			removeEntityInternal(entity);
		}
	}

	private class ManagerDelayedInformer implements BooleanInformer {
		@Override
		public boolean value() {
			return updating;
		}
	}

}
