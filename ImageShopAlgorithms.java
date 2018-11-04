/* 
 * Note: these methods are public in order for them to be used by other files
 * in this assignment; DO NOT change them to private.  You may add additional
 * private methods to implement required functionality if you would like.
 * 
 * You should remove the stub lines (TODO and return null) from each method
 * and replace them with your implementation that returns an updated image.
 */

// TODO: comment this file explaining its behavior

import java.util.*;
import acm.graphics.*;

public class ImageShopAlgorithms implements ImageShopAlgorithmsInterface {

	/*
	 * flipHorizontal takes in an image and reverses its pixel orientation 
	 * so that the sign on the y-value of each pixel is flipped.
	 */
	public GImage flipHorizontal(GImage source) {
		GImage newImage;
		int[][] pixels = source.getPixelArray();
		int[][]newPixels = new int[pixels.length][pixels[0].length];
		for(int row = 0; row < pixels.length; row++) {
			int lastCol = pixels[0].length - 1;
			for(int col = 0; col < pixels[0].length; col++) {
				// Here we iterate through each pixel in the image and 
				// reassign its y-value.
				newPixels[row][col] = pixels[row][lastCol];
				lastCol--;
			}
		}
		source.setPixelArray(newPixels);
		newImage = new GImage(newPixels);
		return newImage;
	}

	/*
	 * rotateLeft turns the image 90 degrees counterclockwise by 
	 * turning the columns into rows starting from the column that is
	 * farthest right. 
	 */
	public GImage rotateLeft(GImage source) {
		GImage newImage;
		GImage imageLeft = source;
		int[][] pixels = imageLeft.getPixelArray();
		int[][]newPixels = new int[pixels[0].length][pixels.length];
		for(int row = 0; row < pixels.length; row++) {
			int lastCol = pixels[0].length - 1;
			for(int col = 0; col < pixels[0].length; col++) {
				newPixels[col][row] = pixels[row][lastCol];
				while(lastCol > 0) {
					lastCol--;
					break;
				}
			}
		}
		imageLeft.setPixelArray(newPixels);
		newImage = new GImage(newPixels);
		return newImage;
	}
	
	/*
	 * rotateRight turns the image 90 degrees clockwise by turning 
	 * the columns into rows starting from the farthest left column
	 * and placing it as the first row.
	 */
	public GImage rotateRight(GImage source) {
		GImage newImage;
		GImage imageRight = source;
		int[][] pixels = imageRight.getPixelArray();
		int[][]newPixels = new int[pixels[0].length][pixels.length];
		int lastRow = pixels.length;
		for(int row = 0; row < pixels.length; row++) {
			lastRow--;
			for(int col = 0; col < pixels[0].length; col++) {
				newPixels[col][row] = pixels[lastRow][col];
			}
		}
		imageRight.setPixelArray(newPixels);
		newImage = new GImage(newPixels);
		return newImage;
	}


	/*
	 * This algorithm is called green screen.
	 * It takes an image and removes pixels that have twice as much green as red and blue.
	 * Then the removed green is replaced by a transparent.
	 */
	public GImage greenScreen(GImage source) {
		int [][] pixels = source.getPixelArray();
		// This is a for loop to iterate throughout the pixels array.
		for (int row = 0; row < pixels.length; row++) {
			for (int col = 0; col < pixels[0].length; col++) {
				// This portion is getting a location of a pixel and its colors.
				int pixel = pixels[row][col];
				int pixelGreen = GImage.getGreen(pixel);
				int pixelRed = GImage.getRed(pixel);
				int pixelBlue = GImage.getBlue(pixel);
				// The variable "bigger" is just to decide if the green twice as large as the green. 
				int bigger = Math.max(pixelBlue, pixelRed);
				// If it is twice as big then it is replaced by a transparent pixel.
				if (bigger*2 <= pixelGreen) {
					pixel = GImage.createRGBPixel(1, 1, 1, 0);
					pixels[row][col] = pixel;
				}
			}
		}
		source.setPixelArray(pixels);
		return source;
	}

	/*
	 * Equalize takes an image, stores information about its
	 * pixels, and uses that information to generate
	 * a better, clearer, version of the image.
	 */
	public GImage equalize(GImage source) {
		GImage equalize;
		int luminosityRange = 256 ;
		int[][] pixels = source.getPixelArray();
		// We create a new array to store the modified pixels since 
		// we cannot directly change the values in the pixels array.
		int[][] newPixels = new int[pixels.length][pixels[0].length];
		int[] luminosityArray = new int[256];
		int[] cumulativeLuminosityArray = new int[256];
		double totalPixels = (pixels.length) * (pixels[0].length);
		double storeVal;
		for(int row = 0; row < pixels.length; row++) {
			for(int col = 0; col < pixels[0].length; col++) {
				// Loop through each pixel to capture its rgb value.
				int red = GImage.getRed(pixels[row][col]);
				int green = GImage.getGreen(pixels[row][col]);
				int blue = GImage.getBlue(pixels[row][col]); 
				// We increment that value at the index of the result value
				// computed by computeLuminosity() since we want to know 
				// how many pixels at have each luminosity.
				luminosityArray[(computeLuminosity(red, green, blue))]++;
			}
		}
		int startingIndexVal = 0;
		for(int i = 0; i < luminosityRange; i++) {
			for(int j = i; j < luminosityRange && j >= 0; j--) {
				// We take the values in the regular luminosity array and add the
				// value at an index plus all the indices preceding it, to the equivalent index
				// in cumulativeLuminosityArray.
				cumulativeLuminosityArray[startingIndexVal] += luminosityArray[j];
			}
			startingIndexVal++;
		}
		for(int j = 0; j < luminosityRange; j++) {
			// We perform the calculation needed to determine the correct rgb value
			// of the new pixels and set each index in cumulativeLuminosityArray equal
			// to the resulting value.
			storeVal= 255*(cumulativeLuminosityArray[j] / totalPixels);
			int storeValToInt = (int) Math.floor(storeVal);
			cumulativeLuminosityArray[j] = (storeValToInt);
		}
		int setStartingIndex = 0;
		for(int row = 0; row < pixels.length; row++) {
			for(int col = 0; col < pixels[0].length; col++) {
				// Here we actually assign each pixel its new rgb value and
				// add the pixels to newPixels array we created at the beginning. 
				if(setStartingIndex < 255) {
					setStartingIndex++;
				}
				int red = GImage.getRed(pixels[row][col]);
				int green = GImage.getGreen(pixels[row][col]);
				int blue = GImage.getBlue(pixels[row][col]); 
				int luminosity = computeLuminosity(red, green, blue);
				double ratio = ((double)cumulativeLuminosityArray[luminosity] / cumulativeLuminosityArray[255]);
				System.out.println(ratio);
				int rgbPixel = (int) (255 * ratio);
				newPixels[row][col] = GImage.createRGBPixel(rgbPixel, rgbPixel, rgbPixel);
			}
		}
		// We set the new pixel array and return it.
		source.setPixelArray(newPixels);
		equalize = new GImage(newPixels);
		return equalize;
	}

	/*
	 * This algorithm is called negative.
	 * This takes all the colors of the pixels in a array.
	 * Once it has all the colors then it reverses them and creates a image with reverse colors.
	 */
	public GImage negative(GImage source) {
		// First, the pixel array is taken in.
		int [][] pixels = source.getPixelArray();
		// Then a for loop is used to iterate through the array.
		for (int row = 0; row < pixels.length; row++) { 
			for (int col = 0; col < pixels[0].length; col++) { 
				// This is where all the colors are taken in and are reversed by subtracting them from 255.
				int pixel = pixels[row][col]; 
				int newRed = 255 - GImage.getRed(pixel);
				int newBlue = 255 - GImage.getBlue(pixel);
				int newGreen = 255 - GImage.getGreen(pixel);
				pixel = GImage.createRGBPixel(newRed, newGreen, newBlue);
				// Then the new pixel with new colors is put into the array.
				pixels[row][col] = pixel;
			} 
		}
		source.setPixelArray(pixels);
		return source;
	}


	/*
	 * This method is called translate.
	 * It moves a image any amount of pixels.
	 * Then user will be prompted to enter how many x and y pixels to move it.
	 * When the image is moved it will wrap around and bring the image to the other side of the pixel array.
	 */
	public GImage translate(GImage source, int dx, int dy) {
		// To begin we get the pixel array of the original image and create a new array the same size.
		int [][] pixels = source.getPixelArray();
		int [][] newPixels = new int[pixels.length][pixels[0].length];
		// Then we use a for loop to iterate through the pixel array. 
		for (int row = 0; row < pixels.length; row++) { 
			for (int col = 0; col < pixels[0].length; col++) {
				int pixel = pixels[row][col];
				// Here we define the variable to store the new locations that are post translation.
				// Then the new locations for the pixels are put onto the new pixel array.
				// The modulus operation is used to make sure that the image will wrap around.
				int newX = 0;
				int newY = 0;
				if (dx < 0) {
					newY = (col + (pixels[0].length + dx)) % pixels[0].length;
				} else {
					newY = (col + dx) % pixels[0].length;
				}
				if (dy < 0) {
					newX = (row + pixels.length + dy) % pixels.length;
				} else {
					newX = (row + dy) % pixels.length;
				}
				// This is where the colors for the pixels are gotten.
				int red = GImage.getRed(pixel);
				int blue = GImage.getBlue(pixel);
				int green = GImage.getGreen(pixel);
				pixel = GImage.createRGBPixel(red, green, blue);
				newPixels[newX][newY] = pixel;
			}
		}
		// FInally the new pixel array is made the source, or image shown to the user.
		source.setPixelArray(newPixels);
		return source;
	}

	/*
	 * This algorithm is called Blur.
	 * This is used to blur a image by averaging the colors around the pixel.
	 * Then once the 8 surrounding pixels color average is taken a new pixel array will be filled in.
	 * Every time the blur algorithm is called it will continue to blur more.
	 */
	public GImage blur(GImage source) {
		// We start by getting the pixel array of-
		// the image and making a new pixel array the same size.
		int [][] pixels = source.getPixelArray();
		int [][] newPixels = new int[pixels.length][pixels[0].length];
		for (int row = 0; row < pixels.length; row++) {
			for (int col = 0; col < pixels[0].length; col++) {
				// We use a for loop to iterate through the original pixel array.
				int pixel = pixels[row][col];
				// Then we make variable for the total red, green, and blue values
				//Also a "temp" variable is made for the number of pixels taken in.
				int temp = 0;
				int red = 0;
				int green = 0;
				int blue = 0;
				// Now we use another for loop to run through the eight surrounding pixels of the current pixel.
				// Then we add all the colors together into a total.
				for(int row2 = -1; row2 <= 1; row2++) {
					for(int col2 =- 1; col2 <= 1; col2++) {
						if(row + row2 >= 0 && row + row2 < pixels.length && col + col2 >= 0 && col + col2 < pixels[0].length) {
							temp++;
							red += GImage.getRed(pixels[row + row2][col + col2]);
							green += GImage.getGreen(pixels[row + row2][col + col2]);
							blue += GImage.getBlue(pixels[row + row2][col + col2]);
						}
					}
				}
				// Then we set the pixel in the new array to the RBG average of all the surrounding.
				// The average is the total divided by the "temp" variable or how many pixels are taken in.
				int pixelNew = GImage.createRGBPixel(red/temp, green/temp, blue/temp);
				newPixels[row][col] = pixelNew;
			}
		}
		// Finally, we set the new pixel array equal to what the user sees, which is source.
		source.setPixelArray(newPixels);
		return source;
	}
}
