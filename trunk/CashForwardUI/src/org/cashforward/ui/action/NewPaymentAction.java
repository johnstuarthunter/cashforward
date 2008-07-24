/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.action;

import java.util.Date;
import java.util.List;
import org.cashforward.model.Payment;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.cashforward.ui.payment.PaymentCompositePanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class NewPaymentAction extends CallableSystemAction {
    //set lookups here for payees , occurrences, scheduled/current payments
    public void performAction() {
        PaymentServiceAdapter paymentService = 
                new PaymentServiceAdapter();
           
        Payment newPayment = new Payment();
        newPayment.setStartDate(new Date());
        
        PaymentCompositePanel paymentDetailPanel = 
                new PaymentCompositePanel();
        paymentDetailPanel.setPayees(UIContext.getDefault().getPayees());
        paymentDetailPanel.setLabels(UIContext.getDefault().getLabels());
        paymentDetailPanel.setPayment(newPayment);
        DialogDescriptor dd = 
                new DialogDescriptor(paymentDetailPanel, "New Payment");
        dd.setModal(true);
        dd.setLeaf(true);
        dd.setOptionType(DialogDescriptor.OK_CANCEL_OPTION);

        Object result = DialogDisplayer.getDefault().notify(dd);
        
        if (result == DialogDescriptor.OK_OPTION) {
            paymentDetailPanel.getPayment();//or commit
            
            //Scenario currentScenario = UIContext.getDefault().getScenario();
            List currentScenarios = 
                    UIContext.getDefault().getSelectedScenarios();
            newPayment.addScenarios(currentScenarios);
          
           if (paymentService.addOrUpdatePayment(newPayment)) {
               /*if (!newPayment.isScheduled())
                UIContext.getDefault().addCurrentPayment(newPayment);
               else
                UIContext.getDefault().addScheduledPayment(newPayment);*/
               UIContext.getDefault().addPayment(newPayment); 
               UIContext.getDefault().addLabels(newPayment.getLabels());
               UIContext.getDefault().addPayee(newPayment.getPayee());
                       
           }
        }
        
    }

    public String getName() {
        return NbBundle.getMessage(NewPaymentAction.class, "CTL_NewPaymentAction");
    }

    @Override
    protected String iconResource() {
        return "org/cashforward/ui/action/newp.GIF";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
