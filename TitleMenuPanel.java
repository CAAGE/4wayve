import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JComponent;

/**
  * This will run the first title menu of the game at runtime.
  */

public class TitleMenuPanel extends JComponent implements KeyListener, Runnable{
  
  /**
   * The float location of the title
   */
  protected static final float TITLEXOFFSET = 0.01f;
  protected static final float TITLEYOFFSET = 0.01f;
  
	/**
	 * The x location of the blocks in the lanes relative to lane center.
	 */
	protected static final float XOFFSET = -0.025f;
	
	/**
	 * The y location of the keys.
	 */
	protected static final float KEYYLOC = 0.45f;
	
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
	 * The second buffer of this panel.
	 */
	protected BufferedImage buffer;
	
	/**
	 * The length of each frame in nanoseconds.
	 */
	protected long frameNanos;
	
	/**
	 * The lock for this panel.
	 */
	protected Lock myLock; 
	
	/**
	 * Whether this panel is currently active.
	 */
	protected boolean active = false;
	
	/**
	 * The current offset for the background.
	 */
	protected float backgroundOffset;
	
	/**
	 * The rate at which the background scrolls.
	 */
	protected float backgroundRate = 0.00025f;
	
	/**
	 * The background image.
	 */
	protected BufferedImage background;
	
	/**
	 * The image for an inactive key.
	 */
	protected BufferedImage inactiveKey;
	
	/**
	 * The image for a pressed key.
	 */
	protected BufferedImage activeKey;
	
	/**
	 * Which keys are currently pressed.
	 */
	protected boolean[] curPressed;
	
	/**
	 * Title menu overlay for mouse interaction
	 */
	protected MenuOverlay titleMenuOver;
	
	public TitleMenuPanel(BufferedImage backgroundImage) throws IOException{
    inactiveKey = ImageIO.read(ClassLoader.getSystemResource("images/keyinactive.png"));
    activeKey = ImageIO.read(ClassLoader.getSystemResource("images/keyactive.png"));
 //setDoubleBuffered(false);
    background = backgroundImage;
    backgroundOffset = 0;
    myLock = new ReentrantLock();
    frameNanos = 16000000;
    curPressed = new boolean[relLocs.length];
    titleMenuOver = new MenuOverlay();
	}
  
  /**
   * Enable scrolling of background and frame repaints
   */
  public void run(){
    while(true){
      long frameStartTime = System.nanoTime();
      if(active){
        myLock.lock(); try{
          backgroundOffset += (backgroundRate * getWidth());
          float scrheight = (background.getHeight() * getWidth() * 1.0f) / background.getWidth();
          if(backgroundOffset > scrheight){
            backgroundOffset -= scrheight;
          }
        }finally{myLock.unlock();}
        repaint();
      }
      long frameEndTime = System.nanoTime();
			long sleepTime = frameNanos - (frameEndTime - frameStartTime);
			if(sleepTime > 0){
				try{
					Thread.sleep(sleepTime / 1000000);
				} catch(InterruptedException e){}
			} 
    }
  }
  
  /**
   * Activate the title page
   */
  public void activate(){
    active = true;
  }
  
  /**
	 * This will handle keys being pressed.
	 * @param e The key being pressed.
	 */
	public void keyPressed(KeyEvent e){
		switch (e.getKeyCode()) {
		case KeyEvent.VK_A:
			curPressed[0] = true;
			break;
		case KeyEvent.VK_S:
			curPressed[1] = true;
			break;
		case KeyEvent.VK_D:
			curPressed[2] = true;
			break;
		case KeyEvent.VK_J:
			curPressed[3] = true;
			break;
		case KeyEvent.VK_K:
			curPressed[4] = true;
			break;
		case KeyEvent.VK_L:
			curPressed[5] = true;
			break;
		case KeyEvent.VK_LEFT:
			curPressed[6] = true;
			break;
		case KeyEvent.VK_DOWN:
			curPressed[7] = true;
			break;
		case KeyEvent.VK_RIGHT:
			curPressed[8] = true;
			break;
		case KeyEvent.VK_1:
			curPressed[9] = true;
			break;
		case KeyEvent.VK_2:
			curPressed[10] = true;
			break;
		case KeyEvent.VK_3:
			curPressed[11] = true;
			break;
		case KeyEvent.VK_NUMPAD1:
			curPressed[9] = true;
			break;
		case KeyEvent.VK_NUMPAD2:
			curPressed[10] = true;
			break;
		case KeyEvent.VK_NUMPAD3:
			curPressed[11] = true;
			break;
		default:
			//ignore
			break;
		}
	}
	
	/**
	 * This will handle keys being let go.
	 * @param e The key being released.
	 */
	public void keyReleased(KeyEvent e){
		switch (e.getKeyCode()) {
		case KeyEvent.VK_A:
			curPressed[0] = false;
			break;
		case KeyEvent.VK_S:
			curPressed[1] = false;
			break;
		case KeyEvent.VK_D:
			curPressed[2] = false;
			break;
		case KeyEvent.VK_J:
			curPressed[3] = false;
			break;
		case KeyEvent.VK_K:
			curPressed[4] = false;
			break;
		case KeyEvent.VK_L:
			curPressed[5] = false;
			break;
		case KeyEvent.VK_LEFT:
			curPressed[6] = false;
			break;
		case KeyEvent.VK_DOWN:
			curPressed[7] = false;
			break;
		case KeyEvent.VK_RIGHT:
			curPressed[8] = false;
			break;
		case KeyEvent.VK_1:
			curPressed[9] = false;
			break;
		case KeyEvent.VK_2:
			curPressed[10] = false;
			break;
		case KeyEvent.VK_3:
			curPressed[11] = false;
			break;
		case KeyEvent.VK_NUMPAD1:
			curPressed[9] = false;
			break;
		case KeyEvent.VK_NUMPAD2:
			curPressed[10] = false;
			break;
		case KeyEvent.VK_NUMPAD3:
			curPressed[11] = false;
			break;
		default:
			//ignore
			break;
		}
	}
	
	/**
	 * This ignores typing events.
	 * @param e The key being typed.
	 */
	public void keyTyped(KeyEvent e){}

  protected void paintComponent(Graphics g){
    myLock.lock(); try{
      if(buffer == null || buffer.getWidth()!=getWidth() || buffer.getHeight()!=getHeight()){
				buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(getWidth(),getHeight(),Transparency.TRANSLUCENT);
			}
			Graphics2D bufDraw = (Graphics2D)(buffer.getGraphics());
			float scale = (getWidth() * 1.0f) / background.getWidth();
			float scrheight = scale * background.getHeight();
			float cury = backgroundOffset - scrheight;
			while(cury < getHeight()){
				bufDraw.drawImage(background, 0, (int)cury, getWidth(), (int)scrheight, null);
				cury += scrheight;
			}
			for(int i = 0; i<relLocs.length; i++){
        int xloc = (int)((relLocs[i] + XOFFSET) * getWidth());
        int yloc = (int)(KEYYLOC * getHeight());
        int wid = (int)(KEYWID * getWidth());
        int hig = (int)(KEYHIG * getHeight());
        bufDraw.drawImage(curPressed[i] ? activeKey : inactiveKey, xloc, yloc, wid, hig, null);
			}
			
			
titleMenuOver.repaint();
			
			
			bufDraw.dispose();
			
			Graphics2D g2 = (Graphics2D)g;
			g2.drawImage(buffer,0,0,null);
			//make changes visible
			Toolkit.getDefaultToolkit().sync();
			g2.dispose();
    }finally{myLock.unlock();}
  }

  public static void main(String args[]) throws IOException{
    JFrame mainframe = new JFrame();
    mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainframe.setSize(640,480);
    TitleMenuPanel TitlePanel = new TitleMenuPanel(ImageIO.read(ClassLoader.getSystemResource("images/background.png")));
    TitlePanel.activate();
    mainframe.add(TitlePanel);
    mainframe.addKeyListener(TitlePanel);
    mainframe.setVisible(true);
    Thread toRun = new Thread(TitlePanel);
    toRun.start();
  }
}