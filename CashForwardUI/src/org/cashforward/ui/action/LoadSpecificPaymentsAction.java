/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.action;

import ca.odell.glazedlists.EventList;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractAction;
import org.cashforward.model.Payment;
import org.cashforward.model.PaymentSearchCriteria;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * 
 * @author Bill 
 */
public class LoadSpecificPaymentsAction extends AbstractAction
        implements LookupListener {

    PaymentServiceAdapter serviceAdapter;
    Payment.Occurence occurence;
    Lookup.Result occurenceNotifier =
            UIContext.getDefault().lookupResult(Payment.Occurence.class);

    public LoadSpecificPaymentsAction() {
        occurenceNotifier.addLookupListener(this);
    }
    //TODO threading
    public void actionPerformed(ActionEvent e) {
        if (serviceAdapter == null) {
            serviceAdapter = new PaymentServiceAdapter();
        }
        PaymentSearchCriteria filter =
                UIContext.getDefault().getPaymentFilter();

        //hacky I think

        EventList<Payment> paymentList = occurence == Payment.Occurence.NONE ?
                UIContext.getDefault().getCurrentPayments() :
                UIContext.getDefault().getScheduledPayments();

        List allPayments = serviceAdapter.getPayments(filter);
        paymentList.getReadWriteLock().writeLock().lock();
        paymentList.clear();
        paymentList.addAll(allPayments);
        paymentList.getReadWriteLock().writeLock().unlock();

    }

    public void resultChanged(LookupEvent event) {
        Lookup.Result r = (Lookup.Result) event.getSource();
        Collection c = r.allInstances();
        if (!c.isEmpty()) {
            occurence = (Payment.Occurence) c.iterator().next();
        }
    }
}
