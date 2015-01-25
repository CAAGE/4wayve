import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * This displays a score for the player.
 */
public class Score{
	
	/**
	 * The y location of the score.
	 */
	protected static final float RELYLOC = 0.925f;
	
	/**
	 * The height of the score.
	 */
	protected static final float RELHEIGHT = 0.05f;
	
	/**
	 * The width of each digit.
	 */
	protected static final float DIGITWIDTH = 0.05f;
	
	/**
	 * The number of digits to display.
	 */
	protected static final int NUMDIGITS = 4;
	
	/**
	 * The rightmost position this will draw on.
	 */
	protected float rightxposition;
	
	/**
	 * The current score.
	 */
	protected int score;
	
	/**
	 * The digits to draw the score with.
	 */
	protected BufferedImage[] digits;
	
	/**
	 * This sets up a score at the given position.
	 * @param rxloc The rightmost position this will draw on.
	 */
	public Score(float rxloc) throws IOException{
		this.rightxposition = rxloc;
		score = 0;
		digits = new BufferedImage[]{ImageIO.read(ClassLoader.getSystemResource("letters/0.png")),
						ImageIO.read(ClassLoader.getSystemResource("letters/1.png")),
						ImageIO.read(ClassLoader.getSystemResource("letters/2.png")),
						ImageIO.read(ClassLoader.getSystemResource("letters/3.png")),
						ImageIO.read(ClassLoader.getSystemResource("letters/4.png")),
						ImageIO.read(ClassLoader.getSystemResource("letters/5.png")),
						ImageIO.read(ClassLoader.getSystemResource("letters/6.png")),
						ImageIO.read(ClassLoader.getSystemResource("letters/7.png")),
						ImageIO.read(ClassLoader.getSystemResource("letters/8.png")),
						ImageIO.read(ClassLoader.getSystemResource("letters/9.png"))};
	}
	
	/**
	 * This sets the current score for this player.
	 * @param curScore The current score.
	 */
	public void setScore(int curScore){
		score = curScore;
	}
	
	/**
	 * This will paint the score.
	 * @param g The drawing surface.
	 * @param width The width of the drawable surface.
	 * @param height The height of the drawable surface.
	 */
	public void paintComponent(Graphics g, int width, int height){
		Graphics2D g2 = (Graphics2D)g;
		int digitWid = (int)(width * DIGITWIDTH);
		int cursorLoc = (int)(width * rightxposition);
		int yloc = (int)(height * RELYLOC);
		int yheight = (int)(height * RELHEIGHT);
		int divisor = 1;
		for(int i = 0; i<NUMDIGITS; i++){
			int digit = (score / divisor) % 10;
			divisor = divisor * 10;
			cursorLoc -= digitWid;
			g2.drawImage(digits[digit], cursorLoc, yloc, digitWid, yheight, null);
		}
	}
}