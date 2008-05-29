/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.service.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.cashforward.model.Payment;

/**
 * 
 * @author Bill 
 */
public class PaymentCalculator {

    public List<Payment> calculatePayments(Payment payment, Date start, Date end)
            throws Exception {
        List<Payment> payments = new ArrayList();

        Date paymentStart = payment.getStartDate();
        Date paymentEnd = payment.getEndDate();
        Date nextPaymentDate;
        
        //this needs to linked to an enum
        String occurence = payment.getOccurence();
        
        //pseudocode...
        //sample
        if ("weekly".equals(occurence)){//enum - every 7 days
            //when the range start is after the payment start
            //figure out what the earliest payment date is in the range
            
            //get day difference between range start and payment start
            //if positive, range start is after payment
            //so, nextPaymentDate = start + (diff % 7 days)
            
            //after we have the nextPaymentDate, 
            //keep adding 7 days while we are before the range end
            
        }
        
        

        return payments;

    }
}

