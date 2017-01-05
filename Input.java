import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import greenfoot.*;

/**
 * Input is an assistant class that loads assets and resources from external
 * files. Possesses a fully static interface and cannot be extended. No
 * instances should be created.
 * <p>
 * Includes functionality for loading individual images from a sprite-sheet, and
 * for loading an external font.
 * 
 * @author Teddy Zhu
 * @version Mar. 5, 2014
 */
public final class Input {

	/**
	 * Makes use of BufferedImage's subImage method to extract from sprite
	 * sheets. Retrieved and compiled from two sources; one section extracts
	 * grid squares from a BufferedImage while the second converts it to a
	 * GreenfootImage for use. All arrays passed must be of equal length and
	 * correspond with each other by order of index.
	 * 
	 * @param filename
	 *            Picture file-paths.
	 * @param picX
	 *            Widths per sprite in pixels.
	 * @param picY
	 *            Heights per sprite in pixels.
	 * @param rowCt
	 *            Numbers of rows to be extracted.
	 * @param colCt
	 *            Numbers of columns to be extracted.
	 * @return GreenfootImage[] The compiled array of animation frames
	 *         containing all frames from all images specified in parameters;
	 *         null upon failure.
	 */
	public static GreenfootImage[] loadFrames(String[] filename, int[] picX,
			int[] picY, int[] rowCt, int[] colCt) {
		// Simple array matching check. The parameters all correspond with each
		// other and must be equal in length.
		if (filename.length != picX.length || picX.length != picY.length) {
			System.out
					.println("Animation frame loading error in Input: Array length mismatch.");
			return null;
		} else if (picY.length != rowCt.length || rowCt.length != colCt.length) {
			System.out
					.println("Animation frame loading error in Input: Array length mismatch.");
			return null;
		}
		// Set up necessary data.
		int[] width = picX;
		int[] height = picY;
		int[] rows = rowCt;
		int[] cols = colCt;
		// Set up the managed array's size based on parameters.
		int avaSize = 0;
		for (int q = 0; q < rows.length; q++) {
			// avaSize will be the sum of the requested grid squares across all
			// requested files.
			avaSize += rows[q] * cols[q];
		}
		// Arrays for transfer and drawing.
		BufferedImage[] main = new BufferedImage[filename.length];
		BufferedImage[] avaTemp = new BufferedImage[avaSize];
		BufferedImage[] gBufImg = new BufferedImage[avaTemp.length];
		Graphics2D[] graphics = new Graphics2D[avaTemp.length];
		GreenfootImage[] ava = new GreenfootImage[avaTemp.length];
		// These variables prevent overwrite of existing images while adding
		// them to the array, instead allowing them to be appended to the end of
		// the array.
		int currentIndex = 0;
		// Retrieve files.
		for (int a = 0; a < main.length; a++) {
			try {
				main[a] = ImageIO.read(new File(filename[a]));
			} catch (Exception e) {
				// If image retrieval fails;
				System.out.println("Animation frame loading error in Input.");
				e.printStackTrace();
			}
		}
		// For each image, row, and column, perform actions on each grid square.
		for (int y = 0; y < main.length; y++) {
			for (int i = 0; i < rows[y]; i++) {
				for (int j = 0; j < cols[y]; j++) {
					// Retrieve the sub-image,
					avaTemp[(i * cols[y]) + j + currentIndex] = main[y]
							.getSubimage(j * width[y], i * height[y], width[y],
									height[y]);
					// Set up this index in ava with appropriate size,
					ava[(i * cols[y]) + j + currentIndex] = new GreenfootImage(
							avaTemp[(i * cols[y]) + j + currentIndex]
									.getWidth(),
							avaTemp[(i * cols[y]) + j + currentIndex]
									.getHeight());
					// Assign gBufImg the underlying image of ava, assign
					// graphics the underlying graphics of gBufImg, and draw
					// avaTemp's retrieved sub-image onto the graphics.
					gBufImg[(i * cols[y]) + j + currentIndex] = ava[(i * cols[y])
							+ j + currentIndex].getAwtImage();
					graphics[(i * cols[y]) + j + currentIndex] = (Graphics2D) gBufImg[(i * cols[y])
							+ j + currentIndex].getGraphics();
					graphics[(i * cols[y]) + j + currentIndex].drawImage(
							avaTemp[(i * cols[y]) + j + currentIndex], null, 0,
							0);
					// currentIndex refers to the section of indexes already
					// taken up by files in the image arrays.
					// This offset after each switch to another file prevents
					// returning to index zero and overwriting existing images,
					// and instead appends the new file images to the index last
					// left off.
				}
			}
			// As seen, once the processor exits the above loop, it is about to
			// move onto the next file specified by index y.
			// Before this occurs, currentIndex, present as an offset in the
			// arrays being worked with inside the innermost loop,
			// is to be updated with this file's grid length, plus one to
			// indicate the very next image following the last one appended,
			// so that once the next file is extracted from, its images will be
			// added onto the index following the previous image's
			// rather than overwriting the array from index zero.
			// rows[y]-1 refers to the final value of i for this file; the last
			// row. cols[y]-1 refers to the final value of j for this file; the
			// last column. The absolute position in the final array, assuming
			// the first index is zero, is (i*cols[y])+j. Substituting i and j
			// with the final row and column, this formula yields the index
			// containing the last image of this file. Adding one to it gives
			// the next empty index.
			// This index is where the next file should begin appending to, and
			// as such currentIndex is updated and the innermost arrays are
			// offset according to the this image.
			currentIndex += ((rows[y] - 1) * cols[y]) + (cols[y] - 1) + 1;
		}
		return ava;
	}

	/**
	 * Retrieves a Font from an external TrueType file.
	 * 
	 * @param fileName
	 *            The path to the TrueType file.
	 * @param fontSize
	 *            The desired font size.
	 * @return Font The font from the file, null upon failure.
	 */
	public static Font loadFont(String fileName, int fontSize) {
		// Attempt to load the font;
		try {
			// Retrieve the file.
			File fontFile = new File(fileName);
			// Create the font.
			Font tempFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			// Register the new font in the system.
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(
					tempFont);
			// Set up a new copy of the font with a specific style and size.
			Font finalFont = new Font(tempFont.getName(), Font.PLAIN, fontSize);
			return finalFont;
		} catch (Exception e) {
			System.out.println("Error in Input: Failed to load font(s).");
			return null;
		}
	}
}
