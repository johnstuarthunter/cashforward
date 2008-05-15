/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.task;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows Task component.
 */
public class TaskAction extends AbstractAction {

    public TaskAction() {
        super(NbBundle.getMessage(TaskAction.class, "CTL_TaskAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(TaskTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = TaskTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
