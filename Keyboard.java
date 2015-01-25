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
	 * The images of the controls for each player.
	 */
	 protected BufferedImage[] controls;
	
	/**
	 * This will load the images for the keys and their respective controls.
	 * @param yloc The relative y location to draw the keys at.
	 */
	public Keyboard(float yloc) throws IOException{
		keyyloc = yloc;
		curPressed = new int[12];
		keys = new BufferedImage[]{ImageIO.read(ClassLoader.getSystemResource("images/keyinactive.png")),
				ImageIO.read(ClassLoader.getSystemResource("images/keyactive.png")),
				ImageIO.read(ClassLoader.getSystemResource("images/keywrong.png"))
		};
		controls = new BufferedImage[]{
				ImageIO.read(ClassLoader.getSystemResource("letters/a.png")),
				ImageIO.read(ClassLoader.getSystemResource("letters/s.png")),
				ImageIO.read(ClassLoader.getSystemResource("letters/d.png")),
				ImageIO.read(ClassLoader.getSystemResource("letters/j.png")),
				ImageIO.read(ClassLoader.getSystemResource("letters/k.png")),
				ImageIO.read(ClassLoader.getSystemResource("letters/l.png")),
				ImageIO.read(ClassLoader.getSystemResource("letters/left.png")),
				ImageIO.read(ClassLoader.getSystemResource("letters/down.png")),
				ImageIO.read(ClassLoader.getSystemResource("letters/right.png")),
				ImageIO.read(ClassLoader.getSystemResource("letters/1.png")),
				ImageIO.read(ClassLoader.getSystemResource("letters/2.png")),
				ImageIO.read(ClassLoader.getSystemResource("letters/3.png"))
		};				
		
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
	 * @param width The width of the drawable surface.
	 * @param height The height of the drawable surface.
	 */
	public void paintComponent(Graphics g, int width, int height){
		Graphics2D g2 = (Graphics2D)g;
		for(int i = 0; i<relLocs.length; i++){
			if(curPressed[i] >= 0){
				//for key display
				int xloc = (int)((relLocs[i] + XOFFSET) * width);
				int yloc = (int)(keyyloc * height);
				int wid = (int)(KEYWID * width);
				int hig = (int)(KEYHIG * height);
				g2.drawImage(keys[curPressed[i]], xloc, yloc, wid, hig, null);
				
				//for letter display
				int xloc2 = (int)((relLocs[i]+(2*XOFFSET/3))*width);
				float YOFFSET = 0.02f;
				int yloc2 = (int)((keyyloc + YOFFSET) * height);
				g2.drawImage(controls[i],xloc2,yloc2,2*wid/3,2*hig/3,null);
			}
		}
	}
}