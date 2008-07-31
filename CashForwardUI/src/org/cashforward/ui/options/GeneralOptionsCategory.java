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

public final class GeneralOptionsCategory extends OptionsCategory {

    @Override
    public Icon getIcon() {
        return new ImageIcon(ImageUtilities.loadImage("org/cashforward/ui/options/preferences-desktop.png"));
    }

    public String getCategoryName() {
        return NbBundle.getMessage(GeneralOptionsCategory.class, "OptionsCategory_Name_General");
    }

    public String getTitle() {
        return NbBundle.getMessage(GeneralOptionsCategory.class, "OptionsCategory_Title_General");
    }

    public OptionsPanelController create() {
        return OptionsPanelController.createAdvanced("GeneralOptionsCategory");
    }
}
