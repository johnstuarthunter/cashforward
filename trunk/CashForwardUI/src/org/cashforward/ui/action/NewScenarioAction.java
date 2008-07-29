/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.action;

import java.util.Collection;
import org.cashforward.model.Scenario;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class NewScenarioAction extends CallableSystemAction {
    private PaymentServiceAdapter paymentService;
    private Lookup.Result scenarioNotifier =
            UIContext.getDefault().lookupResult(Scenario.class);
    private Scenario currentScenario;
    
    public NewScenarioAction(){
        super();
        setEnabled(false);
        scenarioNotifier.addLookupListener(new LookupListener() {

            public void resultChanged(LookupEvent event) {
                Lookup.Result r = (Lookup.Result) event.getSource();
                Collection c = r.allInstances();
                if (!c.isEmpty()) {
                    NewScenarioAction.this.currentScenario =
                            (Scenario) c.iterator().next();
                    setEnabled(true);
                } else
                    setEnabled(false);
            }
        });
    }        
    
    public void performAction() {
        
        if (currentScenario == null ){
            //alert
            return;
        }        
        Scenario newScenario = null;
        
        //create input panel for name
        NotifyDescriptor.InputLine nameDialog = new NotifyDescriptor.InputLine(
                "New scenario name:",
                "New Scenario",
                NotifyDescriptor.PLAIN_MESSAGE,
                NotifyDescriptor.OK_CANCEL_OPTION);
        Object result = DialogDisplayer.getDefault().notify(nameDialog);
        if (result == DialogDescriptor.YES_OPTION){
            if (nameDialog.getInputText() != null)
                //create Scenario object
                newScenario = new Scenario(nameDialog.getInputText());
        } else {
            return;
        }
        
        if (paymentService == null)
            paymentService = new PaymentServiceAdapter();
        
        if (paymentService.createScenario(currentScenario, newScenario)){
            UIContext.getDefault().addScenario(newScenario);
        } else {
            //alert
            return;
        }
    }

    public String getName() {
        return NbBundle.getMessage(NewScenarioAction.class, "CTL_NewScenarioAction");
    }

    @Override
    protected String iconResource() {
        return "org/cashforward/ui/action/scenario-new.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
