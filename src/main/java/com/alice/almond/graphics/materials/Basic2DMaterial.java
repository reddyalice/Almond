package com.alice.almond.graphics.materials;

import com.alice.almond.dots.Entity;
import com.alice.almond.graphics.AssetManager;
import com.alice.almond.graphics.Camera;
import com.alice.almond.graphics.Material;
import com.alice.almond.graphics.shaders.Basic2DShader;
import com.alice.almond.utils.math.MathUtils;

public class Basic2DMaterial extends Material {

    public Basic2DMaterial() {
        super(new Basic2DShader());
    }

    @Override
    public void LoadCamera(Camera camera) {
        Basic2DShader shader = (Basic2DShader)this.shader;
        shader.LoadCombinedMatrix(camera.combined);
    }

    @Override
    public void LoadEntity(Entity entity) {
        Basic2DShader shader = (Basic2DShader)this.shader;
        shader.LoadTransformationMatrix(MathUtils.CreateTransformationMatrix(entity.position, entity.rotation, entity.scale));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Basic2DMaterial;
    }
    
}
