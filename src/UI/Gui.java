package UI;
import UI.editor.Editor;
import data_manager.DataManager;
import data_manager.DeviceManager;
import experiments.Experiment;

import mouse.Mouse;
import procedures.Procedure;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import global_vars.GlobalConfig;
/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is Gui in UI
 * created on 9/11/2016
 */

public class Gui {
    // main frame
    private JPanel mainFrame;
    // project loader
    private JButton loadButton;
    // project saver
    private JButton saveButton;
    // editing Panel
    private JTabbedPane Edit;
    // location for tree
    private JPanel listLoc;
    private JButton saveAs;
    private JButton devMan;


    // tree of project data
    private static DynamicTree selectionTree;
    private Path projectPath =  GlobalConfig.makePathGlobalPath("VR_DRIVE/DATA/CAGES/empty.cage");

    private File parentFolder = new File(projectPath.getParent().toString());
    private static String currentCage;

    // data manager object
    private static DataManager man;
    // tree root
    private  DefaultMutableTreeNode root = new DefaultMutableTreeNode("VR_MAZE");

    private JPanel reFreshPan;

    /**
     * constructor
     */
    public Gui() {

        //// TODO: 9/29/2016 pop log in window befor loading and allow for path selection
        // building editing panel


        man = DataManager.getManager();
        buildTree(projectPath);
        currentCage = projectPath.toString().replace("empty.cage","newCage.cage");
        Editor ed = new Editor();
        man.setEditingWin(ed);
        Editor.man = man;
        Edit.add(man.getEditingWin());
//        setEditing();

        // load button action
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File f = loadPath(parentFolder,"load",JFileChooser.FILES_ONLY,loadButton);
                System.out.println("loading file "+ f);
                if (f != null){
                    loadBuildTree(Paths.get(f.getAbsolutePath()));
                    currentCage = f.getAbsolutePath();
                }
//                autoSave();


            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                File f = loadPath(parentFolder,"load",JFileChooser.FILES_ONLY,loadButton);
//                System.out.println("loading file "+ f);
//                if (f != null){
//                    loadBuildTree(f.getAbsolutePath());
//                    currentCage = f.getAbsolutePath();
//                }
                Save();

            }
        });

        devMan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                man.getDevManager().OpenDeviceGui();
                man.setDevManager(DeviceManager.getDeviceManager());

            }
        });

        // save project action
        saveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // // TODO: 9/22/2016 fix save switch
//                Editor current = man.getEditingWin();
                man.setEditingWin(new Editor());
                File f = loadPath(parentFolder,"save",JFileChooser.FILES_ONLY, saveButton);
                System.out.println("save to "+f);
                if (f != null) {
                    testMan();
                    man.save(f.getAbsolutePath(),true);

                    currentCage = f.getAbsolutePath();
                }
//                else
//                    man.save(currentCage, false);
////                man.setEditingWin(current);
            }
        });
    }

    private void setEditing(){

        Editor ed = new Editor();
        man.setEditingWin(ed);
        Editor.man = man;
        Edit.removeAll();
        Edit.add(man.getEditingWin());

    }

    private static void autoSave(){
        man.save(currentCage, false);
    }

    private static void Save(){
        System.out.println("ctrl save %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        man.save(currentCage, true);
    }

    private File loadPath(File pth, String title, int op, JButton but) {
        File file = null;
        JFileChooser choice = new JFileChooser();

        choice.setCurrentDirectory(pth);
        choice.setDialogTitle(title);
        choice.setFileSelectionMode(op);

        if (choice.showOpenDialog(but) == JFileChooser.APPROVE_OPTION)
            file = choice.getSelectedFile();
        return file;
    }

    /**
     * todo: currently not working
     *
     */
    public static void goBack()
    {
        Gui.man.goBack();
    }


    private void buildTree(Path path)
    {
        Gui.man = man.load(path);
//        testMan();
        buildUtil();
    }

    private void testMan(){
        System.out.println("*****test block for man");
        HashMap<Integer, Experiment> map = man.getExperimentsList();
        for(Map.Entry<Integer, Experiment> exp: map.entrySet()){
            System.out.println(exp);
        }
        System.out.println("*****end test block for man");
    }

    private void loadBuildTree(Path path)
    {
        Gui.man = man.load(path);
        selectionTree.setMan(Gui.man);

        selectionTree.clear();
        listLoc.repaint();

        testMan();
        addExperiments();
        addMice();
        addProcedures();

        setEditing();
    }


    // helper
    private void buildUtil()
    {
        Gui.selectionTree = new DynamicTree(man);
        this.listLoc.add(selectionTree);
//        InputMap inputMap = selectionTree.getInputMap();
//        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK), "Save");
//        selectionTree.getActionMap().put("Save", new keyBaction());

        addExperiments();
        addMice();
        addProcedures();
    }

    // // TODO: 9/29/2016 fix
    public static void reloadTree()
    {
        System.out.println("load");
        addExperiments();
        addProcedures();
    }


    private static void addExperiments(){ //// TODO: 9/26/2016 use build in dynamic tree object
        HashMap<Integer, Experiment> map = man.getExperimentsList();
        for(Map.Entry<Integer, Experiment> exp: map.entrySet()){
            selectionTree.addExperiment(exp.getValue());
        }
    }

    private static void addMice(){
        HashMap<Integer, Mouse> map = man.getMiceList();
        for(Map.Entry<Integer, Mouse> maus: map.entrySet()){
            selectionTree.addMouse(maus.getValue());
        }
    }

    private static void addProcedures(){
        HashMap<Integer, procedures.Procedure> map = man.getProceduresList();
        for(Map.Entry<Integer, Procedure> pro: map.entrySet()) {
            selectionTree.addProcedure(pro.getValue());
        }
    }

    private static void addMouse()
    {
        Mouse maus = man.createMouse();
        selectionTree.addMouse(maus);
    }


    /**
     * add a new maze
     * @param e an event of pressing the add button in the tree
     */
    private void addMazeToTree(TreeSelectionEvent e) {
        Experiment exp = man.createExperiment();
//        bro.displayExp(exp);
       root.removeAllChildren();
    }

    /**
     * main
     * @param args cm
     */
    public static void main(String[] args) {


        //redirectErrFromConsole();
        //redirectOutFromConsole();


        // set nimbus vector gui style
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        // pack gui
        JFrame frame = new JFrame("Maus 3D");
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                   // Gui.autoSave();
                    System.exit(0); //// TODO: 12/19/2016 add end of run info
            }
            });

        frame.setContentPane(new Gui().mainFrame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 1000);
        frame.setVisible(true);
    }
//
//    public static void redirectErrFromConsole(){
//
//        File file = new File("C:\\Users\\owner\\Google Drive\\VR_DATA\\LOGS\\ERROR\\errors_"+(new Date()).toString().replaceAll("\\s|:","_")+".txt");
//        try{
//            FileOutputStream fos = new FileOutputStream(file);
//            PrintStream ps = new PrintStream(fos);
//            System.setErr(ps);
//        } catch (FileNotFoundException e){
//            System.err.println("error in redirecting sys.err");
//        }
//        System.err.println("###################################################");
//        System.err.println(new Date());
//        System.err.println("###################################################");
//    }
//
//    public static void redirectOutFromConsole(){
//
//
//
//
//
//        File file = new File("C:\\Users\\owner\\Google Drive\\VR_DATA\\LOGS\\OUTSTREAM\\out_"+(new Date()).toString().replaceAll("\\s|:","_")+".txt");
//        try{
//            FileOutputStream fos = new FileOutputStream(file);
//            PrintStream ps = new PrintStream(fos);
//            System.setOut(ps);
//        } catch (FileNotFoundException e){
//            System.err.println("error in redirecting sys.err");
//        }
//        System.out.println("###################################################");
//        System.out.println(new Date());
//        System.out.println("###################################################");
//    }





    private void createUIComponents() {
        // TODO: place cust
    }

    private class keyBaction extends AbstractAction{
        public keyBaction() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Gui.Save();
        }
    }
}