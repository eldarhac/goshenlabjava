package mouse;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is Weight in mouse
 * created on 9/22/2016
 */

public class Weight implements Serializable{
    // date this weight was logged
    private Date date;
    // weight in grams
    private float weight;

    /**
     * constructor
     * @param date date to log enter null for current sys date
     * @param weight in grams
     */
    public Weight(Date date, float weight) {
        this.date = (date == null) ? new Date() : date;
        this.weight = weight;
    }

    /**
     * @return the date this weight was logged
     */
    public Date getDate() {
        return date;
    }

    /**
     * todo: not sure this should be provided
     * @param date edit logging date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the weight logged
     */
    public float getWeight() {
        return weight;
    }

    /**
     *  todo: not sure this should be provided
     * @param weight log weight in grams
     */
    public void setWeight(float weight) {
        this.weight = weight;
    }
}

