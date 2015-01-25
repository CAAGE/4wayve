import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.GraphicsEnvironment;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.File;
import java.awt.event.KeyListener;
import javax.swing.JComponent;
import java.io.IOException;


/**
 * This will run the playback mode of the game.
 */
public class PlaybackModePanel extends JComponent implements Runnable, KeyListener{
	
	/**
	 * The current frame.
	 */
	protected long curFrame;
	
	/**
	 * The last frame to play.
	 */
	protected long lastFrame;
	
	/**
	 * The second buffer of this panel.
	 */
	protected BufferedImage buffer;
	
	/**
	 * The length of each frame in nanoseconds.
	 */
	protected long frameNanos;
	
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
	 * The current state of the keys.
	 */
	protected boolean[] curKeyState;
	
	/**
	 * The lock for this panel.
	 */
	protected Lock myLock;
	
	/**
	 * The start frames of the current song.
	 */
	protected List<List<Long>> startFrames;
	
	/**
	 * The end frames of the current song.
	 */
	protected List<List<Long>> endFrames;
	
	/**
	 * The instruments each player is playing.
	 */
	protected int[] instruments;
	
	/**
	 * The timer display.
	 */
	protected TimerDisplay curTimeDisp;
	
	/**
	 * This creates a cration mode panel.
	 * @param backgroundImage The image to display as a background.
	 * @param backScrollRate The rate at which the background should scroll.
	 * @param blockScrollRate The rate at which the stones fall.
	 * @throws IOException If there is a problem loading an image.
	 */
	public PlaybackModePanel(BufferedImage backgroundImage, float backScrollRate, float blockScrollRate) throws IOException{
		overlay = ImageIO.read(ClassLoader.getSystemResource("images/overlayFlip.png"));
		curKeys = new Keyboard(0.801f);
		setDoubleBuffered(false);
		background = backgroundImage;
		backgroundOffset = 0;
		backgroundRate = backScrollRate;
		myLock = new ReentrantLock();
		frameNanos = 16000000;
		playing = new boolean[4];
		curKeyState = new boolean[12];
		curTimeDisp = new TimerDisplay();
	}
	
	/**
	 * This will set whether a player is active.
	 * @param player The player to set (0-4).
	 * @param active Whether the player should be active.
	 */
	public void setPlayerActive(int player, boolean active){
		playing[player] = active;
		curKeyState[3*player] = false;
		curKeyState[3*player+1] = false;
		curKeyState[3*player+2] = false;
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
	 * This will start playing a song.
	 * @param toLoad The song to play.
	 * @throws IOException If there is a problem reading the song (if this is thrown, no song will play).
	 */
	@SuppressWarnings("unchecked")
	public void playSong(File toLoad) throws IOException{
		myLock.lock(); try{
			Object[] trackData = AudioEventPlayer.readEventFile(toLoad);
			instruments = (int[])(trackData[0]);
			startFrames = (List<List<Long>>)(trackData[1]);
			endFrames = (List<List<Long>>)(trackData[2]);
			curFrame = -60;
			lastFrame = 0;
			for(List<Long> cur : endFrames){
				if(cur.size()>0 && cur.get(cur.size()-1) > lastFrame){
					lastFrame = cur.get(cur.size()-1);
				}
			}
			lastFrame += 60;
			active = true;
		}finally{myLock.unlock();}
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
					if(curFrame>0){
						curTimeDisp.setTimeRatio((1.0f*curFrame)/lastFrame);
					}
					else{
						curTimeDisp.setTimeRatio(0);
					}
				} finally{myLock.unlock();}
				Graphics toDraw = this.getGraphics();
				paintComponent(getGraphics());
				curFrame++;
				if(curFrame == lastFrame){
					active = false;
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
	 * This will start a key press event.
	 * @param lane The lane that should start playing.
	 */
	protected void startEvent(int lane){
		
	}
	
	/**
	 * This will end a key press event.
	 * @param lane The lane that should stop playing.
	 */
	protected void endEvent(int lane){
		
	}
	
	/**
	 * This will handle keys being pressed.
	 * @param e The key being pressed.
	 */
	public void keyPressed(KeyEvent e){
		myLock.lock(); try{
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				if(!curKeyState[0]){
					curKeys.setKeyState(0, playing[0] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(0);
				}
				curKeyState[0] = true;
				break;
			case KeyEvent.VK_S:
				if(!curKeyState[1]){
					curKeys.setKeyState(1, playing[0] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(1);
				}
				curKeyState[1] = true;
				break;
			case KeyEvent.VK_D:
				if(!curKeyState[2]){
					curKeys.setKeyState(2, playing[0] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(2);
				}
				curKeyState[2] = true;
				break;
			case KeyEvent.VK_J:
				if(!curKeyState[3]){
					curKeys.setKeyState(3, playing[1] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(3);
				}
				curKeyState[3] = true;
				break;
			case KeyEvent.VK_K:
				if(!curKeyState[4]){
					curKeys.setKeyState(4, playing[1] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(4);
				}
				curKeyState[4] = true;
				break;
			case KeyEvent.VK_L:
				if(!curKeyState[5]){
					curKeys.setKeyState(5, playing[1] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(5);
				}
				curKeyState[5] = true;
				break;
			case KeyEvent.VK_LEFT:
				if(!curKeyState[6]){
					curKeys.setKeyState(6, playing[2] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(6);
				}
				curKeyState[6] = true;
				break;
			case KeyEvent.VK_DOWN:
				if(!curKeyState[7]){
					curKeys.setKeyState(7, playing[2] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(7);
				}
				curKeyState[7] = true;
				break;
			case KeyEvent.VK_RIGHT:
				if(!curKeyState[8]){
					curKeys.setKeyState(8, playing[2] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(8);
				}
				curKeyState[8] = true;
				break;
			case KeyEvent.VK_1:
				if(!curKeyState[9]){
					curKeys.setKeyState(9, playing[3] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(9);
				}
				curKeyState[9] = true;
				break;
			case KeyEvent.VK_2:
				if(!curKeyState[10]){
					curKeys.setKeyState(10, playing[3] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(10);
				}
				curKeyState[10] = true;
				break;
			case KeyEvent.VK_3:
				if(!curKeyState[11]){
					curKeys.setKeyState(11, playing[3] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(11);
				}
				curKeyState[11] = true;
				break;
			case KeyEvent.VK_NUMPAD1:
				if(!curKeyState[9]){
					curKeys.setKeyState(9, playing[3] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(9);
				}
				curKeyState[9] = true;
				break;
			case KeyEvent.VK_NUMPAD2:
				if(!curKeyState[10]){
					curKeys.setKeyState(10, playing[3] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(10);
				}
				curKeyState[10] = true;
				break;
			case KeyEvent.VK_NUMPAD3:
				if(!curKeyState[11]){
					curKeys.setKeyState(11, playing[3] ? Keyboard.ACTIVE : Keyboard.INVISIBLE);
					startEvent(11);
				}
				curKeyState[11] = true;
				break;
			default:
				//ignore
				break;
			}
		} finally{myLock.unlock();}
	}
	
	/**
	 * This will handle keys being let go.
	 * @param e The key being released.
	 */
	public void keyReleased(KeyEvent e){
		myLock.lock(); try{
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				if(curKeyState[0]){
					curKeys.setKeyState(0, playing[0] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(0);
				}
				curKeyState[0] = false;
				break;
			case KeyEvent.VK_S:
				if(curKeyState[1]){
					curKeys.setKeyState(1, playing[0] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(1);
				}
				curKeyState[1] = false;
				break;
			case KeyEvent.VK_D:
				if(curKeyState[2]){
					curKeys.setKeyState(2, playing[0] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(2);
				}
				curKeyState[2] = false;
				break;
			case KeyEvent.VK_J:
				if(curKeyState[3]){
					curKeys.setKeyState(3, playing[1] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(3);
				}
				curKeyState[3] = false;
				break;
			case KeyEvent.VK_K:
				if(curKeyState[4]){
					curKeys.setKeyState(4, playing[1] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(4);
				}
				curKeyState[4] = false;
				break;
			case KeyEvent.VK_L:
				if(curKeyState[5]){
					curKeys.setKeyState(5, playing[1] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(5);
				}
				curKeyState[5] = false;
				break;
			case KeyEvent.VK_LEFT:
				if(curKeyState[6]){
					curKeys.setKeyState(6, playing[2] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(6);
				}
				curKeyState[6] = false;
				break;
			case KeyEvent.VK_DOWN:
				if(curKeyState[7]){
					curKeys.setKeyState(7, playing[2] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(7);
				}
				curKeyState[7] = false;
				break;
			case KeyEvent.VK_RIGHT:
				if(curKeyState[8]){
					curKeys.setKeyState(8, playing[2] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(8);
				}
				curKeyState[8] = false;
				break;
			case KeyEvent.VK_1:
				if(curKeyState[9]){
					curKeys.setKeyState(9, playing[3] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(9);
				}
				curKeyState[9] = false;
				break;
			case KeyEvent.VK_2:
				if(curKeyState[10]){
					curKeys.setKeyState(10, playing[3] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(10);
				}
				curKeyState[10] = false;
				break;
			case KeyEvent.VK_3:
				if(curKeyState[11]){
					curKeys.setKeyState(11, playing[3] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(11);
				}
				curKeyState[11] = false;
				break;
			case KeyEvent.VK_NUMPAD1:
				if(curKeyState[9]){
					curKeys.setKeyState(9, playing[3] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(9);
				}
				curKeyState[9] = false;
				break;
			case KeyEvent.VK_NUMPAD2:
				if(curKeyState[10]){
					curKeys.setKeyState(10, playing[3] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(10);
				}
				curKeyState[10] = false;
				break;
			case KeyEvent.VK_NUMPAD3:
				if(curKeyState[11]){
					curKeys.setKeyState(11, playing[3] ? Keyboard.INACTIVE : Keyboard.INVISIBLE);
					endEvent(11);
				}
				curKeyState[11] = false;
				break;
			default:
				//ignore
				break;
			}
		} finally{myLock.unlock();}
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
			curTimeDisp.paintComponent(bufDraw, getWidth(), getHeight());
			bufDraw.dispose();
			
			Graphics2D g2 = (Graphics2D)g;
			g2.drawImage(buffer,0,0,null);
			//make changes visible
			Toolkit.getDefaultToolkit().sync();
			g2.dispose();
		} finally{myLock.unlock();}
	}
	
	/**
	 * This will test the playback mode.
	 * @param args Ignored
	 */
	public static void main(String[] args) throws IOException{
		JFrame mainframe = new JFrame();
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setSize(640,480);
		PlaybackModePanel toTest = new PlaybackModePanel(ImageIO.read(ClassLoader.getSystemResource("images/background.png")), 0.0025f, 0.0025f);
		toTest.setPlayerActive(0, true);
		toTest.setPlayerActive(1, true);
		toTest.setPlayerActive(2, true);
		toTest.setPlayerActive(3, true);
		toTest.playSong(new File("C:\\Users\\Benjamin\\GameSaves\\FourWayve\\testtrack.trk"));
		mainframe.add(toTest);
		mainframe.addKeyListener(toTest);
		mainframe.setVisible(true);
		Thread toRun = new Thread(toTest);
		toRun.start();
	}
}