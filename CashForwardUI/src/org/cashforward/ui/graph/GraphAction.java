/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.graph;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows Graph component.
 */
public class GraphAction extends AbstractAction {

    public GraphAction() {
        super(NbBundle.getMessage(GraphAction.class, "CTL_GraphAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(GraphTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = GraphTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
