package unibg.se4med;

import org.json.JSONObject;

import p3d4amb.sat.lib.shapes.ShapeSize;
import p3d4amb.sat.lib.shapes.ImageShape.ImageSet;
import se4med.json.BreakoutJson;
import se4med.json.StereoacuityTestJson;

public class DefaultAppSettings {

	public static String stereoTestSettings = new JSONObject().put(StereoacuityTestJson.imageSet, ImageSet.TNO)
			.put(StereoacuityTestJson.shapeSize, ShapeSize.MEDIUM).put(StereoacuityTestJson.stripes, false)
			.put(StereoacuityTestJson.nullImage, false).put(StereoacuityTestJson.screenInches, 20)
			.put(StereoacuityTestJson.monitorDistance, 40).put(StereoacuityTestJson.startingLevel, 6)
			.put(StereoacuityTestJson.changePosition, false).toString();

	// BREAKOUT JSON EXAMPLE
	// {"name":"user11","difficulty":"0","colored":"1100","rightLensCol":"0","eye":"0","colorChange":"50",
	// "maxColor":255,"sound":"0","effects":"0","startingColor":"0.25","endingColor":"0.75"}
	public static String breakoutSettings = new JSONObject().put(BreakoutJson.difficulty, new String("0"))
			.put(BreakoutJson.startingColor, new String("0")).put(BreakoutJson.endingColor, new String("1"))
			.put(BreakoutJson.colored, new String("1000"))// Only ball is colored
			.put(BreakoutJson.rightLensCol, new String("0")).put(BreakoutJson.eye, new String("0"))
			.put(BreakoutJson.colorChange, new String("50")).put(BreakoutJson.maxColor, new String("255"))
			.put(BreakoutJson.effects, new String("0")).put(BreakoutJson.sound, new String("0")).toString();

	public String getDefaultSettings(String id) {
		switch (id) {
		case "StereoTest":
			return stereoTestSettings;
		case "Breakout":
			return breakoutSettings;
		default:
			return "";
		}
	}
}