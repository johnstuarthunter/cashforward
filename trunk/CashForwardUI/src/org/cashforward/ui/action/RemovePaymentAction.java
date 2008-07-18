/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.action;

import java.util.Collection;
import org.cashforward.model.Payment;
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
import org.openide.util.actions.CallableSystemAction;

public final class RemovePaymentAction extends CallableSystemAction {

    private Lookup.Result paymentNotifier =
            UIContext.getDefault().lookupResult(Payment.class);
    private PaymentService paymentService;
    private Payment payment;
    private Scenario currentScenario;

    public RemovePaymentAction() {
        super();
        paymentService = new PaymentService();
        paymentNotifier.addLookupListener(new LookupListener() {

            public void resultChanged(LookupEvent event) {
                Lookup.Result r = (Lookup.Result) event.getSource();
                Collection c = r.allInstances();
                if (!c.isEmpty()) {
                    RemovePaymentAction.this.payment =
                            (Payment) c.iterator().next();
                    setEnabled(true);
                } else {
                    setEnabled(false);
                }
            }
        });
    }

    public void performAction() {
        if (this.payment == null) {
            return;
        }
        NotifyDescriptor notify =
                new NotifyDescriptor.Confirmation(
                "Are tou sure you want to remove this payment");
        Object result = DialogDisplayer.getDefault().notify(notify);
        if (result == DialogDescriptor.YES_OPTION) {
            try {
                currentScenario = UIContext.getDefault().getScenario();
                if (payment.inMulipleScenarios()) {
                    payment.removeScenario(currentScenario);
                    if (!paymentService.addOrUpdatePayment(payment)){
                         //do something
                        System.out.println("Could not remove the payment");
                        return;
                    }
                }
                else if (!paymentService.removePayment(payment)){
                    //do something
                    System.out.println("Could not remove the payment");
                    return;
                }
                    
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            UIContext.getDefault().removePayment(payment);
            /*f (payment.isScheduled()) {
                UIContext.getDefault().removeScheduledPayment(payment);
            } else {
                UIContext.getDefault().removeCurrentPayment(payment);
            }*/
        }
    }

    public String getName() {
        return NbBundle.getMessage(RemovePaymentAction.class, "CTL_RemovePaymentAction");
    }

    @Override
    protected String iconResource() {
        return "org/cashforward/ui/action/delp.GIF";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
