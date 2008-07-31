package org.cashforward.ui.action;

import java.util.Collection;
import java.util.List;
import org.cashforward.model.Payment;
import org.cashforward.service.PaymentService;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.internal.UILogger;
import org.cashforward.ui.task.PaymentFilter;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;

public final class RemovePaymentAction extends BaseCallableSystemAction {

    private Lookup.Result paymentNotifier =
            UIContext.getDefault().lookupResult(Payment.class);
    private PaymentService paymentService;
    private Payment payment;
    private List currentScenarios;

    public RemovePaymentAction() {
        super();
        setEnabled(false);
        paymentService = new PaymentService();
        paymentNotifier.addLookupListener(new LookupListener() {

            public void resultChanged(LookupEvent event) {
                Lookup.Result r = (Lookup.Result) event.getSource();
                Collection c = r.allInstances();
                if (!c.isEmpty()) {
                    RemovePaymentAction.this.payment =
                            (Payment) c.iterator().next();
                    PaymentFilter filter =
                            UIContext.getDefault().getPaymentFilter();
                    if (filter != null && filter.getPaymentType() ==
                            PaymentFilter.TYPE_CALCULATED)
                        setEnabled(false);
                    else
                        setEnabled(true);
                } else {
                    setEnabled(false);
                }
            }
        });
    }

    public void performAction() {

        NotifyDescriptor notify =
                new NotifyDescriptor.Confirmation(
                getMessage("CTL_confirm_remove_payment"));
        Object result = DialogDisplayer.getDefault().notify(notify);
        if (result == DialogDescriptor.YES_OPTION) {
            try {
                currentScenarios = UIContext.getDefault().getSelectedScenarios();
                if (payment.inMulipleScenarios()) {
                    payment.removeScenarios(currentScenarios);
                    if (!paymentService.addOrUpdatePayment(payment)){
                         UILogger.displayError(getMessage(
                            "CTL_unable_to_remove_payment"));
                        return;
                    }
                }
                else if (!paymentService.removePayment(payment)){
                    UILogger.displayError(getMessage(
                            "CTL_unable_to_remove_payment"));
                    return;
                }
                    
            } catch (Exception e) {
                UILogger.displayError(getMessage(
                            "CTL_unable_to_remove_payment"),e);
                return;
            }
            UIContext.getDefault().removePayment(payment);
        }
    }

    public String getName() {
        return NbBundle.getMessage(RemovePaymentAction.class, "CTL_RemovePaymentAction");
    }

    @Override
    protected String iconResource() {
        return "org/cashforward/ui/action/payment-delete.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
