package experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is RewardStation in experiments
 * created on 9/11/2016
 */

public class RewardStation implements Serializable{
    private static final long serialVersionUID = 1L;
    // name of station
    private String name;
    // id given to station
    private int id = 0;
    // location of station in space [x,y,z]
    private float[] location;
    // reward size
    private float rSize;
    // reward probability between 0-1
    private float probability;
    // delimiter for config file
    public static final String DEL = ",";


    /**
     * copy constructor
     * @param rew a reward station to copy
     */
    public RewardStation(RewardStation rew)
    {
        this.id = rew.getId();
        this.name = rew.getName();
        this.probability = rew.getProbability();
        this.rSize = rew.getrSize();
        this.location = new float[3];
        this.location[0] = rew.getX();
        this.location[1] = rew.getY();
        this.location[2] = rew.getZ();
    }

    /**
     * Constructor
     * @param name name of station
     * @param id id of station
     */
    public RewardStation(String name, int id) {
        location = new float[3];
        this.id = id;
        this.name = name;
        probability = 1f;
        rSize = 1;
    }

    /**
     * @return return the name of the station
     */
    public String getName() {
        return name;
    }

    /**
     * set a location to the station
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public void setLocation(float x, float y, float z)
    {
        location[0] = x;
        location[1] = y;
        location[2] = z;
    }

    /**
     * @return station's id
     */
    public int getId() {
        return id;
    }

    /**
     * set stations id
     * @param id id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the size of the reward
     */
    public float getrSize() {
        return rSize;
    }

    /**
     * set the size of the reward
     * @param rSize new size
     */
    public void setrSize(float rSize) {
        this.rSize = rSize;
    }

    /**
     *
     * @return the probability to which the reward will be given
     */
    public float getProbability() {
        return probability;
    }

    /**
     * srt the probability to which the reward will be given
     * @param probability float between 0-1
     */
    public void setProbability(float probability) {
        if (probability < 0)
            probability = 0;
        if (probability > 1)
            probability = 1;
        this.probability = probability;
    }

    /**
     * save reward station settings to config file
     * @param conf config file path
     */
    public void saveToConfig(String conf)
    {
        try
        {
            FileWriter fw = new FileWriter(conf, true);
//            fw.write(System.getProperty( "line.separator" ));
            fw.write(toConf());
            fw.write(System.getProperty( "line.separator" ));
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    /**
     * @return a string representing station's configuration
     */
    private String toConf() {
        String str = id + DEL;
        str += location[0] + DEL + location[1] + DEL + location[2] + DEL;
        str+= rSize + DEL + probability;
        return str;
    }

    @Override
    public String toString() {
        return name + " " + id + " { " + toConf() + " }";
    }

    /**
     * @return x location
     */
    public float getX() {
        return location[0];
    }

    /**
     * @return y location
     */
    public float getY() {
        return location[1];
    }

    /**
     * @return z location
     */
    public float getZ() {
        return location[2];
    }

    /**
     * set x location
     * @param x float
     */
    public void setX(float x) {
        this.location[0] = x;
    }

    /**
     * set x location
     * @param y float
     */
    public void setY(float y) {
        this.location[1] = y;
    }

    /**
     * set x location
     * @param z float
     */
    public void setZ(float z) {
        this.location[2] = z;
    }

    /**
     * constructor
     * @param x location
     * @param y location
     */
    public RewardStation(float x, float y) {
        location = new float[3];
        this.location[0] = x;
        this.location[1] = y;
    }
}
