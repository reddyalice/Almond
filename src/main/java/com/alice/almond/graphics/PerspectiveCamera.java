package com.alice.almond.graphics;

import org.joml.Vector3f;

public class PerspectiveCamera extends Camera {
	public float fieldOfView = 67;

	public PerspectiveCamera () {
	}


	public PerspectiveCamera (float fieldOfViewY, float viewportWidth, float viewportHeight) {
		this.fieldOfView = fieldOfViewY;
		this.viewportWidth = viewportWidth;
		this.viewportHeight = viewportHeight;
		update();
	}

	final Vector3f tmp = new Vector3f();



	@Override
	public void update () {
		float aspect = viewportWidth / viewportHeight;
		projectionMatrix.setPerspective(Math.abs(near), Math.abs(far), fieldOfView, aspect);
		viewMatrix.lookAt(position, tmp.set(position).add(direction), up);
		combined.set(projectionMatrix);
		combined.mul(viewMatrix);
		invProjectionView.set(combined);
		invProjectionView.invert();

		
	}
}
