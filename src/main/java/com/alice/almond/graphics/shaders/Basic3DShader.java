package com.alice.almond.graphics.shaders;

import com.alice.almond.graphics.Shader;

import org.joml.Matrix4f;

public class Basic3DShader extends Shader {


    private int location_transformationMatrix;
    private int location_combinedMatrix;



    public Basic3DShader() {
        super("res/shaders/Basic3DShader.glsl", false);
    }

    @Override
    protected void BindAttributes() {
        BindAttribute(0, "position");
		BindAttribute(1, "texCoords");
        BindAttribute(2, "normal");
    }

    @Override
    protected void GetAllUniformLocations() {
        location_transformationMatrix = GetUniformLocation("transformationMatrix");
		location_combinedMatrix = GetUniformLocation("combinedMatrix");
    }


    public void LoadTransformationMatrix(Matrix4f matrix){
        this.LoadMatrix(location_transformationMatrix, matrix);
    }

    public void LoadCombinedMatrix(Matrix4f matrix){
        this.LoadMatrix(location_combinedMatrix, matrix);
    }



    
}

