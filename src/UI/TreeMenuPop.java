package UI;

import experiments.Experiment;

import javax.swing.*;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is TreeMenuPop in UI
 * created on 9/15/2016
 */

public class TreeMenuPop extends JPopupMenu {
    public JMenuItem addExperiment;
    public JMenuItem addProcedure;
    public JMenuItem editExperiment;
    public JMenuItem editProcedure;
    public JMenuItem editMouse;
    public JMenuItem addMouse;
    public JMenuItem addReward;
    public JMenuItem delReward;
    public JMenuItem editReward;
    public JMenuItem viewRep;
    public JMenuItem viewLoc;
    Experiment newExp = null;
    public enum SELECT {MOUSE, EXPERIMENT, PROCEDURE, EXPM, PROM, ADD_MOUSE, EXPERIMENT_ADD, REWARD, REW_ED, TABLE_VIEW}

    public TreeMenuPop(SELECT op){
        addExperiment = new JMenuItem("Add an Experiment");
        addProcedure = new JMenuItem("Add a Procedure");
        editExperiment = new JMenuItem("Edit Experiment");
        editProcedure = new JMenuItem("Edit Procedure");
        editMouse = new JMenuItem("Edit Mouse");
        addMouse = new JMenuItem("Add Mouse");
        addReward = new JMenuItem("Add Reward Station");
        editReward = new JMenuItem("Edit Reward Station");
        delReward = new JMenuItem("delete Reward Station");
        viewRep = new JMenuItem("open report file");
        viewLoc = new JMenuItem("open data directory");
        switch (op){
            case MOUSE:
                add(addExperiment);
                add(addProcedure);
                add(editMouse);
                break;
            case EXPERIMENT:
                add(addExperiment);
                add(editExperiment);
                break;
            case PROCEDURE:
                add(addProcedure);
                add(editProcedure);
                break;
            case EXPM:
                add(editExperiment);
                break;
            case PROM:
                add(editProcedure);
                break;
            case ADD_MOUSE:
                add(addMouse);
                break;
            case EXPERIMENT_ADD:
                add(addExperiment);
                break;
            case REW_ED:
                add(editReward);
                add(delReward);
                break;
            case REWARD:
                add(addReward);
                break;
            case TABLE_VIEW:
                add(viewLoc);
                add(viewRep);
                break;
        }

    }
}
