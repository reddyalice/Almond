package com.alice.almond.dots;

import java.util.Comparator;

import com.alice.almond.utils.collections.Array;
import com.alice.almond.utils.collections.Dictionary;
import com.alice.almond.utils.collections.ImmutableArray;

public class SystemHandler {

    private SystemComparator systemComparator = new SystemComparator();
	private Array<ComponentSystem> systems = new Array<ComponentSystem>(true, 16);
	private ImmutableArray<ComponentSystem> immutableSystems = new ImmutableArray<ComponentSystem>(systems);
	private Dictionary<Class<?>, ComponentSystem> systemsByClass = new Dictionary<Class<?>, ComponentSystem>();
	private SystemListener listener;
	
	public SystemHandler(SystemListener listener) {
		this.listener = listener;
	}
	
	public void addSystem(ComponentSystem system){
		Class<? extends ComponentSystem> systemType = system.getClass();		
		ComponentSystem oldSytem = getSystem(systemType);
		
		if (oldSytem != null) {
			removeSystem(oldSytem);
		}
		
		systems.add(system);
		systemsByClass.put(systemType, system);		
		systems.sort(systemComparator);
		listener.systemAdded(system);
	}
	
	public void removeSystem(ComponentSystem system){
		if(systems.removeValue(system, true)) {
			systemsByClass.remove(system.getClass());
			listener.systemRemoved(system);
		}
	}

	public void removeAllSystems() {
		while(systems.size > 0) {
			removeSystem(systems.first());
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ComponentSystem> T getSystem(Class<T> systemType) {
		return (T) systemsByClass.get(systemType);
	}
	
	public ImmutableArray<ComponentSystem> getSystems() {
		return immutableSystems;
	}

    
    private static class SystemComparator implements Comparator<ComponentSystem>{
		@Override
		public int compare(ComponentSystem a, ComponentSystem b) {
			return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
		}
	}
	
	public interface SystemListener {
		void systemAdded(ComponentSystem system);
		void systemRemoved(ComponentSystem system);
	}
}
