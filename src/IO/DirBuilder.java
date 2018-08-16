package IO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import global_vars.GlobalConfig;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is DirBuilder in IO
 * created on 9/11/2016
 */

public class DirBuilder {
    // location for data dir to be built
    private String locationTarget;
    // location of config file
    private String config;
    // dir to build in
    private File location;
    // location log file
    private File locLog;
    // config file rules //// TODO: 9/13/2016 change to global protocol
    private static String logName = "loc";
    private static String end = ".dat";
    public static final String LOC = "LOC";
    public static final String DIR = "DIR";
    public static final String DELIMITER = "#";

    /**
     * constructor
     * @param locationTarget path to build in
     * @param config path of config file
     */
    public DirBuilder(String locationTarget, String config) {
        this.locationTarget = locationTarget;
        this.config = GlobalConfig.makePathGlobalString("VR_DRIVE/CONF/loc_config.conf");
    }

    /**
     * create the working directory for the data
     * will attempt to write it in the provided path but if the path
     * was already used will enumerate path until free path is found
     */
    public void createWorkingDir(){

        File loc = new File(locationTarget);
        boolean created = false;
        int num = 1;
        while (!created) {
            if (!loc.exists()) {
                try {
                    loc.mkdir();
                    created = true;
                } catch (SecurityException e) {
                    return;
                }
            }
            else {
                loc = new File(locationTarget + "_" + num);
                num++;
            }
        }
        location = loc;
        locationTarget = location.getAbsolutePath();
        createLocationFile();
    }

    /**
     * create a location file,
     * loc file a .dat file to store location
     * related data
     */
    private void createLocationFile()
    {
        String logPath = GlobalConfig.makePathSysytemIn(locationTarget + "/" + logName + end);
        File loc = new File(logPath);

        int num = 0;
        boolean created = false;
        while (!created){
            if (!loc.exists())
            {
                try {
                    loc.createNewFile();
                    created = true;
                } catch (IOException e)
                {
                    e.printStackTrace();
                    System.err.println("can't open");
                    return;
                }
            }
            else
            {
                num ++;
                loc = new File(logPath + "_" + num + end);
            }
        }
        locLog = loc;
        System.out.println(locLog);
    }

    public File getLocLog() {
        return locLog;
    }

    /**
     * update configuration file with
     * the location of the locLog file and the working dir
     */
    public void updateConf()
    {
        try
        {
            FileWriter fw = new FileWriter(config, false);
            fw.write(location.getAbsolutePath());
            fw.write(System.getProperty( "line.separator" ));
            fw.write(locLog.getAbsolutePath());
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }



}
