package UI.editor;

import javax.swing.*;
import java.awt.*;
import global_vars.GlobalConfig;
/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is StartPoint in UI.editor
 * created on 9/21/2016
 */

public class StartPoint extends JLabel {
    // x location
    private float x;
    // y location
    private float y;
    // selecting area
    private static final int clickBounds = 20;
    // icon
    private ImageIcon img;


    /**
     * constructor
     * @param x location
     * @param y location
     */
    public StartPoint(float x, float y) {
        this.x = x;
        this.y = y;

        addRewImage(GlobalConfig.makePathGlobalString("LAB_STRUCTURE/src/UI/editor/start.png"));
    }

    /**
     * @return x location
     */
    public float get_X() {
        return x;
    }

    /**
     * set x location
     * @param x location
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return y location
     */
    public float get_Y() {
        return y;
    }

    /**
     * set y location
     * @param y location
     */
    public void setY(float y) {
        this.y = y;
    }

    // set icon dimension
    private void addRewImage(String path)
    {
        img = new ImageIcon(path);
        Image image = img.getImage(); // transform it
        Image newimg = image.getScaledInstance(15, 20,  java.awt.Image.SCALE_SMOOTH);
        img = new ImageIcon(newimg);
    }

    /**
     * @return the icon
     */
    public ImageIcon getImg() {
        return img;
    }

    /**
     * return true if this icon is in the vicinity of the point
     * @param x location
     * @param y location
     * @return true if this is within clickBounds of x,y
     */
    public boolean nearPoint(int x, int y){
        return  (Math.abs(this.x - x) < clickBounds && Math.abs(this.y - y) < clickBounds);

    }
}
