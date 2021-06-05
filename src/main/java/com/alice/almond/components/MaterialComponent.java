package com.alice.almond.components;

import com.alice.almond.dots.Component;
import com.alice.almond.graphics.Material;
import com.alice.almond.graphics.Texture;
import com.alice.almond.graphics.VAO;

public final class MaterialComponent implements Component {
    public final Texture texture;
    public final VAO model;
    public final Material material;

    public MaterialComponent(Texture texture, VAO model, Material material){
        this.texture = texture;
        this.model = model;
        this.material = material;
    }
}