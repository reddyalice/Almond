package com.alice.almond.dots;

import com.alice.almond.utils.collections.Array;
import com.alice.almond.utils.collections.Bits;
import com.alice.almond.utils.collections.Dictionary;
import com.alice.almond.utils.collections.ImmutableArray;
import com.alice.almond.utils.collections.Pool;
import com.alice.almond.utils.collections.SnapshotArray;

public class FamilyHandler {
    ImmutableArray<Entity> entities;
	private Dictionary<ComponentFamily, Array<Entity>> families = new Dictionary<ComponentFamily, Array<Entity>>();
	private Dictionary<ComponentFamily, ImmutableArray<Entity>> immutableFamilies = new Dictionary<ComponentFamily, ImmutableArray<Entity>>();
	private SnapshotArray<EntityListenerData> entityListeners = new SnapshotArray<EntityListenerData>(true, 16);
	private Dictionary<ComponentFamily, Bits> entityListenerMasks = new Dictionary<ComponentFamily, Bits>();
	private BitsPool bitsPool = new BitsPool();
	private boolean notifying = false;


    public FamilyHandler(ImmutableArray<Entity> entities) {
		this.entities = entities;
	}
	
	public ImmutableArray<Entity> getEntitiesFor(ComponentFamily family) {
		return registerFamily(family);
	}
	
	public boolean notifying() {
		return notifying;
	}

    public void addEntityListener (ComponentFamily family, int priority, EntityListener listener) {
		registerFamily(family);

		int insertionIndex = 0;
		while (insertionIndex < entityListeners.size) {
			if (entityListeners.get(insertionIndex).priority <= priority) {
				insertionIndex++;
			} else {
				break;
			}
		}

        for (Bits mask : entityListenerMasks.values()) {
			for (int k = mask.length(); k > insertionIndex; k--) {
				if (mask.get(k - 1)) {
					mask.set(k);
				} else {
					mask.clear(k);
				}
			}
			mask.clear(insertionIndex);
		}

		entityListenerMasks.get(family).set(insertionIndex);

		EntityListenerData entityListenerData = new EntityListenerData();
		entityListenerData.listener = listener;
		entityListenerData.priority = priority;
		entityListeners.insert(insertionIndex, entityListenerData);
	}
	
	public void removeEntityListener (EntityListener listener) {
		for (int i = 0; i < entityListeners.size; i++) {
			EntityListenerData entityListenerData = entityListeners.get(i);
			if (entityListenerData.listener == listener) {
				for (Bits mask : entityListenerMasks.values()) {
					for (int k = i, n = mask.length(); k < n; k++) {
						if (mask.get(k + 1)) {
							mask.set(k);
						} else {
							mask.clear(k);
						}
					}
				}

				entityListeners.removeIndex(i--);
			}
		}
	}
	
	public void updateFamilyMembership (Entity entity) {

		Bits addListenerBits = bitsPool.obtain();
		Bits removeListenerBits = bitsPool.obtain();

		for (ComponentFamily family : entityListenerMasks.keys()) {
			final int familyIndex = family.getIndex();
			final Bits entityFamilyBits = entity.getFamilyBits();

			boolean belongsToFamily = entityFamilyBits.get(familyIndex);
			boolean matches = family.matches(entity) && !entity.removing;

			if (belongsToFamily != matches) {
				final Bits listenersMask = entityListenerMasks.get(family);
				final Array<Entity> familyEntities = families.get(family);
				if (matches) {
					addListenerBits.or(listenersMask);
					familyEntities.add(entity);
					entityFamilyBits.set(familyIndex);
				} else {
					removeListenerBits.or(listenersMask);
					familyEntities.removeValue(entity, true);
					entityFamilyBits.clear(familyIndex);
				}
			}
		}

		notifying = true;
		Object[] items = entityListeners.begin();

		try {
			for (int i = removeListenerBits.nextSetBit(0); i >= 0; i = removeListenerBits.nextSetBit(i + 1)) {
				((EntityListenerData)items[i]).listener.entityRemoved(entity);
			}
	
			for (int i = addListenerBits.nextSetBit(0); i >= 0; i = addListenerBits.nextSetBit(i + 1)) {
				((EntityListenerData)items[i]).listener.entityAdded(entity);
			}
		}
		finally {
			addListenerBits.clear();
			removeListenerBits.clear();
			bitsPool.free(addListenerBits);
			bitsPool.free(removeListenerBits);
			entityListeners.end();
			notifying = false;	
		}
	}
	
	private ImmutableArray<Entity> registerFamily(ComponentFamily family) {
		ImmutableArray<Entity> entitiesInFamily = immutableFamilies.get(family);

		if (entitiesInFamily == null) {
			Array<Entity> familyEntities = new Array<Entity>(false, 16);
			entitiesInFamily = new ImmutableArray<Entity>(familyEntities);
			families.put(family, familyEntities);
			immutableFamilies.put(family, entitiesInFamily);
			entityListenerMasks.put(family, new Bits());

			for (Entity entity : entities){
				updateFamilyMembership(entity);
			}
		}

		return entitiesInFamily;
	}


    private static class EntityListenerData {
		public EntityListener listener;
		public int priority;
	}
	
	private static class BitsPool extends Pool<Bits> {
		@Override
		protected Bits newObject () {
			return new Bits();
		}
	}
}
