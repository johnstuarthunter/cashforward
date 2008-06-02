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
        if ("weekly".equals(occurence)) {//enum - every 7 days
            //when the range start is after the payment start
            //figure out what the earliest payment date is in the range

            //get day difference between range start and payment start
            int offsetStart = DateUtilities.daysBetween(start,paymentStart);
            System.out.println("offsetStart:"+offsetStart);
            //if positive, range start is before payment
            //so, nextPaymentDate = start + (offset / 7 days)
            if (offsetStart > -1)
                nextPaymentDate = paymentStart;
            else
                nextPaymentDate = 
                        DateUtilities.getDateAfterDays(start, offsetStart % 7);

            //System.out.println("nextPaymentDate:"+nextPaymentDate);
            
            //after we have the nextPaymentDate, 
            //keep adding 7 days while we are before the range and payment end
            while (nextPaymentDate.before(end) && 
                    nextPaymentDate.before(paymentEnd)) {
                payments.add(createPayment(payment, nextPaymentDate));
                nextPaymentDate = 
                        DateUtilities.getDateAfterDays(nextPaymentDate, 7);
                //System.out.println("nextPaymentDate:"+nextPaymentDate);
                
            }

        }

        return payments;

    }

    private Payment createPayment(Payment base, Date nextDate) {
        Payment newPayment = null;
        newPayment = new Payment(base.getAmount(), base.getPayee(), nextDate);
        return newPayment;

    }

}

