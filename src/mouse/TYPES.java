package mouse;

import java.io.Serializable;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is TYPES in mouse
 * created on 9/18/2016
 */

public enum TYPES implements Serializable{
    WILD ("wild type"), TYPE1 ("type 1");
    private final String str;

    TYPES(String str){
        this.str = str;
    }
    public String str(){
        return str;
    }
}
