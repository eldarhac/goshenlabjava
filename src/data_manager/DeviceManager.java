package data_manager;

import global_vars.GlobalConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * The device manager is a singleton class designed
 * to encapsulate the system communication ports for each device used
 * by the VR.
 * This class stores a list of devices that match a functional name (i.e "Lickometer") with
 * a system port (i.e "COM1")
 *
 * this class allows the existence of multiple types of devices, including information
 * about UDP communication required by the encoder system
 */
public class DeviceManager implements Serializable{
    // not a very good serial id
    private static final long serialVersionUID = 1L;
    // an array of devices supported by the system
    private Device[] deviceSystemIds;
    // number of devices supported by the system
    private int numDevs = 0;
    // a char to indicate an empty port
    private String emptyDev = "#";
    // a list of devices required by the system,
    // edit this list by adding the user friendly device name to extend
    // system devices support
    private String[] devNames = new String[]{"Encoder", "Reward","IP,PORT", "RATE"};
    // singleton instance
    private static DeviceManager self = null;

    /**
     * a singleton get constructor
     * @return a device manager instance
     */
    public static DeviceManager getDeviceManager(){
        if (self == null){
            try{
                DeviceManager.loadDefault(GlobalConfig.makePathGlobalPath("/VR_DRIVE/CONF/device_configuration"));
            } catch (IOException e){
                self = new DeviceManager();
            }

        }

        return self;
    }

    /**
     * resets the device manager by setting
     * its self pointer to null, make sure to call on a new
     * get if wished to continue using it
     */
    public static void resetManager(){
        self = null;
    }

    // hidden constructor
    private DeviceManager() {
        deviceSystemIds = new Device[devNames.length];

        for (int i = 0; i < devNames.length; i++){
            deviceSystemIds[i] = new Device(devNames[i], emptyDev);
            System.out.println(devNames[i]);
        }

        this.numDevs = devNames.length;

    }

    /**
     * editing window for device manager
     * this window allows to graphically edit the devices' information
     * save and load configuration files
     */
    public void OpenDeviceGui(){

        // building main frame for window
        JFrame frame = new JFrame("Device Manager");
        JPanel devicePanel = new JPanel();

        // a list of panels to store each device's editing controls
        JPanel[] oneDevPanel = new JPanel[self.numDevs];
        JTextField[] device = new JTextField[self.numDevs];
        JLabel[] deviceTextFieldLabel = new JLabel[self.numDevs];


        // creating editing controls for each device
        for (int i = 0; i < self.numDevs; i++){

            oneDevPanel[i] = new JPanel();
            oneDevPanel[i].setLayout(new FlowLayout());
            deviceTextFieldLabel[i] = new JLabel(self.deviceSystemIds[i].getName(), JLabel.LEFT);

            device[i] = new JTextField(self.deviceSystemIds[i].getSystemIdName(), 15);
            oneDevPanel[i].add(deviceTextFieldLabel[i]);
            oneDevPanel[i].add(device[i]);
            devicePanel.add(oneDevPanel[i]);
        }

        // creating function buttons
        JButton set = new JButton("set");
        JButton clear = new JButton("clear");
        JButton saveBut = new JButton("save");
        JButton load = new JButton("load");

        // action for setting new values for devices
        set.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < self.numDevs; i++){
                    self.deviceSystemIds[i].setSystemIdName(device[i].getText());
                }
                frame.setVisible(false);
                frame.dispose();
            }
        });

        // action for resetting devices
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < numDevs; i++){
                    deviceSystemIds[i].setSystemIdName(emptyDev);
                }
                frame.setVisible(false);
                frame.dispose();
            }
        });


        // action for saving devices
        saveBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String path = getFilePath(new File(GlobalConfig.makePathGlobalString("/VR_DRIVE/CONF")), "Saving Conf file",JFileChooser.FILES_ONLY ,saveBut, "save");

                if (path != null) {
                    for (int i = 0; i < self.numDevs; i++){
                        self.deviceSystemIds[i].setSystemIdName(device[i].getText());
                    }
                    try (OutputStream out = new FileOutputStream(path); ObjectOutputStream oos = new ObjectOutputStream(out)) {
                        oos.writeObject(self);
                        System.out.println("saving to conf");
                    } catch (IOException exp) {
                        System.err.println("couldn't save configuration file");
                    }

                }

                frame.setVisible(false);
                frame.dispose();
            }
        });

        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = getFilePath(new File(GlobalConfig.makePathGlobalString("/VR_DRIVE/CONF")), "Saving Conf file",JFileChooser.FILES_ONLY ,load, "load");

                if (path != null) {
                    try (InputStream in = new FileInputStream(path); ObjectInputStream ois = new ObjectInputStream(in))
                    {

                        self = ((DeviceManager) ois.readObject());
                        for (int i = 0; i < self.numDevs; i++){
                            System.out.println(device[i].getText());
                            self.deviceSystemIds[i].setSystemIdName(device[i].getText());
                        }
                    }catch (IOException exp)
                    {
                        System.err.println("can't find file to open device manager");
                    }catch (ClassNotFoundException y)
                    {
                        System.err.println("File does not contain DataManager object");
                    }

                }
                frame.setVisible(false);
                frame.dispose();
            }
        });

        // pack all panels into frame
        JPanel control = new JPanel(new FlowLayout());
        JPanel loadSave = new JPanel(new FlowLayout());


        loadSave.add(load);
        loadSave.add(saveBut);
        control.add(set);
        control.add(clear);
        devicePanel.add(control);
        devicePanel.add(loadSave);
        frame.add(devicePanel);
        frame.setSize(250, 300);
        frame.setVisible(true);
    }


    // load default device manager
    private static void loadDefault(Path path) throws IOException{
        try (InputStream in = new FileInputStream(path.toFile()); ObjectInputStream ois = new ObjectInputStream(in))
        {
          self = (DeviceManager) ois.readObject();

        }catch (ClassNotFoundException y)
        {
            System.err.println("File does not contain Device Manager object");
        }
    }

    /**
     * return the system id for a device
     * @param devName a user name for the device
     * @return system id for device
     */
    public Device getDevSysID(String devName){
        return Arrays.stream(deviceSystemIds).filter(x->x.getName().equals(devName)).findFirst().get();
    }

    /*
     *
     * @param pth default load path
     * @param title , title of load window
     * @param op, loading option
     * @param but, call button
     * @param apMsg, confirmation masg
     * @return path of file
     */
    private String getFilePath(File pth, String title, int op, JButton but, String apMsg) {
        File file = null;
        JFileChooser choice = new JFileChooser();

        choice.setCurrentDirectory(pth);
        choice.setDialogTitle(title);
        choice.setFileSelectionMode(op);
        choice.setApproveButtonText(apMsg);

        if (choice.showOpenDialog(but) == JFileChooser.APPROVE_OPTION)
            file = choice.getSelectedFile();
        if (file != null)
            return file.getAbsolutePath();
        return null;

    }

    /**
     * default value 500
     * @return returns the daq sample rate
     */
    public String getRate(){
        String val = getDevSysID("RATE").getSystemIdName();
        if (val.equals(emptyDev)){
            return "500";
        }

        return val;
    }
//
//    public static void main(String[] args) {
//        DeviceManager dm = DeviceManager.getDeviceManager();
//        dm.OpenDeviceGui();
//        System.out.println(dm.getDevSysID("Reward").getSystemIdName());
//        System.out.println(dm.getRate());
//    }
}
