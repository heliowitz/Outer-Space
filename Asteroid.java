import greenfoot.GreenfootImage;

import java.util.ArrayList; //import arraylist

/**
 * This is the class of asteriod that could hit planets or othat celestial
 * bodies
 * 
 * Also have resources to exploit
 * 
 * @author Gaven Ma
 * @version 1.0
 */
public class Asteroid extends CelestialBody {
	private double explodePower; // The destruction power an asteroid releases
									// when explodes. (when the asteroid hits a
									// celestial body)
	private boolean firstTurn = true; // a boolean that tells the program if the
										// asteroid already has a direction to
										// turn to
	private int counter = 0; // set a counter
	private ArrayList<CelestialBody> cList = new ArrayList<CelestialBody>(); // create
																				// a
																				// new
																				// arraylist
	private int radiusOfCollision; // The radius of impact of the asteroid. (If
									// planets go in this range, then the two
									// will collide)

	/**
	 * The constructor of the asteriod. Initializes the explode power.
	 * 
	 * @param explodePower
	 *            The destruction power an asteroid releases when explodes
	 * @param mass
	 *            The mass of the asteroid
	 * @param radius
	 *            The radius of collision(If planets go in this range, then the
	 *            two will collide)
	 */
	public Asteroid(double explodePower, double mass, int radius) {
		this.explodePower = explodePower; // set the explode pwoer
		this.mass = mass; // set the mass of the asteroid
		this.resource = 0;
		this.radiusOfCollision = radius;
		setImage(new GreenfootImage("images/asteroid1.png"));
	}

	/**
	 * Another constructor of the asteroid that takes in the initial resource of
	 * asteroid
	 * 
	 * @param explodePower
	 *            The destruction power an asteroid releases when explodes
	 * @param mass
	 *            The mass of the asteroid
	 * @param resource
	 *            The initialresource of the asteroid
	 * @param radius
	 *            The radius of collision(If planets go in this range, then the
	 *            two will collide)
	 */
	public Asteroid(double explodePower, double resource, double mass,
			int radius) {
		this.explodePower = explodePower; // set the explode pwoer
		this.mass = mass; // set the mass of the asteroid
		this.resource = resource; // set the resource
		this.radiusOfCollision = radius;
		setImage(new GreenfootImage("asteroid1.png"));
	}

	/**
	 * Act - do whatever the Asteroid wants to do. This method is called
	 * whenever the 'Act' or 'Run' button gets pressed in the environment.
	 */
	public void act() {
		// counter++;
		// TODO
		super.act();
		if (firstTurn == true) {
			this.setRotation((int) Math.random() * 359); // set a random
															// direction for the
															// asteroid to turn
															// to
			firstTurn = false; // set the boolean to false
		}
		/**
		 * if (counter % 5 == 0) { this.move(2); // move in the direction every
		 * 5 acts 2 units counter = 0; // reset the counter }
		 */
		checkCollision(); // to check if the asteroid crashed any celestial
		// body and react to them
		checkRemove(); // to check if the asteroid has disappeared in this world
	}

	private void checkRemove() {
		// TODO
		if (getWorld() != null) {
			if (getX() < -200 || getX() > 1160 || getY() < -100 || getY() > 840) {
				getWorld().removeObject(this);
			}
		}
	}

	private void checkCollision() {
		// TODO
		if (getWorld() != null) {
			cList = (ArrayList<CelestialBody>) this.getObjectsInRange(
					radiusOfCollision, CelestialBody.class);
			for (CelestialBody c : cList) {
				if (c instanceof Planet) {
					((Planet) c).attack((long) explodePower); // attack
																// different
																// planets(fake
					// method that is to be
					// implemented)
					try {
						getWorld().removeObject(this);
					} catch (NullPointerException e) {
					}
				}
				if (c instanceof Moon) {
					c.reduceAttackPower(0.7); // reduce the attack power to 70%
					c.exploit(0.5, true);
					// exploit the resource of the moon
					try {
						getWorld().removeObject(this);
					} catch (NullPointerException e) {
					}
				}
				if (c instanceof Star) {
					try {
						getWorld().removeObject(this);
					} catch (NullPointerException e) {
					} // just remove this asteroid
													// if
													// it touches sun
				}
			}
		}
	}
}