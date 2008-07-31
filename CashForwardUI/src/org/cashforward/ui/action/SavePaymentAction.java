package org.cashforward.ui.action;

import org.cashforward.model.Payment;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.cashforward.ui.internal.UILogger;
import org.openide.util.HelpCtx;

/**
 * Synchronizes the selected <code>Payment</code> with the data store.
 * 
 * @author Bill
 */
public final class SavePaymentAction extends BaseCallableSystemAction {
    private PaymentServiceAdapter paymentService = 
            new PaymentServiceAdapter();
    
    public void performAction() {
        Payment payment = UIContext.getDefault().getPayment();
        UILogger.LOG.finest("saving payment:"+payment);
        if (payment == null)
            return;
        if (paymentService.addOrUpdatePayment(payment)){
            UIContext.getDefault().setPayment(payment);
        } else {
            UILogger.displayError(getMessage("CTL_unable_to_update_payment"));
        }
    }

    public String getName() {
        return getMessage("CTL_SavePaymentAction");
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
