package com.heartpirates;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
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
			os.reset();
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
		int xw = w;

		while (xw > 0) {
			int aval = 0;
			for (int y = 0; y < h; y++) {
				int sp = xw + y * w;
				if (sp > 0 && sp < pixels.length)
					aval |= (pixels[sp] >> 24);
			}
			if (aval != 0) {
				break;
			}
			xw--;
		}

		// find bottom crop
		int yw = h;

		while (yw > 0) {
			int aval = 0;
			for (int x = 0; x < w; x++) {
				int sp = x + yw * w;
				if (sp > 0 && sp < pixels.length)
					aval += (pixels[sp] >> 24);
			}
			if (aval != 0) {
				break;
			}
			yw--;
		}

		return bimg.getSubimage(0, 0, xw + 1, yw + 1);
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			String s = args[0];
			if (s.equalsIgnoreCase("test")) {
				System.out.println("Test");
			} else {
				Emoticon e = new Emoticon(args[args.length - 1]);
				try {
					// write the bytes to the output stream
					System.out.write(e.getPNG());
					System.out.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			try {
				createAndShowGUI();
				System.out.println("Showing gui");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void createAndShowGUI() throws IOException {
		JFrame frame = new JFrame("Emoticon");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Emoticon e = new Emoticon("シ (╯°□°）╯︵ ┻━┻ シ");

		ByteArrayInputStream input = new ByteArrayInputStream(e.getPNG());
		BufferedImage bimg = ImageIO.read(input);

		final BufferedImage image = bimg;
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