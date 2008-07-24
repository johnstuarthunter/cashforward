/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.internal;

import java.util.ArrayList;
import java.util.List;
import org.cashforward.model.Scenario;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {

        com.jidesoft.utils.Lm.verifyLicense(
                "Bill Snyder", "CashForward",
                "U4Fnx9Ak6M1DGKsRXc2fNF8nTG0c2aC");

        //TODO does this happen on the EDT?
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
        /*
        List cpayments = serviceAdapter.getCurrentPayments();
        if (cpayments != null) {
            UIContext.getDefault().addCurrentPayments(cpayments);
        }

        List spayments = serviceAdapter.getScheduledPayments();
        if (spayments != null) {
            UIContext.getDefault().addScheduledPayments(spayments);
        }
        */
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
