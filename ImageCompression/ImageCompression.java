
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

	// create an array that stors 9 image12T
	Pixal image12T0[][] = new Pixal[width][height];
	Pixal image12T1[][] = new Pixal[width][height];
	Pixal image12T2[][] = new Pixal[width][height];
	Pixal image12T3[][] = new Pixal[width][height];
	Pixal image12T4[][] = new Pixal[width][height];
	Pixal image12T5[][] = new Pixal[width][height];
	Pixal image12T6[][] = new Pixal[width][height];
	Pixal image12T7[][] = new Pixal[width][height];
	Pixal image12T8[][] = new Pixal[width][height];
	Pixal image12T9[][] = new Pixal[width][height];

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
					image12[x][y] = new Pixal();
					image12[x][y].r = (double) newR;
					image12[x][y].g = (double) newG;
					image12[x][y].b = (double) newB;
					image12T9[x][y] = new Pixal();
					image12T9[x][y].r = (double) newR;
					image12T9[x][y].g = (double) newG;
					image12T9[x][y].b = (double) newB;
					image12T[x][y] = new Pixal();
					image12T[x][y].r = (double) newR;
					image12T[x][y].g = (double) newG;
					image12T[x][y].b = (double) newB;
					ind++;

				}
			}
			encodingImage(imageDWT, img, level);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void zeroOut(Pixal[][] Image) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				if (Image[x][y].isHigh) {
					Image[x][y].r = 0;
					Image[x][y].g = 0;
					Image[x][y].b = 0;

				}
			}
		}
	}

	public void encodingImage(double[] image1, BufferedImage img, int level) {
		frame = new JFrame();
		if (level == -1) {
			int need = 9;
			int widthT = width;
			int heightT = height;
			int initWidth = 0;
			int initHeight = 0;
			int needT = 9;
			for (int x = 0; x < need; x++) {
				calculateLevel2(initWidth, initHeight, widthT, heightT);
				widthT = widthT / 2;
				heightT = heightT / 2;
				initHeight = initHeight + heightT;

				// store the image into the array

				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						if (x == 0) {
							image12T8[i][j] = new Pixal();
							image12T8[i][j].r = image12T[i][j].r;
							image12T8[i][j].g = image12T[i][j].g;
							image12T8[i][j].b = image12T[i][j].b;
							image12T8[i][j].isHigh = image12T[i][j].isHigh;
						} else if (x == 1) {
							image12T7[i][j] = new Pixal();
							image12T7[i][j].r = image12T[i][j].r;
							image12T7[i][j].g = image12T[i][j].g;
							image12T7[i][j].b = image12T[i][j].b;
							image12T7[i][j].isHigh = image12T[i][j].isHigh;
						} else if (x == 2) {
							image12T6[i][j] = new Pixal();
							image12T6[i][j].r = image12T[i][j].r;
							image12T6[i][j].g = image12T[i][j].g;
							image12T6[i][j].b = image12T[i][j].b;
							image12T6[i][j].isHigh = image12T[i][j].isHigh;
						} else if (x == 3) {
							image12T5[i][j] = new Pixal();
							image12T5[i][j].r = image12T[i][j].r;
							image12T5[i][j].g = image12T[i][j].g;
							image12T5[i][j].b = image12T[i][j].b;
							image12T5[i][j].isHigh = image12T[i][j].isHigh;
						} else if (x == 4) {
							image12T4[i][j] = new Pixal();
							image12T4[i][j].r = image12T[i][j].r;
							image12T4[i][j].g = image12T[i][j].g;
							image12T4[i][j].b = image12T[i][j].b;
							image12T4[i][j].isHigh = image12T[i][j].isHigh;
						} else if (x == 5) {
							image12T3[i][j] = new Pixal();
							image12T3[i][j].r = image12T[i][j].r;
							image12T3[i][j].g = image12T[i][j].g;
							image12T3[i][j].b = image12T[i][j].b;
							image12T3[i][j].isHigh = image12T[i][j].isHigh;
						} else if (x == 6) {
							image12T2[i][j] = new Pixal();
							image12T2[i][j].r = image12T[i][j].r;
							image12T2[i][j].g = image12T[i][j].g;
							image12T2[i][j].b = image12T[i][j].b;
							image12T2[i][j].isHigh = image12T[i][j].isHigh;
						} else if (x == 7) {
							image12T1[i][j] = new Pixal();
							image12T1[i][j].r = image12T[i][j].r;
							image12T1[i][j].g = image12T[i][j].g;
							image12T1[i][j].b = image12T[i][j].b;
							image12T1[i][j].isHigh = image12T[i][j].isHigh;
						} else if (x == 8) {
							image12T0[i][j] = new Pixal();
							image12T0[i][j].r = image12T[i][j].r;
							image12T0[i][j].g = image12T[i][j].g;
							image12T0[i][j].b = image12T[i][j].b;
							image12T0[i][j].isHigh = image12T[i][j].isHigh;
						}
					}
				}
			}
			// widthT = width;
			// heightT = height;
			for (int i = 0; i < need; i++) {
				if (i == 0) {
					zeroOut(image12T0);
				} else if (i == 1) {
					zeroOut(image12T1);
				} else if (i == 2) {
					zeroOut(image12T2);
				} else if (i == 3) {
					zeroOut(image12T3);
				} else if (i == 4) {
					zeroOut(image12T4);
				} else if (i == 5) {
					zeroOut(image12T5);
				} else if (i == 6) {
					zeroOut(image12T6);
				} else if (i == 7) {
					zeroOut(image12T7);
				} else if (i == 8) {
					zeroOut(image12T8);
				}
			}
			for (int i = 0; i < need + 1; i++) {
				if (i == 9) {
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							image12T[x][y].r = image12T9[x][y].r;
							image12T[x][y].g = image12T9[x][y].g;
							image12T[x][y].b = image12T9[x][y].b;
						}
					}
				}
				if (i == 6) {
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							image12T[x][y].r = image12T6[x][y].r;
							image12T[x][y].g = image12T6[x][y].g;
							image12T[x][y].b = image12T6[x][y].b;
						}
					}
					int widthtemp = widthT;
					int heighttemp = heightT;
					for (int x = i; x < 9; x++) {

						decodingImage2(widthtemp, heighttemp, need);
						widthtemp = widthtemp * 2;
						heighttemp = heighttemp * 2;
					}
				}
				if (i == 5) {
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							image12T[x][y].r = image12T5[x][y].r;
							image12T[x][y].g = image12T5[x][y].g;
							image12T[x][y].b = image12T5[x][y].b;
						}
					}
					int widthtemp = widthT;
					int heighttemp = heightT;
					for (int x = i; x < 9; x++) {

						decodingImage2(widthtemp, heighttemp, need);
						widthtemp = widthtemp * 2;
						heighttemp = heighttemp * 2;
					}
				}
				if (i == 4) {
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							image12T[x][y].r = image12T4[x][y].r;
							image12T[x][y].g = image12T4[x][y].g;
							image12T[x][y].b = image12T4[x][y].b;
						}
					}
					int widthtemp = widthT;
					int heighttemp = heightT;
					for (int x = i; x < 9; x++) {

						decodingImage2(widthtemp, heighttemp, need);
						widthtemp = widthtemp * 2;
						heighttemp = heighttemp * 2;
					}
				}
				if (i == 3) {
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							image12T[x][y].r = image12T3[x][y].r;
							image12T[x][y].g = image12T3[x][y].g;
							image12T[x][y].b = image12T3[x][y].b;
						}
					}
					int widthtemp = widthT;
					int heighttemp = heightT;
					for (int x = i; x < 9; x++) {

						decodingImage2(widthtemp, heighttemp, need);
						widthtemp = widthtemp * 2;
						heighttemp = heighttemp * 2;
					}
				}
				if (i == 2) {
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							image12T[x][y].r = image12T2[x][y].r;
							image12T[x][y].g = image12T2[x][y].g;
							image12T[x][y].b = image12T2[x][y].b;
						}
					}
					int widthtemp = widthT;
					int heighttemp = heightT;
					for (int x = i; x < 9; x++) {

						decodingImage2(widthtemp, heighttemp, need);
						widthtemp = widthtemp * 2;
						heighttemp = heighttemp * 2;
					}
				}
				if (i == 1) {
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							image12T[x][y].r = image12T1[x][y].r;
							image12T[x][y].g = image12T1[x][y].g;
							image12T[x][y].b = image12T1[x][y].b;
						}
					}
					int widthtemp = widthT;
					int heighttemp = heightT;
					for (int x = i; x < 9; x++) {

						decodingImage2(widthtemp, heighttemp, need);
						widthtemp = widthtemp * 2;
						heighttemp = heighttemp * 2;
					}
				}
				if (i == 7) {
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							image12T[x][y].r = image12T7[x][y].r;
							image12T[x][y].g = image12T7[x][y].g;
							image12T[x][y].b = image12T7[x][y].b;
						}
					}
					int widthtemp = widthT;
					int heighttemp = heightT;
					for (int x = i; x < 9; x++) {

						decodingImage2(widthtemp, heighttemp, need);
						widthtemp = widthtemp * 2;
						heighttemp = heighttemp * 2;
					}
					// decodingImage2(widthT, heightT, need);
				}
				if (i == 8) {
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							image12T[x][y].r = image12T8[x][y].r;
							image12T[x][y].g = image12T8[x][y].g;
							image12T[x][y].b = image12T8[x][y].b;
						}
					}
					int widthtemp = widthT;
					int heighttemp = heightT;
					for (int x = i; x < 9; x++) {

						decodingImage2(widthtemp, heighttemp, need);
						widthtemp = widthtemp * 2;
						heighttemp = heighttemp * 2;
					}
					// drawImage2(img, need);
				}
				if (i == 0) {
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							image12T[x][y].r = image12T0[x][y].r;
							image12T[x][y].g = image12T0[x][y].g;
							image12T[x][y].b = image12T0[x][y].b;
						}
					}
					int widthtemp = widthT;
					int heighttemp = heightT;
					for (int x = i; x < 9; x++) {

						decodingImage2(widthtemp, heighttemp, need);
						widthtemp = widthtemp * 2;
						heighttemp = heighttemp * 2;
					}
					// drawImage2(img, need);
				}

				widthT = widthT * 2;
				heightT = heightT * 2;
				// wait 2 seconds

				drawImage2(img, need);
				try {
					Thread.sleep(1500);
					frame.remove(lbIm1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// for (int x = 0; x < width; x++) {
			// for (int y = 0; y < height; y++) {
			// image12T[x][y].r = image12T8[x][y].r;
			// image12T[x][y].g = image12T8[x][y].g;
			// image12T[x][y].b = image12T8[x][y].b;
			// }
			// }
			// decodingImage2(widthT, heightT, need);

		} else {
			int iniLevel = 9;
			int need = iniLevel - level;
			int widthT = width;
			int heightT = height;
			int initWidth = 0;
			int initHeight = 0;
			for (int x = 0; x < need; x++) {
				calculateLevel2(initWidth, initHeight, widthT, heightT);
				widthT = widthT / 2;
				heightT = heightT / 2;
				initHeight = initHeight + heightT;
			}
			System.out.println("done");
			// loop through the image
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {

					if (image12T[x][y].isHigh) {
						image12T[x][y].r = 0;
						image12T[x][y].g = 0;
						image12T[x][y].b = 0;

					}
				}
			}
			for (int x = 0; x < need; x++) {

				decodingImage2(widthT, heightT, need);
				widthT = widthT * 2;
				heightT = heightT * 2;
			}

			drawImage(img, need);
		}

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
	}

	public void decodingImageFinal(int widthT, int heightT, Pixal[][] image12TDA) {

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image12[x][y].r = image12T3[x][y].r;
				image12[x][y].g = image12T3[x][y].g;
				image12[x][y].b = image12T3[x][y].b;
				image12[x][y].isHigh = image12T3[x][y].isHigh;
			}
		}
		System.out.println("widthT " + widthT);
		// 128 * 2 - 1 = 255
		int d = height - 1;
		// heightT = height - heightT * 2;
		for (int y = height - 1; y >= height - heightT; y--) {
			for (int x1 = 0; x1 < widthT; x1++) {
				Pixal averagePixal = image12[x1][y];
				Pixal differencePixal = image12[x1][y - heightT];

				// Pixal differencePixal2 = image12T[x1][y - heightT + 1];
				// int newP = (y + 1) / 2;
				int newP = d;
				image12T3[x1][newP].r = averagePixal.r - differencePixal.r;
				image12T3[x1][newP].g = averagePixal.g - differencePixal.g;
				image12T3[x1][newP].b = averagePixal.b - differencePixal.b;
				int newP2 = d - 1;
				// int newP = (y + 1) / 2;
				image12T3[x1][newP2].r = averagePixal.r + differencePixal.r;
				image12T3[x1][newP2].g = averagePixal.g + differencePixal.g;
				image12T3[x1][newP2].b = averagePixal.b + differencePixal.b;

				// image12T[x1][y - heightT + 1].r = averagePixal2.r + differencePixal2.r;
				// image12T[x1][y - heightT + 1].g = averagePixal2.g + differencePixal2.g;
				// image12T[x1][y - heightT + 1].b = averagePixal2.b + differencePixal2.b;

			}
			d = d - 2;
		}
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image12[x][y].r = image12T3[x][y].r;
				image12[x][y].g = image12T3[x][y].g;
				image12[x][y].b = image12T3[x][y].b;
				image12[x][y].isHigh = image12T3[x][y].isHigh;
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
				image12T3[newP][y].r = averagePixal.r + differencePixal.r;
				image12T3[newP][y].g = averagePixal.g + differencePixal.g;
				image12T3[newP][y].b = averagePixal.b + differencePixal.b;
				int newP2 = 2 * (x1) + 1;
				image12T3[newP2][y].r = averagePixal.r - differencePixal.r;
				image12T3[newP2][y].g = averagePixal.g - differencePixal.g;
				image12T3[newP2][y].b = averagePixal.b - differencePixal.b;

			}
		}
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
				double r = image12T[x][y].r;
				double g = image12T[x][y].g;
				double b = image12T[x][y].b;
				int pix = 0xff000000 | (((int) r & 0xff) << 16) | (((int) g & 0xff) << 8) | ((int) b & 0xff);
				img.setRGB(x, y, pix);
			}
		}
	}

	public void drawImage2(BufferedImage img, int need) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int a = 0;
				double r = image12T[x][y].r;
				double g = image12T[x][y].g;
				double b = image12T[x][y].b;
				int pix = 0xff000000 | (((int) r & 0xff) << 16) | (((int) g & 0xff) << 8) | ((int) b & 0xff);
				img.setRGB(x, y, pix);

			}
		}
		//
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
		if (level != -1) {
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
		// Use label to display the image

	}

	public static void main(String[] args) {
		ImageCompression ren = new ImageCompression();
		ren.showIms(args);
	}

}
