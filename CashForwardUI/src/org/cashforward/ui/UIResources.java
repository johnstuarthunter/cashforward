package org.cashforward.ui;

import javax.swing.ImageIcon;
import org.openide.util.Utilities;

/**
 * Central place for loading other images need by the application.
 *
 * @author Bill
 */
public class UIResources {

    public static final String ICON_PAYMENT_SCHEDULED = 
            "org/cashforward/ui/payment/scheduled.png";
    public static final String ICON_PAYMENT_CURRENT=
            "org/cashforward/ui/payment/current.png";
    public static final String ICON_PAYMENT_PROJECTED =
            "org/cashforward/ui/payment/projected.png";
    public static final String ICON_SCENARIO = 
            "org/cashforward/ui/scenario/scenario.png";
    
    public static ImageIcon getImage(String path){
        return new ImageIcon(Utilities.loadImage(path));
    }
}
