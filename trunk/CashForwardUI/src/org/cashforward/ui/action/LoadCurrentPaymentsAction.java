/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.ui.action;

import ca.odell.glazedlists.EventList;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.cashforward.model.Payment;
import org.cashforward.model.Scenario;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.adapter.PaymentServiceAdapter;
import org.cashforward.ui.task.PaymentFilter;

/**
 * 
 * @author Bill 
 */
public class LoadCurrentPaymentsAction extends AbstractAction {
    
    PaymentServiceAdapter serviceAdapter;
    
    public LoadCurrentPaymentsAction(){
        super("Current");
        
        putValue(Action.SHORT_DESCRIPTION, "Current");
    }
    
    //TODO threading
    public void actionPerformed(ActionEvent e) {
        if (serviceAdapter == null)
            serviceAdapter = new PaymentServiceAdapter();
        
        EventList<Payment> paymentList = (EventList)
                UIContext.getDefault().getCurrentPayments();
        
        Scenario scenario = 
                UIContext.getDefault().getScenario();
        
        List allPayments = serviceAdapter.getCurrentPayments(scenario);
        if (allPayments == null)
            return;
        
        paymentList.getReadWriteLock().writeLock().lock();
        paymentList.clear();
        paymentList.addAll(allPayments);
        paymentList.getReadWriteLock().writeLock().unlock();
        
        PaymentFilter filter = UIContext.getDefault().getPaymentFilter();
        filter.setPaymentType(PaymentFilter.TYPE_CURRENT);
        UIContext.getDefault().setPaymentFilter(filter);
        
    }
    
}
