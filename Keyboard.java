import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

/**
 * This will draw a keyboard on the screen.
 */
public class Keyboard{
	
	/**
	 * The index of an invisible key.
	 */
	public static final int INVISIBLE = -1;
	
	/**
	 * The index of an unpressed key.
	 */
	public static final int INACTIVE = 0;
	
	/**
	 * The index of a pressed key.
	 */
	public static final int ACTIVE = 1;
	
	/**
	 * The index of a key pressed at the wrong time.
	 */
	public static final int WRONG = 2;
	
	/**
	 * The x location of the blocks in the lanes relative to lane center.
	 */
	protected static final float XOFFSET = -0.025f;
	
	/**
	 * The width of the keys.
	 */
	protected static final float KEYWID = 0.05f;
	
	/**
	 * The height of the keys.
	 */
	protected static final float KEYHIG = 0.101f;
	
	/**
	 * The relative x locations of the lanes.
	 */
	protected static final float[] relLocs = new float[]{1.0f/16, 2.0f/16, 3.0f/16, 5.0f/16, 6.0f/16, 7.0f/16, 9.0f/16, 10.0f/16, 11.0f/16, 13.0f/16, 14.0f/16, 15.0f/16};
	
	/**
	 * The y location of the keys.
	 */
	protected float keyyloc = 0.1f;
	
	/**
	 * Which keys are currently pressed.
	 */
	protected int[] curPressed;
	
	/**
	 * The possible key images.
	 */
	protected BufferedImage[] keys;
	
	/**
	 * This will load the images for the keys.
	 * @param yloc The relative y location to draw the keys at.
	 */
	public Keyboard(float yloc) throws IOException{
		keyyloc = yloc;
		curPressed = new int[12];
		keys = new BufferedImage[]{ImageIO.read(ClassLoader.getSystemResource("images/keyinactive.png")),
				ImageIO.read(ClassLoader.getSystemResource("images/keyactive.png")),
				ImageIO.read(ClassLoader.getSystemResource("images/keywrong.png"))};
	}
	
	/**
	 * This will set the current state of a key.
	 * @param key The key to set the state of.
	 * @param curState The state to set it to (see the constants in this class).
	 */
	public void setKeyState(int key, int curState){
		curPressed[key] = curState;
	}
	
	/**
	 * This will paint the keyboard.
	 * @param g The drawing surface.
	 */
	public void paintComponent(Graphics g, int width, int height){
		Graphics2D g2 = (Graphics2D)g;
		for(int i = 0; i<relLocs.length; i++){
			if(curPressed[i] >= 0){
				int xloc = (int)((relLocs[i] + XOFFSET) * width);
				int yloc = (int)(keyyloc * height);
				int wid = (int)(KEYWID * width);
				int hig = (int)(KEYHIG * height);
				g2.drawImage(keys[curPressed[i]], xloc, yloc, wid, hig, null);
			}
		}
	}
}