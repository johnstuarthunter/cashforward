/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.action;

import org.cashforward.model.Payment;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class SavePaymentAction extends CallableSystemAction {
    private PaymentServiceAdapter paymentService = 
            new PaymentServiceAdapter();
    
    public void performAction() {
        Payment payment = UIContext.getDefault().getPayment();
        System.out.println("saving payment:"+payment);
        if (payment == null)
            return;
        if (paymentService.addOrUpdatePayment(payment)){
            //what?
            UIContext.getDefault().setPayment(payment);
        } else {
            //what/
        }
    }

    public String getName() {
        return NbBundle.getMessage(SavePaymentAction.class, "CTL_SavePaymentAction");
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
