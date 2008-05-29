/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.ui.adapter;

import java.util.List;
import org.cashforward.model.Payment;
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
    
    public List<Payment> getAllPayments(){
        try {
            return paymentService.getPayments(null);
        } catch (Exception e) {
        }
        
        return null;
    }
}
