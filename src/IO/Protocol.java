package IO;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is Protocol in IO
 * created on 9/12/2016
 */

public enum Protocol {
    DELIMITER ("#"), REWARD ("REW");
    private final String str;

    Protocol(String str){
        this.str = str;
    }
    public String str(){
        return str;
    }

    @Override
    public String toString() {
        return str;
    }
}
