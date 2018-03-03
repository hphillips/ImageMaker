import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class ImageMaker {
	static double margin = 0.15;
	static double aspect_ratio=2.9;
	static double min_end_length = 0.05;

	public static List<String> split_text(FontMetrics fm, int min_width, int max_width, String[] words){
		List<String> output_rows = new ArrayList<String>();
		int cur_row=0;
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
	        		cur_row++;
	        	}else{
	        		cur_line=test_line;        		
	        	}
	        	cur_word++;
	        }
        	output_rows.add(cur_line);
	        return output_rows;
	}	
	
	public static String writeImg(String text){
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		Font font = new Font("Arial", Font.PLAIN, 48);
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();
		
		int width = fm.stringWidth(text);
		int height = fm.getHeight();
		int abs_max_length=fm.stringWidth("This is a test string meant to test how well my text arrangement");
		double square = Math.ceil(Math.sqrt((double)(height*width)));
		int min_width = (int) ((double)square*Math.sqrt(aspect_ratio));
		min_width = (int) Math.min(min_width, abs_max_length);
		int max_width = (int) ((double)min_width*(1+margin-min_end_length));
		
		g2d.dispose();
		
		String[] words=text.split(" ");
		List<String> lines = split_text(fm, min_width, max_width, words);
		
		int text_height = 0;
		int text_width=0;
		for(String s:lines){
			//System.out.println(s);
			text_height+=fm.getHeight();
			text_width=Math.max(fm.stringWidth(s), text_width);
		}
		
		int img_width =(int) ((double)text_width*(1+margin));
		int text_left =(int) ((double)text_width*(margin)/2.0);
		
		int img_height=text_height+2*text_left;
		int text_top  =text_left;
		
		img = new BufferedImage(img_width, img_height, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setFont(font);
		fm = g2d.getFontMetrics();
		
		int y=text_left;
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, img_width, img_height);
		g2d.setColor(Color.BLACK);
		for(String s:lines){
			g2d.drawString(s, text_left, y+fm.getAscent());
			y+=fm.getHeight();
		}
		
		
		g2d.dispose();
		String filename="text_"+Math.abs(text.hashCode())+".png";
		try {
		    ImageIO.write(img, "png", new File(filename));
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
		return filename;
		
	}

	public static void main(String[] args) {
		String filename = null;
		String text_color = "Black";
		String background_color = "White";
		String read_mode = "lines";
		String render_mode = "rearrange";

		for(int i=0; i<args.length; i++){
			if(args[i]=="-f"){
				filename = args[i+1];
				i++;
			}else if(args[i]=="-t"){
				text_color = args[i+1];
				i++;
			}else if(args[i]=="-b"){
				background_color=args[i+1];
				i++;
			}else if(args[i]=="-rm"){
				read_mode = args[i+1];
				i++;
			}else if(args[i]=="-om"){
				render_mode = args[i+1];
				i++;
			}
		}
		Scanner scan = new Scanner(System.in);
		while(scan.hasNextLine()){
			System.out.println(writeImg(scan.nextLine()));
		}
	}

}
