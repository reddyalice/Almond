package com.alice.almond.graphics;

import java.io.Serializable;

public final class Model2D implements Serializable {

	public final float[] vertices;
    public final float[] textureCoords;
    public final int[] indices;
 
    public Model2D(float[] vertices, float[] textureCoords, int[] indices) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.indices = indices;
    }
 

}
