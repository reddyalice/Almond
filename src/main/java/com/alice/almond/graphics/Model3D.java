package com.alice.almond.graphics;

import java.io.Serializable;

public final class Model3D implements Serializable {

	public final float[] vertices;
    public final float[] textureCoords;
    public final float[] normals;
    public final int[] indices;
    public final float furthestPoint;
 
    public Model3D(float[] vertices, float[] textureCoords, float[] normals, int[] indices,
            float furthestPoint) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.furthestPoint = furthestPoint;
    }
 

}
