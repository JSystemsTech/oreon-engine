package org.oreon.gl.demo.oreonworlds.water;

import org.oreon.core.math.Quaternion;
import org.oreon.core.util.Constants;
import org.oreon.gl.demo.oreonworlds.shaders.OceanBRDFShader;
import org.oreon.modules.gl.water.Water;

public class Ocean extends Water{

	public Ocean() {
		super(128, 256, OceanBRDFShader.getInstance());
		
		getWorldTransform().setScaling(Constants.ZFAR,1,Constants.ZFAR);
		getWorldTransform().setTranslation(-Constants.ZFAR/2,190,-Constants.ZFAR/2);
		
		setClip_offset(4);
		setClipplane(new Quaternion(0,-1,0,getWorldTransform().getTranslation().getY() + getClip_offset()));

		this.loadSettingsFile("src/main/resources/oreonworlds/water/water_settings.txt");
	}

}
