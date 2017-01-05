import java.awt.Color;
import greenfoot.Greenfoot;
import greenfoot.World;

/**
 * Menu acts as a temporary World, where simulation settings are defined by the
 * user before beginning the main simulation. Only one instance should be
 * created. The World SolSystem is created and takes over after the user
 * finishes with settings.
 * 
 * @author Teddy Zhu
 * @version Mar. 21, 2014
 * 
 */
public class Menu extends World {

	// VARIABLES-----------------------------------------------------------

	// Chance of spawning asteroids to be passed to SolSystem. This is the
	// number from which a random integer will be picked, which, if it equals
	// zero, spawns one. The higher it is, the lower the spawn rate.
	private int chance;
	// Text display.
	private Log display;
	// Visual indicator.
	private StatusSet visual;

	// CONSTRUCTOR----------------------------------------------------------

	/**
	 * Constructs a Menu.
	 */
	public Menu() {
		// World size.
		super(960, 640, 1, true);
		// Default spawning chance.
		chance = 250;
		// Initializing Actors.
		display = new Log();
		// The numbers which the user will be manipulating represent chance as
		// the number of frames out of 1000 for which an asteroid will spawn.
		// Thus it is inversely related to chance by 1/chance*1000=frames. In
		// this case, if chance is 250, it is 4 frames out of 1000.
		visual = new StatusSet(new double[] { 4 }, new double[] { 1000 }, 90,
				new double[] { 25 },
				new Color[] { new Color(100, 216, 255, 255) },
				new Color[] { new Color(0, 0, 0, 0) });
		// Graphical effect on visual.
		visual.toggleGradient(0, true);
		// Adding Actors.
		addObject(display, getWidth() / 2, 100);
		addObject(visual, getWidth() / 2, getHeight() / 2);
		// Background is the black screen allotted to Overlays.
		setBackground(Overlay.IMG);
	}

	// GREENFOOT STRUCTURE---------------------------------------------------

	/**
	 * Runs every frame. Checks for key input.
	 */
	public void act() {
		// Checking for keys. The visual will be used to keep track of frames
		// per thousand (FPT).
		if (Greenfoot.isKeyDown("up")) {
			// Visual will prevent FPT from going over the set maximum.
			visual.update(0, 1);
			// Visual allows FPT to decrease to zero, but conversion to chance
			// would cause division by zero, thus minimum FPT is 1.
		} else if (Greenfoot.isKeyDown("down") && visual.getStatus(0) > 1) {
			visual.update(0, -1);
		}
		// Update text display with the numerical value of FPT.
		display.update("<<Planetary Defense Simulator>>",
				"Use up and down arrows to adjust asteroid spawn rate.",
				"Asteroids will spawn " + visual.getStatus(0)
						+ " times every 1000 frames.", "Hit enter to begin.",
				"In the simulation, hold any number key between 1 and 5 for status.");
		// Exit condition.
		if (Greenfoot.isKeyDown("enter")) {
			// Convert FPT to chance, which is the number used by SolSystem, and
			// pass it over, switching Worlds.
			chance = (int) Math.round(1000.0 / visual.getStatus(0));
			Greenfoot.setWorld(new SolSystem(chance));
		}
	}

}
