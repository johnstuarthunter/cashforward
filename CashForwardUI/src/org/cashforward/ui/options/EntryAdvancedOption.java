/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.options;

import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

public final class EntryAdvancedOption extends AdvancedOption {

    public String getDisplayName() {
        return NbBundle.getMessage(EntryAdvancedOption.class, "AdvancedOption_DisplayName_Entry");
    }

    public String getTooltip() {
        return NbBundle.getMessage(EntryAdvancedOption.class, "AdvancedOption_Tooltip_Entry");
    }

    public OptionsPanelController create() {
        return new EntryOptionsPanelController();
    }
}
