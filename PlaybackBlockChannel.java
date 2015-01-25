import java.util.List;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class PlaybackBlockChannel implements KeyListener{
	
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
	
	public PlaybackBlockChannel(boolean[] playing, Keyboard curKeys, List<List<Long>> startFrames, List<List<Long>> endFrames, int[] instruments){
		this.curKeys = curKeys;
		this.curKeyState = new boolean[12];
		this.endFrames = endFrames;
		this.instruments = instruments;
		this.playing = playing;
		this.startFrames = startFrames;
	}
	
	public void update(long curFrame){
		
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
		
	}
}