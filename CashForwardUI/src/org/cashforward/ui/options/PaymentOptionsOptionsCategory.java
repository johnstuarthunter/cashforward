/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.options;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.spi.options.OptionsCategory;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
import org.openide.util.ImageUtilities;

public final class PaymentOptionsOptionsCategory extends OptionsCategory {

    @Override
    public Icon getIcon() {
        return new ImageIcon(ImageUtilities.loadImage("org/cashforward/ui/options/payment_prefs.png"));
    }

    public String getCategoryName() {
        return NbBundle.getMessage(PaymentOptionsOptionsCategory.class, "OptionsCategory_Name_PaymentOptions");
    }

    public String getTitle() {
        return NbBundle.getMessage(PaymentOptionsOptionsCategory.class, "OptionsCategory_Title_PaymentOptions");
    }

    public OptionsPanelController create() {
        return new PaymentOptionsOptionsPanelController();
    }
}
