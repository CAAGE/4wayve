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
 * This will run the creation mode of the game.
 */
public class CreationModePanel extends JComponent implements Runnable{
	
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
	protected float backgroundRate;
	
	/**
	 * The background image.
	 */
	protected BufferedImage background;
	
	/**
	 * This creates a cration mode panel.
	 * @param backgroundImage The image to display as a background.
	 * @param backScrollRate The rate at which the background should scroll.
	 */
	public CreationModePanel(BufferedImage backgroundImage, float backScrollRate){
		setDoubleBuffered(true);
		background = backgroundImage;
		backgroundOffset = 0;
		backgroundRate = backScrollRate;
		myLock = new ReentrantLock();
	}
	
	/**
	 * While active, this will scroll the entities.
	 */
	public void run(){
		while(true){
			if(active){
				myLock.lock(); try{
					backgroundOffset += backgroundRate;
					float scrheight = (background.getHeight() * getWidth() * 1.0f) / background.getWidth();
					if(backgroundOffset > scrheight){
						backgroundOffset -= scrheight;
					}
				}finally{myLock.unlock();}
				Graphics toDraw = this.getGraphics();
				repaint();
			}
			try{
				Thread.sleep(15);
			} catch(InterruptedException e){}
		}
	}
	
	/**
	 * This will make this panel active (the thread will do stuff).
	 */
	public void activate(){
		active = true;
	}
	
	/**
	 * This will paint the creation mode screen.
	 * @param g The drawing surface.
	 */
	protected void paintComponent(Graphics g){
		myLock.lock(); try{
			Graphics2D g2 = (Graphics2D)g;
			float scale = (getWidth() * 1.0f) / background.getWidth();
			float scrheight = scale * background.getHeight();
			float cury = backgroundOffset - scrheight;
			while(cury < getHeight()){
				g2.drawImage(background, 0, (int)cury, getWidth(), (int)scrheight, null);
				cury += scrheight;
			}
		}finally{myLock.unlock();}
	}
	
	/**
	 * This will test the creation mode.
	 * @param args Ignored
	 */
	public static void main(String[] args) throws IOException{
		JFrame mainframe = new JFrame();
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setSize(640,480);
		CreationModePanel toTest = new CreationModePanel(ImageIO.read(ClassLoader.getSystemResource("images/background.png")), 1.0f);
		toTest.activate();
		mainframe.add(toTest);
		mainframe.setVisible(true);
		Thread toRun = new Thread(toTest);
		toRun.start();
	}
}