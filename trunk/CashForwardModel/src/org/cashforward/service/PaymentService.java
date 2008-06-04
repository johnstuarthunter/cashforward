/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.service;

import org.cashforward.model.PaymentSearchCriteria;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.cashforward.model.Payment;
import org.cashforward.persistence.PersistenceService;
import org.cashforward.service.internal.PaymentCalculator;

/**
 *
 * @author Bill 
 * 
 * NOTE:
 * The functionality exposed by this class will evolve and the UI evolves.
 * 
 * Yet to see:
 * -filtering payments by amount, payee
 * -considering payment overrides when query payments
 * 
 */
public class PaymentService {
    
    PersistenceService persistenceService;
    PaymentCalculator paymentCalculator;
    
     public PaymentService(){
         //TODO read db from config
         persistenceService = 
                 PersistenceService.getInstance(PersistenceService.STORAGE_DEV);
         paymentCalculator = new PaymentCalculator();
     }
    
    protected PaymentService(PersistenceService persistenceService){
        if (persistenceService == null)
            throw new IllegalArgumentException("PersistenceService cannot be null.");
        this.persistenceService = persistenceService;
    }
    
    public List<Payment> getScheduledPayments() 
        throws Exception {
            return persistenceService.getAllPayments();
    }
    
    public List<Payment> getPayments(PaymentSearchCriteria criteria) 
        throws Exception {
        Date start = criteria.getDateStart();
        Date end = criteria.getDateEnd();
        
        List<Payment> allPayments = new ArrayList();    
        List<Payment> payments = persistenceService.getAllPayments();
        for (Payment payment : payments) {
            List newPayments = 
                    paymentCalculator.calculatePayments(payment, start, end);
            //System.out.println(newPayments.size());
            allPayments.addAll(newPayments);
        }
        
        return allPayments;
    }
    
    public boolean addOrUpdatePayment(Payment newPayment) throws Exception {
        return persistenceService.addOrUpdatePayment(newPayment);
    }
    
    public boolean removePayment(Payment oldPayment) throws Exception {
        return persistenceService.removePayment(oldPayment);
    }
    
}
