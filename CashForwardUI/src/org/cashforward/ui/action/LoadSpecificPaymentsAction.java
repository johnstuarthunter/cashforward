/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.action;

import ca.odell.glazedlists.EventList;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import org.cashforward.model.Payment;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.cashforward.ui.task.PaymentFilter;

/**
 * 
 * @author Bill 
 */
public class LoadSpecificPaymentsAction extends AbstractAction {

    PaymentServiceAdapter serviceAdapter;
    PaymentFilter filter;

    //TODO threading
    public void actionPerformed(ActionEvent e) {
        filter = UIContext.getDefault().getPaymentFilter();
        
        if (filter == null || filter.getPaymentSearchCriteria() == null)
            return;
        
        if (serviceAdapter == null) {
            serviceAdapter = new PaymentServiceAdapter();
        }

        EventList<Payment> paymentList =
                UIContext.getDefault().getPayments();

        filter.setScenario(UIContext.getDefault().getScenario());
        
        List allPayments = serviceAdapter.getPayments(
                filter.getPaymentSearchCriteria());
        
        if (allPayments == null)
            return;
        
        paymentList.getReadWriteLock().writeLock().lock();
        paymentList.clear();
        paymentList.addAll(allPayments);
        paymentList.getReadWriteLock().writeLock().unlock();
        
        filter.setPaymentType(PaymentFilter.TYPE_CALCULATED);
        UIContext.getDefault().setPaymentFilter(filter);
    }

}
