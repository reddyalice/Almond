package com.alice.almond.graphics.materials;

import com.alice.almond.dots.Entity;
import com.alice.almond.graphics.Camera;
import com.alice.almond.graphics.Material;
import com.alice.almond.graphics.shaders.Basic3DShader;
import com.alice.almond.utils.math.MathUtils;

public class Basic3DMaterial extends Material {

    public Basic3DMaterial() {
        super(new Basic3DShader());
    }

    @Override
    public void LoadCamera(Camera camera) {
        Basic3DShader shader = (Basic3DShader)this.shader;
        shader.LoadCombinedMatrix(camera.combined);
    }

    @Override
    public void LoadEntity(Entity entity) {
        Basic3DShader shader = (Basic3DShader)this.shader;
        shader.LoadTransformationMatrix(MathUtils.CreateTransformationMatrix(entity.position, entity.rotation, entity.scale));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Basic2DMaterial;
    }
    
}
