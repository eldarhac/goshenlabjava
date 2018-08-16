package UI.editor;

import UI.TreeMenuPop;

import experiments.Experiment;
import mouse.Mouse;
import mouse.TYPES;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is MouseEditor in UI.editor
 * created on 9/15/2016
 */

// todo add the option to run maze from mouse
public class MouseEditor extends Editor implements Serializable{
    private static final long serialVersionUID = 1L;
    // current mouse
    private Mouse maus;
    // list of experiments
    private JList expList;
    // list of procedures
    private JList proList;
    // panel for lists
    private JPanel listField;
    // id info panel
    private JPanel infoField;
    // schedule panel
    private JPanel controlField;
    // editing lock
    private boolean editable = false;
    // mouse type
    private JLabel typesLabel;
    // mouse name
    private JTextField nameField;
    // age field
    private JTextField dateField;
    // weight field
    private JTextField weightField;
    //window division upper
    private JPanel upperPanel;
    // window division lower
    private JPanel lowerPanel;
    // experiment picker
    private JComboBox<Experiment> expChoice;
    // mouse schedule
    private ScheduleTable schedule;

    /**
     * constructor
     * @param maus current mouse
     * @param add eding mode
     */
    public MouseEditor(Mouse maus, boolean add) {
        editable = add;
        this.maus = maus;
        init();

    }

    // init window
    private void init()
    {

        upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel,BoxLayout.X_AXIS));
        lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        listField = new JPanel();
        infoField = new JPanel();
        listField.setLayout(new FlowLayout(FlowLayout.CENTER));
        infoField.setLayout(new BoxLayout(infoField, BoxLayout.Y_AXIS));
        expList = new JList();
        expList.setLayout(new BoxLayout(expList, BoxLayout.Y_AXIS));
        proList = new JList();
        buildInfoField();
        buildListsField();
        buildConFeild();
        buildLowerField(0);
//        upperPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        lowerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        upperPanel.add(infoField);
        upperPanel.add(listField);
        upperPanel.add(controlField);
        this.add(upperPanel);
        this.add(lowerPanel);
    }

    //// TODO: 9/29/2016 check if could be deleted
    public void switchEntity (Object o)
    {
        this.maus = (Mouse) o;
        buildInfoField();
        buildListsField();

    }

    // build maze picker combo box
    private void buildMazeChoices()
    {
        expChoice = new JComboBox<>();
        HashMap<Date, Experiment> done = maus.getTookPlace();
        for (Experiment exp: man.getExperimentsList().values())
        {
            if (!done.containsValue(exp))
                expChoice.addItem(exp);
//            expChoice.addItem(exp);
        }
    }

    // build schedule field
    private void buildLowerField(int i)
    {
        buildMazeChoices();
        buildSchedule(i);
        JScrollPane scrollPane = new JScrollPane(schedule);
        schedule.setFillsViewportHeight(true);
        lowerPanel.removeAll();
        lowerPanel.add(scrollPane);
    }

    // create jtable representing schedule
    private void buildSchedule(int i)
    {
        String[] columns = {"Action", "Date", "File Location","Comment"};
        int size = maus.getExperiments().size();
        size += maus.getTookPlace().size();
        Object[][] data = new Object[size + i][4];
        schedule = new ScheduleTable(data, columns);
        fillArray(data);
        schedule.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value, boolean isSelected, boolean hasFocus, int row, int col) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

//            String status = (String)table.getModel().getValueAt(row, col);
            if (schedule.lockedrows.contains(row)) {
                setBackground(Color.GREEN);
                setForeground(Color.BLACK);
            } else {
                Object o = table.getModel().getValueAt(row, 1);
                if (!(o instanceof Date)) {
                    setBackground(Color.CYAN);
                    setForeground(Color.BLACK);
                }
                else{
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
            }
            return this;
        }
    });
    }

    // fill table info
    private void fillArray(Object[][] data)
    {
        int i = 0;
        HashMap<Date, Experiment> preformed = maus.getTookPlace();
        for (Map.Entry<Date, Experiment> ran: preformed.entrySet())
        {
            schedule.lockedrows.add(i);
            data[i][0] = ran.getValue();
            data[i][1] = ran.getKey();
            data[i][2] = maus.getReportPath(ran.getKey());
            i++;
        }

        for (Map.Entry<Integer, Experiment> exp: maus.getExperiments().entrySet()){
            if (!preformed.containsValue(exp.getValue()))
            {
//                System.out.println("here");
                data[i][0] = exp.getValue();
                data[i][1] = "not assigned";
                i++;
            }
        }
    }

    //// TODO: 9/29/2016 might not need this
    private void addToScheduale(){
        buildLowerField(1);
    }

    // create info area
    private void buildInfoField()
    {
        Border blackline = BorderFactory.createLineBorder(Color.black);
        TitledBorder title = BorderFactory.createTitledBorder(blackline, maus.sysId());
        title.setTitleJustification(TitledBorder.LEFT);
        title.setTitlePosition(TitledBorder.CENTER);
        infoField.setBorder(title);
        listField.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel nameLabel = new JLabel("identifier: ");
        nameField = new JTextField(maus.getIdName(),7);
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addField(nameLabel, nameField, namePanel);

        JLabel dateLabel = new JLabel("age (weeks): ");
        dateField = new JTextField(Integer.toString(maus.getAge()),7);
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addField(dateLabel, dateField, datePanel);

        JLabel weightLabel = new JLabel("weight (g): ");
        weightField = new JTextField(Float.toString(maus.getCurrentWeight()),7);
        JPanel weightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addField(weightLabel, weightField, weightPanel);

        addTypeBox();
    }

    // mouse type choices
    private void addTypeBox()
    {
        typesLabel = new JLabel("type: ");
        JComboBox<TYPES> box = new JComboBox<>();
        box.addItem(TYPES.TYPE1);
        box.addItem(TYPES.WILD);
        box.setSelectedItem(maus.getType());
        JPanel typesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        typesPanel.add(typesLabel);
        typesPanel.add(box);
        infoField.add(typesPanel);
    }

    // add a field given a label a field and a panel to add to
    private void addField(JLabel label, JTextField field, JPanel panel)
    {

        if (!editable)
            field.setEditable(false);
        panel.add(label);
        panel.add(field);
        infoField.add(panel);
    }

    // build lists with info
    private void buildListsField()
    {

        expList = new JList(maus.getExperiments().values().toArray());
        proList = new JList(maus.getProcedures().values().toArray());
//        listField.setMinimumSize(new Dimension(1500,1500));
//        listField.setMaximumSize(new Dimension(1500,1500));
//        expList.setBorder(BorderFactory.createLineBorder(Color.red));
        JScrollPane pane = new JScrollPane(expList);
        pane.setMaximumSize(new Dimension(200, 250));
        pane.setMinimumSize (new Dimension (200,250));
        pane.setBorder(BorderFactory.createLineBorder(Color.red));
        listField.add(pane);

        JScrollPane pane2 = new JScrollPane(proList);
        pane2.setMaximumSize(new Dimension(90, 250));
        pane2.setMinimumSize (new Dimension (90,250));
        pane2.setBorder(BorderFactory.createLineBorder(Color.red));
        listField.add(pane2);
        proList.setFixedCellWidth(250);
        expList.setFixedCellWidth(250);
        expList.addMouseListener(new ClickListener());
    }

    // build panel with action buttons
    private void buildConFeild()
    {
        controlField = new JPanel();
        controlField.setLayout((new BoxLayout(controlField ,BoxLayout.Y_AXIS)));
        JButton kill = new JButton("kill");
        JButton addComment = new JButton("add comment");
        JButton save = new JButton("save mouse");

        controlField.add(addComment);
        controlField.add(save);
        controlField.add(kill);

        // save mouse
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                Gui.reloadTree();
                saveEdit();
            }
        });

        // // TODO: 9/29/2016 make a dead mouse non editable
        // kill mouse and make it none editable
        kill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveEdit();
                maus.KILL(null);
                repaint();
            }
        });

        // create a comment
        addComment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToScheduale();
            }
        });


        controlField.setBorder(BorderFactory.createLineBorder(Color.black));

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!maus.aliveAndKicking()){
            ImageIcon image = new ImageIcon(maus.getIconPath());
            Graphics2D g2d = (Graphics2D) g;
            int x = (this.getWidth() - image.getImage().getWidth(null)) / 2;
            int y = (this.getHeight() - image.getImage().getHeight(null)) / 2;
            g2d.drawImage(image.getImage(), x, y, null);
        }
    }

    // save the changes done to this mouse
    private void saveEdit()
    {
        maus.logWeight(new Date(), Float.parseFloat(weightField.getText()));
        //maus.setAge(Integer.parseInt(dateField.getText())); // todo: add editing lock for maus and exp
//        System.out.println(nameField.getText());
        consciousGuard(nameField.getText());
        maus.setIdName(nameField.getText());
    }

    // check if name is legal
    private boolean consciousGuard(String str)
    {
        Pattern p = Pattern.compile("^\\p{L}+[\\p{L}\\p{Z}\\p{P}]*");
        Matcher m = p.matcher(str);
        JFrame pop = new JFrame();
        if (m.matches())
        {
            JOptionPane.showMessageDialog(null,
                    "Pleas refrain from giving the mouse a humanizing name",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);

        }
        return true;
    }

    // list action listner
    class ClickListener extends MouseAdapter {
        Object prev = null;
        public void mouseClicked(MouseEvent e){
            // build menus fitting selections
            Object selected = expList.getSelectedValue();
            TreeMenuPop menu = null;
            if (SwingUtilities.isLeftMouseButton(e) && selected == prev)
                expList.clearSelection();

            if (selected instanceof Experiment)
            {
                if (SwingUtilities.isRightMouseButton(e))
                {
                    menu = new TreeMenuPop(TreeMenuPop.SELECT.EXPERIMENT);
                }

                if (e.getClickCount() == 2)
                {
                    man.setEditingWin(new ExperimentEditor((Experiment) selected, true));
                }
            }
            else
            {
                if (SwingUtilities.isRightMouseButton(e))
                {
                    menu = new TreeMenuPop(TreeMenuPop.SELECT.EXPERIMENT_ADD);
                }
            }

            if (menu != null) {
                menu.addExperiment.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        Experiment newExp = Editor.man.createExperiment();
                        maus.addExperiment(newExp);
                        expList.removeAll();
                        expList.setListData(maus.getExperiments().values().toArray());
                    }
                });
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
            prev = selected;


        }
    }

    // schedule table
    private class ScheduleTable extends JTable {
        private HashSet<Integer> lockedrows;
        //constructor
        public ScheduleTable(Object[][] rowData, Object[] columnNames) {
            super(rowData, columnNames);
            lockedrows = new HashSet<>();
            addMouseListener(new TableAdapter());
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return lockedrows.contains(row) && column != 3;
        }
    }

//    TODO: fix methods to work with linux
    private void locMenu(String path, MouseEvent e){
        TreeMenuPop menu = new TreeMenuPop(TreeMenuPop.SELECT.TABLE_VIEW);

        menu.viewLoc.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                try{
                    Runtime.getRuntime().exec("explorer.exe /select," + path);
                } catch (IOException err)
                {
                    System.err.println("cant open dir");
                }


            }
        });

        menu.viewRep.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                ProcessBuilder pb = new ProcessBuilder("Notepad.exe", path);
                try {
                    pb.start();
                } catch (IOException err){
                    System.err.println("cant open file");
                }
            }
        });
        menu.show(e.getComponent(), e.getX(), e.getY());

    }
    // actions for table editing
    private class TableAdapter extends MouseAdapter{

        public void mouseClicked(MouseEvent e) {
            int[] selectedRow = schedule.getSelectedRows();
            int[] selectedCols = schedule.getSelectedColumns();
            if (e.getClickCount() == 1){
                System.out.println("her0000000000000000000000");
                if (selectedRow.length != 0)
                {
                    Object o = schedule.getValueAt(selectedRow[0], 0);
                    if (o instanceof Experiment){
                        System.out.println("exp sel at table");
                        Object o1 = schedule.getValueAt(selectedRow[0], selectedCols[0]);
                        if (o1 instanceof String)
                        {
                            System.out.println("selected string");
                            if (((String) o1).contains("report")){
                                    locMenu((String) o1,e);
                                    //todo: add system invariant
//                                ProcessBuilder pb = new ProcessBuilder("Notepad.exe", (String) o1);
//                                try {
//                                    pb.start();
//                                } catch (IOException err){
//                                    System.err.println("cant open file");
//                                }
                            }

                            return;
                        }
                        man.setEditingWin(new ExperimentEditor((Experiment) o, true));
                        return;
                    }

                }
                new ScheduleNew();
            }
        }
    }

    // new schedualing window
    private class ScheduleNew extends JFrame{
        private JPanel chooser;
        private JPanel top;
        private JPanel bottom;
        private JPanel mainPanel;
        private int mode;
        JComboBox<Experiment> exps;
        private ScheduleNew()
        {

            buildWindow();
            setSize(400,400);
            setVisible(true);
            Point location = MouseInfo.getPointerInfo().getLocation();
            this.setLocation(location);
        }

        private void buildWindow(){
            mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            JButton addExp = new JButton("add Experiment");
            JButton addPro = new JButton("add Procedure");
            JButton cancel = new JButton("cancel");
            JButton confirm = new JButton("confirm");
            chooser = new JPanel();
            top = new JPanel();
            top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
            bottom = new JPanel();
            bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
            top.add(addExp);
            top.add(addPro);
            bottom.add(cancel);
            bottom.add(confirm);
            mainPanel.add(top);
            mainPanel.add(chooser);
            mainPanel.add(bottom);
            this.add(mainPanel);

            addExp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    expAdder();
                    repaint();
                }
            });

            addPro.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    proAdder();
                    repaint();
                }
            });

            confirm.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveInfo();


                }
            });

        }

        private void expAdder()
        {
            chooser.removeAll();
            exps = new JComboBox<>();

            for (Experiment exp: man.getExperimentsList().values())
                exps.addItem(exp);

            chooser.add(exps);
            mode = 1;
        }

        private void proAdder()
        {
            chooser.removeAll();
            mode = 2;
        }

        private void saveInfo(){
            switch (mode){
                case 1:
                    maus.addExperiment((Experiment) exps.getSelectedItem());
                    break;
                case 2:
                    System.out.println("will add pro");
            }
            setVisible(false);
            dispose();
        }
    }
}
