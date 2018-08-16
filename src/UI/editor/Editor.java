package UI.editor;

import data_manager.DataManager;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Created by Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is Editor in UI.editor
 * created on 9/18/2016
 */
//// TODO: 9/22/2016 add save, editing station on change
public class Editor extends JPanel {
//    private static final long serialVersionUID = 1L;

    // data man object
    public static DataManager man;



    /**
     * sets a new editor in this window
     * @param ed editor class
     */
    public void setEditor(Editor ed)
    {
        this.removeAll();
        this.add(ed);
        this.revalidate();
        this.repaint();
    }

}
