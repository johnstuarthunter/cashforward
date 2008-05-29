/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.ui.action;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import org.cashforward.model.Payment;
import org.cashforward.ui.adapter.PaymentServiceAdapter;

/**
 * 
 * @author Bill 
 */
public class SavePaymentAction extends AbstractAction {
    private Payment payment;
     List<Payment> payments;
    private PaymentServiceAdapter adapter;
    
    public SavePaymentAction(Payment payment, List<Payment> payments){
        this.payment = payment;
        this.payments = payments;
    }
    
    public void actionPerformed(ActionEvent e) {
        if (adapter == null)
            adapter = new PaymentServiceAdapter();
    
        adapter.addOrUpdatePayment(payment);    
    }
    
    
    
}
