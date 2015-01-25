import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.util.List;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * This will draw blocks and handle key presses for playback.
 */
public class PlaybackBlockChannel implements KeyListener{
	
	/**
	 * The x location of the blocks in the lanes relative to lane center.
	 */
	protected static final float XOFFSET = -0.025f;
	
	/**
	 * The width of the keys.
	 */
	protected static final float STONEWIDTH = 0.05f;
	
	/**
	 * The relative x locations of the lanes.
	 */
	protected static final float[] relLocs = new float[]{1.0f/16, 2.0f/16, 3.0f/16, 5.0f/16, 6.0f/16, 7.0f/16, 9.0f/16, 10.0f/16, 11.0f/16, 13.0f/16, 14.0f/16, 15.0f/16};
	
	/**
	 * The height of the caps.
	 */
	protected static final float CAPHEIGHT = 7.0f / 1080;
	
	/**
	 * The ending y location.
	 */
	public static final float FINALY = 0.8f;
	
	/**
	 * The amount of frames to allow the player around the leading and trailing edges.
	 */
	protected static final int FUDGEFRAMES = 20;
	
	/**
	 * The rate at which blocks scroll.
	 */
	protected float blockScrollRate;
	
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
	 * The current frame in the song.
	 */
	protected long curFrame;
	
	/**
	 * The index of the next event for each lane.
	 */
	protected int[] nextEvent;
	
	/**
	 * The amount of points each lane has garnered.
	 */
	protected float[] currentPoints;
	
	/**
	 * Whether each lane is currently hitting a note.
	 */
	protected boolean[] currentlyHitting;
	
	/**
	 * The number of held frames on the current note.
	 */
	protected int[] currentFrames;
	
	/**
	 * How many frames were missed on the leading edge.
	 */
	protected int[] missedFirst;
	
	/**
	 * The red images.
	 */
	protected BufferedImage[] redImgs;
	
	/**
	 * The yellow images.
	 */
	protected BufferedImage[] yelImgs;
	
	/**
	 * The blue images.
	 */
	protected BufferedImage[] bluImgs;
	
	/**
	 * The purple images.
	 */
	protected BufferedImage[] purImgs;
	
	/**
	 * The gray blocks.
	 */
	protected BufferedImage[] graImgs;
	
	/**
	 * The blocks for each player.
	 */
	protected BufferedImage[][] colImgs;
	
	/**
	 * The displays for the player scores.
	 */
	protected Score[] playerScores;
	
	/**
	 * This loads images for drawing blocks.
	 * @param blockScrollRate The rate at which blocks scroll.
	 * @param playing The currently active players.
	 * @param curKeys The keyboard drawing library.
	 * @param startFrames The starting frames of the songs for each player.
	 * @param endFrames The ending frames of the songs for each player.
	 * @param instruments The instruments to play for each player.
	 * @param playerScores The displays for the player scores.
	 */
	public PlaybackBlockChannel(float blockScrollRate, boolean[] playing, Keyboard curKeys, List<List<Long>> startFrames, List<List<Long>> endFrames, int[] instruments, Score[] playerScores) throws IOException{
		this.blockScrollRate = blockScrollRate;
		this.curKeys = curKeys;
		this.curKeyState = new boolean[12];
		this.endFrames = endFrames;
		this.instruments = instruments;
		this.playing = playing;
		this.startFrames = startFrames;
		curFrame = -100;
		nextEvent = new int[12];
		currentPoints = new float[12];
		currentlyHitting = new boolean[12];
		currentFrames = new int[12];
		missedFirst = new int[12];
		redImgs = new BufferedImage[]{ImageIO.read(ClassLoader.getSystemResource("images/blockBottomRed.png")), ImageIO.read(ClassLoader.getSystemResource("images/blockMiddleRed.png")), ImageIO.read(ClassLoader.getSystemResource("images/blockTopRed.png"))};
		yelImgs = new BufferedImage[]{ImageIO.read(ClassLoader.getSystemResource("images/blockBottomYellow.png")), ImageIO.read(ClassLoader.getSystemResource("images/blockMiddleYellow.png")), ImageIO.read(ClassLoader.getSystemResource("images/blockTopYellow.png"))};
		bluImgs = new BufferedImage[]{ImageIO.read(ClassLoader.getSystemResource("images/blockBottomBlue.png")), ImageIO.read(ClassLoader.getSystemResource("images/blockMiddleBlue.png")), ImageIO.read(ClassLoader.getSystemResource("images/blockTopBlue.png"))};
		purImgs = new BufferedImage[]{ImageIO.read(ClassLoader.getSystemResource("images/blockBottomPurple.png")), ImageIO.read(ClassLoader.getSystemResource("images/blockMiddlePurple.png")), ImageIO.read(ClassLoader.getSystemResource("images/blockTopPurple.png"))};
		graImgs = new BufferedImage[]{ImageIO.read(ClassLoader.getSystemResource("images/blockBottomGray.png")), ImageIO.read(ClassLoader.getSystemResource("images/blockMiddleGray.png")), ImageIO.read(ClassLoader.getSystemResource("images/blockTopGray.png"))};
		colImgs = new BufferedImage[][]{redImgs, yelImgs, bluImgs, purImgs};
		this.playerScores = playerScores;
	}
	
	/**
	 * This will update scores for currently pressed keys.
	 * @param curFrame The current frame of the song.
	 */
	public void update(long curFrame){
		this.curFrame = curFrame;
		for(int i = 0; i<currentlyHitting.length; i++){
			if(nextEvent[i]>=startFrames.get(i).size()){
				continue;
			}
			if(currentlyHitting[i]){
				//check that you haven't missed the end
				long nextEndFrame = endFrames.get(i).get(nextEvent[i]);
				if(nextEndFrame < curFrame){
					int offset = (int)(curFrame - nextEndFrame);
					if(offset > FUDGEFRAMES){
						currentlyHitting[i] = false;
						nextEvent[i]++;
					}
				}
			}
			else{
				//check that you haven't passed next
				long nextStartFrame = startFrames.get(i).get(nextEvent[i]);
				if((nextStartFrame+FUDGEFRAMES) < curFrame){
					nextEvent[i]++;
				}
			}
		}
	}
	
	/**
	 * This will start a key press event.
	 * @param lane The lane that should start playing.
	 * @return Whether the start was valid.
	 */
	protected boolean startEvent(int lane){
		//check if next event is within 6 frames
		if(nextEvent[lane] >= startFrames.get(lane).size()){
			return false;
		}
		boolean valid = false;
		long actualStart = startFrames.get(lane).get(nextEvent[lane]).longValue();
		long offset = Math.abs(actualStart - curFrame);
		if(offset < FUDGEFRAMES){
			try{
				AudioEventPlayer.startNote(instruments[lane/3], lane%3);
			} catch(MidiUnavailableException|InvalidMidiDataException e){}
			currentlyHitting[lane] = true;
			currentFrames[lane] = 1;
			missedFirst[lane] = (int)offset;
			valid = true;
		}
		else if(curFrame > actualStart){
			nextEvent[lane]++;
		}
		return valid;
	}
	
	/**
	 * This will end a key press event.
	 * @param lane The lane that should stop playing.
	 */
	protected void endEvent(int lane){
		if(currentlyHitting[lane]){
			try{
				AudioEventPlayer.endNote(instruments[lane/3], lane%3);
			} catch(MidiUnavailableException|InvalidMidiDataException e){}
			//see how far from the end you are
			long actualEnd = endFrames.get(lane).get(nextEvent[lane]).longValue();
			long offset = Math.abs(actualEnd - curFrame);
			if(offset < FUDGEFRAMES){
				currentFrames[lane]++;
				float multiplier = 10*(1-(offset * 1.0f / FUDGEFRAMES))*(1-(missedFirst[lane] * 1.0f / FUDGEFRAMES));
				currentPoints[lane] += (currentFrames[lane] * multiplier);
				playerScores[lane/3].setScore((int)(currentPoints[lane/3] + currentPoints[lane/3+1] + currentPoints[lane/3+2]));
			}
			currentlyHitting[lane] = false;
			nextEvent[lane]++;
		}
	}
	
	/**
	 * This will handle keys being pressed.
	 * @param e The key being pressed.
	 */
	public void keyPressed(KeyEvent e){
		switch (e.getKeyCode()) {
		case KeyEvent.VK_A:
			if(!curKeyState[0]){
				boolean valid = startEvent(0);
				curKeys.setKeyState(0, playing[0] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[0] = true;
			break;
		case KeyEvent.VK_S:
			if(!curKeyState[1]){
				boolean valid = startEvent(1);
				curKeys.setKeyState(1, playing[0] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[1] = true;
			break;
		case KeyEvent.VK_D:
			if(!curKeyState[2]){
				boolean valid = startEvent(2);
				curKeys.setKeyState(2, playing[0] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[2] = true;
			break;
		case KeyEvent.VK_J:
			if(!curKeyState[3]){
				boolean valid = startEvent(3);
				curKeys.setKeyState(3, playing[1] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[3] = true;
			break;
		case KeyEvent.VK_K:
			if(!curKeyState[4]){
				boolean valid = startEvent(4);
				curKeys.setKeyState(4, playing[1] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[4] = true;
			break;
		case KeyEvent.VK_L:
			if(!curKeyState[5]){
				boolean valid = startEvent(5);
				curKeys.setKeyState(5, playing[1] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[5] = true;
			break;
		case KeyEvent.VK_LEFT:
			if(!curKeyState[6]){
				boolean valid = startEvent(6);
				curKeys.setKeyState(6, playing[2] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[6] = true;
			break;
		case KeyEvent.VK_DOWN:
			if(!curKeyState[7]){
				boolean valid = startEvent(7);
				curKeys.setKeyState(7, playing[2] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[7] = true;
			break;
		case KeyEvent.VK_RIGHT:
			if(!curKeyState[8]){
				boolean valid = startEvent(8);
				curKeys.setKeyState(8, playing[2] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[8] = true;
			break;
		case KeyEvent.VK_1:
			if(!curKeyState[9]){
				boolean valid = startEvent(9);
				curKeys.setKeyState(9, playing[3] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[9] = true;
			break;
		case KeyEvent.VK_2:
			if(!curKeyState[10]){
				boolean valid = startEvent(10);
				curKeys.setKeyState(10, playing[3] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[10] = true;
			break;
		case KeyEvent.VK_3:
			if(!curKeyState[11]){
				boolean valid = startEvent(11);
				curKeys.setKeyState(11, playing[3] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[11] = true;
			break;
		case KeyEvent.VK_NUMPAD1:
			if(!curKeyState[9]){
				boolean valid = startEvent(9);
				curKeys.setKeyState(9, playing[3] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[9] = true;
			break;
		case KeyEvent.VK_NUMPAD2:
			if(!curKeyState[10]){
				boolean valid = startEvent(10);
				curKeys.setKeyState(10, playing[3] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[10] = true;
			break;
		case KeyEvent.VK_NUMPAD3:
			if(!curKeyState[11]){
				boolean valid = startEvent(11);
				curKeys.setKeyState(11, playing[3] ? (valid ? Keyboard.ACTIVE : Keyboard.WRONG) : Keyboard.INVISIBLE);
			}
			curKeyState[11] = true;
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
	}
	
	/**
	 * This ignores typing events.
	 * @param e The key being typed.
	 */
	public void keyTyped(KeyEvent e){}
	
	/**
	 * This will paint the keyboard.
	 * @param g The drawing surface.
	 * @param width The width of the drawable surface.
	 * @param height The height of the drawable surface.
	 */
	public void paintComponent(Graphics g, int width, int height){
		Graphics2D g2 = (Graphics2D)g;
		for(int lane = 0; lane < nextEvent.length; lane++){
			List<Long> laneStarts = startFrames.get(lane);
			List<Long> laneEnds = endFrames.get(lane);
			
			int laneX = (int)(width * (relLocs[lane] + XOFFSET));
			int laneW = (int)(width * STONEWIDTH);
			int capHeight = (int)(height * CAPHEIGHT);
			
			for(int i = nextEvent[lane]+1; i<laneStarts.size(); i++){
				float bulkStartY = FINALY - blockScrollRate*(laneEnds.get(i)-curFrame);
				float bulkEndY = FINALY - blockScrollRate*(laneStarts.get(i)-curFrame);
				if(bulkEndY < (-2*CAPHEIGHT)){
					break;
				}
				int midStartY = (int)(bulkStartY * height);
				int midHeight = (int)((bulkEndY - bulkStartY) * height);
				g2.drawImage(colImgs[lane/3][1], laneX, midStartY, laneW, midHeight, null);
			}
			if(nextEvent[lane]<laneStarts.size()){
				float bulkStartY = FINALY - blockScrollRate*(laneEnds.get(nextEvent[lane])-curFrame);
				float bulkEndY = FINALY - blockScrollRate*(laneStarts.get(nextEvent[lane])-curFrame);
				if(currentlyHitting[lane]){
					int midStartY = (int)(bulkStartY * height);
					int midHeightCol = (int)((FINALY - bulkStartY) * height);
					int midStartSecondY = midStartY + midHeightCol;
					int midHeightGray = (int)((bulkEndY - FINALY) * height);
					g2.drawImage(colImgs[lane/3][1], laneX, midStartY, laneW, midHeightCol, null);
					g2.drawImage(graImgs[1], laneX, midStartSecondY, laneW, midHeightGray, null);
				}
				else{
					if(bulkEndY >= (-2*CAPHEIGHT)){
						int midStartY = (int)(bulkStartY * height);
						int midHeight = (int)((bulkEndY - bulkStartY) * height);
						g2.drawImage(colImgs[lane/3][1], laneX, midStartY, laneW, midHeight, null);
					}
				}
			}
			for(int i = nextEvent[lane]-1; i>=0; i--){
				float bulkStartY = FINALY - blockScrollRate*(laneEnds.get(i)-curFrame);
				if(bulkStartY > (1+2*CAPHEIGHT)){
					break;
				}
				float bulkEndY = FINALY - blockScrollRate*(laneStarts.get(i)-curFrame);
				int midStartY = (int)(bulkStartY * height);
				int midHeight = (int)((bulkEndY - bulkStartY) * height);
				g2.drawImage(graImgs[1], laneX, midStartY, laneW, midHeight, null);
			}
		}
	}
}