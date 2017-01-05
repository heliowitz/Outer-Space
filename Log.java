import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import greenfoot.Actor;
import greenfoot.GreenfootImage;

/**
 * Log is a String display Actor that can be updated. Capable of following
 * another Actor, if needed. Capable of scrolling while maintaining previous
 * messages, if needed. Multiple instances may be created.
 * <p>
 * Automatically resizes self based on the length and format of data passed. To
 * update normal Logs, call update(). To update scrolling Logs, call addLine().
 * 
 * @author Teddy Zhu
 * @version Mar. 5, 2014
 */
public class Log extends Actor {

	// INSTANCE VARIABLES---------------------------------------------------

	// Display image.
	private GreenfootImage dispImg;
	// Blank placeholder used when the Log is hidden.
	private GreenfootImage blank;
	// Target to be followed, if needed.
	private Actor target;
	// Log visible or not.
	private boolean visible;
	// Positioning offsets for following function.
	private int xOffset, yOffset;
	// List of history, previous lines displayed, for scrolling Logs.
	private ArrayList<String> history;
	// Maximum number of lines on-screen at once for scrolling Logs.
	private int maxLines;

	// CONSTANTS------------------------------------------------------------

	// The default font for this class.
	private static final Font MAIN_FONT = Input.loadFont("images/dispFont.ttf",
			32);

	// CONSTRUCTORS----------------------------------------------------------

	/**
	 * Constructs a new non-scrolling Log.
	 */
	public Log() {
		// Initialize placeholder and display as blank temporarily.
		blank = new GreenfootImage(1, 1);
		dispImg = new GreenfootImage(blank);
		// Begins as visible.
		visible = true;
		// Set up image, default offsets.
		setImage(dispImg);
		xOffset = 0;
		yOffset = 0;
		// Default values for unused scrolling Log variables.
		history = new ArrayList<String>(0);
		this.maxLines = 0;
	}

	/**
	 * Constructs a new non-scrolling Log that follows an Actor.
	 * 
	 * @param target
	 *            The target Actor to be followed.
	 */
	public Log(Actor target) {
		// Call a simpler constructor and then set target.
		this();
		this.target = target;
	}

	/**
	 * Constructs a new scrolling Log that follows an Actor and keeps a certain
	 * amount of history on-screen.
	 * 
	 * @param target
	 *            The target Actor to be followed, null if the Log is static.
	 * @param maxLines
	 *            Maximum number of lines allowed on-screen. Negatives ignored.
	 */
	public Log(Actor target, int maxLines) {
		this(target);
		// Prepare history.
		history = new ArrayList<String>(Math.abs(maxLines));
		this.maxLines = Math.abs(maxLines);
	}

	// INTERFACE------------------------------------------------------------

	/**
	 * Updates the text being displayed on this Log. Each parameter is displayed
	 * as its own line, in the order passed. Do not use for scrolling Logs.
	 * 
	 * @param txt
	 *            Any number of String parameters, each of which is shown as
	 *            another separate line.
	 */
	public void update(String... txt) {
		// Set up size.
		dispImg = getStringDispBounds(MAIN_FONT, 32, txt);
		// Prepare drawing tools. Default color and font.
		dispImg.setFont(MAIN_FONT);
		dispImg.setColor(new Color(255, 255, 255, 120));
		// Preparing data for baseline positioning of each line of text.
		FontMetrics txtLine = dispImg.getAwtImage().getGraphics()
				.getFontMetrics(MAIN_FONT);
		int ascent = txtLine.getAscent();
		int descent = txtLine.getDescent();
		// Run through each text line and draw them appropriately.
		for (int i = 0; i < txt.length; i++) {
			// drawString() draws the baseline of the leftmost character at the
			// coordinates given; since the image size perfectly fits ascent and
			// descent, this baseline is drawn at the baseline of the image
			// size, which should perfectly place the text inside.
			// The baseline position of the first line of text is ascent. Every
			// successive line's baseline is further down by units of total line
			// height, which is ascent + descent.
			dispImg.drawString(txt[i], 0, ascent + i * (ascent + descent));
		}
		// Offsets to position a following Log's top left corner over target.
		xOffset = dispImg.getWidth() / 2;
		yOffset = dispImg.getHeight() / 2;
	}

	/**
	 * Adds another line of text to the current display, displacing previous
	 * lines as needed according to maximum number of lines allowed. Do not use
	 * for non-scrolling Logs.
	 * 
	 * @param txt
	 *            The line of text to be added.
	 */
	public void addLine(String txt) {
		// If there are less than the maximum allowed number of lines, no
		// modification is needed for history.
		// If the maximum number has been reached, the first
		// element in history is removed and all others displaced back.
		if (history.size() >= maxLines) {
			history.remove(0);
		}
		// When history is prepared, as needed,
		// Add the new line to history and display fully.
		history.add(txt);
		// Fully displaying by converting ArrayList to acceptable format for
		// varargs and giving to standard update.
		update(history.toArray(new String[history.size()]));
	}

	/**
	 * Makes the current display visible.
	 */
	public void show() {
		visible = true;
	}

	/**
	 * Hides the current display from view.
	 */
	public void hide() {
		visible = false;
	}

	// PRIVATE METHODS------------------------------------------------------

	/**
	 * Retrieves a blank image of what the size would be if the given strings of
	 * given font were to fill it.
	 * 
	 * @param font
	 *            The Font to be used.
	 * @param fontSize
	 *            The size of the font.
	 * @param text
	 *            The text to be displayed, as any number of String parameters.
	 *            Each parameter is its own line.
	 * @return GreenfootImage An empty image, null if no text was given.
	 */
	private GreenfootImage getStringDispBounds(Font font, int fontSize,
			String... text) {
		if (text != null) {
			// Arbitrary image.
			GreenfootImage bounded = new GreenfootImage(" ", fontSize,
					new Color(0.0f, 0.0f, 0.0f), null);
			// Uses font metrics to retrieve would-be dimensions.
			FontMetrics dimension = bounded.getAwtImage().getGraphics()
					.getFontMetrics(font);
			// Firstly, run through each line and find the widest one, while at
			// the same time, add all the heights together. These will be final
			// dimensions. Width must be compared repeatedly, so start with
			// element 0's. Height will be a summation, but since the height for
			// any font is the same for any possible line, calculate
			// immediately.
			int maxLineWidth = dimension.charsWidth(text[0].toCharArray(), 0,
					text[0].length());
			int totalHeight = text.length
					* (dimension.getAscent() + dimension.getDescent());
			// Beginning with the second element, if it exists;
			for (int i = 1; i < text.length; i++) {
				// Calculate width of this line.
				int currentLineWidth = dimension.charsWidth(
						text[i].toCharArray(), 0, text[i].length());
				// If this width is greater than recorded max, update recorded
				// max and continue.
				if (currentLineWidth > maxLineWidth) {
					maxLineWidth = currentLineWidth;
				}
			}
			// Create final image.
			bounded = new GreenfootImage(maxLineWidth, totalHeight);
			return bounded;
		}
		return null;
	}

	/**
	 * Follows the target Actor, if one was given, with the Log's top left
	 * corner over the target center.
	 */
	private void followTarget() {
		// Assuming that a target was provided and it is part of the world,
		if (target != null && target.getWorld() != null) {
			// Follow target location with offset.
			setLocation(target.getX() + xOffset, target.getY() + yOffset);
		}
	}

	// GREENFOOT STRUCTURES---------------------------------------------------

	/**
	 * Runs every frame. Follows target if applicable and manages visibility.
	 */
	public void act() {
		followTarget();
		// If visibility is toggled, show the current display, otherwise show
		// the blank placeholder.
		if (visible) {
			setImage(dispImg);
		} else {
			setImage(blank);
		}
	}

}
