/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.service;

import java.util.List;
import org.cashforward.model.Payment;
import org.cashforward.persistence.PersistenceService;

/**
 *
 * @author Bill 
 */
public class PaymentService {
    
    PersistenceService persistenceService;
    
     public PaymentService(){
         persistenceService = 
                 PersistenceService.getInstance(PersistenceService.STORAGE_DEV);
     }
    
    protected PaymentService(PersistenceService persistenceService){
        if (persistenceService == null)
            throw new IllegalArgumentException("PersistenceService cannot be null.");
        this.persistenceService = persistenceService;
    }
    
    public List<Payment> getPayments(PaymentSearchCriteria criteria) 
        throws Exception {
            return persistenceService.getAllPayments();
    }
    
    public boolean addOrUpdatePayment(Payment newPayment) throws Exception {
        return persistenceService.addOrUpdatePayment(newPayment);
    }
    
    public boolean removePayment(Payment oldPayment) throws Exception {
        return persistenceService.removePayment(oldPayment);
    }
    
}
