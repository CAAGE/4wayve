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
	 * The height of the caps.
	 */
	protected static final float CAPHEIGHT = 7.0f / 1080;
	
	/**
	 * The width of the keys.
	 */
	protected static final float STONEWIDTH = 0.05f;
	
	/**
	 * The starting y position for blocks.
	 */
	protected static final float startY = 0.2f;
	
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
	
	/**
	 * This sets up a visualizer for the block start and end times.
	 * @param startTimeList The list of start frames.
	 * @param endTimeList The list of end frames.
	 * @param xLocation The relative x position to draw at.
	 * @param scrollRate The rate to scroll the blocks.
	 * @param color The color of the blocks (0=red, 1=yellow, 2=blue, 3=purple).
	 * @throws IOException If there is a problem reading images.
	 */
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
	
	/**
	 * This rewinds the channel to zero.
	 */
	public void rewind(){
		curTime = 0;
	}
	
	/**
	 * This sets the current time (for visualization).
	 */
	public void setTime(long time){
		curTime = time;
	}
	
	/**
	 * This will paint the blocks.
	 * @param g The location to draw to.
	 * @param width The width of the drawable surface.
	 * @param height The height of the drawable surface.
	 */
	public void paintComponent(Graphics g, int width, int height){
		Graphics2D g2 = (Graphics2D)g;
		for(int i=startTimes.size()-1; i>=0; i--){
			int startX = (int)(xLoc * width);
			int wid = (int)(STONEWIDTH * width);
			float bulkStartY;
			float bulkEndY;
			if(endTimes.get(i) < 0){
				bulkStartY = startY;
				bulkEndY = (curTime - startTimes.get(i))*rate + bulkStartY;
			}
			else{
				bulkStartY = (curTime - endTimes.get(i))*rate + startY;
				bulkEndY = (endTimes.get(i) - startTimes.get(i))*rate + bulkStartY;
			}
			if(bulkStartY > (1+2*CAPHEIGHT)){
				break;
			}
			int midStartY = (int)(height * bulkStartY);
			int midHeight = (int)(height * (bulkEndY - bulkStartY));
			int capHig = (int)(CAPHEIGHT * height);
			int topStartY = midStartY - capHig;
			int botStartY = midStartY + midHeight;
			g2.drawImage(top, startX, topStartY, wid, capHig, null);
			g2.drawImage(mid, startX, midStartY, wid, midHeight, null);
			g2.drawImage(bot, startX, botStartY, wid, capHig, null);
		}
	}
}