/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.service;

import java.util.Calendar;
import java.util.List;
import org.cashforward.model.Payment;
import org.cashforward.model.PaymentSearchCriteria;
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
        List<Payment> result = instance.getScheduledPayments();
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
        start.set(Calendar.MONTH, Calendar.MAY);
        start.set(Calendar.DAY_OF_MONTH, 1);
        
        Calendar end = Calendar.getInstance();
        end.set(Calendar.MONTH, Calendar.DECEMBER);
        end.set(Calendar.DAY_OF_MONTH, 1);
        
        PaymentSearchCriteria criteria = new PaymentSearchCriteria();
        criteria.setDateStart(start.getTime());
        criteria.setDateEnd(end.getTime());
        
        PaymentService instance = new PaymentService();
        List<Payment> result = instance.getPayments(criteria);
        assertTrue("calculator is working", result.size() > 0);
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
        System.out.println("addOrUpdatePayment");
        Payment newPayment = null;
        PaymentService instance = new PaymentService();
        boolean expResult = false;
        boolean result = instance.addOrUpdatePayment(newPayment);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removePayment method, of class PaymentService.
     */
    @Test
    public void testRemovePayment() throws Exception {
        System.out.println("removePayment");
        Payment oldPayment = null;
        PaymentService instance = new PaymentService();
        boolean expResult = false;
        boolean result = instance.removePayment(oldPayment);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}