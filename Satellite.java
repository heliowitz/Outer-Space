import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.lang.Math; //import math function
import java.util.List; //import list utilities

/**
 * Satellite is a Ship that orbits the planet it belongs to and will shoot 
 * at enemy ships such as Destroyers which come into range 
 * Based on the evolution of the base planet it belongs to, the satellite can
 * upgrade by increasing its shooting speed
 * 
 * @author Jack Ding
 * @version March 2014
 */
public class Satellite extends Ship {
    private int centerX; //determines x coordinate of center
    private int centerY; //determines y coordiate of center
    private double orbitLength; //radius length from center

    private int spinCounter = 0; // determines speed of orbit 

    private int degree = 0; //determines current degree of orbit

    private double addX; //change in x in orbit
    private double addY; //change in y in orbit
    
    private int increase = 2; //speed of shooting 

    /**
     * constructs a satellite which will orbit around the planet and shoot at enemies
     * 
     * @param owner     the Planet the satellite belongs to
     * 
     * @ param maxHP        the maximum HP the satellite can have 
     */
    public Satellite(Planet owner, int maxHP) {
        super(owner); 
        this.maxHP = maxHP; //assigns maxHP to maxHP

        currHP = maxHP; //assigns currHP to maxHP

        hp = new HealthBar(maxHP, currHP, this); //initiates HealthBar hp
        // Image.
        setImage(new GreenfootImage("images/satellite1.png")); //sets image

    }
    
    /**
     * adds healthbar to world
     * 
     * @param w     the world to add to
     */
    public void addedToWorld(World w) {

        w.addObject(hp, getX(), getY() - 30); //adds hp slightly above satellite

    }

    /**
     * Does whatever is necessary that the Satellite wants to do
     * Will orbit the center of the orbit and will orbit around that center
     * Will also detect enemies and will shoot at them
     * Will upgrade is appropriate 
     */
    public void act() {
       
        powerUp(); //calls powerUp() method 
        
        updateCenter(owner.getX(), owner.getY()); //updates the center based on the owner's location
        spinCounter++; //increases spinCounter
        shootCounter = shootCounter + increase; //increases shootCounter

        
        
        missileCollision(); //detects for missile collision 
        
        if (spinCounter >= 1) { //if spinCounter is greater than 1
            orbit(90); //orbits around center with given length
            spinCounter = 0; //sets spinCounter back to 0
        }

        detectEnemies(150); //detectEnemies() method with range 150 

        if (nearestDestroyer != null) {  //if there is a near Destroyer

            turnTowards(nearestDestroyer.getX(), nearestDestroyer.getY()); //turn towards the Destroyer

            shoot(); //shoot 

        }
        
        else //otherwise 
        {
            turn(Greenfoot.getRandomNumber(3+1)); //turn at a random speed if there is nothing to track
        }
        
        checkHealth(); //checks if health is at 0
    }

    /**
     * Will orbit the satellite around the center
     * 
     * @param length        the radius of the orbit desired
     */
    public void orbit(int length) {
        orbitLength = length; //assigns length to orbitLength

        addX = ((double) centerX + ((orbitLength) * (Math.sin((Math.PI / 180)
                * degree)))); //determine addX based on change in x based on change in degree
        addY = ((double) centerY + ((orbitLength) * (Math.cos((Math.PI / 180)
                * degree)))); //determine addY based on change in y based on change in degree

        this.setLocation((int) addX, (int) addY); //sets the new location 

        degree++; //increase the degree by 1

        if (degree >= 359) { //if the degree is about to finish a whole revolution
            degree = 0; //sets degree back to 0 
        }

    }

    /**
     * updates the center of the orbit
     * 
     * @param newX      new x coordinate of center
     * 
     * @param newY      new y coordiante of center
     */
    public void updateCenter(int newX, int newY) {
        centerX = newX; //updates the x center
        centerY = newY; //updates y center
    }

    /**
     * determines if the satellite should be upgraded
     * will increase the shooting rate 
     */
    public void powerUp()
    {
        if (owner.getCivStage() >= 3) //if the base planet's civ level is 3 or higher
        {
            increase = 4; //increase the shooting speed 
        }
    }
   

}
