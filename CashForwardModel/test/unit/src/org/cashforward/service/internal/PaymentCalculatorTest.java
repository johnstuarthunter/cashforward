/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.service.internal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.cashforward.model.Payee;
import org.cashforward.model.Payment;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wsnyder
 */
public class PaymentCalculatorTest {

    public PaymentCalculatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of calculatePayments method, of class PaymentCalculator.
     */
    @Test
    public void testCalculatePayments() throws Exception {
        System.out.println("calculatePayments");
        Calendar start = Calendar.getInstance();
        start.set(Calendar.MONTH, Calendar.MAY);
        start.set(Calendar.DAY_OF_MONTH, 1);
        
        Calendar end = Calendar.getInstance();
        end.set(Calendar.MONTH, Calendar.DECEMBER);
        end.set(Calendar.DAY_OF_MONTH, 1);
        //end.set(Calendar.YEAR, 2009);
        
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MONTH, Calendar.JUNE);
        now.set(Calendar.DAY_OF_MONTH, 2);

        Calendar paymentEndDate = Calendar.getInstance();
        paymentEndDate.set(Calendar.MONTH, Calendar.AUGUST);
        paymentEndDate.set(Calendar.DAY_OF_MONTH, 3);
        
        Payment payment = new Payment();
        payment.setAmount(4.00f);
        payment.setPayee(new Payee("GetGo"));
        payment.setOccurence(Payment.Occurence.BIWEEKLY.name());
        payment.setStartDate(now.getTime());
        payment.setEndDate(paymentEndDate.getTime());
        
       
        PaymentCalculator instance = new PaymentCalculator();
        
        List<Payment> result = instance.calculatePayments(
                payment, start.getTime(), end.getTime());
        
        
        for (Payment payment1 : result) {
            System.out.println(payment1);
        }
        
    }

}