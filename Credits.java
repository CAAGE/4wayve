import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.GraphicsEnvironment;
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
  * This will run the credits when that option is selected.
  */

public class Credits extends JComponent implements MouseListener, Runnable{
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
	protected static final float backgroundRate = 0.0005f;
	
	/**
	 * The background image.
	 */
	protected BufferedImage background;
	
	
	/**
	 * The image for the group name / title and corresponding location
	 */
	protected BufferedImage title;
	protected static final float WIDTITLE = 0.5f;
	protected static final float HEITITLE = 0.25f;
	protected static final float XLOCTITLE = 0f;
	protected static final float YLOCTITLE = -0.01f;
		
	/**
	 * Title menu overlay for mouse interaction
	 */
	protected MenuOverlay titleMenuOver;
	
	/**
	 * Constructor, set up buffered images and start-up offsets
	 * @param backgroundImage The image displayed in the background
	 * @param currOffset The current y offset of the background
	 */
	public Credits(BufferedImage backgroundImage, float curOffset) throws IOException{
    title = ImageIO.read(ClassLoader.getSystemResource("images/CAAGE.png"));
    
    
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
  public void mouseReleased(MouseEvent e){
    titleMenuOver.setTrans(false, e.getX(), getWidth());
    TitleMenuPanel.showMenu();
  }
  
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
    Credits TitlePanel = new Credits(ImageIO.read(ClassLoader.getSystemResource("images/credits.png")), -300f);
    TitlePanel.activate();
    mainframe.add(TitlePanel);
    mainframe.addMouseListener(TitlePanel);
    mainframe.setVisible(true);
    Thread toRun = new Thread(TitlePanel);
    toRun.start();
  }
}