import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Overlaps title menu to allow mouse interaction
 */
public class MenuOverlay extends JPanel{
  /**
   * A list of each icon
   */
  protected MenuIcon[] icons;
  
  /**
   * When clicked, the id of the selected val
   */
  protected int selected;
  
  /**
   * Constructor, merely build 4 icon objects
   */  
  public MenuOverlay(){
    icons = new MenuIcon[4];
    selected = -1;
    for(int i = 0; i < 4; ++i){
      icons[i] = new MenuIcon(i);
    }
  }
  
  /**
   * Set the transparency based on where it was clicked, if released it clears all transparency on all icons
   * @return The value of the originally clicked selection upon release or -1 if only just pressed
   */
  protected int setTrans(boolean mouseClick, int x, int width){
    float relWid = (float)x / (float)width;
    if(mouseClick){
      //If event is located in the left quarter, left half, right half, right quarter respectively
      if(relWid < 0.25f){
        icons[0].setHighlight(mouseClick);
        selected = 0;
      }
      else if(relWid < 0.5f){
        icons[1].setHighlight(mouseClick);
        selected = 1;
      }
      else if(relWid < 0.75f){
        icons[2].setHighlight(mouseClick);
        selected = 2;
      }
      else{
        icons[3].setHighlight(mouseClick);
        selected = 3;
      }
      return -1;
    }
    else{
      //Clear all transparency when mouse is deselected
      icons[0].setHighlight(mouseClick);
      icons[1].setHighlight(mouseClick);
      icons[2].setHighlight(mouseClick);
      icons[3].setHighlight(mouseClick);
      int temp = selected;
      selected = -1;
      return temp;
    }
  }
  
  /**
   * Draw this component onto the field
   * @param g The field to be painted
   * @param height The height of the field
   * @param width The width of the field
   */
  protected void paintComponent(Graphics g, int height, int width){
    for(int i = 0; i < 4; ++i){
      icons[i].paintComponent(g, height, width);
    }
  }
  
  /**
   * Each individual selector of the menu
   */
  class MenuIcon{
    /**
     * This menu icon's id on the menu overlay
     */
    protected int thisID;
    
    /**
     * The leftmost x location in percent
     */
    protected float relXLoc;
    protected float relWid = 0.25f;
    
    /**
     * Control transparency of highlighted portions
     * H is the highlighted transparency
     * D is the deselected transparency
     * c is the current transparency
     */
    protected static final float HTRANSPARENT = 0.3f;
    protected static final float DTRANSPARENT = 0.0f;
    protected float cTransparent;
    
    public MenuIcon(int id){
      thisID = id;
      relXLoc = (float)id / 4f;
      setOpaque(false);
      cTransparent = DTRANSPARENT;
    }
    
    /**
     * Set the highlight based on if it was clicked (true) or released (false)
     * @param mouseClick Wether the icon was clicked or released
     */
    protected void setHighlight(boolean mouseClick){
      if(mouseClick){cTransparent = HTRANSPARENT;}
      else{cTransparent = DTRANSPARENT;}
    }
    
    /**
     * Paint this icon
     * @param g The drawing surface
     * @param width The window width
     * @param height The window height
     */
    protected void paintComponent(Graphics g, int width, int height){
      int thisHei = (int)(height);
      int thisWid = (int)(relWid * width);
      int xLoc = (int)(relXLoc * width);

      g.setColor(new Color(1f,1f,1f,cTransparent));
      g.fillRect(xLoc, 0, thisWid, thisHei);
    }
  }
}