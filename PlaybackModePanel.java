import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.JFileChooser;
import java.util.Random;
import java.util.ArrayList;
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
	 * The rate at which the blocks scroll.
	 */
	protected float blockRate;
	
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
	 * The block manager.
	 */
	protected PlaybackBlockChannel curBlocks;
	
	/**
	 * The displays for the player scores.
	 */
	protected Score[] playerScores;
	
	/**
	 * The image to use for shading scores.
	 */
	protected BufferedImage shadowImg;
	
	/**
	 * This creates a cration mode panel.
	 * @param backgroundImage The image to display as a background.
	 * @param backScrollRate The rate at which the background should scroll.
	 * @param blockScrollRate The rate at which the stones fall.
	 * @throws IOException If there is a problem loading an image.
	 */
	public PlaybackModePanel(BufferedImage backgroundImage, float backScrollRate, float blockScrollRate) throws IOException{
		overlay = ImageIO.read(ClassLoader.getSystemResource("images/overlayFlip.png"));
		shadowImg = ImageIO.read(ClassLoader.getSystemResource("images/shadows.png"));
		curKeys = new Keyboard(0.801f);
		setDoubleBuffered(false);
		background = backgroundImage;
		backgroundOffset = 0;
		backgroundRate = backScrollRate;
		this.blockRate = blockScrollRate;
		myLock = new ReentrantLock();
		frameNanos = 16000000;
		playing = new boolean[4];
		curKeyState = new boolean[12];
		curTimeDisp = new TimerDisplay();
		playerScores = new Score[]{new Score(0.225f), new Score(0.475f), new Score(0.725f), new Score(0.975f)};
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
	 * @throws IOException If there is a problem reading the song (if this is thrown, no song will play).
	 */
	@SuppressWarnings("unchecked")
	public void playSong() throws IOException{
		JFileChooser toSelTrk = new JFileChooser();
		toSelTrk.setFileFilter(new FileNameExtensionFilter("Track files.", "trk"));
		toSelTrk.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int res = toSelTrk.showOpenDialog(this);
		if(res != JFileChooser.APPROVE_OPTION){
			throw new IOException("User canceled.");
		}
		File toLoad = toSelTrk.getSelectedFile();
		myLock.lock(); try{
			Object[] trackData = AudioEventPlayer.readEventFile(toLoad);
			int[] tmpinstruments = (int[])(trackData[0]);
			List<List<Long>> tmpstartFrames = (List<List<Long>>)(trackData[1]);
			List<List<Long>> tmpendFrames = (List<List<Long>>)(trackData[2]);
			curFrame = -360;
			lastFrame = 0;
			for(List<Long> cur : tmpendFrames){
				if(cur.size()>0 && cur.get(cur.size()-1) > lastFrame){
					lastFrame = cur.get(cur.size()-1);
				}
			}
			lastFrame += 300;
			//randomize tracks
			Random rand = new Random();
			instruments = new int[4];
			for(int i = 0; i<instruments.length; i++){
				instruments[i] = playing[i] ? -1 : 0;
			}
			startFrames = new ArrayList<List<Long>>();
			endFrames = new ArrayList<List<Long>>();
			for(int i = 0; i<12; i++){
				startFrames.add(new ArrayList<Long>());
				endFrames.add(new ArrayList<Long>());
			}
			boolean[] assigned = new boolean[4];
			boolean foundAll = false;
			while(!foundAll){
				List<Integer> missing = new ArrayList<>();
				for(int i = 0; i<instruments.length; i++){
					if(instruments[i] < 0){
						missing.add(i);
					}
				}
				if(missing.size()==0){
					foundAll = true;
					break;
				}
				int toAssign = missing.get(rand.nextInt(missing.size()));
				int maxNotes = -1;
				int maxTrks = 0;
				for(int i = 0; i<assigned.length; i++){
					if(!assigned[i]){
						int notes = tmpstartFrames.get(3*i).size() + tmpstartFrames.get(3*i+1).size() + tmpstartFrames.get(3*i+2).size();
						if(notes > maxNotes){
							maxNotes = notes;
							maxTrks = i;
						}
					}
				}
				assigned[maxTrks] = true;
				instruments[toAssign] = tmpinstruments[maxTrks];
				startFrames.set(3*toAssign, new ArrayList<>(tmpstartFrames.get(3*maxTrks)));
				startFrames.set(3*toAssign+1, new ArrayList<>(tmpstartFrames.get(3*maxTrks+1)));
				startFrames.set(3*toAssign+2, new ArrayList<>(tmpstartFrames.get(3*maxTrks+2)));
				endFrames.set(3*toAssign, new ArrayList<>(tmpendFrames.get(3*maxTrks)));
				endFrames.set(3*toAssign+1, new ArrayList<>(tmpendFrames.get(3*maxTrks+1)));
				endFrames.set(3*toAssign+2, new ArrayList<>(tmpendFrames.get(3*maxTrks+2)));
			}
			//create block manager
			curBlocks = new PlaybackBlockChannel(blockRate, playing, curKeys, startFrames, endFrames, instruments, playerScores);
			playerScores[0].setScore(0);
			playerScores[1].setScore(0);
			playerScores[2].setScore(0);
			playerScores[3].setScore(0);
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
					if(curBlocks!=null){
						curBlocks.update(curFrame);
					}
				} finally{myLock.unlock();}
				Graphics toDraw = this.getGraphics();
				paintComponent(getGraphics());
				curFrame++;
				if(curFrame == lastFrame){
					active = false;
					TitleMenuPanel.showMenu();
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
	 * This will handle keys being pressed.
	 * @param e The key being pressed.
	 */
	public void keyPressed(KeyEvent e){
		myLock.lock(); try{
			if(curBlocks!=null){
				curBlocks.keyPressed(e);
			}
		} finally{myLock.unlock();}
	}
	
	/**
	 * This will handle keys being let go.
	 * @param e The key being released.
	 */
	public void keyReleased(KeyEvent e){
		myLock.lock(); try{
			if(curBlocks!=null){
				curBlocks.keyReleased(e);
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
			if(curBlocks!=null){
				curBlocks.paintComponent(bufDraw, getWidth(), getHeight());
			}
			curKeys.paintComponent(bufDraw, getWidth(), getHeight());
			curTimeDisp.paintComponent(bufDraw, getWidth(), getHeight());
			//some shadows
			int shadY = (int)(0.9f*getHeight());
			int shadW = (int)(0.25f*getWidth());
			int shadH = (int)(0.1f*getHeight());
			bufDraw.drawImage(shadowImg, 0, shadY, shadW, shadH, null);
			bufDraw.drawImage(shadowImg, (int)(0.25*getWidth()), shadY, shadW, shadH, null);
			bufDraw.drawImage(shadowImg, (int)(0.5*getWidth()), shadY, shadW, shadH, null);
			bufDraw.drawImage(shadowImg, (int)(0.75*getWidth()), shadY, shadW, shadH, null);
			for(int i = 0; i<playing.length; i++){
				if(playing[i]){
					playerScores[i].paintComponent(bufDraw, getWidth(), getHeight());
				}
			}
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
		PlaybackModePanel toTest = new PlaybackModePanel(ImageIO.read(ClassLoader.getSystemResource("images/background.png")), 0.0025f, 0.005f);
		toTest.setPlayerActive(0, true);
		toTest.setPlayerActive(1, true);
		toTest.setPlayerActive(2, true);
		toTest.setPlayerActive(3, true);
		mainframe.add(toTest);
		mainframe.addKeyListener(toTest);
		mainframe.setVisible(true);
		Thread toRun = new Thread(toTest);
		toRun.start();
		toTest.playSong();
	}
}