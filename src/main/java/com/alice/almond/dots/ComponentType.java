package com.alice.almond.dots;

import com.alice.almond.utils.collections.Bits;
import com.alice.almond.utils.collections.Dictionary;

public final class ComponentType {
	private static Dictionary<Class<? extends Component>, ComponentType> assignedComponentTypes = new Dictionary<Class<? extends Component>, ComponentType>();
	private static int typeIndex = 0;

	private final int index;

	private ComponentType () {
		index = typeIndex++;
	}

	public int getIndex () {
		return index;
	}

	public static ComponentType getFor (Class<? extends Component> componentType) {
		ComponentType type = assignedComponentTypes.get(componentType);

		if (type == null) {
			type = new ComponentType();
			assignedComponentTypes.put(componentType, type);
		}

		return type;
	}


	public static int getIndexFor (Class<? extends Component> componentType) {
		return getFor(componentType).getIndex();
	}


	public static Bits getBitsFor (Class<? extends Component>... componentTypes) {
		Bits bits = new Bits();

		int typesLength = componentTypes.length;
		for (int i = 0; i < typesLength; i++) {
			bits.set(ComponentType.getIndexFor(componentTypes[i]));
		}

		return bits;
	}

	@Override
	public int hashCode () {
		return index;
	}

	@Override
	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ComponentType other = (ComponentType)obj;
		return index == other.index;
	}
}
