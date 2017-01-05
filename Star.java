import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList; //import arraylist

/**
 * The sun is at the centre of the solar system. It serves as the resource
 * centre of all civilizations and will expand to eat up all planets It has some
 * attacking power
 * 
 * @author Gaven Ma
 * @version 1.0
 */
public class Star extends CelestialBody {
	private double radiusOfRadiation; // how far the sun would radiate (in
										// lightyears)
	private int counter = 0; // A counter
	private ArrayList<CelestialBody> cList = new ArrayList<CelestialBody>(); // create
																				// a
																				// new
																				// arraylist

	/**
	 * The contructor of the sun
	 * 
	 * <p>
	 * Constructs a centering sun with a certain remaining resource and mass
	 * 
	 * @param resource
	 *            The intended initial resource of the sun
	 * @param mass
	 *            The mass of the sun
	 */
	public Star(double resource, double mass) {
		this.resource = resource; // initializing the initial resource of the
									// sun
		attackPower = 100; // initialized to 100 for attacking power
		radiusOfRadiation = 100; // initialize it to be 100
		this.mass = mass; // The mass of the sun
		setImage(new GreenfootImage("images/Sun.gif"));
	}

	/**
	 * Act - do whatever the Sun wants to do. This method is called whenever the
	 * 'Act' or 'Run' button gets pressed in the environment.
	 */
	public void act() {
		counter++; // counter adds one every act
		if (counter % 100 == 0) // does it every 100 acts
		{
			attackPower++; // attack power get bigger
			radiusOfRadiation++;// radius of radiation gets bigger
		}
		if (counter % 150 == 0) // does it every 150 years
		{
			attack(); //attack other planets
			counter = 0; // reset the counter
		}
		

	}

	private void attack() {
		cList = (ArrayList<CelestialBody>) this.getObjectsInRange(
				(int) radiusOfRadiation, CelestialBody.class); // get all
																// celectial
																// bodies within
																// the range of
																// attack
		for (CelestialBody c : cList) {
			if (c instanceof Planet) {
				((Planet) c).attack((long) attackPower); // attack different
															// planets(fake
															// method that is to
															// be implemented)
			}
		}
	}

	/**
	 * This method gets the current destroying radius of the sun
	 * 
	 * @return double The current radius at which the sun is attacking other
	 *         planets
	 */
	public double getRadius() // get the radius of desturction(radiation)
	{
		return radiusOfRadiation; // return the value
	}

}