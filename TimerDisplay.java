import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * This will display a bar for the timer.
 */
public class TimerDisplay{
	
	/**
	 * The height of the timer.
	 */
	protected static final float RELHEIGHT = 20.0f / 1080;
	
	/**
	 * The left bound of the bar.
	 */
	protected static final float LEFTBOUND = 17.0f / 1920;
	
	/**
	 * The right bound of the bar.
	 */
	protected static final float RIGHTBOUND = 1903.0f / 1920;
	
	/**
	 * The relative amount of time used.
	 */
	protected float timeRat;
	
	/**
	 * The image used as the back of the timer.
	 */
	protected BufferedImage background;
	
	/**
	 * The image used for the bar.
	 */
	protected BufferedImage barImg;
	
	/**
	 * The image used at the end of the bar.
	 */
	protected BufferedImage endImg;
	
	/**
	 * This will load the images for the timer.
	 * @throws IOException If there is a problem loading an image.
	 */
	public TimerDisplay() throws IOException{
		timeRat = 0;
		background = ImageIO.read(ClassLoader.getSystemResource("images/timebar.png"));
		barImg = ImageIO.read(ClassLoader.getSystemResource("images/timefill.png"));
		endImg = ImageIO.read(ClassLoader.getSystemResource("images/timecursor.png"));
	}
	
	/**
	 * This sets the amount of time used.
	 * @param rat The ratio of time used.
	 */
	public void setTimeRatio(float rat){
		timeRat = rat;
	}
	
	/**
	 * This will paint the keyboard.
	 * @param g The drawing surface.
	 * @param width The width of the drawable surface.
	 * @param height The height of the drawable surface.
	 */
	public void paintComponent(Graphics g, int width, int height){
		int imghei = (int)(RELHEIGHT * height);
		int imgwid = (int)(RELHEIGHT * width);
		int leftSpot = (int)(width * LEFTBOUND);
		int rightSpot = (int)(width*(timeRat*RIGHTBOUND + (1-timeRat)*LEFTBOUND));
		Graphics2D g2 = (Graphics2D)g;
		//print the background
		g2.drawImage(background, 0, 0, width, imghei, null);
		//print the bar
		g2.drawImage(barImg, leftSpot, 0, (rightSpot-leftSpot), imghei, null);
		//print the terminal point
		g2.drawImage(endImg, rightSpot - (imgwid/2), 0, imgwid, imghei, null);
	}
}