/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.action;

import java.util.Collection;
import org.cashforward.model.Payment;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class SkipScheduledPaymentAction extends CallableSystemAction {

    private Payment payment;
    private PaymentServiceAdapter paymentService =
            new PaymentServiceAdapter();
    private Lookup.Result paymentNotifier =
            UIContext.getDefault().lookupResult(Payment.class);

    public SkipScheduledPaymentAction() {
        super();
        setEnabled(payment != null);
        paymentNotifier.addLookupListener(new LookupListener() {

            public void resultChanged(LookupEvent event) {
                Lookup.Result r = (Lookup.Result) event.getSource();
                Collection c = r.allInstances();
                if (!c.isEmpty()) {
                    SkipScheduledPaymentAction.this.payment =
                            (Payment) c.iterator().next();
                    setEnabled(!Payment.Occurence.NONE.name().equals(
                            payment.getOccurence()));
                } else
                    setEnabled(false);
            }
        });
    }

    public void performAction() {
        if (payment == null) {
            return;
        }
        if (paymentService.skipNextPayment(payment)) { 
            //do something
        }
    }

    public String getName() {
        return NbBundle.getMessage(SkipScheduledPaymentAction.class, "CTL_SkipScheduledPaymentAction");
    }

    @Override
    protected String iconResource() {
        return "org/cashforward/ui/action/skipp.GIF";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
