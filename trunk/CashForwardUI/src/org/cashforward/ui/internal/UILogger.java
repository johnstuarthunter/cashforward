/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.internal;

import java.util.logging.Logger;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *  Interface-centric logger utilitiy. Ties into the NetBeans facilities and
 * provides consistent mechanisms to warn/alert users.
 *
 * @author Bill
 */
public class UILogger {

    public static final Logger LOG =
            Logger.getLogger("org.cashforward.modules.ui");

    public static void displayError(String message, Exception ex) {
        NotifyDescriptor.Exception e = 
                new NotifyDescriptor.Exception(ex, message);
        DialogDisplayer.getDefault().notifyLater(e);

    }
}
