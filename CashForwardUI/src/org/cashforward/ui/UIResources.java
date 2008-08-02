/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.ui;

import javax.swing.ImageIcon;

/**
 *
 * @author Bill
 */
public class UIResources {

    public static final String ICON_PAYMENT = 
            "/org/cashforward/ui/payment/payment-detail.png";
    public static final String ICON_SCENARIO = 
            "/org/cashforward/ui/action/scenario-new.png";
    public static final String ICON_LABELS = 
            "/org/cashforward/ui/payment/payment-detail.png";
    
    public static ImageIcon getImage(String path){
        return new ImageIcon(UIResources.class.getResource(path));
    }
}
