import greenfoot.*;
import java.util.ArrayList;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Area;

/**
 * SolSystem is the controller and main World for the Planetary Defense
 * Simulator. Only one instance should be created. This simulation is designed
 * to end after approximately two to three minutes.
 * <p>
 * Background graphics: http://www.java-gaming.org/index.php?topic=26246.0
 * <p>
 * Actor graphics: Created by Jack Ding.
 * <p>
 * This simulation project is a collaborative effort created by Jack Ding, Adam
 * Guo, Gaven Ma, Teddy Zhu. It portrays a single solar system with one sun,
 * around which several planets orbit. On a few of them, life is capable of
 * developing, and will eventually reach a level of civilization allowing for
 * space travel. The ideas revolve around the Kardashev scale created by a
 * Soviet astronomer.
 * 
 * @author Teddy Zhu
 * @version March 4, 2014
 */
public class SolSystem extends World {

	// VARIABLES------------------------------------------------

	// The system's components; heavenly bodies.
	private Planet[] planets = new Planet[5];
	private ArrayList<Asteroid> asteroids;
	private Star sol;
	// Transition for fading.
	private Overlay overlay;
	// Prompt log to show messages about the system's state.
	private Log worldLog;
	// Orbital variation data for each Planet.
	private double[] orbitPos = new double[] { 12, 95, 173, 276, 312 };
	private double[] orbitSpeed = new double[] { -0.62, 0.74, 0.5, 0.23, -0.31 };
	private double[] pathPos = new double[] { 0, 0, 0, 0, 0 };
	private double[] pathSpeed = new double[] { 0.073, 0.092, 0.01, 0.04, 0.05 };
	private double[] orbitRadX = new double[] { 193, 243, 297, 352, 413 };
	private double[] orbitRadY = new double[] { 184, 145, 182, 200, 313 };
	// Asteroid spawn rate, inversely related to probability of spawning.
	private int asteroidSpawnRate;

	// CONSTANTS-----------------------------------------------

	// Graphics and audio.
	private static final GreenfootImage BG_IMG = new GreenfootImage(
			"images/bg.png");
	private static final GreenfootSound BG_THEME = new GreenfootSound(
			"sounds/bgTheme.wav");

	// CONSTRUCTOR-------------------------------------------------

	/**
	 * Constructs a SolSystem.
	 * 
	 * @param asteroidSpawnRate
	 *            The rate at which asteroids spawn, where probability of
	 *            spawning each frame is 1/asteroidSpawnRate. Negatives ignored.
	 */
	public SolSystem(int asteroidSpawnRate) {
		// Set up resolution.
		super(960, 640, 1, false);
		// Setting up layering rules.
		setPaintOrder(Overlay.class, Log.class, HealthBar.class, Ship.class,
				CelestialBody.class, StatusSet.class, Planet.Glow.class);
		// Set background image.
		setBackground(BG_IMG);
		// Create the Planets and other bodies.
		sol = new Star(200000, 1e20);
		planets[0] = new Planet(sol, 1, 1e13, true);
		planets[1] = new Planet(sol, 0, 1e14, false);
		planets[2] = new Planet(sol, 0, 1e13, true);
		planets[3] = new Planet(sol, 2, 1e16, false);
		planets[4] = new Planet(sol, 1, 1e13, true);
		asteroids = new ArrayList<Asteroid>(0);
		overlay = new Overlay(3);
		worldLog = new Log(null, 3);
		// Add assets to world.
		addObject(worldLog, getWidth() / 2, 570);
		addObject(sol, getWidth() / 2, getHeight() / 2);
		for (int i = 0; i < planets.length; i++) {
			addObject(planets[i], 0, 0);
			planets[i].setOrbit(orbitPos[i], orbitSpeed[i], pathPos[i],
					pathSpeed[i], orbitRadX[i], orbitRadY[i]);
		}
		addObject(overlay, getWidth() / 2, getHeight() / 2);
		// Note spawn rate passed.
		this.asteroidSpawnRate = Math.abs(asteroidSpawnRate);
	}

	// GREENFOOT STRUCTURE------------------------------------------------

	/**
	 * Runs every frame. Checks for user commands to show data of any Planet,
	 * and plays music.
	 */
	public void act() {
		// One time call to start music, at beginning.
		
		if (!BG_THEME.isPlaying()) {
			BG_THEME.setVolume(50);
			BG_THEME.playLoop();
		}
		
		// Check for key-press of each Planet's index+1, which corresponds to
		// requesting the status of the first Planet, second, etc.
		for (int i = 0; i < planets.length; i++) {
			if (Greenfoot.isKeyDown(Integer.toString(i + 1))) {
				// Update the Log while showing it.
				planets[i].updateLog();
				planets[i].showLog();
			} else {
				// If nothing is pressed, hide the Log. No updates are needed
				// while hidden.
				planets[i].hideLog();
			}
		}
		// Check if spawning should occur this frame and do so if possible.
		spawnAsteroid();
	}

	// PRIVATE METHODS----------------------------------------------------

	/**
	 * Brings a declared array of objects into the world. The lengths of a, x,
	 * and y must be equal.
	 * 
	 * @param a
	 *            The declared array of Actors; must have a set length.
	 * @param x
	 *            The x coordinate of each Actor's spawn point.
	 * @param y
	 *            The x coordinate of each Actor's spawn point.
	 * @param clss
	 *            The class of the Actor.
	 * @param params
	 *            The classes of the desired constructor's parameters, in order;
	 *            null if no parameters;
	 * @param init
	 *            The parameters to be passed to the desired constructor, in
	 *            order, omit if none.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void spawn(Actor[] a, int[] x, int[] y, Class clss, Class[] params,
			Object... init) {
		// Coordinate number must correspond with actor number.
		if (a.length != x.length || a.length != y.length) {
			System.out.println("Spawn error in World: Array length mismatch.");
			return;
		}
		// Runs through the actor array and...
		for (int i = 0; i < a.length; i++) {
			try {
				// ...each actor is assigned an instance by identifying the
				// specified constructor using the class array,
				// and creating a new instance using that constructor by passing
				// it its requested parameters.
				// Equivalent to a generic call of the new function. The object
				// is then added to the world at its respective coordinates
				// defined in the coordinate arrays.
				a[i] = (Actor) clss.getDeclaredConstructor(params).newInstance(
						init);
				addObject(a[i], x[i], y[i]);
			} catch (Exception e) {
				// The above is potentially capable of failing. Any errors are
				// noted and sent to console for review.
				System.out.println("Spawn error in World.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Spawns an Asteroid every once in a while from within a certain off-screen
	 * region and directs it towards the solar system.
	 */
	@SuppressWarnings("unchecked")
	private void spawnAsteroid() {
		// Using the probability passed into SolSystem,
		if (Greenfoot.getRandomNumber(asteroidSpawnRate) == 0) {
			// Add a new Asteroid to the reference list and;
			asteroids.add(new Asteroid(Greenfoot.getRandomNumber(900) + 350,
					1e10, 50));
			// This most recent addition must be the final element and have
			// index size-1 because the references are trimmed down to clear
			// off all empty spaces every act() iteration. Thus the most
			// recent addition must have expanded the size.
			// Now add it to the world.
			// First creating point slightly off-screen. Create area of
			// region that can spawn. This is a rectangular band of uniform
			// width 100 around the outside of the screen.
			Rectangle2D.Double outerRect = new Rectangle2D.Double(-100, -100,
					getWidth() + 200, getHeight() + 200);
			Rectangle2D.Double innerRect = new Rectangle2D.Double(0, 0,
					getWidth(), getHeight());
			// Create the actual band.
			Area spawnRing = new Area(outerRect);
			spawnRing.subtract(new Area(innerRect));
			// Now generate points until one of them lies inside.
			// Initial values are guaranteed outside the band to force the while
			// loop to run.
			int spawnX = getWidth() / 2, spawnY = getHeight() / 2;
			while (!spawnRing.contains(spawnX, spawnY)) {
				// Until the point lies inside the band subset, generate points
				// within the outer rectangle boundary.
				spawnX = Greenfoot
						.getRandomNumber((int) outerRect.getWidth() + 1) - 100;
				spawnY = Greenfoot
						.getRandomNumber((int) outerRect.getHeight() + 1) - 100;
			}
			// Add it to the point.
			// Initial velocity is a centered angular range of 120 degrees
			// towards the sun. Note that angle1 is the minimum angle, which is
			// the direct line vector to the sun minus 60, half the range.
			double angle1 = Helper.getAngle(spawnX, spawnY, sol.getX(),
					sol.getY()) - 60;
			// The velocity is magnitude 1 to 5, direction minimum angle to
			// minimum plus 120, towards the sun.
			asteroids.get(asteroids.size() - 1).setVelocity(
					new Vector(Greenfoot.getRandomNumber(6) + 1, Greenfoot
							.getRandomNumber(121) + angle1, false));
			// Add the asteroid.
			addObject(asteroids.get(asteroids.size() - 1), spawnX, spawnY);
		}
		// Clearing empty spaces;
		asteroids = (ArrayList<Asteroid>) Helper
				.cleanReferences((ArrayList<? extends Actor>) asteroids);
	}

	// INTERFACE----------------------------------------------------------

	/**
	 * Updates the world's designated main log with a message.
	 * 
	 * @param txt
	 *            The message to be displayed.
	 */
	public void prompt(String txt) {
		worldLog.addLine(txt);
	}

	/**
	 * Spawns several asteroids at the given point with outwards velocity
	 * vectors, in an explosion type effect.
	 * 
	 * @param x
	 *            The x-coordinate of the spawn point.
	 * @param y
	 *            The y-coordinate of the spawn point.
	 */
	@SuppressWarnings("unchecked")
	public void spawnAsteroidsAt(int x, int y) {
		// The number of asteroids to be spawned.
		int spawnCount = Greenfoot.getRandomNumber(4) + 4;
		for (int i = 0; i < spawnCount; i++) {
			// Add a new Asteroid with random damaging power to the reference
			// list and;
			asteroids.add(new Asteroid(Greenfoot.getRandomNumber(900) + 350,
					1e10, 50));
			// This most recent addition must be the final element and have
			// index size-1 because the references are trimmed down to clear
			// off all empty spaces every act() iteration. Thus the most
			// recent addition must have expanded the size.
			// Now add it to the world.
			// The initial velocity is a random direction in a full circle.
			asteroids.get(asteroids.size() - 1).setVelocity(
					new Vector(Greenfoot.getRandomNumber(5) + 1, Greenfoot
							.getRandomNumber(360), false));
			// Add it.
			addObject(asteroids.get(asteroids.size() - 1), x, y);
		}
		// Clearing empty spaces;
		asteroids = (ArrayList<Asteroid>) Helper
				.cleanReferences((ArrayList<? extends Actor>) asteroids);
	}
}
