package com.heartpirates;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Emoticon {

	int w = 40;
	int h = 20;
	BufferedImage bimg;

	boolean headless = GraphicsEnvironment.isHeadless();

	static Color fgColor = new Color(0x202020);
	static Color bgColor = new Color(0xCFBFAD);

	public static final Font FONT1 = getFont("fonts/arialuni.ttf", 14f);
	public static final Font FONT2 = getFont("fonts/hkgokukaikk.ttf", 16f);
	public static final Font FONT3 = new Font("Unicode", Font.PLAIN, 12);
	public static Font FONT = FONT1;

	public static String[] emotes = { "(　ﾟДﾟ)＜!!", "(¬д¬。)", "(＃｀д´)ﾉ",
			"(/ﾟДﾟ)/", "（；¬＿¬)", "(」゜ロ゜)」", "(；￣Д￣）", "＼(｀0´)／",
			"(╯°□°）╯︵ ┻━┻", "(╯°Д°）╯︵/(.□ . \\)", "ヽ(#ﾟДﾟ)ﾉ┌┛Σ(ノ´Д`)ノ",
			"d(｀⌒´メ)z", "(∩｀-´)⊃━☆ﾟ.*･｡ﾟ", "ヽ₍⁽ˆ⁰ˆ⁾₎」♪♬", "ヽ(ﾟДﾟ)ﾉ", "∑(;°Д°)",
			"ヽ（゜ロ゜；）ノ", "(⊙︿⊙✿)", "(⊙△⊙✿)", "ヾ(´･ ･｀｡)ノ”", "⊙﹏⊙", "(♠•﹏•)",
			"〆(・∀・＠)", "φ(^ω^*)ﾉ", "φ(°ρ°*)メ", "＿〆(。。)", "〆(´Ｕ_｀*)",
			"(,, ･∀･)ﾉ゛", "(。･д･)ﾉﾞ", "(｡･ω･)ﾉﾞ", "ヽ(´･ω･`)､", "(=ﾟωﾟ)ノ",
			"( ﾟ▽ﾟ)/", "(。-ω-)ﾉ", "ヾ( ‘ – ‘*)", "ヾ(･ω･`｡)", "ヾ(・ω・ｏ)",
			"ヽ(๏∀๏ )ﾉ", "(´⊙ω⊙`)！", "(((( ;°Д°))))", "(*ﾟﾛﾟ)", "щ(゜ロ゜щ)",
			"o(´^｀)o", "(_ _)ヾ(‘ロ‘)", "＿ﾉ乙(､ﾝ､)_", "_(┐「ε:)_", "_(：3 」∠ )_",
			"（/｡＼)", "（*/∇＼*）", "(*ﾉ▽ﾉ)", "(*ﾉωﾉ)", "(/ω＼)", "(つω⊂* )",
			"(*ﾟｰﾟ)ゞ", "ε=ε=(っ*´□`)っ", "ヽ(￣д￣;)ノ=3=3=3", "( /)u(\\ )",
			"ヽ(●ﾟ´Д｀ﾟ●)ﾉﾟ", "(ノД`)・゜・。", "⊂(‘ω’⊂%20)))Σ≡=─~~", "ε===(っ≧ω≦)っ",
			"ε=ε=ε=ε=ε=ε=┌(;ʘ∀ʘ)┘", "┗( ●-﹏ ｀｡)づ", "⌒(｡･.･｡)⌒", "(•ㅅ•)",
			"( ◐ω◐ )", "─=≡Σ((( つ•̀ω•́)つ", "ε=ε=(っ*´□`)っ" };

	public static String getEmote(int n) {
		if (n < 0)
			n = (int) (Math.random() * emotes.length);
		return emotes[n % emotes.length];
	}

	public static Font getFont(String path, float size) {
		Font f = null;
		try {
			try {
				f = Font.createFont(Font.TRUETYPE_FONT, Emoticon.class
						.getClassLoader().getResourceAsStream(path));
			} catch (Exception e) {
				if (f == null) {
					f = Font.createFont(Font.TRUETYPE_FONT, Emoticon.class
							.getClassLoader().getResourceAsStream("font.ttf"));
				}
			}
			return f.deriveFont(size);
		} catch (Exception e) {
		}
		return null;
	}

	public Emoticon(String emote) {
		if (emote.length() < 1)
			emote = ("シ (╯°□°）╯︵ ┻━┻ シ");

		w = emote.length() * 16 + 1;

		bimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bimg.getGraphics();
		if (FONT != null) {
			g.setFont(FONT);
		} else {
			g.setFont(FONT1);
		}

		Font f = g.getFont();
		int size = f.getSize();

		if (!headless)
			System.out.println(f.getName());

		g.setColor(fgColor);
		g.drawString(emote, 0, size);
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

		xw++;
		yw++;

		if (xw > w)
			xw = w;
		if (yw > h)
			yw = h;

		return bimg.getSubimage(0, 0, xw, yw);
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			String[] vars = args[0].split("\\?");
			String s = vars[0];

			if (s.equalsIgnoreCase("jp")) {
				FONT = FONT2;
			}

			if (s.equalsIgnoreCase("test")) {
				System.out.println("Test");
			} else if (s.equalsIgnoreCase("print")) {
				try {
					System.out.write("Text Print Test".getBytes());
					System.out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					String emote = URLDecoder.decode(vars[vars.length - 1],
							"UTF-8");
					Emoticon e = new Emoticon(emote);
					// write the bytes to the output stream
					System.out.write(e.getPNG());
					System.out.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else { // no args
			try {
				if (GraphicsEnvironment.isHeadless()) {
					Emoticon e = new Emoticon(getEmote(-1)); // random emote
					// write the bytes to the output stream
					System.out.write(e.getPNG());
					System.out.flush();
				} else {
					// not in headless mode, show gui
					createAndShowGUI();
					System.out.println("Showing gui");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void createAndShowGUI() throws IOException {
		JFrame frame = new JFrame("Emoticon");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		String emote = getEmote(-1);
		Emoticon e = new Emoticon(emote);

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