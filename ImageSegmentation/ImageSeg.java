
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.lang.Math;

public class ImageSeg {

	JFrame frame = new JFrame("Image Segmentation");
	JLabel lbIm1;
	BufferedImage imgOne;
	int width = 640; // default image width and height
	int height = 480;
	JLabel prevlbIm1;
	int prevFrameLength = width * height * 3;
	int prevFrame[] = new int[3 * width * height];
	int curFrame[] = new int[3 * width * height];
	int tempFrame[] = new int[3 * width * height];
	int mode1Frame[] = new int[3 * width * height];
	int mode1background[] = new int[3 * width * height];
	int mode1Final[][] = new int[480][3 * width * height];

	/**
	 * Read Image RGB
	 * Reads the image of given width and height at the given imgPath into the
	 * provided BufferedImage.
	 */

	// Background Subtraction Algorithm

	private void readSubtractedImageRGB(int width, int height, String foreground, String background,
			BufferedImage img, int count) {
		try {
			int frameLength = width * height * 3;

			File file = new File(foreground);
			File backgroundFile = new File(background);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			RandomAccessFile backgroundRaf = new RandomAccessFile(backgroundFile, "r");
			backgroundRaf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];
			byte[] backgroundBytes = new byte[(int) len];
			raf.read(bytes);
			backgroundRaf.read(backgroundBytes);

			int ind = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind + height * width];
					byte b = bytes[ind + height * width * 2];

					byte backgroundR = backgroundBytes[ind];
					byte backgroundG = backgroundBytes[ind + height * width];
					byte backgroundB = backgroundBytes[ind + height * width * 2];

					int newR = Byte.toUnsignedInt(r);
					int newG = Byte.toUnsignedInt(g);
					int newB = Byte.toUnsignedInt(b);

					// int prevR = Byte.toUnsignedInt(prevFrame[(x + y * width) * 3]);
					// int prevG = Byte.toUnsignedInt(prevFrame[(x + y * width) * 3 + 1]);
					// int prevB = Byte.toUnsignedInt(prevFrame[(x + y * width) * 3 + 2]);

					double[] hsv = RGBtoHSV(newR, newG, newB);
					double[] prevHsv = RGBtoHSV(prevFrame[(x + y * width) * 3], prevFrame[(x + y * width) * 3 + 1],
							prevFrame[(x + y * width) * 3 + 2]);
					// if (Math.abs(hsv[0] - prevHsv[0]) < 5
					// && Math.abs(hsv[1] - prevHsv[1]) < 5
					// && Math.abs(hsv[2] - prevHsv[2]) < 5) {
					if ((Math.abs(hsv[0] - prevHsv[0]) < 8
							&& Math.abs(hsv[1] - prevHsv[1]) < 2.5
							&& Math.abs(hsv[2] - prevHsv[2]) < 2.5)) {
						// && (Math.abs(prevFrame[(x + y * width) * 3] - newR) < 10
						// || Math.abs(prevFrame[(x + y * width) * 3 + 1] - newG) < 10
						// || Math.abs(prevFrame[(x + y * width) * 3 + 2] - newB) < 10)
						// if (prevFrame[(x + y * width) * 3] != newR
						// && prevFrame[(x + y * width) * 3 + 1] != newG
						// && prevFrame[(x + y * width) * 3 + 2] != newB) {

						curFrame[(x + y * width) * 3] = backgroundR;
						curFrame[(x + y * width) * 3 + 1] = backgroundG;
						curFrame[(x + y * width) * 3 + 2] = backgroundB;

					} else {
						curFrame[(x + y * width) * 3] = newR;
						curFrame[(x + y * width) * 3 + 1] = newG;
						curFrame[(x + y * width) * 3 + 2] = newB;

					}
					mode1Final[count][(x + y * width) * 3] = curFrame[(x + y * width) * 3];
					mode1Final[count][(x + y * width) * 3 + 1] = curFrame[(x + y * width) * 3 + 1];
					mode1Final[count][(x + y * width) * 3 + 2] = curFrame[(x + y * width) * 3 + 2];
					prevFrame[(x + y * width) * 3] = newR;
					prevFrame[(x + y * width) * 3 + 1] = newG;
					prevFrame[(x + y * width) * 3 + 2] = newB;
					ind++;
				}
			}
			// processImage(curFrame, tempFrame, width, height, img, count, mode1Final);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readImageRGB(int width, int height, String imgPath, String backgroundPath, BufferedImage img,
			int count) {
		try {
			int frameLength = width * height * 3;

			File file = new File(imgPath);
			File backgroundFile = new File(backgroundPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			RandomAccessFile backgroundRaf = new RandomAccessFile(backgroundFile, "r");
			backgroundRaf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];
			byte[] backgroundBytes = new byte[(int) len];
			raf.read(bytes);
			backgroundRaf.read(backgroundBytes);

			int ind = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind + height * width];
					byte b = bytes[ind + height * width * 2];

					byte backgroundR = backgroundBytes[ind];
					byte backgroundG = backgroundBytes[ind + height * width];
					byte backgroundB = backgroundBytes[ind + height * width * 2];

					int newR = Byte.toUnsignedInt(r);
					int newG = Byte.toUnsignedInt(g);
					int newB = Byte.toUnsignedInt(b);
					double[] hsv = RGBtoHSV(newR, newG, newB);
					// if (x == 600 && y == 475) {
					// System.out.println("hsv: " + hsv[0] + " " + hsv[1] + " " + hsv[2]);
					// }
					mode1background[(x + y * width) * 3] = Byte.toUnsignedInt(backgroundR);
					mode1background[(x + y * width) * 3 + 1] = Byte.toUnsignedInt(backgroundG);
					mode1background[(x + y * width) * 3 + 2] = Byte.toUnsignedInt(backgroundB);

					int pix;

					if (hsv[0] >= 40 && hsv[0] <= 190 && hsv[1] >= 49 && hsv[1] <= 255 && hsv[2] >= 49
							&& hsv[2] <= 255) {

						mode1Frame[(x + y * width) * 3] = 1000;
						mode1Frame[(x + y * width) * 3 + 1] = 1000;
						mode1Frame[(x + y * width) * 3 + 2] = 1000;
					} else {
						mode1Frame[(x + y * width) * 3] = newR;
						mode1Frame[(x + y * width) * 3 + 1] = newG;
						mode1Frame[(x + y * width) * 3 + 2] = newB;

					}
					ind++;
				}

			}
			drawImageForMode1(mode1Frame, mode1background, width, height, img, count);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// credit to GeekforGeeks for the RGBtoHSV method
	// https://www.geeksforgeeks.org/program-change-rgb-color-model-hsv-color-model/

	private double[] RGBtoHSV(int r, int g, int b) {
		double[] hsl = new double[3];
		double r1 = r / 255.0;
		double g1 = g / 255.0;
		double b1 = b / 255.0;
		double cmax = Math.max(r1, Math.max(g1, b1));
		double cmin = Math.min(r1, Math.min(g1, b1));
		double diff = cmax - cmin;
		if (cmax == cmin) {
			hsl[0] = 0;
		} else if (cmax == r1) {
			hsl[0] = (60 * ((g1 - b1) / diff) + 360) % 360;
		} else if (cmax == g1) {
			hsl[0] = (60 * ((b1 - r1) / diff) + 120) % 360;
		} else if (cmax == b1) {
			hsl[0] = (60 * ((r1 - g1) / diff) + 240) % 360;
		}
		if (cmax == 0) {
			hsl[1] = 0;
		} else {
			hsl[1] = (diff / cmax) * 100;
		}
		hsl[2] = (cmax) * 100;
		return hsl;
	}

	public void drawImageForMode1(int[] pixels, int[] background, int width, int height, BufferedImage img, int count) {
		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				// int pix = 0;
				if (pixels[(x + y * width) * 3] == 1000 && pixels[(x + y * width) * 3 + 1] == 1000
						&& pixels[(x + y * width) * 3 + 2] == 1000) {
					// pix = 0xff000000 | ((background[(x + y * width) * 3] & 0xff) << 16)
					// | ((background[(x + y * width) * 3 + 1] & 0xff) << 8)
					// | (background[(x + y * width) * 3 + 2] & 0xff);
					mode1Final[count][(x + y * width) * 3] = background[(x + y * width) * 3];
					mode1Final[count][(x + y * width) * 3 + 1] = background[(x + y * width) * 3 + 1];
					mode1Final[count][(x + y * width) * 3 + 2] = background[(x + y * width) * 3 + 2];
				} else {

					double[][] surroundingPixels = new double[9][3];
					surroundingPixels[0][0] = pixels[((x + 1) + y * width) * 3];
					surroundingPixels[0][1] = pixels[((x + 1) + y * width) * 3 + 1];
					surroundingPixels[0][2] = pixels[((x + 1) + y * width) * 3 + 2];

					surroundingPixels[1][0] = pixels[((x - 1) + y * width) * 3];
					surroundingPixels[1][1] = pixels[((x - 1) + y * width) * 3 + 1];
					surroundingPixels[1][2] = pixels[((x - 1) + y * width) * 3 + 2];

					surroundingPixels[2][0] = pixels[(x + (y + 1) * width) * 3];
					surroundingPixels[2][1] = pixels[(x + (y + 1) * width) * 3 + 1];
					surroundingPixels[2][2] = pixels[(x + (y + 1) * width) * 3 + 2];

					surroundingPixels[3][0] = pixels[(x + (y - 1) * width) * 3];
					surroundingPixels[3][1] = pixels[(x + (y - 1) * width) * 3 + 1];
					surroundingPixels[3][2] = pixels[(x + (y - 1) * width) * 3 + 2];

					surroundingPixels[4][0] = pixels[((x + 1) + (y + 1) * width) * 3];
					surroundingPixels[4][1] = pixels[((x + 1) + (y + 1) * width) * 3 +
							1];
					surroundingPixels[4][2] = pixels[((x + 1) + (y + 1) * width) * 3 +
							2];

					surroundingPixels[5][0] = pixels[((x - 1) + (y + 1) * width) * 3];
					surroundingPixels[5][1] = pixels[((x - 1) + (y + 1) * width) * 3 +
							1];
					surroundingPixels[5][2] = pixels[((x - 1) + (y + 1) * width) * 3 +
							2];

					surroundingPixels[6][0] = pixels[((x + 1) + (y - 1) * width) * 3];
					surroundingPixels[6][1] = pixels[((x + 1) + (y - 1) * width) * 3 +
							1];
					surroundingPixels[6][2] = pixels[((x + 1) + (y - 1) * width) * 3 +
							2];

					surroundingPixels[7][0] = pixels[((x - 1) + (y - 1) * width) * 3];
					surroundingPixels[7][1] = pixels[((x - 1) + (y - 1) * width) * 3 +
							1];
					surroundingPixels[7][2] = pixels[((x - 1) + (y - 1) * width) * 3 +
							2];

					surroundingPixels[8][0] = pixels[(x + y * width) * 3];
					surroundingPixels[8][1] = pixels[(x + y * width) * 3 + 1];
					surroundingPixels[8][2] = pixels[(x + y * width) * 3 + 2];

					// loop through the surrounding pixels and check if they are 1 1 1

					Boolean isBoundary = false;
					for (int i = 0; i < 9; i++) {
						if (surroundingPixels[i][0] == 1000 && surroundingPixels[i][1] == 1000
								&& surroundingPixels[i][2] == 1000) {
							// pix = 0xff000000 | ((background[(x + y * width) * 3] & 0xff) << 16)
							// | ((background[(x + y * width) * 3 + 1] & 0xff) << 8)
							// | (background[(x + y * width) * 3 + 2] & 0xff);
							// mode1Final[count][(x + y * width) * 3] = 1000;
							// mode1Final[count][(x + y * width) * 3 + 1] = 1000;
							// mode1Final[count][(x + y * width) * 3 + 2] = 1000;
							pixels[(x + y * width) * 3] = 2000;
							pixels[(x + y * width) * 3 + 1] = 2000;
							pixels[(x + y * width) * 3 + 2] = 2000;
							mode1Final[count][(x + y * width) * 3] = background[(x + y * width) * 3];
							mode1Final[count][(x + y * width) * 3 + 1] = background[(x + y * width) * 3 + 1];
							mode1Final[count][(x + y * width) * 3 + 2] = background[(x + y * width) * 3 + 2];
							isBoundary = true;
							break;
						}
					}
					if (!isBoundary) {
						// pix = 0xff000000 | ((pixels[(x + y * width) * 3] & 0xff) << 16)
						// | ((pixels[(x + y * width) * 3 + 1] & 0xff) << 8)
						// | (pixels[(x + y * width) * 3 + 2] & 0xff);
						double[][] surroundingPixels2 = new double[9][3];
						surroundingPixels2[0][0] = pixels[((x + 1) + y * width) * 3];
						surroundingPixels2[0][1] = pixels[((x + 1) + y * width) * 3 + 1];
						surroundingPixels2[0][2] = pixels[((x + 1) + y * width) * 3 + 2];

						surroundingPixels2[1][0] = pixels[((x - 1) + y * width) * 3];
						surroundingPixels2[1][1] = pixels[((x - 1) + y * width) * 3 + 1];
						surroundingPixels2[1][2] = pixels[((x - 1) + y * width) * 3 + 2];

						surroundingPixels2[2][0] = pixels[(x + (y + 1) * width) * 3];
						surroundingPixels2[2][1] = pixels[(x + (y + 1) * width) * 3 + 1];
						surroundingPixels2[2][2] = pixels[(x + (y + 1) * width) * 3 + 2];

						surroundingPixels2[3][0] = pixels[(x + (y - 1) * width) * 3];
						surroundingPixels2[3][1] = pixels[(x + (y - 1) * width) * 3 + 1];
						surroundingPixels2[3][2] = pixels[(x + (y - 1) * width) * 3 + 2];

						surroundingPixels2[4][0] = pixels[((x + 1) + (y + 1) * width) * 3];
						surroundingPixels2[4][1] = pixels[((x + 1) + (y + 1) * width) * 3 +
								1];
						surroundingPixels2[4][2] = pixels[((x + 1) + (y + 1) * width) * 3 +
								2];

						surroundingPixels2[5][0] = pixels[((x - 1) + (y + 1) * width) * 3];
						surroundingPixels2[5][1] = pixels[((x - 1) + (y + 1) * width) * 3 +
								1];
						surroundingPixels2[5][2] = pixels[((x - 1) + (y + 1) * width) * 3 +
								2];

						surroundingPixels2[6][0] = pixels[((x + 1) + (y - 1) * width) * 3];
						surroundingPixels2[6][1] = pixels[((x + 1) + (y - 1) * width) * 3 +
								1];
						surroundingPixels2[6][2] = pixels[((x + 1) + (y - 1) * width) * 3 +
								2];

						surroundingPixels2[7][0] = pixels[((x - 1) + (y - 1) * width) * 3];
						surroundingPixels2[7][1] = pixels[((x - 1) + (y - 1) * width) * 3 +
								1];
						surroundingPixels2[7][2] = pixels[((x - 1) + (y - 1) * width) * 3 +
								2];

						surroundingPixels2[8][0] = pixels[(x + y * width) * 3];
						surroundingPixels2[8][1] = pixels[(x + y * width) * 3 + 1];
						surroundingPixels2[8][2] = pixels[(x + y * width) * 3 + 2];
						boolean isBoundaryAdj = false;
						// double tempPiexlR = 0;
						// double tempPiexlG = 0;
						// double tempPiexlB = 0;
						// int num = 0;
						for (int i = 0; i < 9; i++) {
							if (surroundingPixels[i][0] == 2000 && surroundingPixels[i][1] == 2000
									&& surroundingPixels[i][2] == 2000) {
								// pix = 0xff000000 | ((background[(x + y * width) * 3] & 0xff) << 16)
								// | ((background[(x + y * width) * 3 + 1] & 0xff) << 8)
								// | (background[(x + y * width) * 3 + 2] & 0xff);

								// tempPiexlR += surroundingPixels2[i][0];
								mode1Final[count][(x + y * width) * 3] = background[(x + y * width) * 3];
								mode1Final[count][(x + y * width) * 3 + 1] = background[(x + y * width) * 3 + 1];
								mode1Final[count][(x + y * width) * 3 + 2] = background[(x + y * width) * 3 + 2];
								isBoundaryAdj = true;
								break;
							}

						}
						if (!isBoundaryAdj) {
							mode1Final[count][(x + y * width) * 3] = pixels[(x + y * width) * 3];
							mode1Final[count][(x + y * width) * 3 + 1] = pixels[(x + y * width) * 3 + 1];
							mode1Final[count][(x + y * width) * 3 + 2] = pixels[(x + y * width) * 3 + 2];
						}

					}

				}

			}
		}
	}

	public void showIms(String img, String Back, int count) {

		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, img, Back, imgOne, count);
	}

	public void showSubtractImg(String foreground, String background, int count) {
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readSubtractedImageRGB(width, height, foreground, background, imgOne, count);
	}

	private void storeImageRGB(int width, int height, String forground, int[] prevFrame) {
		// store the image rgb to prevFrame
		try {
			int frameLength = width * height * 3;

			File file = new File(forground);
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

					int newR = Byte.toUnsignedInt(r);
					int newG = Byte.toUnsignedInt(g);
					int newB = Byte.toUnsignedInt(b);
					// System.out.println("r: " + newR + " g: " + newG + " b: " + newB);

					prevFrame[(x + y * width) * 3] = newR;
					prevFrame[(x + y * width) * 3 + 1] = newG;
					prevFrame[(x + y * width) * 3 + 2] = newB;
					ind++;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ImageSeg ren = new ImageSeg();
		String foreground = args[0];
		String background = args[1];
		int mode = Integer.parseInt(args[2]);
		File f = new File(foreground);
		File b = new File(background);
		// loop through all files in the directory
		// loop through count from 0 to 480 evenly in 20 seconds
		for (int count = 0; count < 480; count++) {
			// create a new file name
			// convert count to string
			String countString = Integer.toString(count);
			// if count length is less than 4, add 0s to the front
			while (countString.length() < 4) {
				countString = "0" + countString;
			}
			String newFileName = foreground + '.' + countString + ".rgb";
			// create a new file
			// File newFile = new File(newFileName);
			String newFileNameBack = background + '.' + countString + ".rgb";
			String ForeGround = "./" + foreground + "/" + newFileName;
			String BackGround = "./" + background + "/" + newFileNameBack;

			if (mode == 1) {
				System.out.println("Preprocessing progress " + count + "/480");
				ren.showIms(ForeGround, BackGround, count);
			} else {
				// store the first image into prevFrame
				if (count == 0) {
					ren.storeImageRGB(ren.width, ren.height, ForeGround, ren.prevFrame);
				}
				System.out.println("Preprocessing progress " + count + "/480");
				ren.showSubtractImg(ForeGround, BackGround, count);
			}
		}
		if (mode == 1) {
			ren.showMode1();
		} else {
			ren.showMode2();
		}

		// System.out.println("Time taken: " + (end - start) + "ms");

	}

	private void showMode1() {
		// looping though all Mode1Final
		for (int i = 0; i < 480; i++) {
			// loop x and y
			imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pix;

					int r = mode1Final[i][(x + y * width) * 3];
					int g = mode1Final[i][(x + y * width) * 3 + 1];
					int b = mode1Final[i][(x + y * width) * 3 + 2];
					pix = 0xff000000 | ((r & 0xff) << 16)
							| ((g & 0xff) << 8)
							| (b & 0xff);
					imgOne.setRGB(x, y, pix);
				}
			}
			GridBagLayout gLayout = new GridBagLayout();
			frame.getContentPane().setLayout(gLayout);
			prevlbIm1 = lbIm1;
			lbIm1 = new JLabel(new ImageIcon(imgOne));
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.CENTER;
			c.weightx = 0.5;
			c.gridx = 0;
			c.gridy = 0;

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			if (prevlbIm1 != null)
				frame.remove(prevlbIm1);
			// frame.getContentPane().remove(prevlbIm1);
			frame.getContentPane().add(lbIm1, c);

			frame.pack();
			frame.setVisible(true);
			try {
				Thread.sleep(28);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void showMode2() {
		// looping though all Mode1Final
		for (int i = 0; i < 480; i++) {
			// loop x and y
			imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pix;

					int r = mode1Final[i][(x + y * width) * 3];
					int g = mode1Final[i][(x + y * width) * 3 + 1];
					int b = mode1Final[i][(x + y * width) * 3 + 2];
					pix = 0xff000000 | ((r & 0xff) << 16)
							| ((g & 0xff) << 8)
							| (b & 0xff);
					imgOne.setRGB(x, y, pix);
				}
			}
			GridBagLayout gLayout = new GridBagLayout();
			frame.getContentPane().setLayout(gLayout);
			prevlbIm1 = lbIm1;
			lbIm1 = new JLabel(new ImageIcon(imgOne));
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.CENTER;
			c.weightx = 0.5;
			c.gridx = 0;
			c.gridy = 0;

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			if (prevlbIm1 != null)
				frame.remove(prevlbIm1);
			// frame.getContentPane().remove(prevlbIm1);
			frame.getContentPane().add(lbIm1, c);

			frame.pack();
			frame.setVisible(true);
			try {
				Thread.sleep(28);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
