/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.payment;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows Payment component.
 */
public class PaymentAction extends AbstractAction {

    public PaymentAction() {
        super(NbBundle.getMessage(PaymentAction.class, "CTL_PaymentAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(PaymentTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = PaymentTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
