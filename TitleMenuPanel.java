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
import javax.swing.JPanel;
import javax.swing.JComponent;
import java.awt.CardLayout;

/**
  * This will run the first title menu of the game at runtime.
  */

public class TitleMenuPanel extends JComponent implements MouseListener, Runnable{
  /**
   * Array to keep cards understandable
   */
  protected static final String[] CARD = new String[]{"Title","Create","Play","Credits"};
  protected static CardLayout layout;
  protected static JPanel cards;
  protected static TitleMenuPanel TitlePanel;
  protected static CreationModePanel CreatePanel;
  protected static PlaybackModePanel PlayPanel;
  protected static Credits CreditPanel;

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
	 * State change variable to determine card changes
	 */
	protected int currentState;
	
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
    if(active){
      currentState = titleMenuOver.setTrans(false, e.getX(), getWidth());
      if(currentState == 0){
        layout.show(cards, CARD[1]);
        CreatePanel.activate();
        active = false;
      }
      else if(currentState == 1){
        layout.show(cards, CARD[2]);
        boolean valid = true;
        try{
          PlayPanel.playSong();
        } catch(IOException exc){valid=false;}
        active = !valid;
      }
      else if(currentState == 2){
        layout.show(cards, CARD[3]);
        CreditPanel.activate();
        active = false;
      }
      else{System.exit(0);}
    }
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
  
  public static void showMenu(){
    TitlePanel.activate();
    layout.show(cards, CARD[0]);
  }

  public static void main(String args[]) throws IOException{
    JFrame mainframe = new JFrame("4Way(ve)");
    mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainframe.setSize(640,480);
    
    cards = new JPanel();
    layout = new CardLayout();
    cards.setLayout(layout);
    
    TitlePanel = new TitleMenuPanel(ImageIO.read(ClassLoader.getSystemResource("images/background.png")), 0f);
    TitlePanel.activate();
    
    CreatePanel = new CreationModePanel(ImageIO.read(ClassLoader.getSystemResource("images/background.png")), 0.0025f, 0.005f);
    CreatePanel.setMetronomeFrames(60);
    CreatePanel.setPlayerActive(0, true);
    CreatePanel.setPlayerActive(1, true);
    CreatePanel.setPlayerActive(2, true);
    CreatePanel.setPlayerActive(3, true);
    mainframe.addKeyListener(CreatePanel);
    
    PlayPanel = new PlaybackModePanel(ImageIO.read(ClassLoader.getSystemResource("images/background.png")), 0.0025f, 0.005f);
    PlayPanel.setPlayerActive(0, true);
    PlayPanel.setPlayerActive(1, true);
    PlayPanel.setPlayerActive(2, true);
    PlayPanel.setPlayerActive(3, true);
    mainframe.addKeyListener(PlayPanel);
    
    CreditPanel = new Credits(ImageIO.read(ClassLoader.getSystemResource("images/credits.png")), -300f);
    mainframe.addMouseListener(CreditPanel);
    
    cards.add(TitlePanel, CARD[0]);
    cards.add(CreatePanel, CARD[1]);
    cards.add(PlayPanel, CARD[2]);
    cards.add(CreditPanel, CARD[3]);
    
    mainframe.add(cards);
    
    mainframe.addMouseListener(TitlePanel);
    mainframe.setVisible(true);
    Thread toRun = new Thread(TitlePanel);
    toRun.start();
    Thread toRunCreate = new Thread(CreatePanel);
    toRunCreate.start();
    Thread toRunPlay = new Thread(PlayPanel);
    toRunPlay.start();
    Thread toRunCredit = new Thread(CreditPanel);
    toRunCredit.start();
  }
}