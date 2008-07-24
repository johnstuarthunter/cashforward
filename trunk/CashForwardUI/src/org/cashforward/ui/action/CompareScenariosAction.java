/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.action;

import java.util.Collection;
import java.util.List;
import org.cashforward.model.Scenario;
import org.cashforward.ui.UIContext;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class CompareScenariosAction extends CallableSystemAction {

    private Lookup.Result scenarioNotifier =
            UIContext.getDefault().lookupResult(Scenario.class);
    private Collection<Scenario> currentScenarios;

    public CompareScenariosAction(){
        super();
        scenarioNotifier.addLookupListener(new LookupListener() {

            public void resultChanged(LookupEvent event) {
                Lookup.Result r = (Lookup.Result) event.getSource();
                Collection c = r.allInstances();
                if (!c.isEmpty() && c.size() > 1) {
                    CompareScenariosAction.this.currentScenarios = c;
                    setEnabled(true);
                } else
                    setEnabled(false);
            }
        });
    }

    public void performAction() {
        // TODO implement action body
    }

    public String getName() {
        return NbBundle.getMessage(CompareScenariosAction.class, "CTL_CompareScenariosAction");
    }

    @Override
    protected String iconResource() {
        return "org/cashforward/ui/action/utilities-system-monitor.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
