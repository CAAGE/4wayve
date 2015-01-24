import java.awt.Graphics2D;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

/**
 * This will draw the blocks in one channel.
 */
public class CreationBlockChannel{
	
	/**
	 * The names of the bottom images.
	 */
	protected static final String[] botNames = new String[]{"images/blockBottomRed.png", "images/blockBottomYellow.png", "images/blockBottomBlue.png", "images/blockBottomPurple.png"};
	
	/**
	 * The names of the middle images.
	 */
	protected static final String[] midNames = new String[]{"images/blockMiddleRed.png", "images/blockMiddleYellow.png", "images/blockMiddleBlue.png", "images/blockMiddlePurple.png"};
	
	/**
	 * The names of the top images.
	 */
	protected static final String[] topNames = new String[]{"images/blockTopRed.png", "images/blockTopYellow.png", "images/blockTopBlue.png", "images/blockTopPurple.png"};
	
	/**
	 * The starting y position for blocks.
	 */
	protected static final float startY = 0.2f;
	
	/**
	 * The image to use for the top.
	 */
	protected BufferedImage top;
	
	/**
	 * The image to use for the middle.
	 */
	protected BufferedImage mid;
	
	/**
	 * The image to use for the bottom.
	 */
	protected BufferedImage bot;
	
	/**
	 * The sorted list of start times.
	 */
	protected List<Long> startTimes;
	
	/**
	 * The list of end times.
	 */
	protected List<Long> endTimes;
	
	/**
	 * The current frame.
	 */
	protected long curTime;
	
	/**
	 * The rate at which blocks scroll.
	 */
	protected float rate;
	
	/**
	 * The x location to draw the blocks at.
	 */
	protected float xLoc;
	
	public CreationBlockChannel(List<Long> startTimeList, List<Long> endTimeList, float xLocation, float scrollRate, int color) throws IOException{
		startTimes = startTimeList;
		endTimes = endTimeList;
		rate = scrollRate;
		xLoc = xLocation;
		curTime = 0;
		top = ImageIO.read(ClassLoader.getSystemResource(topNames[color]));
		mid = ImageIO.read(ClassLoader.getSystemResource(midNames[color]));
		bot = ImageIO.read(ClassLoader.getSystemResource(botNames[color]));
	}
	
	public void rewind(){
		curTime = 0;
	}
	
	public void setTime(long time){
		curTime = time;
	}
	
	public void paintComponent(Graphics g, int width, int height){
		
	}
}