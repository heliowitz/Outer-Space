import java.util.List;
import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Probes here.
 * 
 * @author (your name)
 * @version (a version number or a date)
 */
public class Probe extends Ship {
	// Changes to Adam's code: now extends Ship, modified constructor to fit
	// super & Planet call.
	// Removed maxHP and now using super's protected variables. Altered
	// HealthBar call to fit.
	// Spelling errors. Declared variables that weren't created in detect
	// method.
	// Changed myWidth variable to method call. Imported List. Changed distance
	// to double.
	// Added infinity comparison. Changed nearestResource to CelestialBody.
	// Changed return type of detect, added null optional.
	// Removed block in for loop that should only be inside the if and not
	// outside as well, put call in act().
	private double distance;
	private int maxCap;
	private int cap;
	private CelestialBody nearestResource;

	/**
	 * Act - do whatever the Probes wants to do. This method is called whenever
	 * the 'Act' or 'Run' button gets pressed in the environment.
	 */
	public void act() {
		detectResources();
		// check if I'm at the edge of the world,and if so, remove myself
		if (atSpaceEdge()) {
			getWorld().removeObject(this);
		}
	}

	/**
	 * method that creates a hp bar for probes
	 */
	public Probe(Planet owner, int maxHP) {
		super(owner);
		this.maxHP = maxHP;
		hp = new HealthBar(currHP, this.maxHP, this);
		// Image.
		setImage(new GreenfootImage(1, 1));
	}

	/**
	 * Method that checks if this object is at the edge of the Space
	 */
	public boolean atSpaceEdge() {
		if (getX() < -(getImage().getWidth() / 2)
				|| getX() > getWorld().getWidth() + (getImage().getWidth() / 2))
			return true;
		else if (getY() < -(getImage().getWidth() / 2)
				|| getY() > getWorld().getHeight()
						+ (getImage().getWidth() / 2))
			return true;
		else
			return false;
	}

	public CelestialBody detectResources() {
		double nearestDistance;
		List<CelestialBody> resources = getObjectsInRange(400,
				CelestialBody.class);
		if (resources.size() != 0) {
			nearestDistance = Double.POSITIVE_INFINITY;

			for (int i = 0; i < resources.size(); i++) {
				distance = getDistance(resources.get(i));

				if (!(resources.get(i) instanceof Planet && ((Planet) resources
						.get(i)).hasCiv())) {
					if (distance < nearestDistance) {
						nearestDistance = distance;

						nearestResource = resources.get(i);
					}

				}
				return nearestResource;
			}
		}
		return null;
	}
}