
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

	/**
	 * Read Image RGB
	 * Reads the image of given width and height at the given imgPath into the
	 * provided BufferedImage.
	 */
	private void readSubtractedImageRGB(int width, int height, String foreground, String background,
			BufferedImage img) {
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

					// detect if the pixel in the last frame is different from the current frame
					// System.out.println("R: " + newR + " G: " + newG + " B: " + newB);
					// System.out.println(
					// "prev" + prevFrame[(x + y * width) * 3] + " " + prevFrame[(x + y * width) * 3
					// + 1] + " "
					// + prevFrame[(x + y * width) * 3 + 2]);

					// if the pixel is different from the previous frame, then it is a moving
					// object

					if (Math.abs(prevFrame[(x + y * width) * 3] - newR) < 10
							&& Math.abs(prevFrame[(x + y * width) * 3 + 1] - newG) < 10
							&& Math.abs(prevFrame[(x + y * width) * 3 + 2] - newB) < 10) {
						// if the pixel is different, set the pixel to white
						curFrame[(x + y * width) * 3] = backgroundR;
						curFrame[(x + y * width) * 3 + 1] = backgroundG;
						curFrame[(x + y * width) * 3 + 2] = backgroundB;
					} else {
						// if the pixel is the same, set the pixel to black
						curFrame[(x + y * width) * 3] = newR;
						curFrame[(x + y * width) * 3 + 1] = newG;
						curFrame[(x + y * width) * 3 + 2] = newB;
					}

					int pix;
					// detect if the pixel is green
					// if (newR < 120 && newG > 120 && newB < 170) {
					// pix = 0xff000000 | ((backgroundR & 0xff) << 16) | ((backgroundG & 0xff) << 8)
					// | (backgroundB & 0xff);
					// } else {
					// pix = 0xff000000 | ((newR & 0xff) << 16) | ((newG & 0xff) << 8) | (newB &
					// 0xff);
					// }

					pix = 0xff000000 | ((newR & 0xff) << 16) | ((newG & 0xff) << 8) | (newB &
							0xff);
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x, y, pix);
					prevFrame[(x + y * width) * 3] = newR;
					prevFrame[(x + y * width) * 3 + 1] = newG;
					prevFrame[(x + y * width) * 3 + 2] = newB;
					ind++;
				}
			}
			drawImage(curFrame, width, height, img);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void drawImage(int[] pixels, int width, int height, BufferedImage img) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pix = 0xff000000 | ((pixels[(x + y * width) * 3] & 0xff) << 16) | ((pixels[(x
						+ y * width) * 3 + 1] & 0xff) << 8) | (pixels[(x + y * width) * 3 + 2] &
								0xff);
				img.setRGB(x, y, pix);
			}
		}
	}

	private void readImageRGB(int width, int height, String imgPath, String backgroundPath, BufferedImage img) {
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
					// System.out.println("r: " + newR + " g: " + newG + " b: " + newB);
					int pix;
					// detect if the pixel is green
					if (newR < 120 && newG > 120 && newB < 170) {
						pix = 0xff000000 | ((backgroundR & 0xff) << 16) | ((backgroundG & 0xff) << 8)
								| (backgroundB & 0xff);
					} else {
						pix = 0xff000000 | ((newR & 0xff) << 16) | ((newG & 0xff) << 8) | (newB & 0xff);
					}

					// pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x, y, pix);
					ind++;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showIms(String img, String Back) {

		// Read image

		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, img, Back, imgOne);

		// Use label to display the image

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
	}

	public void showSubtractImg(String foreground, String background) {
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readSubtractedImageRGB(width, height, foreground, background, imgOne);

		// Use label to display the image

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
				ren.showIms(ForeGround, BackGround);
			} else {
				// store the first image into prevFrame
				if (count == 0) {
					ren.storeImageRGB(ren.width, ren.height, ForeGround, ren.prevFrame);
				}
				ren.showSubtractImg(ForeGround, BackGround);
			}
			// ren.showIms(ForeGround, BackGround);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
