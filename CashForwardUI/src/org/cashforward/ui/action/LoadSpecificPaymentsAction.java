/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.action;

import ca.odell.glazedlists.EventList;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.List;
import javax.swing.AbstractAction;
import org.cashforward.model.Payment;
import org.cashforward.model.PaymentSearchCriteria;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;

/**
 * 
 * @author Bill 
 */
public class LoadSpecificPaymentsAction extends AbstractAction {

    PaymentServiceAdapter serviceAdapter;

    public LoadSpecificPaymentsAction() {
    }
    //TODO threading
    public void actionPerformed(ActionEvent e) {
        if (serviceAdapter == null) {
            serviceAdapter = new PaymentServiceAdapter();
        }
        PaymentSearchCriteria filter =
                UIContext.getDefault().getPaymentFilter();

        EventList<Payment> paymentList =
                UIContext.getDefault().getCurrentPayments();

        List allPayments = serviceAdapter.getPayments(filter);
        paymentList.getReadWriteLock().writeLock().lock();
        paymentList.clear();
        paymentList.addAll(allPayments);
        paymentList.getReadWriteLock().writeLock().unlock();

    }
}
