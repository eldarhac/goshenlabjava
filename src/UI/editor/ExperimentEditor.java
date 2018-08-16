package UI.editor;

import IO.Deploy;
import UI.TreeMenuPop;
import experiments.Experiment;
import experiments.LogEntry;
import experiments.RewardStation;
import mouse.Mouse;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import global_vars.GlobalConfig;

import static global_vars.GlobalConfig.makePathGlobalString;


/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is ExperimentEditor in UI.editor
 * created on 9/15/2016
 */

    // // TODO: 9/26/2016 lock edit option
public class ExperimentEditor extends Editor {

    // the current experiment being edited
    private Experiment currentExp;
    // lock fields
    private boolean editable = false;
    // list of reward stations
    private JList<RewardStation> rewards;
    private JPanel workSpace;
    // control panel
    private JPanel locManPanel;
    // reward list panel
    private JPanel rewardPanel;
    // name tag
    private JLabel expName;
    // visual maze editor
//    private EditCanvas can;
    // display name of maze
    private JTextField mazePath;
    // display the data folder
    private JTextField locationPath;
    //personal train
    private JTextField rewardNumSequence;
    //personal train
    private JTextField rewardLapSequence;
//    private JPanel personal;
    // panel to load tools
    private JPanel toolPanel;
    // stat
    private JPanel statPanel;
    // indicator for maze deployment
    private boolean readyToDeploy;
    // button to activate maze
    private JButton run;
    // deploy object for maze deploying
    private Deploy dep;
    // process running the maze
    private Process runningMaze;
    // indicates if the maze is running
    private boolean running = false;
    // current mouse
    private Mouse currentMouse;
    // division upper
    private JPanel upperPanel;
    // division lower
    private JPanel lowerPanel;

    private boolean sortByDate = false;

    /**
     * constructor
     * @param currentExp the experiment to edit
     * @param editable true if editing is enabled
     */
    public ExperimentEditor(Experiment currentExp, boolean editable) {
        this.currentExp = currentExp;
        this.editable = editable;
        rewards = new JList<>(currentExp.getRewardList());
        rewards.addMouseListener(new PickReward());
//        can = new EditCanvas(currentExp);

       doWin();
    }

    private void doWin(){
        super.removeAll();
        this.removeAll();
        toolPanel = new JPanel(new FlowLayout());
        toolPanel.setMaximumSize(new Dimension(500, 40));
        setWin();
        buildInfoPanel();
        createWin();
        buildStatPanel();
        super.revalidate();
        super.repaint();
    }

    /**
     * reload the list of reward station
     */
    private void reloadList()
    {
        rewards.setListData(currentExp.getRewardList());
        rewards.repaint();
//        can.repaint();
//        rewards = new JList<>(currentExp.getRewardList());
//        rewards.addMouseListener(new PickReward());

    }

    /**
     * add panels to window
     */
    private void setWin()
    {
        rewardPanel = new JPanel(new FlowLayout());
        expName = new JLabel(currentExp.toString());
        JScrollPane pane = new JScrollPane(rewards, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setMaximumSize(new Dimension(200, 250));
        pane.setMinimumSize (new Dimension (200,250));
        rewardPanel.add(pane);
        rewards.setFixedCellWidth(50);
//        rewardPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        rewardPanel.setLayout(new BoxLayout(rewardPanel, BoxLayout.Y_AXIS));
    }

    /**
     * build info panel
     */
    private void buildInfoPanel()
    {
//        personal = new JPanel();
//        personal.setLayout(new BoxLayout(personal, BoxLayout.PAGE_AXIS));
        locManPanel = new JPanel();
        locManPanel.setLayout(new BoxLayout(locManPanel, BoxLayout.PAGE_AXIS));
        JPanel MazePan = new JPanel(new FlowLayout());
        JPanel locPanel = new JPanel(new FlowLayout());

        JButton maze = new JButton("Set Maze");
        JButton location = new JButton("Location");
        JButton modeChange = new JButton("Personal");
        run = new JButton();
        run.setIcon(setDeploy());
        modeChange.setIcon(setIconEdit());
        mazePath = new JTextField(name(currentExp.getMaze()), 12);
        locationPath = new JTextField(name(currentExp.getLocation()), 12);

        if (currentExp.getMaze() == null)
            mazePath.setBorder(BorderFactory.createLineBorder(Color.red));
        if (currentExp.getLocation() == null)
            locationPath.setBorder(BorderFactory.createLineBorder(Color.red));

        mazePath.setEditable(false);
        locationPath.setEditable(false);
        MazePan.add(maze);
        MazePan.add(mazePath);
        locPanel.add(location);
        locPanel.add(locationPath);

        JPanel filePan = new JPanel();
        filePan.setLayout(new BoxLayout(filePan, BoxLayout.Y_AXIS));
        filePan.setMaximumSize(new Dimension(300,80));

        // add a an exe blender file
        maze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//
                if (!currentExp.isCanEdit())
                    return;
                File root = new File(makePathGlobalString("VR_DRIVE/EXE_MAZE"));

                FileNameExtensionFilter filter = new FileNameExtensionFilter("Application", ".exe", "exe", ".blend", "blend");
                File currentMaze = loadPath(root, "add maze", JFileChooser.FILES_ONLY, maze, filter);
                if (currentMaze != null)
                    currentExp.setMaze(currentMaze);
                mazePath.setText(name(currentExp.getMaze()));
                if (currentExp.getMaze() != null)

                    mazePath.setBorder(BorderFactory.createLineBorder(Color.green));
                else
                    mazePath.setBorder(BorderFactory.createLineBorder(Color.red));
                run.setIcon(setDeploy());
//                can.mazeMap();
//                can.repaint();
            }
        });

        // add data location folder
        location.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                File root = new File("C:\\Users\\owner\\Documents\\TEST\\loc_test");
                File root = new File(makePathGlobalString("DATA"));
                File currentLocation = loadPath(root, "add maze", JFileChooser.DIRECTORIES_ONLY, location, null);
                if (currentLocation != null)
                    currentExp.setLocation(currentLocation);
                locationPath.setText(name(currentExp.getLocation()));
                if (currentExp.getLocation() != null)
                    locationPath.setBorder(BorderFactory.createLineBorder(Color.green));
                else
                    locationPath.setBorder(BorderFactory.createLineBorder(Color.red));

                run.setIcon(setDeploy());
            }
        });

        // switch between reward editor and start editor
        modeChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//               canvas = new EditorFrame(currentExp);
//                can.mode = (can.mode == 0) ? 1 : 0;
//                modeChange.setIcon(setIconEdit());
                personalConfiguration();
            }
        });

        // call on deploy for current maze
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (readyToDeploy)
                {
                    if (currentMouse != null)
                    {
//                        setPersonal();
                        currentExp.setCurMaus(currentMouse);
                        dep = Deploy.getDeploy(currentExp.getMaze(), currentExp, currentExp.getLocation(), man);
                        long s = System.currentTimeMillis();
                        runningMaze = dep.run();
                        running = true;
                        currentMouse.expRanOnMouse(currentExp);
                        currentExp.addMaus(currentMouse);
                        JFrame blackframe = new JFrame();
                        blackframe.getContentPane().setBackground( Color.black );
                        blackframe.setSize(1920, 1080);
                        blackframe.setVisible(true);
                        //run.setIcon(setDeploy());
                        try{
                            runningMaze.waitFor();
                            TimeUnit.SECONDS.sleep(2);
                            dep.stop();
                            createEndOfExeReport();
                            System.out.println("ended "+ (System.currentTimeMillis() - s));
                        } catch (InterruptedException err){
                            System.err.println("error in maze threading ");
                        }

                    }
                    else{
                        JOptionPane.showMessageDialog(null,
                                "Please select a mouse",
                                "Warning",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
                else
                {

                    String msg = "please select a maze and a directory";
                    if(currentExp.getMaze() == null && currentExp.getLocation() != null)
                        msg = "please select a maze";
                    if (currentExp.getMaze() != null && currentExp.getLocation() == null)
                        msg = "please select a data directory";

                    JOptionPane.showMessageDialog(null,
                            msg,
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);


                }
            }

        });

        JButton autoRew = new JButton("Auto");
        JButton tone = new JButton("Tone");
        tone.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            setTone();
            }
        });


        autoRew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autoReward();
            }
            });
        JButton editable = new JButton(currentExp.isCanEdit() ? "lock": "locked");
        editable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentExp.setCanEdit(false);
                doWin();
            }
        });

        filePan.add(MazePan);
        filePan.add(locPanel);

        locManPanel.add(filePan);
        toolPanel.add(modeChange);
        toolPanel.add(autoRew);
        toolPanel.add(run);
//        toolPanel.add(tone);
        toolPanel.add(editable);

    }

    private void setPersonal(String path, boolean mitToneIndicator){
        currentExp.setLapSequence(rewardLapSequence.getText());
        currentExp.setRewSequence(rewardNumSequence.getText());
        currentExp.setPersonalTrainLocation(makePathGlobalString(path));
        currentExp.setMitTone(mitToneIndicator);
        File myFoo = new File(makePathGlobalString(path));

        try{
            FileWriter fooWriter = new FileWriter(myFoo, false); // true to append
            // false to overwrite.
            fooWriter.write(rewardNumSequence.getText());
            fooWriter.write(System.getProperty( "line.separator" ));
            fooWriter.write(rewardLapSequence.getText());
            fooWriter.close();
        } catch (IOException exp){

        }
    }

    public void setTone(){
        JFrame frame = new JFrame("Edit: tone");
//        JTextField toneLable = new JTextField("tone", 4);
//        JTextField y = new JTextField(Float.toString(rew.getY()), 4);
//        JTextField z = new JTextField(Float.toString(rew.getZ()), 4);
//        JTextField r = new JTextField(Float.toString(rew.getrSize()), 4);

        JSlider prob = new JSlider(JSlider.HORIZONTAL, 0, 10, 5);
        prob.setMajorTickSpacing(2);
        prob.setMinorTickSpacing(1);
        prob.setPaintTicks(true);
        prob.setPaintLabels(true);
        JPanel buttonPane = new JPanel();
        JPanel fieldsPanel = new JPanel();
//        JPanel tonePanel = new JPanel();
//        JPanel yPanel = new JPanel();
//        JPanel zPanel = new JPanel();
//        JPanel rPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.PAGE_AXIS));
        buttonPane.setLayout(new FlowLayout());
//        xPanel.setLayout(new FlowLayout());
//        yPanel.setLayout(new FlowLayout());
//        zPanel.setLayout(new FlowLayout());
//        JLabel xl = new JLabel("x: ");
//        JLabel yl = new JLabel("y: ");
//        JLabel zl = new JLabel("z: ");
//        JLabel rl = new JLabel("r: ");
        // create buttons
        JButton cancel = new JButton("cancel");
        JButton confirm = new JButton("ok");
//         create actions to confirm button
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//
                File myFoo = new File(makePathGlobalString("VR_DRIVE/CONF/tone.conf"));

                try{
                    FileWriter fooWriter = new FileWriter(myFoo, false); // true to append
                    // false to overwrite.
                    double pro = prob.getValue() / 10.0;
                    currentExp.setToneProbability(pro);
                    fooWriter.write(Double.toString(pro));
                    fooWriter.close();
                } catch (IOException exp){

                }
                frame.setVisible(false);
                frame.dispose();
            }
        });

        // create action to cancel button
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        ////////////////////////////////////////////////////
        // pack fields into window
        ///////////////////////////////////////////////////
//        xPanel.add(xl);
//        xPanel.add(x);
//        yPanel.add(yl);
//        yPanel.add(y);
//        zPanel.add(zl);
//        zPanel.add(z);
//        rPanel.add(rl);
//        rPanel.add(r);
//        fieldsPanel.add(xPanel);
//        fieldsPanel.add(yPanel);
//        fieldsPanel.add(zPanel);
//        fieldsPanel.add(rPanel);
        fieldsPanel.add(prob);
        buttonPane.add(confirm);
        buttonPane.add(cancel);
//        frame.setLocationRelativeTo(null);
        Point location = MouseInfo.getPointerInfo().getLocation();
        frame.setLocation(location);
//        frame.setUndecorated(true);
//        frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        frame.add(fieldsPanel, BorderLayout.PAGE_START);
        frame.add(buttonPane, BorderLayout.PAGE_END);
        frame.setSize(240, 280);
        frame.setVisible(true);
    }

    public void createEndOfExeReport(){


        String path ="";
        File report = null;
        try {
//            File location = new File("C:\\Users\\owner\\Documents\\VR_DRIVE\\CONF\\loc_config.conf");
            File location = new File(makePathGlobalString("/VR_DRIVE/CONF/loc_config.conf"));
            System.out.println("location"+ location);
            Scanner scanner = new Scanner(location);
            if (scanner.hasNext()) {
                path = scanner.next();
            }
            scanner.close();
            report = new File(GlobalConfig.makePathSysytemIn(path+"/report.info"));
            currentMouse.setReportPath(report.getAbsolutePath());

        } catch (FileNotFoundException m) {
            m.printStackTrace();
        }

        System.out.println(path);
        if (report != null){
            try{
                PrintWriter writer = new PrintWriter(report);
                System.out.println(currentExp);
                writer.println("Mouse................................................"+currentMouse.getIdName());
                writer.println("    Age (W).........................................."+currentMouse.getAge());
                writer.println("    Weight (g)......................................."+currentMouse.getCurrentWeight());
                writer.println("Experiment..........................................."+currentExp.toString());
                writer.println("    Location File...................................."+dep.getInfoLoc());
                writer.println("    Personal reward sequence........................." + currentExp.getRewSequence());
                writer.println("    Personal reward lap number......................." + currentExp.getLapSequence());
                writer.println("    Tone probability................................." + currentExp.getToneProbability());
                writer.println("    Calibration value................................"+ calibValue());
                double duration = duration();
                writer.println("    Duration........................................."+duration+" s, "+ duration / 60+" m");
                writer.println("    Number of Stations..............................."+numOfStations());
                writer.println("    Number of Laps..................................."+(double)numOfStations() / currentExp.getRewardList().size());
                writer.println("    Amount Drank....................................."+amountDrank());
                writer.println("    Maze File...................................."+currentExp.getMaze());

//                writer.println("    Personal rounds.................................."+getRounds());
                writer.close();
                report.setReadOnly();
            }catch (FileNotFoundException e){
                System.err.println("report failed");
            }
        }


        JFrame frame = new JFrame("|Experiment review");
        JPanel rep = new JPanel();
        JTextArea text = new JTextArea();
        JScrollPane pane = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        try {

            BufferedReader in = new BufferedReader(new FileReader(report));
            String line = in.readLine();
            while (line != null) {
                text.append(line + "\n");
                line = in.readLine();
            }
            in.close();
        }catch (IOException e){

        }
        frame.add(pane);

        frame.setSize(500, 600);
        frame.setVisible(true);


    }

    private double calibValue(){
        double calib = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(makePathGlobalString("VR_DRIVE\\CONF\\calib_val.dat")))) {
            String line;
            while ((line = br.readLine()) != null) {
                calib = Double.parseDouble(line);

            }
        } catch (IOException ex){
            System.err.println("calibration value not available");
        }
        return calib;
    }

    private double getRounds(){
        int stationPassed = numOfStations();
        double lapsCount = 0;

        String[] rewards = rewardNumSequence.getText().split(",");
        String[] laps = rewardNumSequence.getText().split(",");
        if (laps.length != rewards.length){
            return 0;
        }
        int[] rewrdsInt = new int[laps.length];
        for(int i = 0; i <= laps.length; i++){
            int r = Integer.parseInt(rewards[i]);
            int l = Integer.parseInt(laps[i]);
            rewrdsInt[i] = r * l;
        }

        int idx = 0;
        while (stationPassed > 1){
            stationPassed = Math.floorDiv(stationPassed, rewrdsInt[idx]);
            if (stationPassed > 0)
                lapsCount += Integer.parseInt(laps[idx]);
            idx++;
        }

        return lapsCount;
    }

    private double duration(){
//        try {
//            LineNumberReader reader  = new LineNumberReader(new FileReader(dep.getInfoLoc()+"\\rewardActivity.dat"));
//            double start = 0,end = 0;
//            String lineRead = reader.readLine();
//            if (lineRead != null){
//                start = Double.parseDouble(lineRead.split(",")[0]);
//                System.out.println("start === " + start);
//            }
//            while ((lineRead = reader.readLine()) != null) {
//                end = Double.parseDouble(lineRead.split(",")[0]);
//            }
//            System.out.println("end - start = "+ (end -start));
//            return end-start;
//
//        } catch (IOException p){
//
//        }
        double start = 0, end = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(GlobalConfig.makePathSysytemIn(dep.getInfoLoc()+"/loc.dat")))) {
            String line;
            boolean flag = true;
            while ((line = br.readLine()) != null) {
                if (flag){
                    start = Double.parseDouble(line.split(",")[0]);
                    System.out.println("start " + start);
                    flag = false;
                }
                if (!line.equals(""))
                    end = Double.parseDouble(line.split(",")[0]);
            }
        } catch (IOException ex){
            System.err.println("huston we have got a problem");
        }
        System.out.println("end " + end);
        return end - start;
    }

    private int numOfStations(){
        int counter = 0;
        try{
            LineNumberReader reader  = new LineNumberReader(new FileReader(GlobalConfig.makePathSysytemIn(dep.getInfoLoc()+"/rewardActivity.dat")));

            String lineRead = "";
            while ((lineRead = reader.readLine()) != null) {}

            counter = reader.getLineNumber();
            reader.close();

        } catch (FileNotFoundException e){

        } catch (IOException p){

        }
//
        return counter;
    }

    private double amountDrank(){
        double drank = 0;
        try{
            LineNumberReader reader  = new LineNumberReader(new FileReader(GlobalConfig.makePathSysytemIn(dep.getInfoLoc()+"/rewardActivity.dat")));

            String lineRead = "";
            while ((lineRead = reader.readLine()) != null) {
                drank += Double.parseDouble(lineRead.split(",")[3]);
            }
            reader.close();

        } catch (FileNotFoundException e){

        } catch (IOException p){

        }
//
        return drank;
    }

    // build panel for exp info
    private void buildStatPanel()
    {
        statPanel = new JPanel();
        statPanel.setLayout(new BoxLayout(statPanel, (BoxLayout.Y_AXIS)));

        // mouse picker for current mouse
        JComboBox<Mouse> mice = new JComboBox<>();
        mice.setMaximumSize(new Dimension(100, 20));

//        mice.setModel(new DefaultComboBoxModel<Mouse>());
        mice.addItem(null);
        for (Mouse mick: man.getMiceList().values())
        {
            if (mick.aliveAndKicking())
                mice.addItem(mick);
        }
        mice.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                currentMouse = (Mouse) mice.getSelectedItem();
            }
        });

        statPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        statPanel.add(mice);
        lowerPanel.add(statPanel);
        createConTable();
    }

    // create table of the mice that went through this exp
    private void createConTable()
    {
//        String[] columnNames = {"Mouse", "Date Of exp"};
//        int numrows = currentExp.getExeLog().size();
//        Object[][] data = new Object[numrows][2];
//        int i = 0;
//
//        if (!sortByDate) {
//            TreeMap<Date, Mouse> log = new TreeMap<>();
//            log.putAll(currentExp.getExeLog());
//
//            for (Map.Entry<Date, Mouse> sub : log.entrySet()) {
//                data[i][0] = sub.getValue();
//                data[i][1] = sub.getKey();
//                i++;
//            }
//        }
//        else {
//            TreeMap<Mouse, Date> log = new TreeMap<>();
//
//            for (Map.Entry<Date, Mouse> ent: currentExp.getExeLog().entrySet()){
//
//                log.put(ent.getValue(), ent.getKey());
//            }
//
//            for (Map.Entry<Mouse, Date> sub : log.entrySet()) {
//                data[i][1] = sub.getValue();
//                data[i][0] = sub.getKey();
//                i++;
//            }
//        }
        TableModel tableModel = new ExpTable(currentExp.getLog());
        JTable table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
//        scrollPane.setMaximumSize(new Dimension(200,1000));
        statPanel.add(scrollPane);
    }

    // get name to display in file picker
    private String name(File f)
    {
        if (f == null)
            return "add file";
        return f.getName();
    }

    // set icon for deploy button
    private ImageIcon setDeploy()
    {
        readyToDeploy = (currentExp.getMaze() != null && currentExp.getLocation() != null && !running);

        String path;
        if (readyToDeploy)
            path = makePathGlobalString("LAB_STRUCTURE/src/UI/editor/play.png");
        else
            path =  makePathGlobalString("LAB_STRUCTURE/src/UI/editor/pause.png");

        return addButtImage(path);
    }

    // set icon for maze editor
    private ImageIcon setIconEdit()
    {

        String path = makePathGlobalString("LAB_STRICTURE/src/UI/editor/rew.png");
//        if (can.mode == 1)
//            path = GlobalConfig.makePathGlobalString("LAB_STRUCTURE/src/UI/editor/start.png");

        return addButtImage(path);
    }

    // add image to button
    private ImageIcon addButtImage(String path)
    {
        ImageIcon img = new ImageIcon(path);
        Image image = img.getImage(); // transform it
        Image newimg = image.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    /**
     * opens an open file/directory dialog window
     * @param pth path to load from
     * @param title loading title
     * @param op selection mode
     * @param but button called loading
     * @param filter show files with this filter, null for all files
     * @return a file to load
     */
    private File loadPath(File pth, String title, int op, JButton but, FileNameExtensionFilter filter) {
        File file = null;
        JFileChooser choice = new JFileChooser();
        if (filter != null)
        {
            choice.setFileFilter(filter);
        }
        choice.setCurrentDirectory(pth);
        choice.setDialogTitle(title);
        choice.setFileSelectionMode(op);

        if (choice.showOpenDialog(but) == JFileChooser.APPROVE_OPTION)
            file = choice.getSelectedFile();
        return file;
    }

    /**
     * build editor main panel
     */
    private void createWin()
    {
        this.lowerPanel = new JPanel();
        this.lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
        this.upperPanel = new JPanel();
        this.upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.X_AXIS));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.workSpace = new JPanel();
        this.workSpace.setLayout(new BoxLayout(workSpace, BoxLayout.Y_AXIS));
        this.workSpace.setBorder(BorderFactory.createLineBorder(Color.black));
//        rewardPanel.add(locManPanel);

//        this.canvas = new EditorFrame();
//        JScrollPane pane = new JScrollPane(can, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//        pane.setMaximumSize(new Dimension(500, 500));
//        pane.setMinimumSize (new Dimension (500,500));
//        pane.setBorder(BorderFactory.createLineBorder(Color.black));
////        this.add(canvas);
//        upperPanel.add(pane);
        rewardPanel.add(toolPanel);
        workSpace.add(rewardPanel);
        workSpace.add(locManPanel);
        upperPanel.add(workSpace);
//        workSpace.add(personal);
        this.add(upperPanel);
        this.add(lowerPanel);
    }

    private void personalConfiguration(){
        if (!currentExp.isCanEdit())
            return;
        JFrame frame = new JFrame("Personal Editor");
        JPanel personal = new JPanel();
        JButton save = new JButton("Save");
        JButton clear = new JButton("Clear");
        personal.setLayout(new BoxLayout(personal, BoxLayout.PAGE_AXIS));
        JCheckBox mitTone = new JCheckBox();
        JLabel toneLabel = new JLabel("Check for tone");
        JPanel checker = new JPanel();
        checker.add(toneLabel);
        checker.add(mitTone);
        mitTone.setSelected(currentExp.isMitTone());
        JPanel sequenceL = new JPanel(new FlowLayout());
        rewardLapSequence = new JTextField(currentExp.getLapSequence(),12);
        rewardNumSequence = new JTextField(currentExp.getRewSequence(),12);

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!rewardLapSequence.getText().equals("Assign Lap Lengths") & !rewardNumSequence.getText().equals("Assign Reward Sequence") ){
                    setPersonal("VR_DRIVE/CONF/personalTrain.conf", mitTone.isSelected());
                }
                frame.setVisible(false);
                frame.dispose();
            }
        });

        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentExp.clearPersonal();

                frame.setVisible(false);
                frame.dispose();
            }
        });




        sequenceL.add(rewardNumSequence);
        sequenceL.add(rewardLapSequence);
        sequenceL.add(checker);
        sequenceL.add(save);
        sequenceL.add(clear);

//        personal.add(sequenceN);
        personal.add(sequenceL);
        frame.add(personal);
        frame.setSize(170, 280);
        frame.setVisible(true);
    }

    private void autoReward(){
        if (!currentExp.isCanEdit())
            return;
        JFrame frame = new JFrame("Multi Reward Editor");
        Integer[] choice = new Integer[26];
        for (int i =0; i <26; i++)
            choice[i] = i;
        JComboBox<Integer> numOfRewards = new JComboBox<>(choice);
        JTextField defRew = new JTextField("0.1", 4);
        JTextField defRadius = new JTextField("35", 4);
        JPanel buttonPane = new JPanel();
        JPanel fieldsPanel = new JPanel();
        JPanel listPanel = new JPanel();
        JPanel defRewPanel = new JPanel();
        JPanel defRadPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.PAGE_AXIS));
        buttonPane.setLayout(new FlowLayout());
        defRewPanel.add(defRew);
        defRadPanel.add(defRadius);
        listPanel.add(numOfRewards);
        fieldsPanel.add(listPanel);
        fieldsPanel.add(defRewPanel);
        fieldsPanel.add(defRadPanel);
        JButton confirm = new JButton("ok");
//         create actions to confirm button
        JButton cancel = new JButton("cancel");
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentExp.clearRewards();
                int numOfrew = (Integer) numOfRewards.getSelectedItem();
                //System.out.println(numOfrew);
                uniformDisReward(numOfrew, Float.parseFloat(defRew.getText()), Integer.parseInt(defRadius.getText()));
                reloadList();
//                can.initCanvas(currentExp);
//                can = new EditCanvas(currentExp);
                frame.setVisible(false);
                frame.dispose();
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        buttonPane.add(confirm);
        buttonPane.add(cancel);
        frame.add(fieldsPanel, BorderLayout.PAGE_START);
        frame.add(buttonPane, BorderLayout.PAGE_END);
        frame.setSize(240, 280);
        frame.setVisible(true);
    }

    

    private void uniformDisReward(int numOfRewards, float defRew, int radius){
//        int radius = 35; //35;
        if (numOfRewards == 0)
            return;
        double section = 2*Math.PI/numOfRewards;
        float x,y;
        Vector<RewardStation> rews = new Vector<>();
        for (int i = numOfRewards - 1; i>=0; i--){
            double angle = (i) * section  + 0.174533;
            x = (float) (radius * Math.cos(angle));
            y = (float) (radius * Math.sin(angle));

//            RewardStation rew = new RewardStation(x,y);
            RewardStation rew = new RewardStation("r",i);
            rew.setX(x);
            rew.setY(y);
            rew.setrSize(defRew);
            rews.add(rew);

            //currentExp.setReward(rew);

        }
        currentExp.setRewardList(rews);
    }

    /**
     * reward station editing panel
     * @param rew reward station to edit
     */
    private void editReward (RewardStation rew) {
        if (!currentExp.isCanEdit())
            return;
        // create editing fields
        JFrame frame = new JFrame("Edit: " + rew + " reward station");
        JTextField x = new JTextField(Float.toString(rew.getX()), 4);
        JTextField y = new JTextField(Float.toString(rew.getY()), 4);
        JTextField z = new JTextField(Float.toString(rew.getZ()), 4);
        JTextField r = new JTextField(Float.toString(rew.getrSize()), 4);

        JSlider prob = new JSlider(JSlider.HORIZONTAL, 0, 10, (int) (rew.getProbability() * 10));
        prob.setMajorTickSpacing(2);
        prob.setMinorTickSpacing(1);
        prob.setPaintTicks(true);
        prob.setPaintLabels(true);
        JPanel buttonPane = new JPanel();
        JPanel fieldsPanel = new JPanel();
        JPanel xPanel = new JPanel();
        JPanel yPanel = new JPanel();
        JPanel zPanel = new JPanel();
        JPanel rPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.PAGE_AXIS));
        buttonPane.setLayout(new FlowLayout());
        xPanel.setLayout(new FlowLayout());
        yPanel.setLayout(new FlowLayout());
        zPanel.setLayout(new FlowLayout());
        JLabel xl = new JLabel("x: ");
        JLabel yl = new JLabel("y: ");
        JLabel zl = new JLabel("z: ");
        JLabel rl = new JLabel("r: ");
        // create buttons
        JButton cancel = new JButton("cancel");
        JButton confirm = new JButton("ok");
//         create actions to confirm button
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rew.setX(Float.parseFloat(x.getText()));
                rew.setY(Float.parseFloat(y.getText()));
                rew.setZ(Float.parseFloat(z.getText()));
                rew.setrSize(Float.parseFloat(r.getText()));
                rew.setProbability((float)(prob.getValue() / 10.0));
//                can.initCanvas(currentExp);
                frame.setVisible(false);
                frame.dispose();
            }
        });

        // create action to cancel button
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        ////////////////////////////////////////////////////
        // pack fields into window
        ///////////////////////////////////////////////////
        xPanel.add(xl);
        xPanel.add(x);
        yPanel.add(yl);
        yPanel.add(y);
        zPanel.add(zl);
        zPanel.add(z);
        rPanel.add(rl);
        rPanel.add(r);
        fieldsPanel.add(xPanel);
        fieldsPanel.add(yPanel);
        fieldsPanel.add(zPanel);
        fieldsPanel.add(rPanel);
        fieldsPanel.add(prob);
        buttonPane.add(confirm);
        buttonPane.add(cancel);
//        frame.setLocationRelativeTo(null);
        Point location = MouseInfo.getPointerInfo().getLocation();
        frame.setLocation(location);
//        frame.setUndecorated(true);
//        frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        frame.add(fieldsPanel, BorderLayout.PAGE_START);
        frame.add(buttonPane, BorderLayout.PAGE_END);
        frame.setSize(240, 280);
        frame.setVisible(true);
    }


    /**
     * mouse listener class for list editing
     */
    private class PickReward extends MouseAdapter{
        RewardStation prev = null;
        public void mouseClicked(MouseEvent e)
        {
            if (e.getClickCount() == 2)
            {
                RewardStation rew = rewards.getSelectedValue();
                if(rew != null)
                    editReward(rew);

                return;
            }

            RewardStation selected = rewards.getSelectedValue();
            TreeMenuPop menu = null;
            // check to deselect the list
            if (selected == prev)
            {
                rewards.clearSelection();
//                can.select(-3);
            }

            // choose menu type
            if (SwingUtilities.isRightMouseButton(e))
            {
                if (selected != null)
                    menu = new TreeMenuPop(TreeMenuPop.SELECT.REW_ED);
                else
                    menu = new TreeMenuPop(TreeMenuPop.SELECT.REWARD);
            }

            // build menus
            if (menu != null) {
                // action for add reward option
                menu.addReward.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        RewardStation rew = new RewardStation("r", currentExp.getRewardList().size() + 1);
                        currentExp.setReward(rew);
//                        can.initCanvas(currentExp);
                        reloadList();

                    }
                });
                // action for deleting a reward
                menu.delReward.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        currentExp.removeReward(selected);
//                        can.initCanvas(currentExp);
                        reloadList();
                    }
                });
                // action to open editing panel
                menu.editReward.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        editReward(selected);
                    }
                });
                menu.show(e.getComponent(), e.getX(), e.getY());
            }

            // highlight selected station
            if (selected != null)
            {
//                can.select(selected.getId());
            }


            // setting the last selection
            prev = selected;
        }
    }

    /**
     * private class to act as a table of executed experiments
     * the table has two columns {Mouse name, Date of execution}
     * this allows to sort the data either by mouse or by date
     */
    private class ExpTable extends AbstractTableModel {
        private String[] columnNames = {"Mouse", "Date Of exp"};
        private List<LogEntry> listOfLogs;
        private static final int COLUMN_MAU = 0;
        private static final int COLUMN_DATE = 1;

        public ExpTable(List<LogEntry> listOfLogs) {
            this.listOfLogs = listOfLogs;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return listOfLogs.size();
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (listOfLogs.isEmpty()) {
                return Object.class;
            }
            return getValueAt(0, columnIndex).getClass();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            LogEntry logEnt = listOfLogs.get(rowIndex);
            Object returnValue = null;

            switch (columnIndex) {
                case COLUMN_MAU:
                    returnValue = logEnt.getMau();
                    break;
                case COLUMN_DATE:
                    returnValue = logEnt.getDat();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid column index");
            }

            return returnValue;
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            LogEntry logEnt = listOfLogs.get(rowIndex);
            if (columnIndex == COLUMN_MAU) {
                logEnt.setMau((Mouse) value);
            }
        }

    }


//    /**
//     * maze visuale editing panel
//     */
//    private class EditCanvas extends JLabel { //// TODO: 9/20/16 add an experiment member
//
//        // list of rewards to place on map
//        private HashMap<Integer, RewardIcon> rewStations;
//        // load info from copy of upper exp
//        private Experiment exp;
//        // start point
//        private StartPoint startPoint;
//        // edit mode
//        private int mode = 0;
//        // edit guard
//        private boolean editable = false;
//        // map of maze
//        private ImageIcon map;
//        //
//        private int[] origin = {250,250};
//
//        ImageIcon dirImage;
//
//        // constructor
//        private EditCanvas(Experiment exp) {
//            initCanvas(exp);
//            addMouseListener(new ClickEditor());
//        }
//
//        // builds the panel again
//        private void initCanvas(Experiment exp)
//        {
//            dirImage = new ImageIcon(GlobalConfig.makePathGlobalString("LAB_STRUCTURE/src/UI/editor/dir.png"));
//            Image image = dirImage.getImage(); // transform it
//            Image newimg = image.getScaledInstance(15, 20,  java.awt.Image.SCALE_SMOOTH);
//            dirImage = new ImageIcon(newimg);
//            this.exp = exp;
//            startPoint = exp.getStart();
//            rewStations = new HashMap<>();
//            // adds the maze map // TODO: 9/21/2016 add a case no map and then lock
//            mazeMap();
////            setIcon(map);
//            setRews();
//            this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
//            this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
////            addMouseListener(new ClickEditor());
//            this.setMinimumSize(new Dimension(500,500));
//        }
//
//        // add a new reward station to map
//        private void addRewImage(int id, int x, int y, RewardStation rew)
//        {
//            RewardIcon rewIcon = new RewardIcon(id, x, y);
//            rewIcon.setRew(rew);
//            rewStations.put(id, rewIcon);
//        }
//
//        // update the selected station's icon
//        private void select(int id)
//        {
//            for (RewardIcon rew: rewStations.values())
//            {
//                rew.setSelected(rew.getId() == id);
//                rew.select();
//            }
//            repaint();
//        }
//
//        // set a map of the maze on the editor
//        private void mazeMap()
//        {
//            // attempt to set a maze map
//            File maze = currentExp.getMaze();
//            if (maze != null)
//            {
//                String path = GlobalConfig.makePathSysytemIn(maze.getParent() + "/maze_im.gif");
//                File f = new File(path);
//                if (f.exists())
//                {
//                    map = new ImageIcon(path);
//                    setIcon(map);
//                    editable = true;
//                    return;
//                }
//            }
//            // set default map
////            map = new ImageIcon("C:\\Users\\owner\\Documents\\LAB_STRUCTURE\\src\\UI\\editor\\maze.gif");
//            map = new ImageIcon(GlobalConfig.makePathGlobalString("LAB_STRUCTURE/src/UI/editor/maze.gif"));
//            setIcon(map);
//            editable = false;
//        }
//
//        // set the rewards in the map
//        private void setRews()
//        {
//            for (RewardStation rew: exp.getRewardList())
//            {
//                addRewImage(rew.getId(), (int) rew.getX()*10 + origin[0], (int) rew.getY()*10 + origin[1], rew);
//            }
//        }
//
//        // delete a reward from map and upper list
//        private void delStation(int id)
//        {
//            RewardIcon rewIcon = rewStations.get(id);
//            if (rewIcon != null)
//            {
//                rewStations.remove(id);
//                System.out.println(rewIcon.getRew());
//                currentExp.removeReward(rewIcon.getRew());
//                reloadList();
//            }
//        }
//
//        @Override
//        protected void paintComponent(Graphics g) {
//            super.paintComponent(g);
//            // add all the graphics to the map editing panel
//            if (editable) {
//                for (RewardIcon rew : rewStations.values()) {
//                    g.drawImage(rew.getImg().getImage(), rew.getX() - 5, rew.getY() - 7, null);
//                }
//                if (startPoint != null)
//                    g.drawImage(startPoint.getImg().getImage(), (int) startPoint.get_X(), (int) startPoint.get_Y() - 20, null);
//                else {
//
//                    g.drawImage(dirImage.getImage(), 22 * 10 + origin[0],origin[1],null );
//                }
//            }
//        }
//
//        // select a point from the map that is in the area of x,y //// TODO: 9/21/2016 add menu option
//        private int selectPoint(int x, int y)
//        {
//            for (RewardIcon rew: rewStations.values())
//            {
//                if (rew.nearPoint(x, y))
//                    return rew.getId();
//            }
//            return -1;
//        }

//        // click listener for map editing
//        private class ClickEditor extends MouseAdapter{
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
//                if (editable) {
//                    // add map graphic component
//                    if (SwingUtilities.isLeftMouseButton(e)) {
//                        // add a reward to the map
//                        if (mode == 0) {
//                            RewardIcon newRew = new RewardIcon(rewStations.size() + 1, e.getX(), e.getY());
//                            rewStations.put(rewStations.size() + 1, newRew);
//                            RewardStation rew = new RewardStation((float) (e.getX() - origin[0])/10, (float) (e.getY()-origin[1])/10);
//                            newRew.setRew(rew);
//                            currentExp.setReward(rew);
//                            reloadList(); //// TODO: 9/21/2016 make last selected
//
//                        }
//                        // add a start position
//                        if (mode == 1) {
//                            startPoint = new StartPoint((float) e.getX(), (float) e.getY());
//                            currentExp.setStart(startPoint);
//                        }
//                    }
//                    // delete map graphic component
//                    if (SwingUtilities.isRightMouseButton(e)) {
//                        if (mode == 0) {
//                            int index = selectPoint(e.getX(), e.getY());
//                            if (index != -1) {
//                                delStation(index);
//                            }
//                        }
//
//                        if (mode == 1) {
//                            if (startPoint.nearPoint(e.getX(), e.getY())) { // // TODO: 9/21/2016 fix issues with id's
//                                startPoint = null;
//                                currentExp.setStart(null);
//                            }
//                        }
//                    }
//                    repaint();
//                }
//            }
//        }
//    }
}