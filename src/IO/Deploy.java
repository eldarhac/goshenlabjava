package IO;

import data_manager.DataManager;
import experiments.Experiment;

import java.io.*;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;
import global_vars.GlobalConfig;
//import org.apache.commons.io.FileUtils;


/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is Deploy in IO
 * created on 9/12/2016
 */

public class Deploy implements Serializable{
    private static final long serialVersionUID = 1L;
    // maze to deploy
    private File maze;
    // experiment being deployed
    private Experiment experiment;
    // data dir
    private File dirLoc;
    //singleton entity
    private static Deploy self = null;
    // running indicator
    private static boolean running = false;
    // dir builder instance
    private DirBuilder dirBuild;
    // conf file
    private File conf;
    // process running blender file exe
    private Process runMaze;
    //
    private String infoLoc;
    // data manager
    private DataManager man;
    private Process camera;



    /*
     * constructor
     * @param maze maze file to run
     * @param experiment exp being run
     * @param dirLoc location
     */
    private Deploy(File maze, Experiment experiment, File dirLoc, DataManager man) {
        this.maze = maze;
        this.experiment = experiment;
        this.dirLoc = dirLoc;
        this.man = man;
    }


     /**
     * constructor
     * @param maze maze file to run
     * @param experiment exp being run
     * @param dirLoc location
      *@return a deploying object
     */
    public static Deploy getDeploy(File maze, Experiment experiment, File dirLoc, DataManager man)
    {
        if (self != null)
            return self;
        return new Deploy(maze, experiment, dirLoc, man);
    }

    /**
     * execute the maze
     * @return the process in charge of the exe of the maze
     */
    public Process run()
    {
        if (!running)
        {

            conf = new File(GlobalConfig.makePathGlobalString("/VR_DRIVE/CONF/system_configuration" ));
            conf.delete();
            try
            {
                conf.createNewFile();
            } catch (IOException e)
            {
                System.err.println("passs");
            }

            running = true;
            System.out.println("run");

            dirBuild = new DirBuilder(Paths.get(dirLoc.getAbsolutePath(), experiment.getName()).toString(), conf.getAbsolutePath());
            dirBuild.createWorkingDir();
            dirBuild.updateConf();
            experiment.addDate(new Date());
            System.out.println(conf.getParentFile().getAbsolutePath());
            String rewardConf = experiment.saveToConfig(conf.getParentFile().getAbsolutePath());
            writeConfigurationFile(rewardConf);

            try{



                // launching matlab to start record data
                String mlLaunchPath = GlobalConfig.makePathGlobalString("VR_DRIVE/MATLAB_LAUNCH/start_data.py");
//                "C:\\Users\\owner\\Documents\\VR_PROJECT\\VR_DRIVE\\MATLAB_LAUNCH\\start_data.py"

                Process d = Runtime.getRuntime().exec(new String[]{"python", mlLaunchPath, man.getDevManager().getRate()});
//
//                // launching camera server - edit the ip address in this call
                String cmLaunchPath =  GlobalConfig.makePathGlobalString("VR_DRIVE/CAMERA/camera_2p-master/video_streaming_with_flask_example-master/main.py");
//                "C:\\Users\\owner\\Documents\\VR_PROJECT\\VR_DRIVE\\CAMERA\\camera_2p-master\\video_streaming_with_flask_example-master\\main.py"
                camera = Runtime.getRuntime().exec(new String[]{"python", cmLaunchPath, "132.64.105.56"});
//                // builds process handler for the maze
                ProcessBuilder build = new ProcessBuilder(maze.getAbsolutePath());
                System.out.println("maze path " + maze.getAbsolutePath());
//
                build.redirectError(new File(GlobalConfig.makePathGlobalString("VR_DRIVE/TEMP/DEBUGIO/err.conf")));
//                p.redirectError(new File(GlobalConfig.makePathGlobalString("VR_DRIVE/TEMP/DEBUGIO/err.conf")));
                build.redirectOutput(new File(GlobalConfig.makePathGlobalString("VR_DRIVE/TEMP/DEBUGIO/out.conf")));

                runMaze = build.start();

            }
            catch (IOException run)
            {

                System.err.println("cant run the provided maze");
            }
        }
        else
        {
            System.out.println("already running running");
        }
        return runMaze;
    }

    /**
     * terminate the run
     * @return true if run was terminated, false otherwise
     */
    public boolean stop()
    {
        if(running)
        {
            if (runMaze != null){
                runMaze.destroy();
                camera.destroy();
                rearrangeData();

            }

            self = null;
            running = false;

            return true;
        }
        else
        {
            System.out.println("not running");
        }
        return false;
    }

    // moves the data files acquired in this run to their destination folder
    private void rearrangeData(){

        copyTempData(GlobalConfig.makePathGlobalString("VR_DRIVE/CONF/reward"));
//        copyTempData(GlobalConfig.makePathGlobalString("VR_DRIVE/CONF/personalTrain.conf"));
        copyTempData(GlobalConfig.makePathGlobalString("VR_DRIVE/TEMP/DEBUGIO/err.conf"));
        copyTempData(GlobalConfig.makePathGlobalString("VR_DRIVE/TEMP/DEBUGIO/out.conf"));

        copyTempData(GlobalConfig.makePathGlobalString("VR_DRIVE/TEMP/DEBUGIO/ni_mat_errors.err"));
        copyTempData(GlobalConfig.makePathGlobalString("VR_DRIVE/TEMP/DEBUGIO/ni_mat.out"));

        copyTempData(GlobalConfig.makePathGlobalString("VR_DRIVE/TEMP/DEBUGIO/launch_errors.err"));
        copyTempData(GlobalConfig.makePathGlobalString("VR_DRIVE/TEMP/DEBUGIO/launch_out.out"));
        copyTempData(GlobalConfig.makePathGlobalString("VR_DRIVE/CONF/personalTrain.conf"));
        copyTempData(GlobalConfig.makePathGlobalString("VR_DRIVE/CONF/system_configuration"));

    }

    // copies the file specified by "form" to the location of the current maze's data
    private void copyTempData(String from){
        try{

            File afile = new File(from);

            String path ="";

            try {

                File location = new File(GlobalConfig.makePathGlobalString("VR_DRIVE/CONF/loc_config.conf"));
                Scanner scanner = new Scanner(location);
                if (scanner.hasNext()) {
                    path = scanner.next();
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            this.infoLoc = path;
            System.out.println("trying for- "+path +"/"+ afile.getName());
            if(afile.renameTo(new File(path +"/"+ afile.getName()))){

                System.out.println("File was moved successful!");
            }else{
                System.out.println("failed to move file");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public String getInfoLoc() {
        return infoLoc;
    }

    /**
     * writes all the data to a configuration file,
     * used before maze deployment to insure current properties will be loaded
     * by the maze
     */
    private void writeConfigurationFile(String rewardConf){

        writePersonalConf();
        try{
            FileWriter confWriter = new FileWriter(this.conf, false);

            // write reward configuration location
            confWriter.write(rewardConf);
            confWriter.write(System.getProperty( "line.separator" ));

            // write location file
            confWriter.write(dirBuild.getLocLog().getAbsolutePath());
            confWriter.write(System.getProperty( "line.separator" ));

            // write personal location
            confWriter.write(experiment.getPersonalTrainLocation());
            confWriter.write(System.getProperty( "line.separator" ));

            //write personal tone indicator
            confWriter.write(experiment.isMitTone() ? "True": "#");
            confWriter.write(System.getProperty( "line.separator" ));

            // write reward tone device
            confWriter.write(man.getDevManager().getDevSysID("Reward").getSystemIdName());
//            confWriter.write("COM10,9600");
            confWriter.write(System.getProperty( "line.separator" ));

            // write calibration value
            confWriter.write(calibValue());
            confWriter.write(System.getProperty( "line.separator" ));

            // udp info not currently not used but need to be written for parsing
            confWriter.write(man.getDevManager().getDevSysID("IP,PORT").getSystemIdName());
//            confWriter.write("127.0.0.1,6008");
            confWriter.write(System.getProperty( "line.separator" ));

            // encouder not used but need to be written for parsing
            confWriter.write("COM12,9600"); // not really used in current version
            confWriter.write(System.getProperty( "line.separator" ));

            confWriter.close();

        } catch (IOException ioe){
            System.err.println("IOException: " + ioe.getMessage());
        }


    }

    // return the current calibration value
    private String calibValue(){
        String calib = "6400";

        try (BufferedReader br = new BufferedReader(new FileReader(GlobalConfig.makePathGlobalString("VR_DRIVE/CONF/calib_val.dat")))) {
            String line;
            while ((line = br.readLine()) != null) {
                calib = line;
                line = br.readLine();
            }
        } catch (IOException ex){
            System.err.println("calibration value not available");
            ex.printStackTrace();
            calib = "6400";
        }
        return calib;
    }

    // logs the personal train info to the configuration file
    private void writePersonalConf(){
        File myFoo = new File(GlobalConfig.makePathGlobalString("VR_DRIVE/CONF/personalTrain.conf"));

        try{
            FileWriter fooWriter = new FileWriter(myFoo, false); // true to append
            // false to overwrite.
            fooWriter.write(this.experiment.getRewSequence());
            fooWriter.write(System.getProperty( "line.separator" ));
            fooWriter.write(this.experiment.getLapSequence());
            fooWriter.close();
        } catch (IOException exp){

        }

    }
}
