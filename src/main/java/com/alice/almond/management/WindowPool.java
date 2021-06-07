package com.alice.almond.management;

import com.alice.almond.graphics.Camera;
import com.alice.almond.graphics.Window;
import com.alice.almond.utils.collections.Array;

import org.lwjgl.glfw.GLFW;

public class WindowPool {
    
    public final int max;
    public int peak;
    
    private final Array<Window> freedNontWindows;
    private final Array<Window> freedTransWindows;


    public WindowPool () {
		this(4, Integer.MAX_VALUE);
	}

    public WindowPool (int initialCapacity) {
		this(initialCapacity, Integer.MAX_VALUE);
	}

    public WindowPool (int initialCapacity, int max) {
		freedNontWindows = new Array<Window>(false, initialCapacity);
        freedTransWindows = new Array<Window>(false, initialCapacity);
		this.max = max;
	}

    public Window obtain (Scene scene, Camera camera, String title, int width, int height, boolean transparent) {
		if(transparent){
            
            if(freedTransWindows.size > 0){
                Window window = freedTransWindows.pop();
                window.camera = camera;
                window.scene = scene;
                window.setTitle(title);
                window.setWidth(width);
                window.setHeight(height);
                return window;
            }else{
                return new Window(title, scene, camera, width, height, transparent);
            }

        }else{
            
            if(freedNontWindows.size > 0){
                Window window = freedNontWindows.pop();
                window.camera = camera;
                window.scene = scene;
                window.setTitle(title);
                window.setWidth(width);
                window.setHeight(height);
                return window;
            }else{
               return new Window(title, scene, camera, width, height, transparent);
            }  


        }
	}

    public void free (Window window) {
		if (window == null) throw new IllegalArgumentException("Window cannot be null.");

        if(window.transparent){
		    if (freedTransWindows.size < max) {
			    freedTransWindows.add(window);
			    peak = Math.max(peak, freedTransWindows.size);
			    reset(window);
		    } else {
			    discard(window);
		    }
        }else{
            if (freedTransWindows.size < max) {
			    freedTransWindows.add(window);
			    peak = Math.max(peak, freedTransWindows.size);
			    reset(window);
		    } else {
			    discard(window);
		    }
        }   
    }

    private void discard(Window window) {
        GLFW.glfwDestroyWindow(window.windowId);
    }

    private void reset(Window window) {
        GLFW.glfwHideWindow(window.windowId);
    }




}
