package com.alice.almond.dots;

import com.alice.almond.utils.collections.Pool;

public class PooledManager extends EntityManager {

    private EntityPool entityPool;
	private ComponentPools componentPools;

    public PooledManager () {
		this(10, 100, 10, 100);
	}


	public PooledManager (int entityPoolInitialSize, int entityPoolMaxSize, int componentPoolInitialSize, int componentPoolMaxSize) {
		super();

		entityPool = new EntityPool(entityPoolInitialSize, entityPoolMaxSize);
		componentPools = new ComponentPools(componentPoolInitialSize, componentPoolMaxSize);
	}

	
	@Override
	public Entity createEntity () {
		return entityPool.obtain();
	}

	
	@Override
	public <T extends Component> T createComponent (Class<T> componentType) {
		return componentPools.obtain(componentType);
	}

	
	public void clearPools () {
		entityPool.clear();
		componentPools.clear();
	}

	@Override
	protected void removeEntityInternal (Entity entity) {
		super.removeEntityInternal(entity);

		if (entity instanceof PooledEntity) {
			entityPool.free((PooledEntity)entity);
		}
	}


    private class PooledEntity extends Entity implements Pool.Poolable {
		@Override
        public Component removeInternal(Class<? extends Component> componentClass) {
			Component removed = super.removeInternal(componentClass);
			if (removed != null) {
				componentPools.free(removed);
			}

			return removed;
		}

		@Override
		public void reset () {
			removeAll();
			flags = 0;
			componentAdded.removeAllListeners();
			componentRemoved.removeAllListeners();
			scheduledForRemoval = false;
			removing = false;
		}
	}

	private class EntityPool extends Pool<PooledEntity> {

		public EntityPool (int initialSize, int maxSize) {
			super(initialSize, maxSize);
		}

		@Override
		protected PooledEntity newObject () {
			return new PooledEntity();
		}
	}
    
}
