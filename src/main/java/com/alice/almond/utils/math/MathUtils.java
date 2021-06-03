package com.alice.almond.utils.math;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Random;
import org.joml.Vector3f;

public class MathUtils {

    public static Random random = new Random();

    public static int nextPowerOfTwo (int value) {
		if (value == 0) return 1;
		value--;
		value |= value >> 1;
		value |= value >> 2;
		value |= value >> 4;
		value |= value >> 8;
		value |= value >> 16;
		return value + 1;
	}

	public static Matrix4f CreateTransformationMatrix(Vector3f translation, Quaternionf rotation, Vector3f scale){
		Matrix4f matrix = new Matrix4f();
		matrix.identity().translate(translation).rotate(rotation).scale(scale.x, scale.y, scale.z);
		return matrix;
	}

    
}
