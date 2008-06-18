/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.ui.adapter;

import ca.odell.glazedlists.EventList;
import java.util.List;
import org.cashforward.model.Payment;
import org.cashforward.model.PaymentSearchCriteria;
import org.cashforward.service.PaymentService;

/**
 * 
 * @author Bill 
 */
public class PaymentServiceAdapter {
    private PaymentService paymentService;
    
    public PaymentServiceAdapter(){
        paymentService = new PaymentService();
    }

    public boolean addOrUpdatePayment(Payment payment) {
        try {
            return paymentService.addOrUpdatePayment(payment);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List getPayees() {
       try {
            return paymentService.getPayees();
        } catch (Exception e) {
        }
        
        return null;
    }
    
    public List<Payment> getPayments(PaymentSearchCriteria criteria){
        try {
            return paymentService.getPayments(criteria);
        } catch (Exception e) {
        }
        
        return null;
    }
    
    public List<Payment> getCurrentPayments(){
        try {
            return paymentService.getCurrentPayments();
        } catch (Exception e) {
        }
        
        return null;
    }
    
    public List<Payment> getScheduledPayments(){
        try {
            return paymentService.getScheduledPayments();
        } catch (Exception e) {
        }
        
        return null;
    }
}
