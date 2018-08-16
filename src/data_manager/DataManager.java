package data_manager;

import UI.editor.Editor;
import experiments.Experiment;
import experiments.HandlingExperimentError;
import experiments.RewardStation;
import mouse.Mouse;
import procedures.Procedure;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is DataManager in data_manager
 * created on 9/13/2016
 */

public class DataManager implements Serializable{
    private static final long serialVersionUID = 1L;
    //singleton access
    private static DataManager self = null;
    // all the experiments in the project
    private HashMap<Integer, Mouse> miceList;
    // all the procedures available
    private HashMap<Integer, Procedure> proceduresList;
    // all the experiments
    private HashMap<Integer, Experiment> experimentsList;
    // current mouse
    private transient Mouse currentMouse;
    // edit panel
    private transient Editor editingWin;
    // previous edit panel
    private transient Editor prevEditingWin;
    // list of devices
    private DeviceManager devManager;



    /**
     *
     * @return the current editing panel
     */
    public Editor getEditingWin() {
        return editingWin;
    }

    /**
     * change the current editing window
     * @param editingWin a new editing window
     */
    public void setEditingWin(Editor editingWin) {
        if (this.editingWin == null)
            this.editingWin = editingWin;
        else
        {
            prevEditingWin = this.editingWin;
            this.editingWin.setEditor(editingWin);
        }
    }

    //// TODO: 9/29/2016 return to periviouse editing pannel currently not working
    public void goBack()
    {
        if (prevEditingWin != null)
        {
            this.editingWin = prevEditingWin;
            prevEditingWin = null;
        }

    }

    /**
     * @return the current selected mouse
     */
    public Mouse getCurrentMouse() {
        return currentMouse;
    }

    /**
     *
     * @param currentMouse current selected mouse
     */
    public void setCurrentMouse(Mouse currentMouse) {
        this.currentMouse = currentMouse;
    }

    /**
     * id creator helper
     */
    public enum OPTIONS {MOUSE, EXPERIMENT, PROCEDURE}

    /**
     *
     * @return a manager;
     */
    public static DataManager getManager(){
        if (self != null)
            return self;

        return new DataManager();
    }

    /**
     * hidden constructor
     */
    private DataManager() {

        this.miceList = new HashMap<>();
        this.proceduresList = new HashMap<>();
        this.experimentsList = new HashMap<>();
        DeviceManager.resetManager();
        this.devManager = DeviceManager.getDeviceManager();
        System.out.println("constructing");
    }

    /**
     * @return the current device manager
     */
    public DeviceManager getDevManager() {
        return devManager;
    }

    /**
     * set a device manager
     * @param devManager device manager
     */
    public void setDevManager(DeviceManager devManager) {
        this.devManager = devManager;
    }

    /**
     * generates an id
     * @param op a field to add
     * @return the new id for the field
     */
    public int generateID(OPTIONS op)
    {
        int newId = 0;
        switch (op){
            case MOUSE:
                newId = miceList.size();
                break;
            case EXPERIMENT:
                newId = experimentsList.size();
                break;
            case PROCEDURE:
                newId = proceduresList.size();
                break;
        }
        newId++;
        return newId;
    }

    /**
     * add a procedure to the data base
     * @param proc procedure
     * @return true if was added false otherwise
     */
    public boolean addProcedure(Procedure proc) {

        if (!proceduresList.containsValue(proc)) {

            proc.setId(generateID(OPTIONS.PROCEDURE));
            proceduresList.put(proc.getId(), proc);
            return true;
        }
        return false;
    }

    /**
     * add a mouse to the data base
     * @param maus mouse to add
     * @return true if was added false otherwise
     */
    public boolean addMouse(Mouse maus){

        if (!miceList.containsValue(maus)){
            maus.setIdNumber(generateID(OPTIONS.MOUSE));
            miceList.put(maus.getIdNumber(), maus);
            return true;
        }
        return false;
    }

    /**
     * add an experiment to the data base
     * @param exp experiment
     * @return true if was added false otherwise
     */
    public boolean addExperiment(Experiment exp){

        if (!experimentsList.containsValue(exp))
        {
            try {
                exp.setId(generateID(OPTIONS.EXPERIMENT));
                experimentsList.put(exp.getId(), exp);
                return true;
            }catch (HandlingExperimentError error)
            {
                System.err.println("trying to edit an experiment");
            }
        }
        System.out.println("Multi exp add");
        return false;
    }

    /**
     * @return the DS of all the mice in the DB
     */
    public HashMap<Integer, Mouse> getMiceList() {
        return miceList;
    }

    /**
     * @return the DS of all the procedures in the DB
     */
    public HashMap<Integer, Procedure> getProceduresList() {
        return proceduresList;
    }

    /**
     * @return the DS of all the experiments in the DB
     */
    public HashMap<Integer, Experiment> getExperimentsList() {
        return experimentsList;
    }

    /**
     * returns the experiment object with the id
     * @param key key to search
     * @return an experiment with the key
     */
    public Experiment getExperiment(int key) {
        return experimentsList.get(key);
    }

    /**
     * returns the mouse object with the id
     * @param key key to search
     * @return an mouse with the key
     */
    public Procedure getProcedure(int key) {
        return proceduresList.get(key);
    }

    /**
     * returns the mouse object with the id
     * @param key key to search
     * @return a mouse with the key
     */
    public Mouse getMouse(int key) {
        return miceList.get(key);
    }

    /**
     * loads a project from a file
     * @param path path of file
     * @return a DataManager for the project
     */
    public DataManager load(Path path){

        try (InputStream in = new FileInputStream(path.toFile()); ObjectInputStream ois = new ObjectInputStream(in))
        {
            self = ((DataManager) ois.readObject());

            if (self.devManager == null){
                DeviceManager.resetManager();
                self.devManager = DeviceManager.getDeviceManager();

            }
        }catch (IOException e)
        {
            System.err.println("###################################################");
            System.err.println("Error related to serialization while loading cage");
            System.err.println("###################################################");
            e.printStackTrace();

        }catch (ClassNotFoundException y)
        {
            System.err.println("File does not contain DataManager object");
        }
        return self;
    }

    /**
     * save project
     * @param path path to save
     */
    public void save(String path, boolean newPath)
    {
        if(!newPath)
            path = getAvailablePath(path);
        else
        {
            System.out.println(new File(path).delete());
        }
        try(OutputStream out = new FileOutputStream(path); ObjectOutputStream oos = new ObjectOutputStream(out)){
            System.out.println("final save " + path);
            oos.writeObject(this);

        }catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("###################################################");
            System.err.println("Error related to serialization save");
            System.err.println("###################################################");
        }
    }

    // sets an incrementing count for a file
    // this method generates a new path if the given path already exists
    private String getAvailablePath(String path){
        File loc = new File(path);
        int num = 1;
        while (true) {
            if (!loc.exists()) {
                return loc.getAbsolutePath();
            }
            else {
                String suf = "_" + num +".cage";
                loc = new File(path.replace(".cage",suf));
                num++;
            }
        }
    }

    /**
     * @return a new experiment with a valid system id
     */
    public Experiment createExperiment(){
        Experiment newExp = new Experiment();
        this.addExperiment(newExp);
        return newExp;
    }

    /**
     * @return a new mouse with a valid system id
     */
    public Mouse createMouse(){
        Mouse maus = new Mouse();
        this.addMouse(maus);
        return maus;
    }

}
