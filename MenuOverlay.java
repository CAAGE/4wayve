import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
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
  
  protected void paintComponent(Graphics g, int height, int width){
    Graphics2D g2 = (Graphics2D) g;
    for(int i = 0; i < 4; ++i){
      
    }
    g2.dispose();
  }
  
  /**
   * Each selector of the menu
   */
  class MenuIcon implements MouseListener{
    /**
     * This menu icon's id on the menu overlay
     */
    protected int thisID;
    
    /**
     * Control transparency of highlighted portions
     */
    protected static final float HTRANSPARENT = 0.3f;
    protected static final float DTRANSPARENT = 0.0f;
    
    /**
     * This icons relative x location
     */
    protected float relXLocation;
    
    public MenuIcon(int id){
      thisID = id;
      relXLocation = (float)id / 4;
      setOpaque(false);
    }
    
    /**
     * 
     */
    public void mouseClicked(MouseEvent e){}
    
    /**
     * 
     */
    public void mousePressed(MouseEvent e){
System.out.println(thisID);
    }
    
    /**
     * 
     */
    public void mouseReleased(MouseEvent e){}
    
    /**
     * Add highlight when mouse enters
     */
    public void mouseEntered(MouseEvent e){
    }
    
    /**
     * Remove highlight when mouse leaves
     */
    public void mouseExited(MouseEvent e){
    }
    
    protected void paintComponent(Graphics g, int width, int height){
      
    }
  }
}