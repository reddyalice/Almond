package com.alice.almond.graphics.materials;

import com.alice.almond.dots.Entity;
import com.alice.almond.graphics.AssetManager;
import com.alice.almond.graphics.Camera;
import com.alice.almond.graphics.Material;
import com.alice.almond.graphics.shaders.BasicShader;
import com.alice.almond.utils.math.MathUtils;

public class BasicMaterial extends Material {

    public BasicMaterial() {
        super(AssetManager.Shaders.basicShader);
    }

    @Override
    public void LoadCamera(Camera camera) {
        BasicShader shader = (BasicShader)this.shader;
        shader.LoadCombinedMatrix(camera.combined);
    }

    @Override
    public void LoadEntity(Entity entity) {
        BasicShader shader = (BasicShader)this.shader;
        shader.LoadTransformationMatrix(MathUtils.CreateTransformationMatrix(entity.position, entity.rotation, entity.scale));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof BasicMaterial;
    }
    
}
