package org.cashforward.ui.action;

import java.util.ArrayList;
import java.util.List;
import org.cashforward.model.Payment;
import org.cashforward.model.Scenario;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.cashforward.ui.internal.UILogger;
import org.cashforward.ui.payment.PaymentCompositePanel;
import org.openide.util.HelpCtx;

/**
 * Synchronizes the selected <code>Payment</code> with the data store.
 * 
 * @author Bill
 */
public final class SavePaymentAction extends BaseCallableSystemAction {
    private PaymentServiceAdapter paymentService = 
            new PaymentServiceAdapter();
    private PaymentCompositePanel paymentForm;
    public void performAction() {
        if (paymentForm == null)
            return;

        //make a copy of the Payment.
        Payment o = UIContext.getDefault().getPayment();
        Payment oldPayment = new Payment(
                o.getAmount(),o.getPayee(),o.getStartDate());
        List oldLabels = new ArrayList(UIContext.getDefault().getLabels());
        oldLabels.retainAll(o.getLabels());
        oldPayment.setLabels(oldLabels);
        List oldScenarios = new ArrayList(UIContext.getDefault().getScenarios());
        oldLabels.retainAll(o.getScenarios());
        oldPayment.addScenarios(oldScenarios);
        oldPayment.setOccurence(o.getOccurence());
        oldPayment.setEndDate(o.getEndDate());
        oldPayment.setDescription(o.getDescription());

        //get the soon to be modified payment
        //Actually, it may be interesting if the UIContext could fire
        //events in a property-change type way so to see old and new values
        //that would help clean up the above code
        
        Payment payment = paymentForm.getPayment();
        UILogger.LOG.finest("saving payment:"+payment);

        List<Scenario> selectedScenarios = new ArrayList(
                UIContext.getDefault().getSelectedScenarios());
        //if the payment has a scenario that is not selected,
        //we need to create a copy of the old payment for it
        List<Scenario> paymentScenarios = new ArrayList(oldPayment.getScenarios());
        paymentScenarios.removeAll(selectedScenarios);

        if (!paymentScenarios.isEmpty()) {//then keep the old payment around
            payment.removeScenarios(paymentScenarios);
            oldPayment.getLabels().removeAll(selectedScenarios);
            if (paymentService.addOrUpdatePayment(oldPayment))
                UIContext.getDefault().getPayments().add(oldPayment);
        }

        if (paymentService.addOrUpdatePayment(payment)){
            UIContext.getDefault().setPayment(payment);
        } else {
            UILogger.displayError(getMessage("CTL_unable_to_update_payment"));
        }
    }

    public void setPaymentForm(PaymentCompositePanel form){
        this.paymentForm = form;
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
