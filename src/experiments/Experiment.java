package experiments;

import IO.Protocol;
import UI.editor.StartPoint;
import mouse.Mouse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import global_vars.GlobalConfig;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is Experiment in experiments
 * created on 9/8/2016
 */

public class Experiment implements Serializable{
    private static final long serialVersionUID = 1L;
    // an Id
    private String nameId;
    // id of experiment
    private int id;
    // list of dates this experiment was preformed
    private LinkedList<Date> dateList = new LinkedList<>();
    // dates of experiment
    private Vector<RewardStation> rewardList;
    // an exe blender maze file
    private File maze;
    // a file with the location of the experiment data output directory
    private File location;
    // name identifier
    private String name;
    // reward symbol
    public static final Protocol REWARD = Protocol.REWARD;
    private String iconPath = GlobalConfig.makePathGlobalString("LAB_STRUCTURE/src/experiments/icon.png");
    // list of mice that ran this experiment
    private HashMap<Date, Mouse> exeLog = new HashMap<>();
    // start location
    private StartPoint start; // deprecated
    // last id that was set
    private int lastId = 1;
    //current mouse
    private transient Mouse curMaus;
    // free idx's
    private PriorityQueue<Integer> freeIds = new PriorityQueue<>();
    // the sequence of number of laps per number of rewards for personal train
    private String lapSequence = "";
    // the sequence of rewards for the personal train
    private String rewSequence = "";
    // probability of getting a tone indicator when the reward is active
    private double toneProbability = 1;
    // personal train path
    private String personalTrainLocation = "#";
    // personal mit tone indicator
    private boolean mitTone = false;
    // locking editing
    private boolean canEdit = true;


    /**
     *
     * @return the current mouse
     */
    public Mouse getCurMaus() {
        return curMaus;
    }

    /**
     * set the mouse that runs this maze
     * @param curMaus a mouse
     */
    public void setCurMaus(Mouse curMaus) {
        this.curMaus = curMaus;
    }

    /**
     *
     * @return the probability of a tone
     */
    public double getToneProbability() {
        return toneProbability;
    }

    /**
     * set probability of tone
     * @param toneProbability the probability to activate a tone station
     */
    public void setToneProbability(double toneProbability) {
        this.toneProbability = toneProbability;
    }

    /**
     * @return the sequence of laps per reward as a string from the personal configuration
     */
    public String getLapSequence() {
        if (!lapSequence.equals(""))
            return lapSequence;
        else{
            return "Assign Lap Lengths";
        }
    }

    /**
     *
     * @return True if this maze can be edited false otherwise
     */
    public boolean isCanEdit() {
        return canEdit;
    }

    /**
     * lock and open editing mode
     * @param canEdit True to open, False to lock
     */
    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    /**
     *
     * @return true if this is personal maze with a tone indicator
     */
    public boolean isMitTone() {
        return mitTone;
    }

    /**
     *
     * @param mitTone set tone activity
     */
    public void setMitTone(boolean mitTone) {
        this.mitTone = mitTone;
    }

    /**
     *
     * @return the path for the personal train configuration file
     */
    public String getPersonalTrainLocation() {
        return personalTrainLocation;
    }

    /**
     * sets the path for the personal train file
     * @param personalTrainLocation path of file
     */
    public void setPersonalTrainLocation(String personalTrainLocation) {
        this.personalTrainLocation = personalTrainLocation;
    }

    /**
     * @return the sequence of rewards as a string from the personal configuration
     */
    public String getRewSequence() {
        if (!rewSequence.equals(""))
            return rewSequence;
        else{
            return "Assign Reward Sequence";
        }
    }

    /**
     * clears the personal configuration
     */
    public void clearPersonal(){
        personalTrainLocation = "#";
        lapSequence = "";
        rewSequence = "";
        mitTone = false;
    }

    /**
     * Returns all the dates this maze was run along with all the mice who ran in it
     * @return a list of LogEntries, basically a list of tuples of a Date object and a Mouse object
     */
    public List<LogEntry> getLog(){
        List<LogEntry> logList = new LinkedList<>();

        for (Map.Entry<Date, Mouse> ent: this.getExeLog().entrySet()){
            logList.add(new LogEntry(ent.getValue(), ent.getKey()));
        }

        return logList;
    }

    public void setLapSequence(String lapSequence) {
        this.lapSequence = lapSequence;
    }

    public void setRewSequence(String rewSequence) {
        this.rewSequence = rewSequence;
    }

    /**
     *
     * @return the list of mice that this experiment was preformed on and the dates
     * for each mouse
     */
    public HashMap<Date, Mouse> getExeLog() {
        return exeLog;
    }

    /**
     * add a mouse this experiment was performed on
     * @param maus mouse
     */
    public void addMaus(Mouse maus)
    {
        exeLog.put(new Date(), maus);
    }

    /**
     *
     * @return the icon of an experiment
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     *
     * @return the start position
     */
    public StartPoint getStart() {
        return start;
    }

    /**
     * set start position
     * @param start pos start
     */
    public void setStart(StartPoint start) {
        this.start = start;
    }

    /**
     *
     * @return a vector with all the rewards in this experiment setting
     */
    public Vector<RewardStation> getRewardList() {
        return rewardList;
    }

    /**
     * add a list of rewards to this experiment
     * @param rewardList a vector of rewards
     */
    public void setRewardList(Vector<RewardStation> rewardList) {
        lastId = 1;
        for (RewardStation rew: rewardList)
            rew.setId(lastId++);

        this.rewardList = rewardList;
    }

    /**
     * add a reward station to excitement
     * @param rew reward station
     */
    public void setReward(RewardStation rew)
    {
        if (freeIds.isEmpty())
            rew.setId(lastId ++);
        else
            rew.setId(freeIds.poll());
        this.rewardList.add(rew);
    }

    /**
     * default constructor
     */
    public Experiment() {
        this.id = -1;
        this.rewardList = new Vector<>();
        this.exeLog = new HashMap<>();
    }

    /**
     * copy constructor
     * @param exp an experiment
     */
    public Experiment(Experiment exp) {
        this.id = -1;
        this.maze = exp.getMaze();
        this.location = exp.getLocation();
        this.rewardList = new Vector<>();
        for (RewardStation rew: exp.getRewardList())
        {
            this.rewardList.add(new RewardStation(rew));
        }

        // todo: experiment copier
        this.lapSequence = exp.lapSequence;
        this.rewSequence = exp.rewSequence;
    }

    public void clearRewards(){
        this.rewardList = new Vector<>();
    }
    /**
     *
     * @return the id of this experiment
     */
    public int getId() {
        return id;
    }

    /**
     * constructor
     * @param maze a blender exe maze file
     * @param location a location for the data output
     */
    public Experiment(File maze, File location) {
        this.id = -1;
        this.maze = maze;
        this.location = location;
        this.exeLog = new HashMap<>();
    }

    /**
     * @return the current mazed used in the experiment
     */
    public File getMaze() {
        return maze;
    }

    /**
     * set a new maze
     * @param maze blender exe file
     */
    public void setMaze(File maze) {
        this.maze = maze;
    }

    /**
     * @return the location of the data output
     */
    public File getLocation() {
        return location;
    }

    /**
     * set the location for data output
     * @param location file dir
     */
    public void setLocation(File location) {
        this.location = location;
    }

    /**
     * saves the experiment's settings to a conf file
     * @param rConf path to config directory
     */
    public String saveToConfig(String rConf)
    {
        System.out.println(rConf + "==============================");
        rConf = GlobalConfig.makePathSysytemIn(rConf + "/reward");
        File rconf = new File(rConf);
        //rconf.delete();
        System.out.println("conFig -------------------------- " + rConf);

        // add reward stations to reward config file
        for (RewardStation rew: rewardList)
        {
            rew.saveToConfig(rConf);
        }

//        try
//        {
//            FileWriter fw = new FileWriter(location, true);
//            fw.write(REW + "#" +rConf);
//            fw.close();
//        }
//        catch(IOException ioe)
//        {
//            System.err.println("IOException: " + ioe.getMessage());
//        }
        return rConf;
    }

    /**
     * add a date this experiment was executed
     * @param date date
     */
    public void addDate(Date date)
    {
//        this.dateList.add(date);
    }

    /**
     * @param id an id of the experiment
     */
    public void setId(int id) throws HandlingExperimentError {
        if (this.id != -1)
            throw new HandlingExperimentError();
        this.id = id;
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "id=" + id +
                '}';
    }

    /**
     * remove a reward station
     * @param rew station to remove
     */
    public void removeReward(RewardStation rew)
    {
        freeIds.add(rew.getId());
        this.rewardList.removeElement(rew);
    }

    /**
     *
     * @return a unique string representing a current deployment of this experiment
     */
    public String getName(){
        String DATE_FORMAT = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
//        String name = "Exp"+ id +"_"+curMaus.toString().replaceAll(" ","")+"_"+ df.format(new Date()).replaceAll("/","_");
        String name = "Exp"+ id +"_"+curMaus.toString().replaceAll(" ","")+"_"+ sdf.format(new Date()).replaceAll("/","_");

        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minuts = rightNow.get(Calendar.MINUTE);
        name += "_{"+hour+"_"+minuts+"}"; 

        return name;
    }
}
