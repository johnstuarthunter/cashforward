package org.cashforward.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import org.cashforward.model.Payment;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.cashforward.ui.internal.UILogger;
import org.cashforward.ui.internal.options.UIOptions;
import org.cashforward.ui.payment.PaymentCompositePanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;

/**
 * Presents a dialog for adding a new <code>Payment</code>
 * 
 * @author Bill
 */
public final class NewPaymentAction extends BaseCallableSystemAction {

    public void performAction() {
        PaymentServiceAdapter paymentService = 
                new PaymentServiceAdapter();
           
        Payment newPayment = new Payment();
        newPayment.setStartDate(new Date());
        
        final PaymentCompositePanel paymentDetailPanel = 
                new PaymentCompositePanel();
        paymentDetailPanel.setPayees(UIContext.getDefault().getPayees());
        paymentDetailPanel.setLabels(UIContext.getDefault().getLabels());
        paymentDetailPanel.setPayment(newPayment);
        DialogDescriptor dd = 
                new DialogDescriptor(paymentDetailPanel, 
                getMessage("CTL_NewPaymentTitle"));
        dd.setModal(true);
        dd.setLeaf(true);
        dd.setOptionType(DialogDescriptor.OK_CANCEL_OPTION);
        
        //--check for required label preferences
        dd.setButtonListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == DialogDescriptor.OK_OPTION &&
                        UIOptions.paymentsRequireLabel()){
                    if (!paymentHasLabel(paymentDetailPanel.getPayment())){
                       System.out.println("label required");
                    }
                }
            }
        });
        //--
        
        Object result = DialogDisplayer.getDefault().notify(dd);
        
        if (result == DialogDescriptor.OK_OPTION) {
            paymentDetailPanel.getPayment();//or commit
            
            List currentScenarios = 
                    UIContext.getDefault().getSelectedScenarios();
            newPayment.addScenarios(currentScenarios);
          
           if (paymentService.addOrUpdatePayment(newPayment)) {
               //update the context
               UIContext.getDefault().addPayment(newPayment); 
               UIContext.getDefault().addLabels(newPayment.getLabels());
               UIContext.getDefault().addPayee(newPayment.getPayee());
           } else {
                 UILogger.displayError(getMessage("CTL_unable_to_add"));
           }
        }
        
    }

    public String getName() {
        return getMessage("CTL_NewPaymentAction");
    }

    @Override
    protected String iconResource() {
        return "org/cashforward/ui/action/payment-new.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
    
    private boolean paymentHasLabel(Payment payment){
        boolean ok = true;
        if (payment.getLabels().size() >= payment.getScenarios().size())
            ok = false;
        return ok;
    }
}
