/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.cashforward.model.Payee;
import org.cashforward.model.Payment;
import org.cashforward.model.PaymentSearchCriteria;
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
public class PaymentServiceTest {

    public PaymentServiceTest() {
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
     * Test of getScheduledPayments method, of class PaymentService.
     */
    @Test
    public void testGetScheduledPayments() throws Exception {
        System.out.println("getScheduledPayments");
        PaymentService instance = new PaymentService();
        Payment sp = new Payment(0.0f, new Payee("Me"), new Date());
        Scenario base = instance.getDefaultScenario();
        sp.addScenario(base);
        sp.setOccurence(Payment.Occurence.BIWEEKLY.name());
        instance.addOrUpdatePayment(sp);
        List<Payment> result = instance.getScheduledPayments(base);
        for (Payment payment : result) {
            System.out.println(payment);
        }
        assertTrue("something is in the db", result.size() > 0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
        
    }

    /**
     * Test of getPayments method, of class PaymentService.
     */
    @Test
    public void testGetPayments() throws Exception {
        System.out.println("getPayments");
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_YEAR, start.getMinimum(Calendar.DAY_OF_YEAR));
        
        Calendar end = Calendar.getInstance();
        end.set(Calendar.DAY_OF_YEAR, start.getMaximum(Calendar.DAY_OF_YEAR));
        
        PaymentSearchCriteria criteria = new PaymentSearchCriteria();
        criteria.setDateStart(start.getTime());
        criteria.setDateEnd(end.getTime());
        
        
        PaymentService instance = new PaymentService();
        List<Payment> result = instance.getPayments(criteria);
        //assertTrue("calculator is working", result.size() > 0);
        for (Payment payment : result) {
            System.out.println(payment);
        }
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of addOrUpdatePayment method, of class PaymentService.
     */
    @Test
    public void testAddOrUpdatePayment() throws Exception {
    }
    /**
     * Test of removePayment method, of class PaymentService.
     */
    @Test
    public void testRemovePayment() throws Exception {
    }

}