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
   * 
   */  
  public MenuOverlay(){
    icons = new MenuIcon[4];
    
    for(int i = 0; i < 4; ++i){
      icons[i] = new MenuIcon(i);
    }
  }
  
  protected void setTrans(boolean mouseClick, int x, int width){
    float relWid = (float)x / (float)width;
    if(mouseClick){
      if(relWid < 0.25f){icons[0].setHighlight(mouseClick);}
      else if(relWid < 0.5f){icons[1].setHighlight(mouseClick);}
      else if(relWid < 0.75f){icons[2].setHighlight(mouseClick);}
      else{icons[3].setHighlight(mouseClick);}
    }
    else{
      icons[0].setHighlight(mouseClick);
      icons[1].setHighlight(mouseClick);
      icons[2].setHighlight(mouseClick);
      icons[3].setHighlight(mouseClick);
    }
  }
  
  /**
   *
   */
  protected void paintComponent(Graphics g, int height, int width){
    for(int i = 0; i < 4; ++i){
      icons[i].paintComponent(g, height, width);
    }
  }
  
  /**
   * Each selector of the menu
   */
  class MenuIcon{
    /**
     * This menu icon's id on the menu overlay
     */
    protected int thisID;
    
    /**
     *
     */
    protected float relXLoc;
    protected float relWid = 0.25f;
    
    /**
     * Control transparency of highlighted portions
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