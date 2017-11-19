package org.oreon.demo.gl.oreonworlds2;

import org.oreon.core.gl.scene.GLCamera;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.Window;
import org.oreon.demo.gl.oreonworlds2.assets.plants.Palm01ClusterGroup;
import org.oreon.demo.gl.oreonworlds2.assets.plants.Tree02ClusterGroup;
import org.oreon.demo.gl.oreonworlds2.gui.GUI;
import org.oreon.demo.gl.oreonworlds2.shaders.TerrainGridShader;
import org.oreon.demo.gl.oreonworlds2.shaders.TerrainShader;
import org.oreon.demo.gl.oreonworlds2.water.Ocean;
import org.oreon.modules.gl.atmosphere.SkySphere;
import org.oreon.modules.gl.atmosphere.Sun;
import org.oreon.modules.gl.terrain.Terrain;
import org.oreon.system.gl.desktop.GLDeferredRenderingEngine;
import org.oreon.system.gl.desktop.GLFWInput;
import org.oreon.system.gl.desktop.GLWindow;

public class Main {

	public static void main(String[] args) {

		CoreEngine coreEngine = new CoreEngine();
		CoreSystem coreSystem = CoreSystem.getInstance();
		GLDeferredRenderingEngine renderingengine = new GLDeferredRenderingEngine();
		Window window = new GLWindow();
		
		renderingengine.setGui(new GUI());
		window.setWidth(1280);
		window.setHeight(720);
		window.setTitle("OREON ENGINE oreonworlds 2.0");
		
		coreSystem.setRenderingEngine(renderingengine);
		coreSystem.setWindow(window);
		coreSystem.setInput(new GLFWInput());
		coreSystem.getScenegraph().setCamera(new GLCamera());

		coreEngine.init(coreSystem);
		
		coreSystem.getScenegraph().setTerrain(Terrain.getInstance());
		Terrain.getInstance().init("oreonworlds2/terrain/terrain_settings.txt",
								   "oreonworlds2/terrain/terrain_settings_LowPoly.txt",
								   TerrainShader.getInstance(),
								   TerrainGridShader.getInstance(), 
								   null);
		
		coreSystem.getScenegraph().addObject(new SkySphere());	
		coreSystem.getScenegraph().addTransparentObject(new Sun());
		coreSystem.getScenegraph().setWater(new Ocean());
		
//		coreSystem.getScenegraph().getRoot().addChild(new Palm01ClusterGroup());
		coreSystem.getScenegraph().getRoot().addChild(new Tree02ClusterGroup());
		
		coreEngine.start();
	}

}
