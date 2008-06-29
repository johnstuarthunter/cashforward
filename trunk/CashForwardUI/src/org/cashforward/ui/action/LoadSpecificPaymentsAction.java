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
    
    public LoadSpecificPaymentsAction(){
    }
    
    //TODO threading
    public void actionPerformed(ActionEvent e) {
        if (serviceAdapter == null)
            serviceAdapter = new PaymentServiceAdapter();
        
       EventList<Payment> paymentList =
                UIContext.getDefault().getCurrentPayments();
                
        Calendar start = Calendar.getInstance();
        start.set(Calendar.MONTH, Calendar.FEBRUARY);
        start.set(Calendar.DAY_OF_MONTH, 1);
        
        Calendar end = Calendar.getInstance();
        end.set(Calendar.MONTH, Calendar.DECEMBER);
        end.set(Calendar.DAY_OF_MONTH, 1);
        end.set(Calendar.YEAR, 2010);
        
        PaymentSearchCriteria criteria = new PaymentSearchCriteria();
        criteria.setDateStart(start.getTime());
        criteria.setDateEnd(end.getTime());
        
        List allPayments = serviceAdapter.getPayments(criteria);
        paymentList.getReadWriteLock().writeLock().lock();
        paymentList.clear();
        paymentList.addAll(allPayments);
        paymentList.getReadWriteLock().writeLock().unlock();

    }
    
}
