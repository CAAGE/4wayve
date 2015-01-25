import java.io.File;
import java.util.Random;
import java.util.ArrayList;
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
	 * The length of a song, in frames.
	 */
	protected static final long SONGLENGTH = 60*60;
	
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
	
	/**
	 * The frame numbers at which the lanes start playing.
	 */
	protected List<List<Long>> startFrames;
	
	/**
	 * The frame numbers at which the lanes stop playing.
	 */
	protected List<List<Long>> endFrames;
	
	/**
	 * The current state of the keys.
	 */
	protected boolean[] curKeyState;
	
	/**
	 * The timer display.
	 */
	protected TimerDisplay curTimeDisp;
	
	/**
	 * This creates a creation mode panel.
	 * @param backgroundImage The image to display as a background.
	 * @param backScrollRate The rate at which the background should scroll.
	 * @param blockScrollRate The rate at which the stones fall.
	 * @throws IOException If there is a problem loading an image.
	 */
	public CreationModePanel(BufferedImage backgroundImage, float backScrollRate, float blockScrollRate) throws IOException{
		overlay = ImageIO.read(ClassLoader.getSystemResource("images/overlay.png"));
		curKeys = new Keyboard(0.1f);
		setDoubleBuffered(false);
		background = backgroundImage;
		backgroundOffset = 0;
		backgroundRate = backScrollRate;
		myLock = new ReentrantLock();
		frameNanos = 16000000;
		playing = new boolean[4];
		startFrames = new ArrayList<>();
		endFrames = new ArrayList<>();
		for(int i = 0; i<12; i++){
			startFrames.add(new ArrayList<Long>());
			endFrames.add(new ArrayList<Long>());
		}
		blockLanes = new CreationBlockChannel[12];
		blockLanes[ 0] = new CreationBlockChannel(startFrames.get( 0), endFrames.get( 0), ( 1.0f/16)-0.025f, blockScrollRate, 0);
		blockLanes[ 1] = new CreationBlockChannel(startFrames.get( 1), endFrames.get( 1), ( 2.0f/16)-0.025f, blockScrollRate, 0);
		blockLanes[ 2] = new CreationBlockChannel(startFrames.get( 2), endFrames.get( 2), ( 3.0f/16)-0.025f, blockScrollRate, 0);
		blockLanes[ 3] = new CreationBlockChannel(startFrames.get( 3), endFrames.get( 3), ( 5.0f/16)-0.025f, blockScrollRate, 1);
		blockLanes[ 4] = new CreationBlockChannel(startFrames.get( 4), endFrames.get( 4), ( 6.0f/16)-0.025f, blockScrollRate, 1);
		blockLanes[ 5] = new CreationBlockChannel(startFrames.get( 5), endFrames.get( 5), ( 7.0f/16)-0.025f, blockScrollRate, 1);
		blockLanes[ 6] = new CreationBlockChannel(startFrames.get( 6), endFrames.get( 6), ( 9.0f/16)-0.025f, blockScrollRate, 2);
		blockLanes[ 7] = new CreationBlockChannel(startFrames.get( 7), endFrames.get( 7), (10.0f/16)-0.025f, blockScrollRate, 2);
		blockLanes[ 8] = new CreationBlockChannel(startFrames.get( 8), endFrames.get( 8), (11.0f/16)-0.025f, blockScrollRate, 2);
		blockLanes[ 9] = new CreationBlockChannel(startFrames.get( 9), endFrames.get( 9), (13.0f/16)-0.025f, blockScrollRate, 3);
		blockLanes[10] = new CreationBlockChannel(startFrames.get(10), endFrames.get(10), (14.0f/16)-0.025f, blockScrollRate, 3);
		blockLanes[11] = new CreationBlockChannel(startFrames.get(11), endFrames.get(11), (15.0f/16)-0.025f, blockScrollRate, 3);
		curKeyState = new boolean[12];
		curTimeDisp = new TimerDisplay();
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
					for(int i = 0; i<blockLanes.length; i++){
						blockLanes[i].setTime((SONGLENGTH) - framesRemaining);
					}
					curTimeDisp.setTimeRatio(1 - (1.0f * framesRemaining)/SONGLENGTH);
				}finally{myLock.unlock();}
				Graphics toDraw = this.getGraphics();
				paintComponent(getGraphics());
				framesRemaining--;
				if(framesRemaining<=0){
					active = false;
					myLock.lock(); try{
						//randomly assign instruments
						Random rand = new Random();
						int[] instruments = new int[4];
						instruments[0] = rand.nextInt(AudioEventPlayer.MAXIMPLEMENTEDINSTRUMENTINDEX+1);
						instruments[1] = rand.nextInt(AudioEventPlayer.MAXIMPLEMENTEDINSTRUMENTINDEX+1);
						instruments[2] = rand.nextInt(AudioEventPlayer.MAXIMPLEMENTEDINSTRUMENTINDEX+1);
						instruments[3] = rand.nextInt(AudioEventPlayer.MAXIMPLEMENTEDINSTRUMENTINDEX+1);
						//write the file
						String locFileName = "track" + System.currentTimeMillis();
						String usrHome = System.getProperty("user.home");
						char pathSepChar = File.separatorChar;
						String fileLoc = usrHome + pathSepChar + "GameSaves" + pathSepChar + "FourWayve";
						File parentFolder = new File(fileLoc);
						if(!parentFolder.exists()){
							parentFolder.mkdirs();
						}
						try{
							AudioEventPlayer.saveEventFile(new File(fileLoc, locFileName + ".trk"), instruments, startFrames, endFrames);
							AudioEventPlayer.generateMidiFile(new File(fileLoc, locFileName + ".mid"), instruments, startFrames, endFrames);
						} catch(IOException|InvalidMidiDataException e){e.printStackTrace();}
						//clear the lists
						startFrames.get(0).clear();
						startFrames.get(1).clear();
						startFrames.get(2).clear();
						startFrames.get(3).clear();
						startFrames.get(4).clear();
						startFrames.get(5).clear();
						startFrames.get(6).clear();
						startFrames.get(7).clear();
						startFrames.get(8).clear();
						startFrames.get(9).clear();
						startFrames.get(10).clear();
						startFrames.get(11).clear();
						endFrames.get(0).clear();
						endFrames.get(1).clear();
						endFrames.get(2).clear();
						endFrames.get(3).clear();
						endFrames.get(4).clear();
						endFrames.get(5).clear();
						endFrames.get(6).clear();
						endFrames.get(7).clear();
						endFrames.get(8).clear();
						endFrames.get(9).clear();
						endFrames.get(10).clear();
						endFrames.get(11).clear();
						//let the menu know the name of the file
					}finally{myLock.unlock();}
				}
				else if(framesRemaining % metronomeFrames == 0){
					try{
						AudioEventPlayer.endNote(-1,0);
						AudioEventPlayer.startNote(-1,0);
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
		framesRemaining = SONGLENGTH;
		active = true;
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
	 * This sets the amount of time between metronome clicks.
	 * @param frameDelay The number of frames between clicks.
	 */
	public void setMetronomeFrames(long frameDelay){
		metronomeFrames = frameDelay;
	}
	
	/**
	 * This will start a key press event.
	 * @param lane The lane that should start playing.
	 */
	protected void startEvent(int lane){
		startFrames.get(lane).add(Long.valueOf(SONGLENGTH-framesRemaining));
		endFrames.get(lane).add(-1l);
	}
	
	/**
	 * This will end a key press event.
	 * @param lane The lane that should stop playing.
	 */
	protected void endEvent(int lane){
		if(startFrames.get(lane).size() > 0){
			List<Long> laneList = endFrames.get(lane);
			laneList.set(laneList.size()-1, Long.valueOf(SONGLENGTH-framesRemaining));
			if(laneList.get(laneList.size()-1) == startFrames.get(lane).get(laneList.size()-1)){
				laneList.remove(laneList.size()-1);
				startFrames.get(lane).remove(startFrames.get(lane).size()-1);
			}
		}
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
			for(int i = 0; i<blockLanes.length; i++){
				blockLanes[i].paintComponent(bufDraw, getWidth(), getHeight());
			}
			curTimeDisp.paintComponent(bufDraw, getWidth(), getHeight());
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
		CreationModePanel toTest = new CreationModePanel(ImageIO.read(ClassLoader.getSystemResource("images/background.png")), 0.0025f, 0.005f);
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