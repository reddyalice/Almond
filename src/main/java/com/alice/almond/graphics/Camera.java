package com.alice.almond.graphics;

import com.alice.almond.utils.math.Ray;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Camera {


    public Window window;

    public final Vector3f position = new Vector3f();
    public final Vector3f direction = new Vector3f(0,0,-1);
    public final Vector3f up = new Vector3f(0,1,0);


    public final Matrix4f viewMatrix = new Matrix4f();
    public final Matrix4f projectionMatrix = new Matrix4f();
    public final Matrix4f combined = new Matrix4f();
    public final Matrix4f invProjectionView = new Matrix4f();

    private final Ray ray = new Ray(new Vector3f(), new Vector3f());

    public float near = 1;
    public float far = 100;

    public float viewportWidth = 0;
    public float viewportHeight = 0;

    public Camera(){
    }

    public void rotate (float angle, float axisX, float axisY, float axisZ) {
		direction.rotateAxis(angle, axisX, axisY, axisZ);
		up.rotateAxis(angle, axisX, axisY, axisZ);
	}


    public void rotate (Vector3f axis, float angle) {
		rotate(angle, axis.x, axis.y, axis.z);
	}

    public void rotate (final Quaternionf quat) {
		quat.transform(direction);
		quat.transform(up);
	}

    public void translate (float x, float y, float z) {
		position.add(x, y, z);
	}


	public void translate (Vector3f vec) {
		position.add(vec);
	}

    private final Vector3f tmpVec = new Vector3f();

    public void lookAt (float x, float y, float z) {
		tmpVec.set(x, y, z).sub(position).normalize();
		if (tmpVec.length() != 0) {
			float dot = tmpVec.dot(up); // up and direction must ALWAYS be orthonormal vectors
			if (Math.abs(dot - 1) < 0.000000001f) {
				// Collinear
				up.set(direction).mul(-1);
			} else if (Math.abs(dot + 1) < 0.000000001f) {
				// Collinear opposite
				up.set(direction);
			}
			direction.set(tmpVec);
			normalizeUp();
		}
	}

    public Vector3f unproject (Vector3f screenCoords, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
		float x = screenCoords.x, y = screenCoords.y;
		x = x - viewportX;
		y = window.getHeight() - y;
		y = y - viewportY;
		screenCoords.x = (2 * x) / viewportWidth - 1;
		screenCoords.y = (2 * y) / viewportHeight - 1;
		screenCoords.z = 2 * screenCoords.z - 1;
		screenCoords.mulProject(invProjectionView);
		return screenCoords;
	}

    public Vector3f project (Vector3f worldCoords, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
		worldCoords.mulProject(combined);
		worldCoords.x = viewportWidth * (worldCoords.x + 1) / 2 + viewportX;
		worldCoords.y = viewportHeight * (worldCoords.y + 1) / 2 + viewportY;
		worldCoords.z = (worldCoords.z + 1) / 2;
		return worldCoords;
	}

    public Ray getPickRay (float screenX, float screenY, float viewportX, float viewportY, float viewportWidth,
		float viewportHeight) {
		unproject(ray.origin.set(screenX, screenY, 0), viewportX, viewportY, viewportWidth, viewportHeight);
		unproject(ray.direction.set(screenX, screenY, 1), viewportX, viewportY, viewportWidth, viewportHeight);
		ray.direction.sub(ray.origin).normalize();
		return ray;
	}


    public abstract void update ();

    public void normalizeUp () {
		tmpVec.set(direction).cross(up);
		up.set(tmpVec).cross(direction).normalize();
	}
    
}
