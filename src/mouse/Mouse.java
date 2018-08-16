
package mouse;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import UI.Gui;
import experiments.*;
import procedures.Procedure;
import global_vars.GlobalConfig;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version: 1.0
 *
 * this is Mouse in mouse
 * created on 9/8/2016
 */
public class Mouse implements Serializable{
    private static final long serialVersionUID = 1L;

    // age of mouse in days
    private int age;
    // id number in system
    private int idNumber;
    // map of experiments by id
    private HashMap<Integer, Experiment> experiments;
    // map of procedures by id
    private HashMap<Integer, Procedure> procedures;
    // map of weight by date todo: and maybe time if weighed more than once
    private LinkedList<Weight> weight;
    // date logged
    private Date dateLogged;
    //path to mouse icon
    private String iconPath = GlobalConfig.makePathGlobalString("LAB_STRUCTURE/src/mouse/icon.png");
    // type of the mouse
    private TYPES type = TYPES.WILD;
    // id name of mouse subject to change
    private String idName;
    // date of farewell
    private Date RIP = null;
    // map of experiments by id
    private HashMap<Date, Experiment> tookPlace = new HashMap<>();
    //
    private HashMap<Date, String> reportLog;

    private Date assisDate;


//    public HashMap<Date, String> getReportLog() {
//        return reportLog;
//    }

    public void setReportPath(String path){
        reportLog.put(assisDate, path);
    }

    public String getReportPath(Date date){
        return reportLog.get(date);
    }


    /**
     * @return get exp preformed on this mouse
     */
    public HashMap<Date, Experiment> getTookPlace() {
        return tookPlace;
    }

    /**
     * add an experement that was preform //todo: try maybe after ex
     * @param exp experiment
     */
    public void expRanOnMouse(Experiment exp)
    {
        addExperiment(exp);
        assisDate = new Date();
        tookPlace.put(assisDate, exp);
    }
    /**
     * @return the name in which the mouse is id'ed by
     */
    public String getIdName() {
        if (idName == null)
            return "Mouse{ "+ idNumber + " }";
        return idName;
    }

    /**
     * set an identifier name
     * @param idName name
     */
    public void setIdName(String idName) {
        this.idName = idName;
    }

    /**
     * @return the icon for a mouse
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     * default constructor
     */
    public Mouse() {
        initMembers();
    }

    /**
     * constructor
     * @param idNumber id of mouse
     * @param age set age in weeks
     */
    public Mouse(int idNumber, int age) {
        this.idNumber = idNumber;
        this.age = age;
        this.initMembers();
    }

    /**
     * @return the type of the mouse
     */
    public TYPES getType() {
        return type;
    }

    /**
     * set the type of the mouse
     * @param type USE ENUM TYPES
     */
    public void setType(TYPES type) {
        this.type = type;
    }

    /*
     * default initialization
     */
    private void initMembers()
    {
        this.experiments = new HashMap<>();
        this.procedures = new HashMap<>();
        this.reportLog = new HashMap<>();
        this.weight = new LinkedList<>();

        dateLogged = new Date();
    }

    /**
     * @return the weight of the mouse
     */
    public float getCurrentWeight()
    {
        try {
            return weight.getLast().getWeight();
        } catch (NoSuchElementException e)
        {
            return 0;
        }
    }

    /**
     * @return the age of the mouse in days
     */
    public int getAge() {
        // get today's date
        Date today = new Date();
        // calculate difference in days between logged date and current date
        int diff =  (int) Math.ceil((today.getTime() - dateLogged.getTime()) / (double) 86400000);
        // convert to weeks
        diff = (int) Math.ceil(diff / 7);
        return (this.age + diff);
    }

    /**
     * set the age of the mouse
     * @param age in days
     */
    public void setAge(int age) {

        this.age = age;
    }

    /**
     * @return the id number of the mouse
     */
    public int getIdNumber() {
        return idNumber;
    }

    /**
     * setting id todo: change this to be locked like exp is
     * @param idNumber id number
     */
    public void setIdNumber(int idNumber) { //// TODO: 9/8/2016 : might not want to allow this
        this.idNumber = idNumber;
    }

    /**
     * @return a map of experiments and their ids as keys
     */
    public HashMap<Integer, Experiment> getExperiments() {
        return experiments;
    }

    /**
     * @return a map of procedures and their ids as keys
     */
    public HashMap<Integer, Procedure> getProcedures() {
        return procedures;
    }

    /**
     * add an experiment to this mouse
     * @param experiment to log
     */
    public void setExperiments(Experiment experiment) {
        this.experiments.put(experiment.getId(), experiment);
    }

    /**
     * add a procedure to this mouse
     * @param procedure to log
     */
    public void setProcedures(Procedure procedure) {
        this.procedures.put(procedure.getId(), procedure);
    }

    /**
     * log current weight, add the weight measured to the current date
     * @param weight weight
     */
    public void logWeight(float weight) {
        this.weight.add(new Weight(null, weight));
    }

    /**
     * log current weight, add the weight measured to the current date
     * @param weight weight
     * @param date date
     */
    public void logWeight(float weight, Date date) {
        this.weight.add(new Weight(date, weight));
    }

    /**
     * @return a map of mouse weight according to dates
     */
    public LinkedList<Weight> getWeight() {
        return weight;
    }

    /**
     * log current weight, add the weight measured on the provided date
     * @param date of measurement
     * @param weight weight
     */
    public void logWeight(Date date, float weight) {

        this.weight.add(new Weight(date, weight));
    }

    @Override
    public String toString() {
        if (aliveAndKicking()) {
            if (idName == null)
                return "Mouse{ "+ idNumber + " }";
            return idName;
        }
        else
            return "deadMaus{ "+ idNumber + " }";
    }

    public String sysId()
    {
        if (aliveAndKicking())
            return "Mouse{ "+ idNumber + " }";
        return "deadMaus{ "+ idNumber + " }";
    }

    /**
     * add an experiment
     * @param exp experiment to add
     */
    public void addExperiment(Experiment exp)
    {
        this.experiments.put(exp.getId(), exp);
    }

    /**
     * kill this mouse
     * @param lastDayOnEarth null to add this date, other date
     */
    public void KILL(Date lastDayOnEarth){
        RIP = (lastDayOnEarth == null) ? new Date(): lastDayOnEarth;

        iconPath = GlobalConfig.makePathGlobalString("LAB_STRUCTURE/src/mouse/deadMaus.png");
    }

    /**
     * @return true if mouse is still alive false otherwise
     */
    public boolean aliveAndKicking(){
        return RIP == null;
    }

    public int compareTo(Mouse anotherMause){
        return idName.compareTo(anotherMause.getIdName());
    }
}
