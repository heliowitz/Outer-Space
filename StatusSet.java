import greenfoot.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * StatusSet is a Greenfoot Actor that serves as a visual status indicator for
 * values, as a proportion of some maximum capacity. The main component is a set
 * of concentric rings that behave as status bars, with changing proportions
 * filled up to represent the status. Any number of bars may be specified.
 * Multiple instances may be created; each is independent. Each bar's color and
 * size may be customized upon creation of an instance. Contains various
 * features likely to be commonly needed that are optional.
 * <p>
 * Sets up a specified number of rings as status bars, each identified by index
 * starting from zero for the innermost bar and increasing by one outwards, and
 * regularly updates the indicator display to correspond with the internal
 * status values being monitored. Commands to update or change values must be
 * done by specifying the index of the bar being adjusted. All arrays passed to
 * constructors must be of equal length; which is equal to the number of bars
 * desired.
 * <p>
 * A basic, non-customized instance will have the following properties; central
 * radius is 30.0, radii of all bars is 10.0, all bars begin fully filled, color
 * scheme of all bars is green filled area over red empty area, regeneration is
 * off for all bars, regeneration rate is 0.0 for all bars, overflow reset and
 * alert are off for all bars, gradient is off for all bars, the central display
 * is blank.
 * 
 * @author Teddy Zhu
 * @version Feb. 19, 2014
 */
public class StatusSet extends Actor {

	// INSTANCE VARIABLES---------------------------------------------------

	// Displayed values of statuses; the value represented on-screen in
	// real-time.
	private double[] currentValue;
	// Maximum values of statuses.
	private double[] maxValue;
	// Radii of bars.
	private double[] barRadii;
	// Colors of bar indicators.
	private Color[] barColors;
	// Colors of bar backgrounds; the portions that are empty.
	private Color[] barBGColors;
	// Regeneration flags of each bar; can be toggled on and off.
	private boolean[] regen;
	// Amounts of passive regeneration change per act().
	private double[] regenRate;
	// Rate of change per act() for bar indicator motion towards the actual
	// target value; a visual effect.
	private double[] changeRate;
	// The percentage of a commanded status change that the visual change rate
	// is. Any value equal to or over 1.0 allows instantaneous visual response.
	private double changeRateFactor;
	// The actual values of statuses, to be approached by the bars' display
	// values.
	private double[] currentTarget;
	// Reset flags of each bar.
	private boolean[] resetWhenFull;
	// Number of bars in the set.
	private int barCount;
	// Radius of central core.
	private double centralRadius;
	// Display image of central region.
	private GreenfootImage avatar;
	// Alert flags of each bar.
	private boolean[] lowStatusAlert;
	// Whether the flickering is visible for the current frame.
	private boolean[] alertFlickering;
	// Color of flickering.
	private Color[] alertColors;
	// Maximum status at which alert will activate, as a percentage of the
	// maximum capacity.
	private double[] alertPercent;
	// Gradient effect flag.
	private boolean[] gradientEffect;

	// CONSTANTS---------------------------------------------------------

	/**
	 * Number of decimal places that retrieved status values will be accurate
	 * to.
	 */
	public final int STATUS_PRECISION = 9;

	// CONSTRUCTOR OVERLOADS-----------------------------------------------

	/**
	 * Constructs a StatusSet, a set of concentric status bar rings, with the
	 * specified custom properties. Ensure the region of central radius around
	 * the indicated point lies entirely within the avatar image.
	 * 
	 * @param currentVal
	 *            The initial, or current value, for each bar, of the status
	 *            being represented; listed outwards.
	 * @param maxVal
	 *            The maximum value, for each bar, that the status which is
	 *            being represented can be at; listed outwards.
	 * @param centralSize
	 *            The radius of the central circle.
	 * @param barSize
	 *            The radius of each bar; listed outwards.
	 * @param colors
	 *            The Color of each bar's filled portion, listed outwards.
	 * @param BGColors
	 *            The Color of each bar's empty portion, listed outwards.
	 * @param ava
	 *            The desired display image for the central core of the status
	 *            set.
	 * @param avaX
	 *            The x coordinate of the point around which the image will be
	 *            cropped, in a circle.
	 * @param avaY
	 *            The y coordinate of the point around which the image will be
	 *            cropped, in a circle.
	 */
	public StatusSet(double[] currentVal, double[] maxVal,
			double centralSize, double[] barSize, Color[] colors,
			Color[] BGColors, GreenfootImage ava, double avaX, double avaY) {
		// Call a more generic constructor.
		this(currentVal, maxVal, centralSize, barSize, colors, BGColors);
		// Set up the central display.
		update(ava, avaX, avaY);
	}

	/**
	 * Constructs a StatusSet, a set of concentric status bar rings, with the
	 * specified custom properties.
	 * 
	 * @param currentVal
	 *            The initial, or current value, for each bar, of the status
	 *            being represented; listed outwards.
	 * @param maxVal
	 *            The maximum value, for each bar, that the status which is
	 *            being represented can be at; listed outwards.
	 * @param centralSize
	 *            The radius of the central circle.
	 * @param barSize
	 *            The radius of each bar; listed outwards.
	 * @param colors
	 *            The Color of each bar's filled portion, listed outwards.
	 * @param BGColors
	 *            The Color of each bar's empty portion, listed outwards.
	 */
	public StatusSet(double[] currentVal, double[] maxVal,
			double centralSize, double[] barSize, Color[] colors,
			Color[] BGColors) {
		// Call a more generic constructor.
		this(currentVal, maxVal, centralSize, barSize);
		// Set up the custom bar colors.
		for (int i = 0; i < barCount; i++) {
			barColors[i] = colors[i];
			barBGColors[i] = BGColors[i];
		}
	}

	/**
	 * Constructs a StatusSet, a set of concentric status bar rings, with the
	 * specified custom properties.
	 * 
	 * @param currentVal
	 *            The initial, or current value, for each bar, of the status
	 *            being represented; listed outwards.
	 * @param maxVal
	 *            The maximum value, for each bar, that the status which is
	 *            being represented can be at; listed outwards.
	 * @param centralSize
	 *            The radius of the central circle.
	 * @param barSize
	 *            The radius of each bar; listed outwards.
	 */
	public StatusSet(double[] currentVal, double[] maxVal,
			double centralSize, double[] barSize) {
		// Call a more generic constructor.
		this(currentVal, maxVal);
		// Set up the custom central radius.
		centralRadius = Math.abs(centralSize);
		// Set up the custom radii for each bar.
		for (int i = 0; i < barCount; i++) {
			barRadii[i] = Math.abs(barSize[i]);
		}
	}

	/**
	 * Constructs a StatusSet, a set of concentric status bar rings, with the
	 * specified custom properties.
	 * 
	 * @param currentVal
	 *            The initial, or current value, for each bar, of the status
	 *            being represented; listed outwards.
	 * @param maxVal
	 *            The maximum value, for each bar, that the status which is
	 *            being represented can be at; listed outwards.
	 * @param centralSize
	 *            The radius of the central circle.
	 * @param barSize
	 *            The uniform radius of all the bars.
	 */
	public StatusSet(double[] currentVal, double[] maxVal,
			double centralSize, double barSize) {
		// Call a more generic constructor.
		this(currentVal, maxVal);
		// Set up the custom central radius.
		centralRadius = Math.abs(centralSize);
		// Set up the custom radius for all bars.
		for (int i = 0; i < barCount; i++) {
			barRadii[i] = Math.abs(barSize);
		}
	}

	/**
	 * Constructs a StatusSet, a set of concentric status bar rings.
	 * 
	 * @param currentVal
	 *            The initial, or current value, for each bar, of the status
	 *            being represented; listed outwards.
	 * @param maxVal
	 *            The maximum value, for each bar, that the status which is
	 *            being represented can be at; listed outwards.
	 */
	public StatusSet(double[] currentVal, double[] maxVal) {
		// Call a more generic constructor.
		this(maxVal);
		// Set up the specified initial status values for each bar.
		for (int i = 0; i < barCount; i++) {
			currentValue[i] = Math.abs(currentVal[i]);
			// Target value should be equal to initial since there are no
			// changes yet.
			currentTarget[i] = currentValue[i];
		}
	}

	/**
	 * Constructs a StatusSet, a set of concentric status bar rings.
	 * 
	 * @param maxVal
	 *            The maximum value, for each bar, that the status which is
	 *            being represented can be at; listed outwards.
	 */
	public StatusSet(double[] maxVal) {
		// Establish the number of bars in this set.
		barCount = maxVal.length;
		// Default central radius.
		centralRadius = 30.0;
		// Initializing all arrays to the appropriate length.
		currentValue = new double[barCount];
		maxValue = new double[barCount];
		barRadii = new double[barCount];
		barColors = new Color[barCount];
		barBGColors = new Color[barCount];
		regen = new boolean[barCount];
		regenRate = new double[barCount];
		changeRate = new double[barCount];
		resetWhenFull = new boolean[barCount];
		currentTarget = new double[barCount];
		lowStatusAlert = new boolean[barCount];
		alertFlickering = new boolean[barCount];
		alertColors = new Color[barCount];
		alertPercent = new double[barCount];
		gradientEffect = new boolean[barCount];
		// Change rate factor, universal to all bars, defaults to 95% any
		// commanded status change.
		changeRateFactor = 0.95;
		// Initializing central display.
		avatar = new GreenfootImage(1, 1);
		// Assigning relevant data to each array, pertaining to each bar.
		for (int i = 0; i < barCount; i++) {
			// This constructor assumes that the bars begin as full.
			currentValue[i] = Math.abs(maxVal[i]);
			// Noting the maximum capacity.
			maxValue[i] = Math.abs(maxVal[i]);
			// Default radius for all bars.
			barRadii[i] = 10.0;
			// Default color scheme for all bars.
			barColors[i] = Color.GREEN;
			barBGColors[i] = Color.RED;
			// Reset feature begins turned off.
			resetWhenFull[i] = false;
			// Regeneration feature begins turned off.
			regen[i] = false;
			// No regeneration rate is specified, the user must decide.
			regenRate[i] = 0.0;
			// Visual effect of indicator moving towards a new target value. As
			// it should be quite fast visually, it is altered depending on the
			// change in status commanded from the controller.
			changeRate[i] = 0.0;
			// Alert feature begins turned off.
			lowStatusAlert[i] = false;
			alertFlickering[i] = false;
			// Default flicker is white.
			alertColors[i] = Color.WHITE;
			// Alert will not activate regardless of how low status is, by
			// default.
			alertPercent[i] = 0.0;
			// Gradient not active by default.
			gradientEffect[i] = false;
			// No target value to begin with, as nothing has changed in status.
			currentTarget[i] = currentValue[i];
		}
	}

	// PRIVATE METHODS--------------------------------------------------

	/**
	 * Creates and returns a drawn circle path centered at the given
	 * coordinates, of the given radius.
	 * 
	 * @param cenX
	 *            The x coordinate of the center.
	 * @param cenY
	 *            The y coordinate of the center.
	 * @param radius
	 *            The radius of the circle.
	 * @return Arc2D.Double A closed circular path.
	 */
	private Arc2D.Double getCircle(double cenX, double cenY, double radius) {
		// Create an arc centered on the passed coordinates, beginning and
		// ending at the same position and forming a closed loop full circle.
		return getPie(cenX, cenY, radius, 0.0, 360.0);
	}

	/**
	 * Creates and returns a drawn pie shape path centered at the given
	 * coordinates, of the given radius, with the solid, non-indented portion
	 * beginning at the given initial angle and ending at the specified angle
	 * away.
	 * 
	 * @param cenX
	 *            The x coordinate of the center.
	 * @param cenY
	 *            The y coordinate of the center.
	 * @param radius
	 *            The radius of the pie.
	 * @param init
	 *            Initial angle of the outer arc, in degrees.
	 * @param angle
	 *            The angular extent of the outer arc, in degrees.
	 * @return Arc2D.Double A closed pie shape path.
	 */
	private Arc2D.Double getPie(double cenX, double cenY, double radius,
			double init, double angle) {
		// Arc2D.Double's constructor requires the upper-left corner of the
		// framing rectangle; which is equal to the central coordinate minus the
		// radius. Passing this calculation allows the path to be created in
		// terms of central position, by this method.
		// Constructor also requires the width and height of the framing
		// rectangle, which is arc diameter, or double the radius.
		// The arc stretches from angle 0 degrees, over an angle of the passed
		// number of degrees.
		// Arc2D.PIE specifies an arc closed by drawing straight line segments
		// from the start of the arc segment to the center of the full ellipse
		// and from that point to the end of the arc segment; a pie.
		return new Arc2D.Double(cenX - radius, cenY - radius, 2.0 * radius,
				2.0 * radius, 0.0, angle, Arc2D.PIE);
	}

	/**
	 * Draws the graphics for the set of status rings for the current status
	 * values, and displays them on-screen. Intended for use in an iterative
	 * method such as Actor's act() to resemble animation.
	 */
	private void update() {
		// Creating display image, a bounded rectangle in which the set of bars
		// will fit.
		// The height and width should both be total diameter, which is double
		// the bar and central radii sum.
		double displaySize = 2.0 * (sumArray(barRadii) + centralRadius);
		// Rounding dimensions up to ensure the actual set will fit.
		GreenfootImage widget = new GreenfootImage(
				(int) Math.ceil(displaySize), (int) Math.ceil(displaySize));
		// Setting up canvas for the foundation of the display image, to be
		// drawn on.
		Graphics2D img = widget.getAwtImage().createGraphics();
		// This variable stores the radius at which to begin drawing each bar.
		// Its starting value, for the first bar, is the
		// size of the central circle, which is reserved for avatars, etc. The
		// for loop will use it to keep track of position,
		// to draw outwards.
		double drawBarFromRadius = centralRadius;
		// Central coordinates of entire set.
		double cenXAndY = displaySize / 2.0;
		// Draw central avatar onto display at center. The parameters still
		// use the upper left corner, so values are corrected as such.
		img.drawImage(avatar.getAwtImage(), (int) (cenXAndY - centralRadius),
				(int) (cenXAndY - centralRadius), null);
		// This class numerically identifies bars from 0 upwards, from inside to
		// out.
		// Now building bars from inside to out and drawing them onto display
		// image foundation.
		for (int i = 0; i < barCount; i++) {
			// These variables will be frequently used in a moment; inner and
			// outer radii of the current bar.
			double innerRadius = drawBarFromRadius;
			double outerRadius = drawBarFromRadius + barRadii[i];
			// Creating circles for the bar boundaries, to form the rings. These
			// are the empty bar backgrounds, not the actual bars.
			// Centered on display center.
			Shape innerBound = getCircle(cenXAndY, cenXAndY, innerRadius);
			Shape outerBound = getCircle(cenXAndY, cenXAndY, outerRadius);
			// Using area subtraction to create an empty ring shape with a
			// transparent center.
			// This ring is the background.
			// After establishing the areas,
			Area innerA = new Area(innerBound);
			Area ring = new Area(outerBound);
			// Subtract the inner, smaller area from the larger to create the
			// ring.
			ring.subtract(innerA);
			// Now creating the actual bar, the green partial ring indicator
			// that will change in arc length to show statistics.
			// First create a new copy of the ring area.
			Area bar = new Area(ring);
			// Temporary variable used to calculate the proportion of the
			// circumference, in degrees, representing the proportion of the
			// current status relative to maximum possible status. Percentage
			// times the total degree measure of the circle, 360 degrees.
			double arcAngle = (currentValue[i] / maxValue[i]) * 360.0;
			// Now retrieve the area of the pie shape for this arc, for the
			// outer boundary circle.
			Area pieShapeIndicator = new Area(getPie(cenXAndY, cenXAndY,
					outerRadius, 0.0, arcAngle));
			// Then turn the bar area, currently still a ring of outer subtract
			// inner, into an arc ring by intersecting it with the pie shape.
			// The pie shape's center is removed, as only the intersecting
			// partial outer ring overlap of both areas is kept.
			// Bar is now an arc ring, fitting into the background ring
			// appropriately according to status.
			bar.intersect(pieShapeIndicator);
			// Draw the bar background onto the display.
			img.setColor(barBGColors[i]);
			img.fill(ring);
			// If the visual should be simple.
			if (!gradientEffect[i]) {
				// Draw the simple indicator onto the display.
				img.setColor(barColors[i]);
				img.fill(bar);
			} else {
				// Draw a gradient bar. From inner bound to outer bound of arc,
				// focused at center;
				RadialGradientPaint grad = new RadialGradientPaint(
				// Coordinates of center.
						(float) cenXAndY, (float) cenXAndY,
						// Bounding radius, outer.
						(float) outerRadius,
						// Key-frame radius positions as a proportion of
						// bounding radius. First color is at inner radius,
						// second at outer.
						new float[] { (float) (innerRadius / outerRadius), 1.0f },
						// Colors to be interpolated between for gradient.
						// Uses the set color and a darker version of it.
						new Color[] { barColors[i].darker(), barColors[i] });
				// Draw arc ring.
				img.setPaint(grad);
				img.fill(bar);
			}
			// Clause for alert feature; if alert is on and should show for
			// current status then;
			if (lowStatusAlert[i]
					&& currentTarget[i] < alertPercent[i] * maxValue[i]) {
				// Draw the flicker if it should be there this frame.
				// Otherwise do nothing.
				if (alertFlickering[i]) {
					img.setColor(alertColors[i]);
					img.fill(bar);
				}
				// Switch the flag for next frame.
				alertFlickering[i] = !alertFlickering[i];
			}
			// This bar is now updated. Moving onto the next one. The radius at
			// which to begin drawing the next is noted down here.
			drawBarFromRadius += barRadii[i];
		}
		// Display.
		setImage(widget);
	}

	/**
	 * Adds all the elements in an array of doubles and returns the sum.
	 * 
	 * @param array
	 *            An array of doubles.
	 * @return double The sum of the array elements.
	 */
	private double sumArray(double[] array) {
		double sum = 0.0;
		// Run through the array and add the numbers to the sum variable.
		for (int i = 0; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

	/**
	 * Corrects a double to constrain it within an inclusive range.
	 * 
	 * @param value
	 *            The double to be checked and corrected.
	 * @param min
	 *            The inclusive minimum of the range which the number must be
	 *            in.
	 * @param max
	 *            The inclusive maximum of the range which the number must be
	 *            in.
	 * @return double Either the original double if it is in-range, or the
	 *         maximum or minimum if it is out of range depending on which side
	 *         of the range it is on; closest boundary is returned.
	 */
	private double correctToRange(double value, double min, double max) {
		// If the value is below the range, set it equal to the minimum.
		if (value < min) {
			return min;
			// If it is above the range, set it equal to the maximum.
		} else if (value > max) {
			return max;
		}
		// Otherwise, it is in-range and no adjustments are needed.
		return value;
	}

	/**
	 * Rounds a double to the given number of decimal places.
	 * 
	 * @param value
	 *            The double to be rounded.
	 * @param decimalPlaces
	 *            The number of decimal places to be rounded to.
	 * @return double The rounded value.
	 */
	private double roundPrecision(double value, int decimalPlaces) {
		// Value is multiplied by an appropriate power of ten to shift the
		// decimal right the number of places that need to be preserved, then
		// rounded to the nearest whole number thus cutting off decimal places
		// past the places preserved, and then divided again by the power of ten
		// to move the decimal back.
		return ((double) Math.round(value * Math.pow(10, decimalPlaces)))
				/ (double) Math.pow(10, decimalPlaces);
	}

	// INTERFACE------------------------------------------------------

	// MUTATORS------------------------------------------------------

	/**
	 * Crops an image into a circle centered at the given coordinates, with the
	 * radius of the status set's central core, and saves it for drawing onto
	 * the central core. Ensure the region of central radius around the
	 * indicated point lies entirely within the avatar image.
	 * 
	 * @param ava
	 *            The desired display image for the central core of the status
	 *            set.
	 * @param cenX
	 *            The x coordinate of the point around which the image will be
	 *            cropped, in a circle.
	 * @param cenY
	 *            The y coordinate of the point around which the image will be
	 *            cropped, in a circle.
	 */
	public void update(GreenfootImage ava, double cenX, double cenY) {
		// Setting up final image for output; size of bounding rectangle of
		// central core.
		avatar = new GreenfootImage((int) (2.0 * centralRadius),
				(int) (2.0 * centralRadius));
		// Setting up canvases for the foundation of the original image, to be
		// modified. Create an empty buffered image of the size of input, of
		// standard RGBA format.
		BufferedImage bImg = new BufferedImage(ava.getWidth(), ava.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		// Retrieve the buffered image's graphics base.
		Graphics2D img = bImg.createGraphics();
		// Cropping. Using the alpha composite for drawing the source onto the
		// destination,
		img.setComposite(AlphaComposite.Src);
		// and rendering with anti-aliasing active for quality of edges, to
		// reduce roughness.
		img.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// Next, using the color white,
		img.setColor(Color.WHITE);
		// Draw a circle at the specified point with proper radius.
		img.fill(getCircle(cenX, cenY, centralRadius));
		// Composite the input image on top, by using the circle as an alpha
		// clipping mask. This will clip the image, constraining it within
		// the white. The source-in rule places input on top and only displays
		// the overlap between image and circle. Transparency in the image can
		// be retained this way, in addition to cropping it, and no white will
		// show after.
		img.setComposite(AlphaComposite.SrcIn);
		// Draw the image's buffered image onto the canvas, with the clip.
		img.drawImage(ava.getAwtImage(), 0, 0, null);
		// Using the final output destination, retrieve the graphics of its
		// buffered image and draw onto it: a sub-image of the bounding
		// rectangle (Upper left corner of the central point and diameter are
		// used as position and width/height) for the clipped region of canvas.
		// The sub-image should fit near perfectly. The avatar is now updated.
		avatar.getAwtImage()
				.createGraphics()
				.drawImage(
						bImg.getSubimage((int) (cenX - centralRadius),
								(int) (cenY - centralRadius),
								(int) (2.0 * centralRadius),
								(int) (2.0 * centralRadius)), 0, 0, null);
	}

	/**
	 * Adjusts the value of the status monitored by the specified bar, by the
	 * given change. Manages the reset feature, by resetting and allocating the
	 * excess to status if reset is allowed and a specified increase exceeds the
	 * capacity.
	 * 
	 * @param barID
	 *            The identifying index of the status bar to be adjusted.
	 * @param change
	 *            The amount by which to change the status value.
	 * @return boolean True if the specified bar exists and the operation
	 *         completed, otherwise false.
	 */
	public boolean update(int barID, double change) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Alter the visual effect change rate of the indicator to be
			// moderately fast in relation to the actual change.
			// Sets the change per act() iteration, the rate of change for the
			// display bars' motion towards representing an updated value; that
			// is, the speed of the visual effect that causes bars to rapidly
			// approach an altered value rather than instantly responding and
			// flickering to the new value. The rate should not be too low to
			// avoid delay lag. Register the magnitude of the new rate, to be
			// used to increase or decrease bars during updates.
			changeRate[barID] = Math.abs(change * changeRateFactor);
			// Set the new target value, which is the current target with the
			// indicated change.
			currentTarget[barID] += change;
			// If the bar is not slated to use the excess from resetting, or it
			// is but the change is a decrease, keep the new target bounded.
			if (!resetWhenFull[barID] || change < 0.0) {
				// If the target is outside the valid boundaries, set it equal
				// to the boundaries themselves.
				currentTarget[barID] = correctToRange(currentTarget[barID],
						0.0, maxValue[barID]);
			} else {
				// If resetting is on, and the change is an increase and thus
				// may overflow, check if target is over the maximum and
				// reset to the excess until the target is within bounds.
				while (currentTarget[barID] >= maxValue[barID]) {
					currentTarget[barID] -= maxValue[barID];
					currentValue[barID] = currentTarget[barID];
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Sets the value of the status monitored by the specified bar to the given
	 * value. Negative values are ignored.
	 * 
	 * @param barID
	 *            The identifying index of the status bar to be adjusted.
	 * @param target
	 *            The value to set the status value to.
	 * @return boolean True if the specified bar exists and the operation
	 *         completed, otherwise false.
	 */
	public boolean updateTo(int barID, double target) {
		// Adjust the status; changing it by the difference between the given
		// and current values.
		return update(barID, Math.abs(target) - currentTarget[barID]);
	}

	/**
	 * Sets the value of the status monitored by the specified bar to the given
	 * value, and then sets the new maximum given. Negative values are ignored.
	 * Status values are bounded to the initial maximum.
	 * 
	 * @param barID
	 *            The identifying index of the status bar to be adjusted.
	 * @param target
	 *            The value to set the status value to.
	 * @param max
	 *            The new maximum.
	 * @return boolean True if the specified bar exists and the operation
	 *         completed, otherwise false.
	 */
	public boolean updateTo(int barID, double target, double max) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Updating status before maximum because the reset feature needs to
			// handle overflows first based on the initial maximum, if reset is
			// active.
			update(barID, Math.abs(target) - currentTarget[barID]);
			maxValue[barID] = Math.abs(max);
			return true;
		}
		return false;
	}

	/**
	 * Toggles the regeneration feature on if it is off, and vice versa, for the
	 * specified bar.
	 * 
	 * @param barID
	 *            The identifying index of the status bar to be affected.
	 * @return boolean True if the specified bar exists and the operation
	 *         completed, otherwise false.
	 */
	public boolean toggleRegen(int barID) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Toggle the flag to its opposite.
			regen[barID] = !regen[barID];
			return true;
		}
		return false;
	}

	/**
	 * Toggles the regeneration feature on or off, as stated, for the specified
	 * bar.
	 * 
	 * @param barID
	 *            The identifying index of the status bar to be affected.
	 * @param value
	 *            True if regeneration is to be turned on, false if it is to be
	 *            turned off.
	 * @return boolean True if the specified bar exists and the operation
	 *         completed, otherwise false.
	 */
	public boolean toggleRegen(int barID, boolean value) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Set the flag to the given boolean value.
			regen[barID] = value;
			return true;
		}
		return false;
	}

	/**
	 * Sets the rate of regeneration, per act() iteration, for a bar. The rate
	 * can be negative if the effect is needed.
	 * 
	 * @param barID
	 *            The identifying index of the status bar to be affected.
	 * @param rate
	 *            The change in status value per act() iteration, as part of
	 *            regeneration.
	 * @return boolean True if the specified bar exists and the operation
	 *         completed, otherwise false.
	 */
	public boolean setRegenRate(int barID, double rate) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Register the new regeneration rate.
			regenRate[barID] = rate;
			return true;
		}
		return false;
	}

	/**
	 * Toggles the reset feature on or off, as stated, for the specified bar.
	 * When active, the bar will automatically reset to the value of the excess
	 * when the status overflows the maximum. If the knowledge of when this
	 * happens is needed, values sent in should be checked manually, as this
	 * feature only manages the visual and mathematical effects for the
	 * instance.
	 * 
	 * @param barID
	 *            The identifying index of the status bar to be affected.
	 * @param value
	 *            True if reset is to be turned on, false if it is to be turned
	 *            off.
	 * @return boolean True if the specified bar exists and the operation
	 *         completed, otherwise false.
	 */
	public boolean toggleResetWhenFull(int barID, boolean value) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Set the flag to the given boolean value.
			resetWhenFull[barID] = value;
			return true;
		}
		return false;
	}

	/**
	 * Toggles the gradient effect on bars on or off, for the specified bar.
	 * When on, bars will not be a uniform color, but have a radial gradient
	 * over them to improve visual interest. The gradient is based on the bar
	 * indicator's current color.
	 * 
	 * @param barID
	 *            The identifying index of the status bar to be affected.
	 * @param value
	 *            True if gradient is to be turned on, false if it is to be
	 *            turned off.
	 * @return boolean True if the specified bar exists and the operation
	 *         completed, otherwise false.
	 */
	public boolean toggleGradient(int barID, boolean value) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Set the flag to the given boolean value.
			gradientEffect[barID] = value;
			return true;
		}
		return false;
	}

	/**
	 * Toggles the alert feature on or off, as stated, for the specified bar.
	 * When active, if the status value falls below a certain percentage, the
	 * bar will flicker between the normal color and another color.
	 * 
	 * @param barID
	 *            The identifying index of the status bar to be affected.
	 * @param value
	 *            True if alert is to be turned on, false if it is to be turned
	 *            off.
	 * @return boolean True if the specified bar exists and the operation
	 *         completed, otherwise false.
	 */
	public boolean toggleAlert(int barID, boolean value) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// If alert is turned off, ensure the bar does not stop on a flicker
			// frame.
			if (!value) {
				alertFlickering[barID] = false;
			}
			// Set the flag to the given boolean value.
			lowStatusAlert[barID] = value;
			return true;
		}
		return false;
	}

	/**
	 * Sets the properties of the alert feature. Percent is constrained between
	 * 0.0 and 1.0, any number outside the range will be corrected to the
	 * closest boundary. The refresh rate is high, thus a low alpha value is
	 * recommended for color. It is also suggested that the hue be similar to
	 * the bar itself.
	 * 
	 * @param barID
	 *            The identifying index of the status bar to be affected.
	 * @param maxPercent
	 *            Maximum status at which alert will activate, as a decimal
	 *            percentage of the maximum capacity between 0.0 and 1.0.
	 * @param color
	 *            Color of the alert flicker.
	 * @return boolean True if the specified bar exists and the operation
	 *         completed, otherwise false.
	 */
	public boolean setCustomAlert(int barID, double maxPercent, Color color) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Register the passed parameters.
			alertPercent[barID] = correctToRange(maxPercent, 0.0, 1.0);
			alertColors[barID] = color;
			return true;
		}
		return false;
	}

	/**
	 * Sets the maximum capacity of the status to a given value, for the
	 * specified bar. Negative values are ignored.
	 * 
	 * @param barID
	 *            The identifying index of the status bar to be affected.
	 * @param value
	 *            The new maximum capacity.
	 * @return boolean True if the specified bar exists and the operation
	 *         completed, otherwise false.
	 */
	public boolean setMax(int barID, double value) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Update the maximum capacity to the magnitude of the given value.
			maxValue[barID] = Math.abs(value);
			return true;
		}
		return false;
	}

	/**
	 * Sets the rate, as a proportion of the commanded status change passed to
	 * any update method, at which the display value will approach the actual
	 * value of status. A percent greater than or equal to 1.0 ensures near
	 * instantaneous response in the displayed value to any update. Negative
	 * values are ignored.
	 * 
	 * @param percent
	 *            The proportion of an update that the displayed value should
	 *            change by every act() iteration, as a decimal percentage.
	 */
	public void setChangeRateFactor(double percent) {
		changeRateFactor = Math.abs(percent);
	}

	// ACCESSORS----------------------------------------------------------

	/**
	 * Retrieves the current true value of the status for the specified bar.
	 * 
	 * @param barID
	 *            The identifying index of the status bar referenced.
	 * @return Double The value of the status if the specified bar exists and
	 *         the operation completed, otherwise null.
	 */
	public Double getStatus(int barID) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Return the value. Corrected for double precision loss.
			return roundPrecision(currentTarget[barID], STATUS_PRECISION);
		}
		return null;
	}

	/**
	 * Retrieves the maximum capacity of the status for the specified bar.
	 * 
	 * @param barID
	 *            The identifying index of the status bar referenced.
	 * @return Double The value of the maximum if the specified bar exists and
	 *         the operation completed, otherwise null.
	 */
	public Double getMax(int barID) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Return the maximum. Corrected for double precision loss.
			return roundPrecision(maxValue[barID], STATUS_PRECISION);
		}
		return null;
	}

	/**
	 * Retrieves the regeneration rate of the status for the specified bar.
	 * 
	 * @param barID
	 *            The identifying index of the status bar referenced.
	 * @return Double The value of the regeneration rate if the specified bar
	 *         exists and the operation completed, otherwise null.
	 */
	public Double getRegenRate(int barID) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Return the heal rate. Corrected for double precision loss.
			return roundPrecision(regenRate[barID], STATUS_PRECISION);
		}
		return null;
	}

	/**
	 * Checks if the status for the selected bar is full.
	 * 
	 * @param barID
	 *            The identifying index of the status bar referenced.
	 * @return Boolean True or false, whether the status is full, if the
	 *         specified bar exists and the operation completed, otherwise null.
	 */
	public Boolean isFull(int barID) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Return the boolean result of the evaluation, current actual value
			// equals maximum.
			return currentTarget[barID] == maxValue[barID];
		}
		return null;
	}

	/**
	 * Checks if the status for the selected bar is empty.
	 * 
	 * @param barID
	 *            The identifying index of the status bar referenced.
	 * @return Boolean True or false, whether the status is empty, if the
	 *         specified bar exists and the operation completed, otherwise null.
	 */
	public Boolean isEmpty(int barID) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Return the boolean result of the evaluation, current actual value
			// equals zero.
			return currentTarget[barID] == 0.0;
		}
		return null;
	}

	/**
	 * Retrieves the radius of the selected bar.
	 * 
	 * @param barID
	 *            The identifying index of the status bar referenced.
	 * @return Double The radius of the specified bar, if the specified bar
	 *         exists and the operation completed, otherwise null.
	 */
	public Double getBarRadius(int barID) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Retrieve the radius of the specified bar.
			return barRadii[barID];
		}
		return null;
	}

	/**
	 * Retrieves an array of two colors in the order; indicator filled color,
	 * empty bar background color; of indices 0 and 1 respectively, for the
	 * specified bar.
	 * 
	 * @param barID
	 *            The identifying index of the status bar referenced.
	 * @return Color[] An array of length two containing the fill color at index
	 *         0 and the background color at index 1, if the specified bar
	 *         exists and the operation completed, otherwise null.
	 */
	public Color[] getBarColors(int barID) {
		// If the index is valid;
		if (barID >= 0 && barID < barCount) {
			// Create and send out a color array containing indicator and
			// background color for this bar.
			return new Color[] { barColors[barID], barBGColors[barID] };
		}
		return null;
	}

	/**
	 * Retrieves the radius of the central region of the status set.
	 * 
	 * @return double The radius of the central region.
	 */
	public double getCentralRadius() {
		// Retrieve the radius of the center.
		return centralRadius;
	}

	// GREENFOOT STRUCTURES--------------------------------------------------

	/**
	 * Runs upon being added as an Actor to a Greenfoot world. Sets up the
	 * initial status set.
	 */
	public void addedToWorld(World w) {
		update();
	}

	/**
	 * Manages the animation of the bars for regeneration and updating of status
	 * values.
	 */
	public void act() {
		// For every bar, update self based on updates from controller.
		for (int i = 0; i < barCount; i++) {
			// If regeneration is on, regenerate.
			if (regen[i]) {
				// Adjust the target value according to regeneration.
				update(i, regenRate[i]);
			}

			// Now managing the approach of the display value to the target
			// value.
			// If the distance from the current display value to the target is
			// less than the change rate, then the change would pass the target
			// and the bar would fluctuate above and below the target, so
			// instead set the display equal to target.
			if (Math.abs(currentTarget[i] - currentValue[i]) < changeRate[i]) {
				currentValue[i] = currentTarget[i];
				// Otherwise, approach the target by the set change rate.
			} else {
				if (currentValue[i] > currentTarget[i]) {
					currentValue[i] -= changeRate[i];
				} else if (currentValue[i] < currentTarget[i]) {
					currentValue[i] += changeRate[i];
				}
			}

			// Ensure the display value is valid and in range of boundaries.
			currentValue[i] = correctToRange(currentValue[i], 0.0, maxValue[i]);
		}
		// Update the display with the display value.
		update();
	}
}