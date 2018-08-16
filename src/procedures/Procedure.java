package procedures;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is Procedure in procedures
 * created on 9/8/2016
 */

//// TODO: 9/29/2016 sit with team to figure out how to cunstruct this
public class Procedure implements Serializable{
    // sys id of procedure
    private int id;
    // dates this procedure was preformed
    protected LinkedList<Date> datesList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
