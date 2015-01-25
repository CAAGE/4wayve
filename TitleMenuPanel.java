import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
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

public class TitleMenuPanel extends JComponent implements KeyListener, MouseListener, Runnable{
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
	protected static final float backgroundRate = 0.00025f;
	
	/**
	 * The background image.
	 */
	protected BufferedImage background;
	
	/**
	 * Menu names / icons and cooresponding sizes
	 */
	protected BufferedImage[] menuIcon;
	protected static final float WIDICON = 0.07f;
	protected static final float HEIICON = 0.7f;
	protected static final float XLOCICON = 0.25f;
	protected static final float YLOCICON = 0.3f;
	protected static final float XICONOFFSET = 0.01f;
	
	/**
	 * The image for the logo / title and cooresponding location params
	 */
	protected BufferedImage title;
	protected static final float WIDTITLE = 0.5f;
	protected static final float HEITITLE = 0.25f;
	protected static final float XLOCTITLE = 0f;
	protected static final float YLOCTITLE = -0.01f;
	
	/**
	 * Which keys are currently pressed.
	 */
	protected boolean[] curPressed;
	
	/**
	 * Title menu overlay for mouse interaction
	 */
	protected MenuOverlay titleMenuOver;
	
	/**
	 * Constructor, set up buffered images and start-up offsets
	 * @param backgroundImage The image displayed in the background
	 * @param currOffset The current y offset of the background
	 */
	public TitleMenuPanel(BufferedImage backgroundImage, float curOffset) throws IOException{
    title = ImageIO.read(ClassLoader.getSystemResource("images/logo.png"));
    
    //Load icons
    menuIcon = new BufferedImage[4];
    menuIcon[0] = ImageIO.read(ClassLoader.getSystemResource("images/createMenuIcon.png"));
    menuIcon[1] = ImageIO.read(ClassLoader.getSystemResource("images/playMenuIcon.png"));
    menuIcon[2] = ImageIO.read(ClassLoader.getSystemResource("images/optionsMenuIcon.png"));
    menuIcon[3] = ImageIO.read(ClassLoader.getSystemResource("images/exitMenuIcon.png"));
    
   setDoubleBuffered(false);
    background = backgroundImage;
    backgroundOffset = curOffset;
    myLock = new ReentrantLock();
    frameNanos = 16000000;
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

	/**
   * Send this ID to the parent to trigger a state change
   * @param e The mouse event that occured
   */
  public void mouseClicked(MouseEvent e){}
  
  /**
   * Ignore mouse pressed
   * @param e The mouse event that occured
   */
  public void mousePressed(MouseEvent e){titleMenuOver.setTrans(true, e.getX(), getWidth());}
  
  /**
   * Ignore mouse release
   * @param e The mouse event that occured
   */
  public void mouseReleased(MouseEvent e){titleMenuOver.setTrans(false, e.getX(), getWidth());}
  
  /**
   * Add highlight when mouse enters
   * @param e The mouse event that occured
   */
  public void mouseEntered(MouseEvent e){}
  
  /**
   * Remove highlight when mouse leaves
   * @param e The mouse event that occured
   */
  public void mouseExited(MouseEvent e){}

  /**
   * Draw the field
   * @param g The field to be drawn
   */
  protected void paintComponent(Graphics g){
    myLock.lock(); try{
      if(buffer == null || buffer.getWidth()!=getWidth() || buffer.getHeight()!=getHeight()){
				buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(getWidth(),getHeight(),Transparency.TRANSLUCENT);
			}
			Graphics2D bufDraw = (Graphics2D)(buffer.getGraphics());
			float scale = (getWidth() * 1.0f) / background.getWidth();
			float scrheight = scale * background.getHeight(); 
			//re-adjust current y-coord on background
			float cury = backgroundOffset - scrheight; 
			while(cury < getHeight()){
				bufDraw.drawImage(background, 0, (int)cury, getWidth(), (int)scrheight, null);
				cury += scrheight;
			}
			//Place Icons onto the menu
			for(int i = 0; i < menuIcon.length; ++i){
        int xloc = (int)(((XLOCICON * i) + XICONOFFSET) * getWidth());
        int yloc = (int)(YLOCICON * getHeight());
        int wid = (int)(WIDICON * getWidth());
        int hig = (int)(HEIICON * getHeight());
        bufDraw.drawImage(menuIcon[i], xloc, yloc, wid, hig, null);
			}
			
			//Place the title using a similar process as the icons
			int xloc = (int)(XLOCTITLE * getWidth());
			int yloc = (int)(YLOCTITLE * getHeight());
			int wid = (int)(WIDTITLE * getWidth());
			int hig = (int)(HEITITLE * getHeight());
			bufDraw.drawImage(title, xloc, yloc, wid, hig, null);
			titleMenuOver.paintComponent(bufDraw, getWidth(), getHeight());
			
			bufDraw.dispose();
			
			//Draw the buffer to the screen
			Graphics2D g2 = (Graphics2D)g;
			g2.drawImage(buffer,0,0,null);
			//make changes visible
			Toolkit.getDefaultToolkit().sync();
			g2.dispose();
    }finally{myLock.unlock();}
  }

  public static void main(String args[]) throws IOException{
    JFrame mainframe = new JFrame("4Way(ve)");
    mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainframe.setSize(640,480);
    TitleMenuPanel TitlePanel = new TitleMenuPanel(ImageIO.read(ClassLoader.getSystemResource("images/background.png")), 0f);
    TitlePanel.activate();
    mainframe.add(TitlePanel);
    mainframe.addKeyListener(TitlePanel);
    mainframe.addMouseListener(TitlePanel);
    mainframe.setVisible(true);
    Thread toRun = new Thread(TitlePanel);
    toRun.start();
  }
}