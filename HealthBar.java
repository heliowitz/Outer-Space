import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color; //imports color functions 

/**
 * HealthBar helps to represent the health of each ship 
 * tracks the location of the desired ship target
 * Will redraw the healthbar based on the current health
 * 
 * @author Jack Ding
 * @version March 2014
 */
public class HealthBar extends Actor {

    private GreenfootImage bar; //new GreenfootImage bar

    private double max; //max HP 
    private double curr; //current HP

    private int length = 30; //length of bar
    private int height = 5; //height of bar

    private double percentageHP; //percentage of current over max HP

    private int trackerShift = 20; //desired shift of the tracker

    private int redSection; //section of bar to be red
    private int greenSection; //section of bar to be green

    private Color green = new Color(0, 255, 0); //sets Color green 
    private Color red = new Color(255, 0, 0);// sets Color red

    private Actor target; //tracking target of bar

    /**
     * Constructs a new HealthBar based on the max, current health and the tracked actor target
     * 
     * @param topHP      maximum HP of the target
     * 
     * @param currHP    current HP of the target
     * 
     * @param tracked   actor being tracked 
     */
    public HealthBar(int topHP, int currHP, Actor tracked) {
        bar = new GreenfootImage(length, height); //create bar as new image

        bar.setColor(green); //set the bar's drawing color as green
        bar.fill(); //fill the bar with green as max health

        this.max = topHP; //assigns topHP as max
        this.target = tracked; //assigns tracked as target

        this.curr = currHP; //assigns currHP as curr

        this.setImage(bar); //sets the image to bar 
    }

    /**
     * Every act, HealthBar will reset its location based on the location of the target with a slight shift
     * If the target isn't there, remove the HealthBar
     */
    public void act() {
        if (target.getWorld() != null) { //if the target exists 
            setLocation(target.getX(), target.getY() - trackerShift); //sets the location with a vertical shift
        } else { //if target doesn't exist
            getWorld().removeObject(this); //remove the bar
        }
    }

    /**
     * Updates the HP bar based on any desired changes in the HP
     * Will redraw the bar based on changes in the current HP and changes in percentages of curr / max HPs
     * sets new image to bar
     * 
     * @param changeHP      the desired change in HP
     *                      a positive value means an increase in HP, a negative value means a decrease
     */
    public void update(int changeHP) {

        curr = curr + changeHP; //changes current hp based on changeHP

        if (curr >= max) { //if curr is greater or equal to max
            curr = max; //assign curr to max
        }

        percentageHP =  (curr / max); //find percentageHP as value of current HP over max HP

        greenSection = (int) (percentageHP * length); //find amount of bar to be green
        redSection = length - greenSection; //remaider is red

        bar.setColor(green);                         //set drawing color to green
        bar.fillRect(0, 0, greenSection, height); //fill green until greenSection

        bar.setColor(red); //set drawing color to red
        bar.fillRect(greenSection, 0, length, height);  //fill red until end

        this.setImage(bar); //sets the image

    }
    
    /**
     * sends out the current health of the actor
     * 
     * @return double       the health of the actor
     */
    public double getHealth()
    {
        return curr; //returns current health (curr)
     }
}