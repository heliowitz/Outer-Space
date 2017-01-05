import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A moon usually orbits the planets or floats around freely
 * 
 * @author Gaven Ma
 * @version 1.0
 */
public class Moon extends CelestialBody {
	private CelestialBody owner; // there has to be an owner of the moon.
									// Initialized to be null
	private int civilizationLevel = 0; // the moon's own civilization level
	private int counter = 0; // A counter
	private double inLevel; // the transitional value of civilization level that
							// determines the ultimate level. The accumulation
							// of this value leads to the final level of
							// civilazation
	private double orbitPos = 0, orbitRad = 50, speed;

	/**
	 * The constructor of the moon
	 * 
	 * @param resource
	 *            The initial resource left for the moon
	 * @param mass
	 *            The mass of the moon
	 */
	public Moon(double resource, double mass) {
		this.resource = resource; // initialize the resource
		this.mass = mass; // the mass of the moon
		setImage(new GreenfootImage("images/moon1.gif"));
	}

	/**
	 * Another constructor of the moon, takes in the owner of the moon
	 * 
	 * @param resource
	 *            The initial resource left for the moon
	 * @param owner
	 *            The owner of the moon
	 * @param mass
	 *            The mass of the moon
	 * @param orbitRad
	 *            The orbital radius of the moon.
	 */
	public Moon(double resource, double mass, CelestialBody owner,
			double orbitRad) {
		this.resource = resource; // initialize the resource
		this.owner = owner; // set the owner of the moon
		this.mass = mass; // set the mass of the moon
		this.orbitRad = orbitRad;

		speed = Math.random() * 6;
		if (Greenfoot.getRandomNumber(2) == 0) {
			speed *= -1;
		}

		setImage(new GreenfootImage("images/moon1.gif"));
	}

	/**
	 * Act - do whatever the Moon wants to do. This method is called whenever
	 * the 'Act' or 'Run' button gets pressed in the environment.
	 */
	public void act() {
		counter++; // counter increases by 1

		if (counter % 10 == 0) {
			inLevel = inLevel + 5; // increase the inLevel every 10 acts by 5
		}

		attackPower = inLevel * 0.05; // the attacking power is directly
										// proportional to the inLevel

		if (inLevel >= Math.pow(10, (civilizationLevel + 1))) // if the
																// accumulation
																// of the
																// inLevel can
																// effect the
																// level of
																// civilization
		{
			civilizationLevel++; // increase the civilazation by 1
		}

		if (this.owner != null) {
			// owner.update(attackPower, resource, inLevel); //update the
			// information of the moon to the owner(fake method for now)
			orbit(this, owner, orbitPos, orbitRad, orbitRad);
			orbitPos += speed;
			if (orbitPos >= 360) {
				orbitPos -= 360;
			}
		} else {
			// to randomly move the moon around if there's no owner of the moon
			if (counter % 5 == 0) {
				setLocation(this.getX() + (Math.random() * 5), this.getY()
						+ (Math.random() * 5));
			} else if (counter % 10 == 0) {
				setLocation(this.getX() - (Math.random() * 5), this.getY()
						- (Math.random() * 5));
				counter = 0; // reset the counter
			}

		}
	}

	private void orbit(Actor obj, Actor src, double ellipseDeg, double radX,
			double radY) {
		// Convert degrees into radian measure for trigonometric functions.
		double radian = (ellipseDeg * Math.PI / 180.0);
		// Coordinates of the specified point on the orbital path.
		double coordX = (src.getX() + (Math.cos(radian) * radX));
		double coordY = (src.getY() + (Math.sin(radian) * radY));
		// Set the location.
		obj.setLocation((int) Math.round(coordX), (int) Math.round(coordY));
	}

}