package org.cashforward.ui.internal;

import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;
import org.cashforward.model.Scenario;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.cashforward.ui.internal.options.UIOptions;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {

        if (UIOptions.isDebuggingOn()) {
            UILogger.turnOnDebugging();
        }


        com.jidesoft.utils.Lm.verifyLicense("CashForward",
                "CashForward", "JoATfLwitIFxtqKBnz25uyW7KBd4kjr2");
        try {
            //overwriting the JIDE stuff...
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            //dont really care
        }

        PaymentServiceAdapter serviceAdapter =
                new PaymentServiceAdapter();

        //determine the most recent scenario
        Scenario recentScenario = null;
        List<Scenario> scenarios = serviceAdapter.getScenarios();
        if (scenarios != null) {
            recentScenario = scenarios.get(0);
            UIContext.getDefault().addScenarios(scenarios);
        } else {
            recentScenario = serviceAdapter.getDefaultScenario();
        }
        List<Scenario> selectedScenarios = new ArrayList();
        selectedScenarios.add(recentScenario);
        UIContext.getDefault().setSelectedScenarios(selectedScenarios);

        //we just load all the payments in memory for now...
        List payments = serviceAdapter.getPayments();
        if (payments != null) {
            UIContext.getDefault().addPayments(payments);
        }

        List payees = serviceAdapter.getPayees();

        if (payees != null) {
            UIContext.getDefault().addPayees(payees);
        }

        List labels = serviceAdapter.getLabels();
        if (labels != null) {
            UIContext.getDefault().addLabels(labels);
        }

    }
}
