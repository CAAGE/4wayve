import javax.sound.midi.InvalidMidiDataException;

import javax.sound.midi.MidiUnavailableException;
import java.util.List;
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
 * This will run the creation mode of the game.
 */
public class CreationModePanel extends JComponent implements Runnable, KeyListener{
	
	/**
	 * The number of frames remaining.
	 */
	protected long framesRemaining;
	
	/**
	 * The number of frames between metronome clicks.
	 */
	protected long metronomeFrames;
	
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
	protected float backgroundRate;
	
	/**
	 * The background image.
	 */
	protected BufferedImage background;
	
	/**
	 * The overlay image.
	 */
	protected BufferedImage overlay;
	
	/**
	 * Which players are currently playing.
	 */
	protected boolean[] playing;
	
	/**
	 * The key display.
	 */
	protected Keyboard curKeys;
	
	/**
	 * The block slides.
	 */
	protected CreationBlockChannel[] blockLanes;
	
	protected List<List<Long>> startFrames;
	
	protected List<List<Long>> endFrames;
	
	/**
	 * This creates a cration mode panel.
	 * @param backgroundImage The image to display as a background.
	 * @param backScrollRate The rate at which the background should scroll.
	 * @throws IOException If there is a problem loading an image.
	 */
	public CreationModePanel(BufferedImage backgroundImage, float backScrollRate) throws IOException{
		overlay = ImageIO.read(ClassLoader.getSystemResource("images/overlay.png"));
		curKeys = new Keyboard(0.1f);
		setDoubleBuffered(false);
		background = backgroundImage;
		backgroundOffset = 0;
		backgroundRate = backScrollRate;
		myLock = new ReentrantLock();
		frameNanos = 16000000;
		playing = new boolean[4];
		blockLanes = new CreationBlockChannel[12];
		for(int i = 0; i<blockLanes.length; i++){
			//blockLanes[i] = new CreationBlockChannel(List<Long> startTimeList, List<Long> endTimeList, float xLocation, float scrollRate, int color);
		}
	}
	
	/**
	 * While active, this will scroll the entities.
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
				Graphics toDraw = this.getGraphics();
				paintComponent(getGraphics());
				framesRemaining--;
				if(framesRemaining<=0){
					active = false;
				}
				else if(framesRemaining % metronomeFrames == 0){
					try{
						AudioEventPlayer.endNote(0,0);
						AudioEventPlayer.startNote(0,0);
					} catch(MidiUnavailableException|InvalidMidiDataException e){}
				}
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
	 * This will make this panel active (the thread will do stuff).
	 */
	public void activate(){
		framesRemaining = 60*60;
		active = true;
	}
	
	/**
	 * This will set whether a player is active.
	 * @param player The player to set (0-4).
	 * @param active Whether the player should be active.
	 */
	public void setPlayerActive(int player, boolean active){
		playing[player] = active;
		if(active){
			curKeys.setKeyState(3*player, Keyboard.INACTIVE);
			curKeys.setKeyState(3*player+1, Keyboard.INACTIVE);
			curKeys.setKeyState(3*player+2, Keyboard.INACTIVE);
		}
		else{
			curKeys.setKeyState(3*player, Keyboard.INVISIBLE);
			curKeys.setKeyState(3*player+1, Keyboard.INVISIBLE);
			curKeys.setKeyState(3*player+2, Keyboard.INVISIBLE);
		}
	}
	
	/**
	 * This sets the amount of time between metronome clicks.
	 * @param frameDelay The number of frames between clicks.
	 */
	public void setMetronomeFrames(long frameDelay){
		metronomeFrames = frameDelay;
	}
	
	/**
	 * This will handle keys being pressed.
	 * @param e The key being pressed.
	 */
	public void keyPressed(KeyEvent e){
		myLock.lock(); try{
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				curKeys.setKeyState(0, playing[0] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_S:
				curKeys.setKeyState(1, playing[0] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_D:
				curKeys.setKeyState(2, playing[0] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_J:
				curKeys.setKeyState(3, playing[1] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_K:
				curKeys.setKeyState(4, playing[1] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_L:
				curKeys.setKeyState(5, playing[1] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_LEFT:
				curKeys.setKeyState(6, playing[2] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_DOWN:
				curKeys.setKeyState(7, playing[2] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_RIGHT:
				curKeys.setKeyState(8, playing[2] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_1:
				curKeys.setKeyState(9, playing[3] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_2:
				curKeys.setKeyState(10, playing[3] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_3:
				curKeys.setKeyState(11, playing[3] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_NUMPAD1:
				curKeys.setKeyState(9, playing[3] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_NUMPAD2:
				curKeys.setKeyState(10, playing[3] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_NUMPAD3:
				curKeys.setKeyState(11, playing[3] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
				break;
			default:
				//ignore
				break;
			}
		}finally{myLock.unlock();}
	}
	
	/**
	 * This will handle keys being let go.
	 * @param e The key being released.
	 */
	public void keyReleased(KeyEvent e){
		myLock.lock(); try{
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				curKeys.setKeyState(0, playing[0] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_S:
				curKeys.setKeyState(1, playing[0] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_D:
				curKeys.setKeyState(2, playing[0] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_J:
				curKeys.setKeyState(3, playing[1] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_K:
				curKeys.setKeyState(4, playing[1] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_L:
				curKeys.setKeyState(5, playing[1] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_LEFT:
				curKeys.setKeyState(6, playing[2] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_DOWN:
				curKeys.setKeyState(7, playing[2] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_RIGHT:
				curKeys.setKeyState(8, playing[2] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_1:
				curKeys.setKeyState(9, playing[3] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_2:
				curKeys.setKeyState(10, playing[3] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_3:
				curKeys.setKeyState(11, playing[3] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_NUMPAD1:
				curKeys.setKeyState(9, playing[3] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_NUMPAD2:
				curKeys.setKeyState(10, playing[3] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			case KeyEvent.VK_NUMPAD3:
				curKeys.setKeyState(11, playing[3] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
				break;
			default:
				//ignore
				break;
			}
		}finally{myLock.unlock();}
	}
	
	/**
	 * This ignores typing events.
	 * @param e The key being typed.
	 */
	public void keyTyped(KeyEvent e){}
	
	/**
	 * This will paint the creation mode screen.
	 * @param g The drawing surface.
	 */
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
			bufDraw.drawImage(overlay, 0, 0, getWidth(), getHeight(), null);
			curKeys.paintComponent(bufDraw, getWidth(), getHeight());
			bufDraw.dispose();
			
			Graphics2D g2 = (Graphics2D)g;
			g2.drawImage(buffer,0,0,null);
			//make changes visible
			Toolkit.getDefaultToolkit().sync();
			g2.dispose();
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
		CreationModePanel toTest = new CreationModePanel(ImageIO.read(ClassLoader.getSystemResource("images/background.png")), 0.0025f);
		toTest.setMetronomeFrames(60);
		toTest.setPlayerActive(0, true);
		toTest.setPlayerActive(1, true);
		toTest.setPlayerActive(2, true);
		toTest.setPlayerActive(3, true);
		toTest.activate();
		mainframe.add(toTest);
		mainframe.addKeyListener(toTest);
		mainframe.setVisible(true);
		Thread toRun = new Thread(toTest);
		toRun.start();
	}
}