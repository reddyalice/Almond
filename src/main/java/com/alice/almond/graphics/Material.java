package com.alice.almond.graphics;

import com.alice.almond.dots.Entity;

public abstract class Material {

    public final Shader shader;

    public Material(Shader shader){
        this.shader = shader;
    }

    public abstract void LoadCamera(Camera camera);
    public abstract void LoadEntity(Entity entity);
    public abstract boolean equals(Object other);
}
