package org.cashforward.ui.action;

import java.util.Collection;
import org.cashforward.model.Scenario;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.cashforward.ui.internal.UILogger;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * Shows the user interface necessary for creating a <code>Scenario</code>.
 *
 * @author Bill
 */
public final class NewScenarioAction extends BaseCallableSystemAction {
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
        
        Scenario newScenario = null;
        
        //create input panel for name
        NotifyDescriptor.InputLine nameDialog = new NotifyDescriptor.InputLine(
                getMessage("CTL_NewScenarioName"),
                getMessage("CTL_NewScenarioTitle"),
                NotifyDescriptor.PLAIN_MESSAGE,
                NotifyDescriptor.OK_CANCEL_OPTION);
        Object result = DialogDisplayer.getDefault().notify(nameDialog);
        if (result == DialogDescriptor.OK_OPTION){
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
             UILogger.displayError(getMessage(
                            "CTL_unable_to_add_scenario"));
        }
    }

    public String getName() {
        return getMessage("CTL_NewScenarioAction");
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
        return true;
    }
    
}
