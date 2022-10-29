
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class ImageCompression {
	public class Pixal {
		double r;
		double g;
		double b;
		boolean isHigh = false;
	}

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	int width = 512; // default image width and height
	int height = 512;
	int image1[] = new int[3 * width * height];
	double imageDWT[] = new double[3 * width * height];
	Pixal image12[][] = new Pixal[width][height];
	Pixal image12T[][] = new Pixal[width][height];

	/**
	 * Read Image RGB
	 * Reads the image of given width and height at the given imgPath into the
	 * provided BufferedImage.
	 */
	private void readImageRGB(int width, int height, String imgPath, BufferedImage img, int level) {
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

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					int newR = Byte.toUnsignedInt(r);
					int newG = Byte.toUnsignedInt(g);
					int newB = Byte.toUnsignedInt(b);
					image1[(x + y * width) * 3] = newR;
					image1[(x + y * width) * 3 + 1] = newG;
					image1[(x + y * width) * 3 + 2] = newB;
					image12[x][y] = new Pixal();
					image12[x][y].r = (double) newR;
					image12[x][y].g = (double) newG;
					image12[x][y].b = (double) newB;
					ind++;

				}
			}
			// copy image1 to imageDWT
			for (int i = 0; i < image1.length; i++) {
				imageDWT[i] = (double) image1[i];
			}
			// imageDWT = Doubles.toArray(Integer.asList(image1));
			encodingImage(imageDWT, img, level);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void encodingImage(double[] image1, BufferedImage img, int level) {
		int iniLevel = 9;
		int need = iniLevel - level;
		int widthT = width;
		int heightT = height;
		int initWidth = 0;
		int initHeight = 0;
		for (int x = 0; x < need; x++) {
			// calculateCol(widthT, heightT);
			// calculateLevel(initWidth, initHeight, widthT, heightT, img);
			calculateLevel2(initWidth, initHeight, widthT, heightT);
			widthT = widthT / 2;
			heightT = heightT / 2;
			initHeight = initHeight + heightT;
			// widthT = widthT / 2;
			// heightT = heightT / 2;
			// initWidth = initWidth + widthT;
			// initHeight = initHeight + heightT;
		}
		// calculateLevel2(widthT, heightT);
		// calculateLevel2(widthT, heightT);
		System.out.println("done");
		// loop through the image
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (image12T[x][y].isHigh) {
					image12T[x][y].r = 0;
					image12T[x][y].g = 0;
					image12T[x][y].b = 0;

				}
				// // int pix = (a << 24) | (r << 16) | (g << 8) | b;
				// // img.setRGB(x, y, pix);
			}
		}
		// decodingImage(widthT, heightT);
		System.out.println(widthT);
		System.out.println(heightT);
		for (int x = 0; x < need; x++) {
			decodingImage2(widthT, heightT, need);
			widthT = widthT * 2;
			heightT = heightT * 2;
		}

		drawImage(img, need);
	}

	public void decodingImage2(int widthT, int heightT, int need) {

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image12[x][y].r = image12T[x][y].r;
				image12[x][y].g = image12T[x][y].g;
				image12[x][y].b = image12T[x][y].b;
				image12[x][y].isHigh = image12T[x][y].isHigh;
			}
		}
		// 128 * 2 - 1 = 255
		int d = height - 1;
		System.out.println("d: " + d);
		// heightT = height - heightT * 2;
		for (int y = height - 1; y >= height - heightT; y--) {
			for (int x1 = 0; x1 < widthT; x1++) {
				Pixal averagePixal = image12[x1][y];
				Pixal differencePixal = image12[x1][y - heightT];

				// Pixal differencePixal2 = image12T[x1][y - heightT + 1];
				// int newP = (y + 1) / 2;
				int newP = d;
				image12T[x1][newP].r = averagePixal.r - differencePixal.r;
				image12T[x1][newP].g = averagePixal.g - differencePixal.g;
				image12T[x1][newP].b = averagePixal.b - differencePixal.b;
				int newP2 = d - 1;
				// int newP = (y + 1) / 2;
				image12T[x1][newP2].r = averagePixal.r + differencePixal.r;
				image12T[x1][newP2].g = averagePixal.g + differencePixal.g;
				image12T[x1][newP2].b = averagePixal.b + differencePixal.b;

				// image12T[x1][y - heightT + 1].r = averagePixal2.r + differencePixal2.r;
				// image12T[x1][y - heightT + 1].g = averagePixal2.g + differencePixal2.g;
				// image12T[x1][y - heightT + 1].b = averagePixal2.b + differencePixal2.b;

			}
			d = d - 2;
		}
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image12[x][y].r = image12T[x][y].r;
				image12[x][y].g = image12T[x][y].g;
				image12[x][y].b = image12T[x][y].b;
				image12[x][y].isHigh = image12T[x][y].isHigh;
			}
		}
		// widthT = widthT * 2;
		int rheightT = height - heightT * 2;
		for (int y = height - 1; y >= rheightT; y--) {
			for (int x1 = 0; x1 < widthT; x1++) {
				Pixal averagePixal = image12[x1][y];
				Pixal differencePixal = image12[x1 + widthT][y];

				// Pixal differencePixal2 = image12T[x1][y - heightT + 1];
				int newP = 2 * (x1);
				image12T[newP][y].r = averagePixal.r + differencePixal.r;
				image12T[newP][y].g = averagePixal.g + differencePixal.g;
				image12T[newP][y].b = averagePixal.b + differencePixal.b;
				int newP2 = 2 * (x1) + 1;
				image12T[newP2][y].r = averagePixal.r - differencePixal.r;
				image12T[newP2][y].g = averagePixal.g - differencePixal.g;
				image12T[newP2][y].b = averagePixal.b - differencePixal.b;

			}
		}
		System.out.println(widthT);
		System.out.println(heightT);
	}

	public void calculateLevel2(int initWidth, int initHeight, int widthT, int heightT) {
		for (int x = 0; x < widthT; x += 2) {
			for (int y = initHeight; y < height; y++) {
				Pixal p = image12[x][y];
				Pixal p2 = image12[x + 1][y];
				double r = (p.r + p2.r) / 2;
				double g = (p.g + p2.g) / 2;
				double b = (p.b + p2.b) / 2;
				image12T[x / 2][y] = new Pixal();
				image12T[x / 2][y].r = r;
				image12T[x / 2][y].g = g;
				image12T[x / 2][y].b = b;
				double r2 = (p.r - p2.r) / 2;
				double g2 = (p.g - p2.g) / 2;
				double b2 = (p.b - p2.b) / 2;
				image12T[x / 2 + (widthT / 2)][y] = new Pixal();
				image12T[x / 2 + (widthT / 2)][y].r = r2;
				image12T[x / 2 + (widthT / 2)][y].g = g2;
				image12T[x / 2 + (widthT / 2)][y].b = b2;
				image12T[x / 2 + (widthT / 2)][y].isHigh = true;
			}
		}
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image12[x][y].r = image12T[x][y].r;
				image12[x][y].g = image12T[x][y].g;
				image12[x][y].b = image12T[x][y].b;
				image12[x][y].isHigh = image12T[x][y].isHigh;
			}
		}
		// int newWidth = heightT / 2;
		int initH = initHeight;
		int initW = initWidth;
		calculateCol(initW, initH, widthT / 2, heightT);
		// copy image12T to image12
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image12[x][y].r = image12T[x][y].r;
				image12[x][y].g = image12T[x][y].g;
				image12[x][y].b = image12T[x][y].b;
				image12[x][y].isHigh = image12T[x][y].isHigh;
			}
		}
	}

	public void drawImage(BufferedImage img, int need) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int a = 0;
				// if (need == 0) {
				// double r = image1[(x + y * width) * 3];
				// double g = image1[(x + y * width) * 3 + 1];
				// double b = image1[(x + y * width) * 3 + 2];
				// int pix = 0xff000000 | (((int) r & 0xff) << 16) | (((int) g & 0xff) << 8) |
				// ((int) b & 0xff);
				// img.setRGB(x, y, pix);
				// } else {
				// // System.out.println("x: " + x + " y: " + y);
				// double r = imageDWT[(x + y * width) * 3];
				// double g = imageDWT[(x + y * width) * 3 + 1];
				// double b = imageDWT[(x + y * width) * 3 + 2];
				// int pix = 0xff000000 | (((int) r & 0xff) << 16) | (((int) g & 0xff) << 8) |
				// ((int) b & 0xff);
				// img.setRGB(x, y, pix);
				// }
				double r = image12T[x][y].r;
				double g = image12T[x][y].g;
				double b = image12T[x][y].b;
				int pix = 0xff000000 | (((int) r & 0xff) << 16) | (((int) g & 0xff) << 8) | ((int) b & 0xff);
				img.setRGB(x, y, pix);
			}
		}
	}

	public void calculateCol(int initW, int initH, int widthT, int heightT) {
		for (int i = 0; i < widthT; i++) {
			for (int j = initH; j < height; j += 2) {

				Pixal p1 = image12[i][j];
				Pixal p2 = image12[i][j + 1];

				double average1 = (p1.r + p2.r) / 2;
				double average2 = (p1.g + p2.g) / 2;
				double average3 = (p1.b + p2.b) / 2;

				double difference1 = (p1.r - p2.r) / 2;
				double difference2 = (p1.g - p2.g) / 2;
				double difference3 = (p1.b - p2.b) / 2;

				image12T[i][(j - initH) / 2 + initH + (heightT / 2)].r = average1;
				image12T[i][(j - initH) / 2 + initH + (heightT / 2)].g = average2;
				image12T[i][(j - initH) / 2 + initH + (heightT / 2)].b = average3;
				image12T[i][(j - initH) / 2 + initH].r = difference1;
				image12T[i][(j - initH) / 2 + initH].g = difference2;
				image12T[i][(j - initH) / 2 + initH].b = difference3;
				image12T[i][(j - initH) / 2 + initH].isHigh = true;

			}
		}

	}

	public void showIms(String[] args) {

		// Read a parameter from command line
		String param1 = args[1];
		int level = Integer.parseInt(param1);
		System.out.println("The second parameter was: " + param1);

		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, args[0], imgOne, level);

		// Use label to display the image
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

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
		frame.getContentPane().add(lbIm1, c);

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		ImageCompression ren = new ImageCompression();
		ren.showIms(args);
	}

}
