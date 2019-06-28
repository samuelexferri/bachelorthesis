package se4med.json;

public class BreakoutJson {
	// BREAKOUT JSON EXAMPLE RESULTS
//	{"score":100,"lives":0,"difficulty":"0","colored":"1000","eye":"0","level":1,"colors":[20,235,0],"initColor":[0,255,0]}

	public static String score = "score"; // final score
	public static String lives = "livesEnd"; // lives remaning
	public static String level = "level"; // level reached
	public static String colors = "colors"; // final lens color
	public static String initColor = "initColor"; // initial lens color

// BREAKOUT JSON EXAMPLE SETTINGS
	// {"name":"user11","difficulty":"1","colored":"1110","rightLensCol":"0","eye":"0","colorChange":"50",
	// "maxColor":165,"sound":"0","effects":"0","startingColor":"0.25","endingColor":"0.75"}

	// table USERAPP -> settings
	public static String difficulty = "difficulty"; // difficulty of treatment
	public static String startingColor = "startingColor"; // select the starting level
	public static String endingColor = "endingColor"; // select the final level
	public static String colored = "colored";
	// is a sequence of 0 and 1
	/*
	 * 1^ elem: ball is colored? 1:true 0:false 2^ elem: paddle is colored? 1:true
	 * 0:false 3^ elem: bricks is colored? 1:true 0:false 4^ elem: obstacles is
	 * colored? 1:true 0:false
	 */
	/*
	 * public static String dynamicBall = "dynamicBall"; // ball changes or not the
	 * color during treatment public static String dynamicBar = "dynamicBar"; // bar
	 * changes or not the color during treatment public static String
	 * dynamicObstacles = "dynamicObstacles"; // bar changes or not the color during
	 * treatment public static String dynamicBricks = "dynamicBricks"; // bricks
	 * change or not the color during treatment
	 */public static String rightLensCol = "rightLensCol"; // color of right lens
	public static String eye = "eye"; // amblyopic Eye
	public static String colorChange = "colorChange"; // every X points change color
	public static String maxColor = "maxColor"; // select color
	public static String effects = "effects"; // select music on/off
	public static String sound = "sound"; // select sound effects on/off

}