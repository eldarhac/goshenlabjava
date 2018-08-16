package data_manager;

import java.io.Serializable;

/**
 * container for system device
 */
public class Device implements Serializable{
    private static final long serialVersionUID = 1L;
    // user friendly name
    private String name;
    // system identifier
    private String systemIdName;

    /**
     * constructor for a device
     * @param name user friendly name (i.e functionality in the system)
     * @param systemIdName Id of the device (e.g "COM1")
     */
    public Device(String name, String systemIdName) {
        this.name = name;
        this.systemIdName = systemIdName;
    }

    /**
     * @return user friendly name of the device
     */
    public String getName() {
        return name;
    }

    /**
     * set name
     * @param name user friendly name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return system identifier for the device
     */
    public String getSystemIdName() {
        return systemIdName;
    }

    /**
     * set system identifier
     * @param systemIdName system identifier
     */
    public void setSystemIdName(String systemIdName) {
        this.systemIdName = systemIdName;
    }
}
