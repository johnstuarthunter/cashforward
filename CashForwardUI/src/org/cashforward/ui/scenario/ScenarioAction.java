/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.scenario;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows Scenario component.
 */
public class ScenarioAction extends AbstractAction {

    public ScenarioAction() {
        super(NbBundle.getMessage(ScenarioAction.class, "CTL_ScenarioAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(ScenarioTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = ScenarioTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
