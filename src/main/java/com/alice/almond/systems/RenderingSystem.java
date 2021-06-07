package com.alice.almond.systems;

import java.util.ArrayList;

import com.alice.almond.components.MaterialComponent;
import com.alice.almond.dots.ComponentSystem;
import com.alice.almond.dots.Entity;
import com.alice.almond.graphics.Material;
import com.alice.almond.graphics.Shader;
import com.alice.almond.graphics.Texture;
import com.alice.almond.graphics.VAO;
import com.alice.almond.graphics.Window;
import com.alice.almond.management.EntityManager;
import com.alice.almond.dots.ComponentFamily;
import com.alice.almond.utils.Event;
import com.alice.almond.utils.collections.Dictionary;
import com.alice.almond.utils.collections.ImmutableArray;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32C;

public class RenderingSystem extends ComponentSystem {
    
    private ImmutableArray<Entity> entities;
    private EntityManager manager;

    public final Event<Float> preRender = new Event<Float>();
    public final Event<Float> render = new Event<Float>();
    public final Event<Float> postRender = new Event<Float>();


    private final Dictionary<Shader, Dictionary<VAO, Dictionary<Material, Dictionary<Texture, ArrayList<Entity>>>>> renderies = new Dictionary<Shader, Dictionary<VAO, Dictionary<Material, Dictionary<Texture, ArrayList<Entity>>>>>();

    public RenderingSystem(){
        
        addedToManager.Add("loadManager", x -> {
            renderies.clear();
            this.manager = x;
            this.entities = x.getEntitiesFor(ComponentFamily.all(MaterialComponent.class).get());
        });


        preRender.Add("sort", x -> {
            for(Entity en : entities){
                MaterialComponent mc = en.getComponent(MaterialComponent.class);
                Shader shader = mc.material.shader;
                Material material = mc.material;
                VAO vao = mc.model;
                Texture texture = mc.texture;

                Dictionary<VAO, Dictionary<Material, Dictionary<Texture, ArrayList<Entity>>>> batch0 = renderies.get(shader);
                if(batch0 != null){
                    Dictionary<Material, Dictionary<Texture, ArrayList<Entity>>> batch1 = batch0.get(vao);
                    if(batch1 != null){
                        Dictionary<Texture, ArrayList<Entity>> batch2 = batch1.get(material);
                        if(batch2 != null){
                            ArrayList<Entity> batch3 = batch2.get(texture);
                            if(batch3 != null){
                                batch3.add(en);
                                batch2.put(texture, batch3);
                                batch1.put(material, batch2);
                                batch0.put(vao, batch1);
                                renderies.put(shader, batch0);
                            }else{
                                batch3 = new ArrayList<Entity>();
                                batch3.add(en);
                                batch2.put(texture, batch3);
                                batch1.put(material, batch2);
                                batch0.put(vao, batch1);
                                renderies.put(shader, batch0);
                            }
                        }else{
                            batch2 = new Dictionary<Texture, ArrayList<Entity>>();
                            ArrayList<Entity> batch3 = new ArrayList<Entity>();
                            batch3.add(en);
                            batch2.put(texture, batch3);
                            batch1.put(material, batch2);
                            batch0.put(vao, batch1);
                            renderies.put(shader, batch0);
                        }
                    }else{
                        batch1 = new Dictionary<Material, Dictionary<Texture, ArrayList<Entity>>>();
                        Dictionary<Texture, ArrayList<Entity>> batch2 = new Dictionary<Texture, ArrayList<Entity>>();
                        ArrayList<Entity> batch3 = new ArrayList<Entity>();
                        batch3.add(en);
                        batch2.put(texture, batch3);
                        batch1.put(material, batch2);
                        batch0.put(vao, batch1);
                        renderies.put(shader, batch0);
                    }
                }else{
                    batch0 = new Dictionary<VAO, Dictionary<Material, Dictionary<Texture, ArrayList<Entity>>>>();
                    Dictionary<Material, Dictionary<Texture, ArrayList<Entity>>> batch1 = new Dictionary<Material, Dictionary<Texture, ArrayList<Entity>>>();
                    Dictionary<Texture, ArrayList<Entity>> batch2 = new Dictionary<Texture, ArrayList<Entity>>();
                    ArrayList<Entity> batch3 = new ArrayList<Entity>();
                    batch3.add(en);
                    batch2.put(texture, batch3);
                    batch1.put(material, batch2);
                    batch0.put(vao, batch1);
                    renderies.put(shader, batch0);

                }





            }
        });


        update.Add("generalUpdate", x -> {
            preRender.Broadcast(x);
            render.Broadcast(x);
            postRender.Broadcast(x);
        });

        render.Add("standardRender", x -> {

            for(Window window : manager.scene.getWindows()){
                GLFW.glfwMakeContextCurrent(window.windowId);
                window.preRender.Broadcast(x);
                //GL11.glEnable(GL11.GL_DEPTH_TEST);
		        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		        GL11.glClearColor(0, 0, 0, 0);
                for(Shader shader : renderies.keys()){

                    shader.Start();

                    for(VAO vao : renderies.get(shader).keys()){

                        GL30.glBindVertexArray(vao.id);
                        GL20.glEnableVertexAttribArray(0);
                        GL20.glEnableVertexAttribArray(1);
                        GL20.glEnableVertexAttribArray(2);
                        
                        for(Material material : renderies.get(shader).get(vao).keys()){
                            material.LoadCamera(window.camera);
                           
                            for(Texture texture : renderies.get(shader).get(vao).get(material).keys()){
                                GL11.glEnable(GL11.GL_TEXTURE_2D);
                               
                                
                                GL20.glActiveTexture(GL13.GL_TEXTURE0);
		                        texture.Bind();

                                for(Entity en : renderies.get(shader).get(vao).get(material).get(texture)){
                                    material.LoadEntity(en);
                                    GL11.glDrawElements(GL11.GL_TRIANGLES, vao.vertexCount, GL11.GL_UNSIGNED_INT, 0);

                                    
                                }
                               
                                GL11.glDisable(GL11.GL_TEXTURE_2D);
                            }
                        }

                        GL20.glDisableVertexAttribArray(2);
                        GL20.glDisableVertexAttribArray(1);
                        GL20.glDisableVertexAttribArray(0);
                        GL30.glBindVertexArray(0);


                    }

                    shader.Stop();
                }



                window.postRender.Broadcast(x);
                GLFW.glfwSwapBuffers(window.windowId);
            }

        });

        
    
    
    }
}
