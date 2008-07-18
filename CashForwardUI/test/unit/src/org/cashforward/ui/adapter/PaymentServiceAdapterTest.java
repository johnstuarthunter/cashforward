/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.ui.adapter;

import java.util.Date;
import java.util.List;
import org.cashforward.model.Payee;
import org.cashforward.model.Payment;
import org.cashforward.model.Scenario;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Bill
 */
public class PaymentServiceAdapterTest {

    public PaymentServiceAdapterTest() {
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
     * Test of addOrUpdatePayment method, of class PaymentServiceAdapter.
     */
    @Test
    public void testAddOrUpdatePayment() {
        System.out.println("addOrUpdatePayment");
        PaymentServiceAdapter instance = new PaymentServiceAdapter();
        Payment payment
            = new Payment(4.01f,new Payee("Starbucks"),new Date());
        payment.setOccurence("daily");
        boolean ok = instance.addOrUpdatePayment(payment);
        assertTrue("Payment added/updated", ok);
        
    }

    /**
     * Test of getAllPayments method, of class PaymentServiceAdapter.
     */
    @Test
    public void testGetAllPayments() {

        System.out.println("getAllPayments");
        PaymentServiceAdapter instance = new PaymentServiceAdapter();
        Scenario base = instance.getDefaultScenario();
        
        Payment payment
            = new Payment(4.01f,new Payee("Starbucks"),new Date());
        payment.setOccurence("daily");
        boolean ok = instance.addOrUpdatePayment(payment);
        assertTrue("Payment added/updated", ok);
        List<Payment> result = instance.getScheduledPayments();
        assertTrue("Got all payments.",result.size() > 1);
    }

}