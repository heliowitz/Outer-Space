import greenfoot.Actor;
import greenfoot.GreenfootImage;

/**
 * Overlay is an Actor created specifically for the purpose of covering the
 * entire screen temporarily. After creation, it will fade out at some rate
 * given, and destroy itself afterwards.
 * <p>
 * Serves as a transition at the simulation start.
 * 
 * @author Teddy Zhu
 * @version Mar. 21, 2014
 * 
 */
public class Overlay extends Actor {

	// Fading rate, amount of transparency decrease per frame.
	private int fadeOutRate;

	/**
	 * Solid black display image for all Overlays.
	 */
	public static final GreenfootImage IMG = new GreenfootImage(
			"images/Black.png");

	/**
	 * Constructs an Overlay.
	 * 
	 * @param fadeOut
	 *            Rate at which this Overlay should fade out after creation, as
	 *            transparency decrease per frame. Negatives ignored.
	 */
	public Overlay(int fadeOut) {
		fadeOutRate = Math.abs(fadeOut);
		setImage(IMG);
	}

	/**
	 * Runs every frame. Fades the Overlay out gradually after it has been added
	 * to the World and then destroys it.
	 */
	public void act() {
		// Temporarily store current alpha.
		int alpha = getImage().getTransparency();
		// If it hasn't faded out fully;
		if (alpha != 0) {
			// If decreasing it further would not produce negative alpha,
			if (alpha - fadeOutRate >= 0) {
				// Do so.
				getImage().setTransparency(alpha - fadeOutRate);
				// Otherwise the alpha left is less than the usual decrease so
				// fade out fully.
			} else {
				getImage().setTransparency(0);
			}
			// Upon fading out fully, remove self.
		} else {
			getWorld().removeObject(this);
		}
	}

}
