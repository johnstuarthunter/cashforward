/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.persistence;

import org.cashforward.*;
import org.cashforward.model.PaymentOverride;
import org.cashforward.persistence.PersistenceService;
import org.cashforward.model.Payment;
import org.cashforward.model.Label;
import org.cashforward.model.Payee;
import java.util.Calendar;
import java.util.Date;
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
public class PaymentCalculationTest {

    private static PersistenceService pservice = null;

    public PaymentCalculationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        pservice = PersistenceService.getInstance(PersistenceService.STORAGE_TEST);
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

    @Test
    public void testPaymentConsolidation() throws Exception {
        Date now = Calendar.getInstance().getTime();
        Label groceries = pservice.findOrCreateNewLabel("Dining");
        Payee walmart = pservice.findOrCreateNewPayee("Chipotle");
        Payment payment = new Payment(6.75f, walmart, now);
        payment.setOccurence("ONCE");
        payment.addLabel(groceries);

        PaymentOverride override = new PaymentOverride();
        override.setAmount(5f);
        now = Calendar.getInstance().getTime();
        override.setPaymentDate(now);
        payment.addPaymentOverride(override);

        pservice.addOrUpdatePayment(payment);
        System.out.println("Payment was added:" + payment.getId());
        assertTrue("Payment was added.", payment.getId() > 0);
        
        //setup is done, now test data
        
        
    }
}
