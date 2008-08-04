/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.ui.internal.options;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.cashforward.ui.internal.UILogger;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author Bill
 */
public class UIOptions {
    
    private static Preferences prefs =
            NbPreferences.forModule(UIOptions.class);
    
    public static final String PAYMENTS_REQUIRE_LABELS = "requireLabels";

    public static final String DEBUGGING_ON = "debugOn";

    public static final String SELECTED_SCENARIOS = "selectedScenarios";
    public static final String SELECTED_LABELS = "selecgedLabels";
    public static final String SELECTED_TYPES = "selectedTypes";

    private static final String NOTHING = "";
    private static final String DELIMETER = "|";

    
    public static boolean paymentsRequireLabel(){
        return prefs.getBoolean(PAYMENTS_REQUIRE_LABELS, false);
    }

    public static void setPaymentsRequireLabel(boolean requireLabel){
        prefs.putBoolean(PAYMENTS_REQUIRE_LABELS, requireLabel);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            UILogger.displayError(NbBundle.getMessage(UIOptions.class,
                    "unable_to_save_prefs"), ex);
        }
    }
    
    public static boolean isDebuggingOn(){
        return prefs.getBoolean(DEBUGGING_ON, false);
    }

    public static void setDebuggingOn(boolean debugOn){
        prefs.putBoolean(DEBUGGING_ON, debugOn);
        try {
            prefs.flush();
            if (debugOn)
                UILogger.turnOnDebugging();
            else
                UILogger.turnOffDebugging();
        } catch (BackingStoreException ex) {
            UILogger.displayError(NbBundle.getMessage(UIOptions.class,
                    "unable_to_save_prefs"), ex);
        }
    }

    public static int[] getSelection(String type){
        String labels = prefs.get(type, NOTHING);
        if (NOTHING.equals(labels))
            return new int[0];
        
        String[] indicies = labels.split(DELIMETER);
        int[] selectedLabels = new int[indicies.length];
        for (int i = 1; i < indicies.length; i++) {
            selectedLabels[i] = Integer.parseInt(indicies[i]);
        }
        return selectedLabels;
    }

    public static void saveSelections(String type, int[] selections){
        StringBuffer selection = new StringBuffer();
        for (int i = 0; i < selections.length; i++) {
            selection.append(selections[i]);
            if (i+1<selections.length)
                selection.append(DELIMETER);
        }
        prefs.put(type, selection.toString());
    }
}
