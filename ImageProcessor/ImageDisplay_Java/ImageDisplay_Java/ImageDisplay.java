
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Arrays;

import javax.swing.*;

public class ImageDisplay {

	JFrame frame;
	JFrame frame2;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage imgOne;
	BufferedImage imgTwo;
	int width = 1920; // default image width and height
	int height = 1080;
	double RGBtoYUV[][] = { { 0.299, 0.587, 0.114 }, { 0.596, -0.274, -0.322 }, { 0.211, -0.523, 0.312 } };
	double YUVtoRGB[][] = { { 1, 0.956, 0.621 }, { 1, -0.272, -0.647 }, { 1, -1.106, 1.703 } };
	double YUV[] = new double[3 * width * height];
	double RGB[] = new double[3 * width * height];
	double finalRGB[] = new double[3 * width * height];

	/**
	 * Read Image RGB
	 * Reads the image of given width and height at the given imgPath into the
	 * provided BufferedImage.
	 */
	// 3 by 3 matrix multiplication with 3 by 1
	public double[][] matrixMultiplication(double[][] matrix1, double[][] matrix2) {
		double[][] result = new double[matrix1.length][matrix2[0].length];
		for (int i = 0; i < matrix1.length; i++) {
			for (int j = 0; j < matrix2[0].length; j++) {
				for (int k = 0; k < matrix1[0].length; k++) {
					result[i][j] += matrix1[i][k] * matrix2[k][j];
				}
			}
		}
		return result;
	}

	private void readImageRGBtoYUV(int width, int height, String imgPath, BufferedImage img, BufferedImage original,
			int Y, int U, int V, double scaleX, double scaleY, int antiAlias) {
		try {
			int frameLength = width * height * 3;

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);

			int ind = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind + height * width];
					byte b = bytes[ind + height * width * 2];

					// store rgb values in RGB array
					RGB[(x + y * width) * 3] = r;
					RGB[(x + y * width) * 3 + 1] = g;
					RGB[(x + y * width) * 3 + 2] = b;
					// System.out.println(r + " " + g + " " + b);
					// in bound between 255 and 0 exclusive
					int newR = Byte.toUnsignedInt(r);
					int newG = Byte.toUnsignedInt(g);
					int newB = Byte.toUnsignedInt(b);
					// if (newR > 255 || newG > 255
					// || newB > 255 || newR < 0
					// || newG < 0 || newB < 0) {
					// // System.out.println("Error");
					// System.out.println(newR + " " + newG + " " + newB);
					// }
					double[][] TempYUV = matrixMultiplication(RGBtoYUV,
							new double[][] { { newR }, { newG }, { newB } });
					// System.out.println(Arrays.deepToString(TempYUV));
					// check TempYUV values are in bound between 255 and 0 exclusive
					// if (TempYUV[0][0] > 255 || TempYUV[1][0] > 255
					// || TempYUV[2][0] > 255 || TempYUV[0][0] < 0
					// || TempYUV[1][0] < 0 || TempYUV[2][0] < 0) {
					// // System.out.println("Error");
					// System.out.println(TempYUV[0][0] + " " + TempYUV[1][0] + " " +
					// TempYUV[2][0]);
					// }
					YUV[(x + y * width) * 3] = TempYUV[0][0];
					YUV[(x + y * width) * 3 + 1] = TempYUV[1][0];
					YUV[(x + y * width) * 3 + 2] = TempYUV[2][0];
					// double[][] tempRGB = matrixMultiplication( YUVtoRGB, TempYUV);
					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					// int pix2 = 0xff000000 | (((byte) tempRGB[0][0] & 0xff) << 16)
					// | (((byte) tempRGB[1][0] & 0xff) << 8)
					// | ((byte) tempRGB[2][0] & 0xff);
					original.setRGB(x, y, pix);
					// img.setRGB(x, y, pix2);
					ind++;
				}
			}
			// File file21 = new File("./sample.txt");
			// PrintStream stream21 = new PrintStream(file21);
			// System.setOut(stream21);
			// System.out.println(Arrays.toString(YUV));
			subsampling(Y, U, V);
			generateImg(img, scaleX, scaleY, antiAlias);
			// System.out.println(Arrays.toString(YUV));
			// File file22 = new File("./sample.txt");
			// PrintStream stream22 = new PrintStream(file);
			// System.out.println(Arrays.toString(YUV));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateImg(BufferedImage img, double scaleX, double scaleY, int antiAlias) {
		// System.out.println(img.getWidth() + " " + img.getHeight());
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// if (YUV[(x + y * width) * 3] > 127 || YUV[(x + y * width) * 3 + 1] > 127
				// || YUV[(x + y * width) * 3 + 2] > 127 || YUV[(x + y * width) * 3] < -128
				// || YUV[(x + y * width) * 3 + 1] < -128 || YUV[(x + y * width) * 3 + 2] <
				// -128) {
				// // System.out.println("Error");
				// System.out.println(YUV[(x + y * width) * 3] + " " + YUV[(x + y * width) * 3 +
				// 1] + " "
				// + YUV[(x + y * width) * 3 + 2]);
				// }
				// System.out.println(YUV[(x + y * width) * 3] + " " + YUV[(x + y * width) * 3 +
				// 1] + " " + YUV[(x + y * width) * 3 + 2]);
				double[][] tempRGB = matrixMultiplication(YUVtoRGB, new double[][] { { YUV[(x
						+ y * width) * 3] },
						{ YUV[(x + y * width) * 3 + 1] }, { YUV[(x + y * width) * 3 + 2] } });
				// double[][] tempRGB = new double[][] { { YUV[(x + y * width) * 3] }, { YUV[(x
				// + y * width) * 3 + 1] },
				// { YUV[(x + y * width) * 3 + 2] } };
				// if (tempRGB[0][0] > 127 || tempRGB[0][0] < -128 || tempRGB[1][0] > 127 ||
				// tempRGB[1][0] < -128
				// || tempRGB[2][0] > 127 || tempRGB[2][0] < -128) {
				// System.out.println(tempRGB[0][0] + " " + tempRGB[1][0] + " " +
				// tempRGB[2][0]);
				// }

				// keep the values in range of 0 to 255

				if (tempRGB[0][0] > 255) {
					tempRGB[0][0] = 255;
				} else if (tempRGB[0][0] < 0) {
					tempRGB[0][0] = 0;
				}

				if (tempRGB[1][0] > 255) {
					tempRGB[1][0] = 255;
				} else if (tempRGB[1][0] < 0) {
					tempRGB[1][0] = 0;
				}

				if (tempRGB[2][0] > 255) {
					tempRGB[2][0] = 255;
				} else if (tempRGB[2][0] < 0) {
					tempRGB[2][0] = 0;
				}
				// add the tempRGB to finalRGB array
				finalRGB[(x + y * width) * 3] = tempRGB[0][0];
				finalRGB[(x + y * width) * 3 + 1] = tempRGB[1][0];
				finalRGB[(x + y * width) * 3 + 2] = tempRGB[2][0];
				// System.out.println(tempRGB[0][0] + " " + tempRGB[1][0] + " " +
				// tempRGB[2][0]);
				// int pix = 0xff000000 | (((byte) tempRGB[0][0] & 0xff)) << 16 | (((byte)
				// tempRGB[1][0] & 0xff) << 8)
				// | ((byte) tempRGB[2][0] & 0xff);

				if (antiAlias == 0) {
					int pix = 0xff000000 | (((byte) tempRGB[0][0] & 0xff)) << 16 | (((byte) tempRGB[1][0] & 0xff) << 8)
							| ((byte) tempRGB[2][0] & 0xff);
					int xScaled = (int) (x * scaleX);
					int yScaled = (int) (y * scaleY);
					if (xScaled > 0.0 && xScaled < img.getWidth() && yScaled > 0.0 && yScaled < img.getHeight()) {
						img.setRGB(xScaled, yScaled, pix);
					}
				}
				// scale the x and y by the scale factors
				// int xScaled = (int) (x * scaleX);
				// int yScaled = (int) (y * scaleY);
				// int widthScaled = (int) (width * scaleX);
				// int heightScaled = (int) (height * scaleY);
				// // implement anti-aliasing by averaging the colors of the surrounding pixels

				// // System.out.println(xScaled + " " + yScaled);
				// // check if the scaled x and y are within the bounds of the image

				// img.setRGB(xScaled, yScaled, pix);
				// System.out.println((x * scaleX) + " " + (y * scaleY));
				// img.setRGB((int) (x * scaleX), (int) (y * scaleY), pix);
			}

		}
		if (antiAlias == 1) {
			antiAlias(img, finalRGB, scaleX, scaleY);
		}
	}

	private void antiAlias(BufferedImage img, double[] finalRGB2, double scaleX, double scaleY) {
		int newWidth = img.getWidth();
		int newHeight = img.getHeight();

		for (int x = 1; x < newWidth - 1; x++) {
			for (int y = 1; y < newHeight - 1; y++) {
				// System.out.println(x + " " + y);
				// find the original x and y
				int xOrig = (int) (x / scaleX);
				int yOrig = (int) (y / scaleY);
				// find the surrounding pixels
				double[][] surroundingPixels = new double[9][3];
				surroundingPixels[0][0] = finalRGB2[((xOrig + 1) + yOrig * width) * 3];
				surroundingPixels[0][1] = finalRGB2[((xOrig + 1) + yOrig * width) * 3 + 1];
				surroundingPixels[0][2] = finalRGB2[((xOrig + 1) + yOrig * width) * 3 + 2];

				surroundingPixels[1][0] = finalRGB2[((xOrig - 1) + yOrig * width) * 3];
				surroundingPixels[1][1] = finalRGB2[((xOrig - 1) + yOrig * width) * 3 + 1];
				surroundingPixels[1][2] = finalRGB2[((xOrig - 1) + yOrig * width) * 3 + 2];

				surroundingPixels[2][0] = finalRGB2[(xOrig + (yOrig + 1) * width) * 3];
				surroundingPixels[2][1] = finalRGB2[(xOrig + (yOrig + 1) * width) * 3 + 1];
				surroundingPixels[2][2] = finalRGB2[(xOrig + (yOrig + 1) * width) * 3 + 2];

				surroundingPixels[3][0] = finalRGB2[(xOrig + (yOrig - 1) * width) * 3];
				surroundingPixels[3][1] = finalRGB2[(xOrig + (yOrig - 1) * width) * 3 + 1];
				surroundingPixels[3][2] = finalRGB2[(xOrig + (yOrig - 1) * width) * 3 + 2];

				surroundingPixels[4][0] = finalRGB2[((xOrig + 1) + (yOrig + 1) * width) * 3];
				surroundingPixels[4][1] = finalRGB2[((xOrig + 1) + (yOrig + 1) * width) * 3 +
						1];
				surroundingPixels[4][2] = finalRGB2[((xOrig + 1) + (yOrig + 1) * width) * 3 +
						2];

				surroundingPixels[5][0] = finalRGB2[((xOrig - 1) + (yOrig + 1) * width) * 3];
				surroundingPixels[5][1] = finalRGB2[((xOrig - 1) + (yOrig + 1) * width) * 3 +
						1];
				surroundingPixels[5][2] = finalRGB2[((xOrig - 1) + (yOrig + 1) * width) * 3 +
						2];

				surroundingPixels[6][0] = finalRGB2[((xOrig + 1) + (yOrig - 1) * width) * 3];
				surroundingPixels[6][1] = finalRGB2[((xOrig + 1) + (yOrig - 1) * width) * 3 +
						1];
				surroundingPixels[6][2] = finalRGB2[((xOrig + 1) + (yOrig - 1) * width) * 3 +
						2];

				surroundingPixels[7][0] = finalRGB2[((xOrig - 1) + (yOrig - 1) * width) * 3];
				surroundingPixels[7][1] = finalRGB2[((xOrig - 1) + (yOrig - 1) * width) * 3 +
						1];
				surroundingPixels[7][2] = finalRGB2[((xOrig - 1) + (yOrig - 1) * width) * 3 +
						2];

				surroundingPixels[8][0] = finalRGB2[(xOrig + yOrig * width) * 3];
				surroundingPixels[8][1] = finalRGB2[(xOrig + yOrig * width) * 3 + 1];
				surroundingPixels[8][2] = finalRGB2[(xOrig + yOrig * width) * 3 + 2];

				// average the surrounding pixels

				double[] avg = new double[3];
				for (int i = 0; i < 9; i++) {
					avg[0] += surroundingPixels[i][0];
					avg[1] += surroundingPixels[i][1];
					avg[2] += surroundingPixels[i][2];
				}
				avg[0] /= 9;
				avg[1] /= 9;
				avg[2] /= 9;

				// set the pixel to the average
				int pix = 0xff000000 | (((byte) (avg[0]) & 0xff)) << 16 | (((byte) (avg[1]) & 0xff) << 8)
						| ((byte) (avg[2]) & 0xff);
				// int pix = (int) (avg[0]) << 16 | (int) (avg[1]) << 8 | (int) (avg[2]);
				// System.out.println(img.getWidth() + " " + img.getHeight() + " ");
				// System.out.println(x + " " + y);
				img.setRGB(x, y, pix);

			}
		}

		// set RGB values for the first row
		for (int x = 0; x < img.getWidth(); x++) {
			if (x == 0) {
				int xOrig = (int) (x / scaleX);
				double[][] surroundingPixels = new double[4][3];

				// right neighbor
				surroundingPixels[0][0] = finalRGB2[((xOrig + 1) + 0 * width) * 3];
				surroundingPixels[0][1] = finalRGB2[((xOrig + 1) + 0 * width) * 3 + 1];
				surroundingPixels[0][2] = finalRGB2[((xOrig + 1) + 0 * width) * 3 + 2];

				// itself
				surroundingPixels[1][0] = finalRGB2[((xOrig) + 0 * width) * 3];
				surroundingPixels[1][1] = finalRGB2[((xOrig) + 0 * width) * 3 + 1];
				surroundingPixels[1][2] = finalRGB2[((xOrig) + 0 * width) * 3 + 2];

				// bottom neighbor
				surroundingPixels[2][0] = finalRGB2[(xOrig + 1 * width) * 3];
				surroundingPixels[2][1] = finalRGB2[(xOrig + 1 * width) * 3 + 1];
				surroundingPixels[2][2] = finalRGB2[(xOrig + 1 * width) * 3 + 2];

				// bottom right neighbor
				surroundingPixels[3][0] = finalRGB2[((xOrig + 1) + 1 * width) * 3];
				surroundingPixels[3][1] = finalRGB2[((xOrig + 1) + 1 * width) * 3 + 1];
				surroundingPixels[3][2] = finalRGB2[((xOrig + 1) + 1 * width) * 3 + 2];

				// average the surrounding pixels

				double[] avg = new double[3];
				for (int i = 0; i < 4; i++) {
					avg[0] += surroundingPixels[i][0];
					avg[1] += surroundingPixels[i][1];
					avg[2] += surroundingPixels[i][2];
				}
				avg[0] /= 4;
				avg[1] /= 4;
				avg[2] /= 4;

				// set the pixel to the average
				int pix = 0xff000000 | (((byte) (avg[0]) & 0xff)) << 16 | (((byte) (avg[1]) & 0xff) << 8)
						| ((byte) (avg[2]) & 0xff);

				img.setRGB(x, 0, pix);

			} else if (x == img.getWidth() - 1) {
				int xOrig = (int) (x / scaleX);
				double[][] surroundingPixels = new double[4][3];

				// left neighbor
				surroundingPixels[0][0] = finalRGB2[((xOrig - 1) + 0 * width) * 3];
				surroundingPixels[0][1] = finalRGB2[((xOrig - 1) + 0 * width) * 3 + 1];
				surroundingPixels[0][2] = finalRGB2[((xOrig - 1) + 0 * width) * 3 + 2];

				// itself
				surroundingPixels[1][0] = finalRGB2[((xOrig) + 0 * width) * 3];
				surroundingPixels[1][1] = finalRGB2[((xOrig) + 0 * width) * 3 + 1];
				surroundingPixels[1][2] = finalRGB2[((xOrig) + 0 * width) * 3 + 2];

				// bottom neighbor
				surroundingPixels[2][0] = finalRGB2[(xOrig + 1 * width) * 3];
				surroundingPixels[2][1] = finalRGB2[(xOrig + 1 * width) * 3 + 1];
				surroundingPixels[2][2] = finalRGB2[(xOrig + 1 * width) * 3 + 2];

				// bottom left neighbor
				surroundingPixels[3][0] = finalRGB2[((xOrig - 1) + 1 * width) * 3];
				surroundingPixels[3][1] = finalRGB2[((xOrig - 1) + 1 * width) * 3 + 1];
				surroundingPixels[3][2] = finalRGB2[((xOrig - 1) + 1 * width) * 3 + 2];

				// average the surrounding pixels

				double[] avg = new double[3];
				for (int i = 0; i < 4; i++) {
					avg[0] += surroundingPixels[i][0];
					avg[1] += surroundingPixels[i][1];
					avg[2] += surroundingPixels[i][2];
				}
				avg[0] /= 4;
				avg[1] /= 4;
				avg[2] /= 4;

				// set the pixel to the average
				int pix = 0xff000000 | (((byte) (avg[0]) & 0xff)) << 16 | (((byte) (avg[1]) & 0xff) << 8)
						| ((byte) (avg[2]) & 0xff);

				img.setRGB(x, 0, pix);

			} else {
				int xOrig = (int) (x / scaleX);
				double[][] surroundingPixels = new double[6][3];

				// right neighbor
				surroundingPixels[0][0] = finalRGB2[((xOrig + 1) + 0 * width) * 3];
				surroundingPixels[0][1] = finalRGB2[((xOrig + 1) + 0 * width) * 3 + 1];
				surroundingPixels[0][2] = finalRGB2[((xOrig + 1) + 0 * width) * 3 + 2];

				// left neighbor
				surroundingPixels[1][0] = finalRGB2[((xOrig - 1) + 0 * width) * 3];
				surroundingPixels[1][1] = finalRGB2[((xOrig - 1) + 0 * width) * 3 + 1];
				surroundingPixels[1][2] = finalRGB2[((xOrig - 1) + 0 * width) * 3 + 2];

				// itself
				surroundingPixels[2][0] = finalRGB2[((xOrig) + 0 * width) * 3];
				surroundingPixels[2][1] = finalRGB2[((xOrig) + 0 * width) * 3 + 1];
				surroundingPixels[2][2] = finalRGB2[((xOrig) + 0 * width) * 3 + 2];

				// bottom neighbor
				surroundingPixels[3][0] = finalRGB2[(xOrig + 1 * width) * 3];
				surroundingPixels[3][1] = finalRGB2[(xOrig + 1 * width) * 3 + 1];
				surroundingPixels[3][2] = finalRGB2[(xOrig + 1 * width) * 3 + 2];

				// bottom right neighbor
				surroundingPixels[4][0] = finalRGB2[((xOrig + 1) + 1 * width) * 3];
				surroundingPixels[4][1] = finalRGB2[((xOrig + 1) + 1 * width) * 3 + 1];
				surroundingPixels[4][2] = finalRGB2[((xOrig + 1) + 1 * width) * 3 + 2];

				// bottom left neighbor
				surroundingPixels[5][0] = finalRGB2[((xOrig - 1) + 1 * width) * 3];
				surroundingPixels[5][1] = finalRGB2[((xOrig - 1) + 1 * width) * 3 + 1];
				surroundingPixels[5][2] = finalRGB2[((xOrig - 1) + 1 * width) * 3 + 2];

				// average the surrounding pixels

				double[] avg = new double[3];
				for (int i = 0; i < 6; i++) {
					avg[0] += surroundingPixels[i][0];
					avg[1] += surroundingPixels[i][1];
					avg[2] += surroundingPixels[i][2];
				}
				avg[0] /= 6;
				avg[1] /= 6;
				avg[2] /= 6;

				// set the pixel to the average
				int pix = 0xff000000 | (((byte) (avg[0]) & 0xff)) << 16 | (((byte) (avg[1]) & 0xff) << 8)
						| ((byte) (avg[2]) & 0xff);

				img.setRGB(x, 0, pix);
			}

			// img.setRGB(x, img.getHeight() - 1, img.getRGB(x, img.getHeight() - 2));
		}

		// set RGB values for the last row
		for (int x = 0; x < img.getWidth(); x++) {
			img.setRGB(x, img.getHeight() - 1, img.getRGB(x, img.getHeight() - 2));
		}

		// set RGB values for the first and last column
		for (int y = 0; y < img.getHeight(); y++) {
			img.setRGB(0, y, img.getRGB(1, y));
			img.setRGB(img.getWidth() - 1, y, img.getRGB(img.getWidth() - 2, y));
		}

	}

	private void subsampling(int Y, int U, int V) {
		// System.out.println(Y + " " + U + " " + V);
		if (Y > 1) {
			for (int i = 0; i < YUV.length; i = i + 3 * width) {
				// System.out.println(y + " " + height + " " + y / height / 3);
				int y = i;
				while (y + Y * 3 < i + 3 * width) {
					// System.out.println(Y + " " + U + " " + V);
					// two y values that needs to be averaged
					int y2 = y + Y * 3;

					double averagedY = (double) ((YUV[y] + YUV[y2]) / 2);
					for (int j = 1; j < Y; j++) {
						YUV[y + j * 3] = averagedY;
					}
					y = y2;
				}
			}

		}

		if (U > 1) {
			for (int i = 0; i < YUV.length; i = i + 3 * width) {
				int y = i + 1;
				while (y + U * 3 < i + 3 * width) {
					// System.out.println(Y + " " + U + " " + V);
					// two y values that needs to be averaged
					int y2 = y + U * 3;

					double averagedY = (double) ((YUV[y] + YUV[y2]) / 2);
					for (int j = 1; j < U; j++) {
						YUV[y + j * 3] = averagedY;
					}
					y = y2;
				}
			}

		}

		if (V > 1) {
			for (int i = 0; i < YUV.length; i = i + 3 * width) {
				// System.out.println(y + " " + height + " " + y / height / 3);
				int y = i + 2;
				while (y + V * 3 < i + 3 * width) {
					// System.out.println(Y + " " + U + " " + V);
					// two y values that needs to be averaged
					int y2 = y + V * 3;

					double averagedY = (double) ((YUV[y] + YUV[y2]) / 2);
					for (int j = 1; j < V; j++) {
						YUV[y + j * 3] = averagedY;
					}
					y = y2;
				}
			}

		}

	}

	public void showProcessedIms(String args, int Y, int U, int V, double scaleX, double scaleY, int antiAlias) {

		// Read a parameter from command line
		System.out.println("The second parameter was: " + args);

		// Read in the specified image
		imgOne = new BufferedImage((int) (width * scaleX), (int) (height * scaleY), BufferedImage.TYPE_INT_RGB);
		imgTwo = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGBtoYUV(width, height, args, imgOne, imgTwo, Y, U, V, scaleX, scaleY, antiAlias);

		// Use label to display the image
		frame = new JFrame();
		frame2 = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);
		frame2.getContentPane().setLayout(gLayout);
		lbIm1 = new JLabel(new ImageIcon(imgOne));
		lbIm2 = new JLabel(new ImageIcon(imgTwo));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.setTitle("Processed Image");
		frame2.setTitle("Original Image");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);
		frame2.getContentPane().add(lbIm2, c);
		frame.pack();
		frame2.pack();
		frame.setVisible(true);
		frame2.setVisible(true);
	}

	public static void main(String[] args) {
		// ImageDisplay ren = new ImageDisplay();
		// ren.showIms(args);
		// args[0] = img
		// args[1] = Y
		// args[2] = U
		// args[3] = V
		// args[4] = scaleX
		// args[5] = scaleY
		// args[6] = antiAlias
		int Y = Integer.parseInt(args[1]);
		int U = Integer.parseInt(args[2]);
		int V = Integer.parseInt(args[3]);
		double scaleX = Double.parseDouble(args[4]);
		double scaleY = Double.parseDouble(args[5]);
		int antiAlias = Integer.parseInt(args[6]);
		ImageCompression ren2 = new ImageCompression();
		ren2.showProcessedIms(args[0], Y, U, V, scaleX, scaleY, antiAlias);
	}

}
