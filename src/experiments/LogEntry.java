package experiments;

import mouse.Mouse;

import java.util.Date;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is LogEntry in experiments
 * created on 6/4/2018
 */

public class LogEntry {
    private Mouse mau;
    private Date dat;

    public LogEntry(Mouse mau, Date dat) {
        this.mau = mau;
        this.dat = dat;
    }

    public Mouse getMau() {
        return mau;
    }

    public Date getDat() {
        return dat;
    }

    public void setMau(Mouse mau) {
        this.mau = mau;
    }
}
