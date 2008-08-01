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
    
    public static final String PAYMENTS_REQUIRE_LABELS = "prl";
    
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
}
