/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.internal;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
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

    private static final String LOGGER_KEY = "org.cashforward.modules.ui";
    private static final String LOGGER_LEVEL_KEY = 
            "org.cashforward.modules.ui.level";
    
    public static final Logger LOG =
            Logger.getLogger(LOGGER_KEY);
    
    public static void turnOnDebugging(){
        System.setProperty(LOGGER_LEVEL_KEY, Level.FINEST.toString());
        try {
            LogManager.getLogManager().readConfiguration();
        } catch (IOException ignored) {
        }
    }
    
    public static void turnOffDebugging(){
        System.setProperty(LOGGER_LEVEL_KEY, Level.INFO.toString());
        try {
            LogManager.getLogManager().readConfiguration();
        } catch (IOException ignored) {
        }
    }
            

    public static void displayError(String message, Exception ex) {
        NotifyDescriptor.Exception e = 
                new NotifyDescriptor.Exception(ex, message);
        DialogDisplayer.getDefault().notifyLater(e);
    }
    
    public static void displayError(String message) {
        NotifyDescriptor.Message e =
                new NotifyDescriptor.Message(message);
        DialogDisplayer.getDefault().notifyLater(e);
    }
}
