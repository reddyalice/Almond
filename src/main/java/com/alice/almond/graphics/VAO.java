package com.alice.almond.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public final class VAO { 

    public final int id;
	public final int vertexCount;
	public VAO(int id, int vertexCount)
	{
		this.id = id;
		this.vertexCount = vertexCount;
	}


	public static VAO loadToVAO(Model3D model){
		int vaoID = createVAO();
		storeDataInAttributeList(0,3,model.vertices);
		storeDataInAttributeList(1,2,model.textureCoords);
		storeDataInAttributeList(2,3,model.normals);
		bindIndicies(model.indices);
		unBindVAO();
		return new VAO(vaoID, model.indices.length);
	}
	
	public static VAO loadToVAO(Model2D model){
		int vaoID = createVAO();
		storeDataInAttributeList(0,2,model.vertices);
		storeDataInAttributeList(1,2,model.textureCoords);
		bindIndicies(model.indices);
		unBindVAO();
		return new VAO(vaoID, model.indices.length);
	}
	
	
	
	private static int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	
	private static void storeDataInAttributeList(int attributeNumber, int attributeSize, float[] data){
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, attributeSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private static void bindIndicies(int[] indicies){
		int vboID = GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indicies);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		
	}
	
	private static IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	
	private static FloatBuffer storeDataInFloatBuffer(float[] data)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private static void unBindVAO(){
		GL30.glBindVertexArray(0);
	}
}
