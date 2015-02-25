package com.imageOptimize;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: thudanih Date: Apr 25, 2011 Time: 11:14:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class AlgoSA {
	Algo algo;
	private Hashtable<Integer, ImageIcon> images = new Hashtable<Integer, ImageIcon>();
	private Hashtable<Integer, String> fileNames = new Hashtable<Integer, String>();
	private List<ImageObject> lstImages;
	private int imgIndex = 0;

	public AlgoSA() {
		int outImageWidth = 800; // width of the largest image
		int outImageHeight = 1000;
		lstImages = new ArrayList<ImageObject>();

		File imageFolder = new File("img/Black");

		if (imageFolder.exists() && imageFolder.isDirectory()) {
			generateImages(imageFolder, "");
		}
		algo = new Algo(outImageHeight, outImageWidth, lstImages);
		int[][] dummy = algo.run();

		algo.setNumOfRandomImagesToRemove(20);
		algo.setSAIterations(15);
		algo.setIncrement(0);
		BufferedImage resizedImg = new BufferedImage(outImageWidth,
				outImageHeight, Transparency.TRANSLUCENT);
		Graphics2D g2 = resizedImg.createGraphics();
		// g2.setBackground(new Color(0,0,0,0));
		// g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN,
		// 0.0f));
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		int placedCount = 0;
		StringBuffer s = new StringBuffer();
		for (int i : images.keySet()) {
			List<EmptyRectSpace> rect = new EmptySpacer(outImageHeight,
					outImageWidth).getEmptyRectangleSpaces(
					/* 0, */algo.copyDummy(dummy), outImageWidth,
					outImageHeight, i);
			if (rect.size() > 0) {
				ImageIcon img = images.get(i);
				int posX = rect.get(0).getStartingI()/* + 1 - img.getIconHeight() */;
				int posY = rect.get(0).getStartingJ()/* + 1 - img.getIconWidth() */;
				System.out
						.println("###============================================"
								+ i + "->" + posX + "," + posY);
				s.append(fileNames.get(i) + "=" + posY + "," + posX + ";"
						+ img.getIconWidth() + "," + img.getIconHeight()
						+ System.getProperty("line.separator"));
				placedCount++;
				g2.drawImage(img.getImage(), posY, posX, img.getIconWidth(),
						img.getIconHeight(), null);
			}
		}
		System.out.println("======================= Placed count: "
				+ placedCount);
		System.out.println("======================= Remaining count: "
				+ (images.size() - placedCount));
		g2.dispose();
		BufferedWriter out;
		FileWriter fstream = null;
		try {
			fstream = new FileWriter("core/src/img/positions.properties");
			// fstream = new
			// FileWriter("E:\\ImageConvert\\src\\outImge\\img.txt");
			out = new BufferedWriter(fstream);
			out.write(s.toString());
			out.close();
			// Close the output stream

		} catch (IOException e) {
			e.printStackTrace();
		}

		File outputFile = new File("core/src/img/generated.png");
		try {
			ImageIO.write(resizedImg, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void generateImages(File imageFolder, String parentName) {
		for (int i = 0, listFilesLength = imageFolder.listFiles().length; i < listFilesLength; i++) {
			File f = imageFolder.listFiles()[i];
			if (f.isDirectory()) {
				generateImages(f, f.getName() + "/");
			} else if (f.isFile()
					&& (f.getName().endsWith(".png")
							|| f.getName().endsWith(".gif") || f.getName()
							.endsWith(".jpg"))) {
				try {
					InputStream ins = new BufferedInputStream(
							new FileInputStream(f));
					byte[] imgData = new byte[ins.available()];
					ins.read(imgData, 0, imgData.length);
					ImageIcon icon = new ImageIcon(imgData);

					images.put(++imgIndex, icon);
					fileNames.put(imgIndex, parentName + f.getName());
					ImageObject newImage = new ImageObject();
					newImage.setId(imgIndex);
					newImage.setHeight(icon.getIconHeight());
					newImage.setWidth(icon.getIconWidth());
					lstImages.add(newImage);
					// String key = (name).replace(path, "");
					// imageStore.put(key, new ImageIcon(imgData));
					ins.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) {
		new AlgoSA();
	}

}