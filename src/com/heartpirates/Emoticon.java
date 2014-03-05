package com.heartpirates;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Emoticon {

	int w = 40;
	int h = 20;
	BufferedImage bimg;

	static Color fgColor = new Color(0x202020);
	static Color bgColor = new Color(0xCFBFAD);

	public Emoticon(String str) {
		w = str.length() * 10;
		bimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bimg.getGraphics();
		Font f = g.getFont();
		int size = f.getSize();

		g.setColor(fgColor);
		g.drawString(str, 0, size);
		g.dispose();

		bimg = crop(bimg);
	}

	public byte[] getPNG() {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(bimg, "PNG", os);
			os.flush();
			return os.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private BufferedImage crop(BufferedImage bimg) {
		int[] pixels = ((DataBufferInt) bimg.getRaster().getDataBuffer())
				.getData();

		// find right crop
		int ww = w;
		boolean w_done = false;
		for (int x = w - 1; x > 0; x--) {
			for (int y = 0; y < h; y++) {
				int sp = x + y * w;
				int pixel = pixels[sp];
				if ((pixel & 0xffffff) > 0) {
					w_done = true;
					break;
				}
			}
			if (w_done) {
				ww = x + 4;
				break;
			}
		}

		// find bottom crop
		int hh = h;
		boolean h_done = false;
		for (int y = h - 1; y > 0; y--) {
			for (int x = 0; x < w; x++) {
				int sp = x + y * w;
				int pixel = pixels[sp];
				if ((pixel & 0xffffff) > 0) {
					h_done = true;
					break;
				}
			}
			if (h_done) {
				hh = y + 1;
				break;
			}
		}

		return bimg.getSubimage(0, 0, ww, hh);
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			String s = args[0];
			if (s.equalsIgnoreCase("test")) {
				System.out.println("Test");
			} else {
				Emoticon e = new Emoticon(args[0]);
				try {
					// write the bytes to the output stream
					System.out.write(e.getPNG());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			try {
				createAndShowGUI();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void createAndShowGUI() {
		JFrame frame = new JFrame("Emoticon");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Emoticon e = new Emoticon("シ (╯°□°）╯︵ ┻━┻ シ");

		final BufferedImage image = e.bimg;
		Canvas canvas;
		frame.add(canvas = new Canvas() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(),
						null);
			}
		});
		canvas.setSize(e.bimg.getWidth(), e.bimg.getHeight());

		frame.pack();
		frame.setVisible(true);
	}
}