import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class ImageMaker {
	
	private double margin = 0.15;
	private double aspect_ratio=2.9;
	private double min_end_length = 0.05;
	
	private Color bg_color = Color.WHITE;
	private Color text_color = Color.BLACK;
	private String render_mode = "rearrange";
	private boolean is_centered=false;
	private FontMetrics context;
	
	public ImageMaker(){
		/*initializing graphics environment*/
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		Font font = new Font("Arial", Font.PLAIN, 30);
		g2d.setFont(font);
		context = g2d.getFontMetrics();
	}
	public String writeText(String text){
		return writeText(text,"text_"+Math.abs(text.hashCode())+".png");
	}
	public String writeText(String text,String filename){
		/*calculating dimensions*/
		int abs_max_width=context.stringWidth("This is a test string meant to test how well my text arrangement");
		int abs_min_width=context.stringWidth("This is the min width");
		int string_width = context.stringWidth(text);
		int string_height = context.getHeight();                               
		double square = Math.ceil(Math.sqrt((double)(string_height*string_width))); //width of a "square" version of the text"
		int min_width = (int) ((double)square*Math.sqrt(aspect_ratio)); //adjusting square to desired aspect ratio
		min_width = (int) Math.max(abs_min_width,Math.min(min_width, abs_max_width)); //adjusting for min/max width 
		int max_width = (int) ((double)min_width*(1+margin-min_end_length)); //amount it can bleed over the min_width

		List<String> lines = split_text(context, min_width, max_width, text, render_mode);

		int text_height = 0;
		int text_width=0;
		for(String s:lines){
			//System.out.println(s);
			text_height+=context.getHeight();
			text_width=Math.max(context.stringWidth(s), text_width);
		}

		int img_width =text_width+2*string_height;
		int text_left =string_height;

		int img_height=text_height+2*text_left;

		//start drawing the image
		BufferedImage img = new BufferedImage(img_width, img_height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);                
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setFont(context.getFont());
		FontMetrics fm = g2d.getFontMetrics();

		int y=text_left;
		
		g2d.setColor(bg_color);
		g2d.fillRect(0, 0, img_width, img_height);
		g2d.setColor(text_color);
		for(String s:lines){
			if(is_centered){
				int cur_width = fm.stringWidth(s);
				g2d.drawString(s, (img_width-cur_width)/2, y+fm.getAscent());
			}else{
				g2d.drawString(s, text_left, y+fm.getAscent());
			}
			y+=fm.getHeight();
		}


		g2d.dispose();
		try {
			ImageIO.write(img, "png", new File(filename));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return filename;
	}
				
	public List<String> split_text(FontMetrics fm, int min_width, int max_width, String text, String render_mode){
		if(render_mode.equals("literal")){
			List<String> ret = new ArrayList<String>();
			for(String s:text.split("[\\r\\n]+")) ret.add(s);
			return ret;
		}
		String[] words = text.split(" ");
		List<String> output_rows = new ArrayList<String>();
		int cur_word=0;
		String cur_line="";
		while(cur_word < words.length){
			String test_line = (cur_line+" "+words[cur_word]).trim();
			if(fm.stringWidth(test_line)>=min_width){
				if(fm.stringWidth(test_line)<=max_width){
					output_rows.add(test_line);
					cur_line="";
				}else{
					output_rows.add(cur_line);
					cur_line=words[cur_word];
				}
			}else{
				cur_line=test_line;
			}
			cur_word++;
		}
		if(cur_line!="")output_rows.add(cur_line);
		return output_rows;
	}

	public void set_render_mode(String render_mode){
		this.render_mode=render_mode;
	}
	
	public void set_font(String set_font){
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		Font font = new Font(set_font, Font.PLAIN, 30);
		g2d.setFont(font);
		context = g2d.getFontMetrics();
	}
	
	public void set_center(boolean is_centered){
		this.is_centered=is_centered;
	}
	
	public void set_bg_color(String color){
		bg_color=getColor(color);
	}

	public void set_text_color(String color){
		text_color=getColor(color);
	}
	
	public void set_bg_color(String r, String g, String b){
		bg_color=new Color(Float.parseFloat(r)/(float)255.0,Float.parseFloat(g)/(float)255.0,Float.parseFloat(b)/(float)255.0);
	}
	
	public void set_text_color(String r, String g, String b){
		text_color=new Color(Float.parseFloat(r)/(float)255.0,Float.parseFloat(g)/(float)255.0,Float.parseFloat(b)/(float)255.0);
	}
	
	public static Color getColor(String color){
		Color ret;
		try {
			Field field = Class.forName("java.awt.Color").getField(color.toLowerCase());
			ret = (Color)field.get(null);
		} catch (Exception e) {
			ret = null; // Not defined
		}
		return ret;
	}
	
	public static void main(String[] args) {
		String filename = null;
		String read_mode = "lines";
		ImageMaker im = new ImageMaker();
		for(int i=0; i<args.length; i++){
			if(args[i].equals("-f")){
				filename = args[i+1];
				i++;
			}else if(args[i].equals("-t")){
				im.set_text_color(args[i+1]);
				i++;
			}else if(args[i].equals("-b")){
				im.set_bg_color(args[i+1]);
				i++;
			}else if(args[i].equals("-rm")){
				read_mode = args[i+1];
				i++;
			}else if(args[i].equals("-om")){
				im.set_render_mode(args[i+1]);
				i++;
			}else if(args[i].equals("-trgb")){
				im.set_text_color(args[i+1],args[i+2],args[i+3]);
				i+=3;
			}else if(args[i].equals("-brgb")){
				im.set_bg_color(args[i+1],args[i+2],args[i+3]);
				i+=3;
			}else if(args[i].equals("-c")){
				im.set_center(true);
			}else if(args[i].equals("-font")){
				im.set_font(args[i+1]);
				i++;
			}
		}
		if(filename == null){
			Scanner scan = new Scanner(System.in);
			if(read_mode.equals("lines")){
				while(scan.hasNextLine()){
					System.out.println(im.writeText(scan.nextLine().trim()));
				}
			}else{
				String text_string = "";
				while(scan.hasNextLine()){
					text_string+=scan.nextLine()+"\n";
				}
				text_string = text_string.substring(0,text_string.length()-1);
				System.out.println(im.writeText(text_string));
			}
			scan.close();
		}else{
			File f = new File(filename);
			FileReader fr = null;
			try {
				fr = new FileReader(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Scanner scan = new Scanner(fr);
			if(read_mode.equals("lines")){
				while(scan.hasNextLine()){
					System.out.println(im.writeText(scan.nextLine().trim()));
				}
			}else{
				char[] text = new char[(int)f.length()];
				try {
					fr.read(text);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String text_string = new String(text);
				System.out.println(im.writeText(text_string));
			}
			scan.close();
		}
	}
}

