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
public class FindPaymentAction extends AbstractAction {

    PaymentServiceAdapter serviceAdapter;
    List<Payment> paymentList;
    
    public FindPaymentAction(List<Payment> paymentList){
        this.paymentList = paymentList;
    }
    
    //TODO threading
    public void actionPerformed(ActionEvent e) {
        if (serviceAdapter == null)
            serviceAdapter = new PaymentServiceAdapter();
        
        List allPayments = serviceAdapter.getAllPayments();
        paymentList.clear();
        paymentList.addAll(allPayments);
        
    }
    
}
