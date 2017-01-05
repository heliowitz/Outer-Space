import greenfoot.*;
import java.util.ArrayList;

/**
 * Helper is an assistant class that provides miscellaneous functionality for
 * certain mathematical or ArrayList management needs. Possesses a fully static
 * interface and cannot be extended. No instances should be created.
 * 
 * @author Teddy Zhu
 * @version Mar. 18, 2014
 * 
 */
public final class Helper {

	/**
	 * Retrieves the angle of the second point relative to the first, in
	 * degrees. The positive x-axis is zero degrees, and the angle increases
	 * clockwise.
	 * 
	 * @param x1
	 *            The first point's x-coordinate.
	 * @param y1
	 *            The first point's y-coordinate.
	 * @param x2
	 *            The second point's x-coordinate.
	 * @param y2
	 *            The second point's y-coordinate.
	 * @return double The angle of the second point relative to the first in
	 *         degrees clockwise of the positive x-axis.
	 */
	public static double getAngle(double x1, double y1, double x2, double y2) {
		// Calculate the angle using the polar coordinate arc-tangent.
		double angle = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
		// Correct to constrain angle within one full rotation.
		angle = correctToRangeWithExcess(angle, 0, 360);
		return angle;
	}

	/**
	 * Corrects a number to the nearest boundary of a specified range if it is
	 * out of range.
	 * 
	 * @param num
	 *            The number to be corrected.
	 * @param min
	 *            The minimum boundary of the range.
	 * @param max
	 *            The maximum boundary of the range.
	 * @return long The corrected value.
	 */
	public static long correctToRange(long num, long min, long max) {
		// If the number is less than the lower bound, it is equal to the lower
		// bound.
		if (num < min) {
			return min;
			// If the number is greater than the higher bound, it is equal to
			// the higher bound.
		} else if (num > max) {
			return max;
		}
		// Otherwise it's in range and needs no correction.
		return num;
	}

	/**
	 * Corrects a number so that it falls within a range, while maintaining the
	 * excess amount. If the number is greater than the maximum, the excess is
	 * added to the minimum. If it is less than the minimum, the excess is
	 * subtracted from the maximum. That is, the range becomes a circular loop,
	 * and if the number falls beyond one boundary, it continues from the other
	 * boundary such that it cannot leave the range.
	 * 
	 * @param num
	 *            The number to be corrected.
	 * @param min
	 *            The minimum boundary of the range.
	 * @param max
	 *            The maximum boundary of the range.
	 * @return double The corrected value.
	 */
	public static double correctToRangeWithExcess(double num, double min,
			double max) {
		double range = Math.abs(max - min);
		// Recursive nature; will repeatedly adjust the number by units of range
		// until it falls in range. This method maintains the excess amount.
		if (num > max) {
			return correctToRangeWithExcess(num - range, min, max);
		} else if (num < min) {
			return correctToRangeWithExcess(num + range, min, max);
		}
		// Once the excess is in range, the number is returned without further
		// modification.
		return num;
	}

	/**
	 * Cleans up a reference list of Actors by removing any who are no longer in
	 * a World and minimizing the size.
	 * 
	 * @param refs
	 *            The references to be cleaned.
	 * @return ArrayList<? extends Actor> The cleaned references.
	 */
	public static ArrayList<? extends Actor> cleanReferences(
			ArrayList<? extends Actor> refs) {
		ArrayList<? extends Actor> cleanedRefs = refs;
		// Run through the references.
		for (int i = 0; i < refs.size(); i++) {
			// For any Actors who are no longer part of the world and have
			// been removed from it,
			if (refs.get(i).getWorld() == null) {
				// Remove them from the reference list.
				cleanedRefs.remove(i);
			}
		}
		// Shorten the list to the minimum size needed and remove empty spaces.
		cleanedRefs.trimToSize();
		// Return it.
		return cleanedRefs;
	}

	/**
	 * Clears a reference list of Actors by removing all of them from the World
	 * and emptying it.
	 * 
	 * @param refs
	 *            The references to be cleared.
	 * @return ArrayList<? extends Actor> The empty reference list.
	 */
	public static ArrayList<? extends Actor> clearReferences(
			ArrayList<? extends Actor> refs) {
		ArrayList<? extends Actor> clearedRefs = refs;
		// Run through the references.
		for (int i = 0; i < refs.size(); i++) {
			// Clear all Actors from world.
			if (refs.get(i).getWorld() != null) {
				clearedRefs.get(i).getWorld().removeObject(clearedRefs.get(i));
			}
		}
		// Clear references.
		// Return it.
		clearedRefs.clear();
		return clearedRefs;
	}

}
