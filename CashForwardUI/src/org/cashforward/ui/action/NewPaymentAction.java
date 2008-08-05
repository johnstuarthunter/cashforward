package org.cashforward.ui.action;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.cashforward.model.Payment;
import org.cashforward.model.Scenario;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.cashforward.ui.internal.UILogger;
import org.cashforward.ui.payment.PaymentCompositePanel;
import org.cashforward.ui.payment.PaymentDetailPanel;
import org.cashforward.util.DateUtilities;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Property;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * Presents a dialog for adding a new <code>Payment</code>
 * 
 * @author Bill
 */
public final class NewPaymentAction extends BaseCallableSystemAction {

    private Lookup.Result scenarioNotifier =
            UIContext.getDefault().lookupResult(Scenario.class);

    public NewPaymentAction() {
        super();
        //setEnabled(false);
        scenarioNotifier.addLookupListener(new LookupListener() {

            public void resultChanged(LookupEvent event) {
                Lookup.Result r = (Lookup.Result) event.getSource();
                Collection c = r.allInstances();
                if (!c.isEmpty()) {
                    setEnabled(true);
                } else {
                    setEnabled(false);
                }
            }
        });
    }

    public void performAction() {
        PaymentServiceAdapter paymentService =
                new PaymentServiceAdapter();

        Payment newPayment = new Payment();
        newPayment.setStartDate(new Date());

        final PaymentCompositePanel paymentDetailPanel =
                new PaymentCompositePanel();
        paymentDetailPanel.allowUpdate(false);
        paymentDetailPanel.setPayees(UIContext.getDefault().getPayees());
        paymentDetailPanel.setLabels(UIContext.getDefault().getLabels());
        paymentDetailPanel.setPayment(newPayment);
        final DialogDescriptor dd =
                new DialogDescriptor(paymentDetailPanel,
                getMessage("CTL_NewPaymentTitle"));
        dd.setModal(true);
        dd.setLeaf(true);
        dd.setOptionType(DialogDescriptor.OK_CANCEL_OPTION);

        //---Prevent closing of form unless the data is valid
        Property dialogValid = BeanProperty.create("valid");
        Property formValid = PaymentDetailPanel.PROP_paymentValid;

        Binding binding =
                Bindings.createAutoBinding(UpdateStrategy.READ,
                paymentDetailPanel.getPaymentDetailComponent(), formValid,
                dd, dialogValid);
        binding.bind();
        //---


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
}
