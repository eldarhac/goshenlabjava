package UI.editor;

import experiments.RewardStation;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is RewardIcon in UI.editor
 * created on 9/15/2016
 */
// // TODO: 9/29/2016 fix id issues  i particular upon creation with pre existing stations
public class RewardIcon extends JLabel {
    // the icon for the reward
    private ImageIcon img;
    // id of reward station
    private int id;
    // x location
    private int x;
    // y location
    private int y;
    // clicker precision distance to be a regarded as a choice
    private static final int clickBounds = 20;
    // the reward station associated with this icon
    private RewardStation rew;
    // selected indicator for icon change
    private boolean selected = false;
    // factor
    //int[] origin = {150,150};

    /**
     * @return the associated station with this icon
     */
    public RewardStation getRew() {
        return rew;
    }

    /**
     * @param rew set station to this reward
     */
    public void setRew(RewardStation rew) {
        this.rew = rew;
        //this.rew.setX((float));
    }

    /**
     * constructor
     * @param id id number
     * @param x location
     * @param y location
     */
    public RewardIcon(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
//        addRewImage("C:\\Users\\owner\\Documents\\LAB_STRUCTURE\\src\\UI\\editor\\rew.png");
        addRewImage(".\\src\\UI\\editor\\rew.png");
    }

    /**
     * @return the icon
     */
    public ImageIcon getImg() {
        return img;
    }

    @Override
    public int getX() {
        return x;
    }

    /**
     * @param x location
     */
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    /**
     * @param y location
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return station id
     */
    public int getId() {
        return id;
    }

    // set the image to this icon
    private void addRewImage(String path)
    {
        img = new ImageIcon(path);
        Image image = img.getImage(); // transform it
        Image newimg = image.getScaledInstance(10, 15,  java.awt.Image.SCALE_SMOOTH);
        img = new ImageIcon(newimg);
    }

    /**
     * mark this station as selected
     */
    public void select()
    {
        if (selected)
        {
//            addRewImage("C:\\Users\\owner\\Documents\\LAB_STRUCTURE\\src\\UI\\editor\\rew.jpeg");
            addRewImage(".\\src\\UI\\editor\\rew.jpeg");
        }
        else
        {
//            addRewImage("C:\\Users\\owner\\Documents\\LAB_STRUCTURE\\src\\UI\\editor\\rew.png");
            addRewImage(".\\src\\UI\\editor\\rew.png");
        }
    }

    /**
     *
     * @return true if station has been selected false otherwise
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     *
     * @param selected indicate that this has been selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img.getImage(), 0,0, null);

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
