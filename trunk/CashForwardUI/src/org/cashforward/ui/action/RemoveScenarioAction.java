package org.cashforward.ui.action;

import java.util.List;
import org.cashforward.model.Scenario;
import org.cashforward.service.PaymentService;
import org.cashforward.ui.UIContext;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;

public final class RemoveScenarioAction extends BaseCallableSystemAction {

    private Lookup.Result scenarioNotifier =
            UIContext.getDefault().lookupResult(Scenario.class);
    private PaymentService paymentService;

    public RemoveScenarioAction() {
        super();
        setEnabled(false);
        paymentService = new PaymentService();
        scenarioNotifier.addLookupListener(new LookupListener() {
            public void resultChanged(LookupEvent event) {
                Lookup.Result r = (Lookup.Result) event.getSource();
                setEnabled(UIContext.getDefault().
                        getSelectedScenarios().size()> 0 &&
                        UIContext.getDefault().getScenarios().size() > 0);
            }
        });

    }

    @Override
    public void performAction() {
        List<Scenario> selectedScenarios =
                UIContext.getDefault().getSelectedScenarios();

        if (selectedScenarios.size() ==
                UIContext.getDefault().getScenarios().size()) {

            NotifyDescriptor notify =
                    new NotifyDescriptor.Message(
                    getMessage("CTL_leave_one_scenario"));
            DialogDisplayer.getDefault().notify(notify);
            return;
        }

        NotifyDescriptor notify =
                new NotifyDescriptor.Confirmation(
                getMessage("CTL_confirm_remove_scenario"));
        Object result = DialogDisplayer.getDefault().notify(notify);
        if (result == DialogDescriptor.YES_OPTION) {
        
            for (Scenario scenario : selectedScenarios) {
                removeScenario(scenario);
            }
        }
        
        Scenario toSelect =
                UIContext.getDefault().getScenarios().get(0);
        selectedScenarios.clear();
        selectedScenarios.add(toSelect);
        UIContext.getDefault().setSelectedScenarios(selectedScenarios);
    }

    private void removeScenario(Scenario scenario) {
        try {
            paymentService.removeScenario(scenario);
            UIContext.getDefault().removeScenario(scenario);
        } catch (Exception ex) {
            NotifyDescriptor notify =
                    new NotifyDescriptor.Message(
                    getMessage("CTL_could_not_remove_scenario") + scenario.getName());
            DialogDisplayer.getDefault().notify(notify);
        }
    }

    public String getName() {
        return NbBundle.getMessage(RemoveScenarioAction.class, "CTL_RemoveScenarioAction");
    }

    @Override
    protected String iconResource() {
        return "org/cashforward/ui/action/scenario-delete.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
