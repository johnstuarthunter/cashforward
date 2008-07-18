/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.persistence;

import org.cashforward.*;
import org.cashforward.model.PaymentOverride;
import org.cashforward.model.Payment;
import org.cashforward.model.Label;
import org.cashforward.model.Payee;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cashforward.model.PaymentSearchCriteria;
import org.cashforward.model.Scenario;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author Bill
 */
public class PersistenceServiceTest {

    private static PersistenceService pservice = null;

    public PersistenceServiceTest() {
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
    public void testGetScenarios(){
        Scenario news = new Scenario("new scenario");
        try {
            pservice.addOrUpdateScenario(news);
            List<Scenario> s = pservice.getScenarios();
            assertTrue("Got Scenarios!",s.size() > 0);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    @Test
    public void testAddScenario() {
        try {
            Scenario scenario = new Scenario("Current");
            pservice.addOrUpdateLabel(scenario);
            System.out.println("Scenario was added:" + scenario.getId());
            assertTrue("Scenario was added.", scenario.getId() > 0);
        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testRemoveScenario() {
        try {
            Payment amIBaweeted = new Payment(0f, new Payee("Homestar"), new Date());
            pservice.addOrUpdatePayment(amIBaweeted);
            assertTrue("Payment was added.",amIBaweeted.getId() > 0);
            
            Scenario scenario = new Scenario("Current To Delete");
            amIBaweeted.addScenario(scenario);
            
            pservice.addOrUpdatePayment(amIBaweeted);
            scenario = amIBaweeted.getScenarios().get(0);
            System.out.println("Scenario was added:" + scenario.getId());
            assertTrue("Scenario was added.",scenario.getId() > 0);

            amIBaweeted.removeScenario(scenario);
            assertTrue("Baweeted!",amIBaweeted.getScenarios().size() == 0);
            pservice.addOrUpdatePayment(amIBaweeted);
            assertTrue("For real! Baweeted!",amIBaweeted.getScenarios().size() == 0);
           
            
        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testAddPayee() {
        try {
            Payee walmart = new Payee("Wal-Mart");
            pservice.addOrUpdatePayee(walmart);
            System.out.println("Payee was added:" + walmart.getId());
            assertTrue("Payee was added.", walmart.getId() > 0);
        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testUpdatePayee() {
        try {
            Payee walmart = new Payee("Wal-Mart");
            pservice.addOrUpdatePayee(walmart);
            System.out.println("Payee was added:" + walmart.getId());
            assertTrue("Payee was added.", walmart.getId() > 0);

            walmart.setName("Walmart");
            pservice.addOrUpdatePayee(walmart);

            Payee updatedPayee = pservice.getPayeeByID(walmart.getId());
            assertEquals(walmart.getName(), updatedPayee.getName());
        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testFindAllPayees() {
        try {
            Payee walmart = new Payee("Bill-Mart");
            pservice.addOrUpdatePayee(walmart);
            System.out.println("Payee was added:" + walmart.getId());
            assertTrue("Payee was added.", walmart.getId() > 0);

            List<Payee> payees = pservice.getPayees();
            assertTrue("Payees exist", payees.size() > 0);
        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testAddLabel() {
        try {
            Label groceries = new Label("Groceries");
            pservice.addOrUpdateLabel(groceries);
            System.out.println("Label was added:" + groceries.getId());
            assertTrue("Label was added.", groceries.getId() > 0);
        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testUpdateLabel() {
        try {
            Label label = pservice.findOrCreateNewLabel("Groceries");
            label.setName("Food");
            pservice.addOrUpdateLabel(label);

            Label updatedLabel = pservice.getLabelByID(label.getId());
            assertEquals(label.getName(), updatedLabel.getName());
            System.out.println(label);
        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testFindAllLabels() {
        try {
            Label groceries = new Label("Groceries");
            pservice.addOrUpdateLabel(groceries);
            System.out.println("Label was added:" + groceries.getId());
            assertTrue("Label was added.", groceries.getId() > 0);

            List<Label> labels = pservice.getLabels();
            assertTrue("Labels exist", labels.size() > 0);
        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    @Test
    public void testAddPayment() {
        try {
            //ScenarioImpl scenario = new ScenarioImpl("Test");
            //pservice.addOrUpdateScenario(scenario);
            
            Date now = Calendar.getInstance().getTime();
            Label groceries = new Label("Groceries");
            Payee walmart = new Payee("Wal-Mart");
            Payment payment = new Payment(45.22f, walmart, now);
            payment.setOccurence("ONCE");
            
            //Label scenarioLabel = new Label("Current");
            //payment.addLabel(scenarioLabel);
            pservice.addOrUpdatePayment(payment);
            
            //scenario.addPayment(payment);
            //pservice.addOrUpdateScenario(scenario);
            
            System.out.println("Payment was added:" + payment.getId());
            assertTrue("Payment was added.", payment.getId() > 0);
        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testGetCurrentPayments() {
        try {
            Scenario base = getDefaultScenario();
            Date now = Calendar.getInstance().getTime();
            Label groceries = new Label("Groceries");
            Payee walmart = new Payee("Wal-Mart");
            Payment payment = new Payment(45.22f, walmart, now);
            payment.setOccurence("NONE");
            payment.addScenario(base);
            pservice.addOrUpdatePayment(payment);
            System.out.println("Payment was added:" + payment.getId());
            assertTrue("Payment was added.", payment.getId() > 0);

            List<Payment> allpayments = pservice.getCurrentPayments();
            assertTrue(allpayments.size() > 0);
        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testGetAllScheduledPayments() {
        try {
            Scenario base = getDefaultScenario();
            Date now = Calendar.getInstance().getTime();
            Label groceries = new Label("Groceries");
            Payee walmart = new Payee("Wal-Mart");
            Payment payment = new Payment(45.22f, walmart, now);
            payment.addScenario(base);
            payment.setOccurence("ONCE");
            pservice.addOrUpdatePayment(payment);
            System.out.println("Payment was added:" + payment.getId());
            assertTrue("Payment was added.", payment.getId() > 0);

            Payment payment2 = new Payment(190f, walmart, now);
            payment2.setOccurence("ONCE");
            payment2.addScenario(base);
            pservice.addOrUpdatePayment(payment2);
            System.out.println("Payment was added:" + payment2.getId());
            assertTrue("Payment was added.", payment2.getId() > 0);

            List<Payment> allpayments = pservice.getSchdeuledPayments();
            assertTrue(allpayments.size() > 0);
        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testAddAndUpdatePayment() {
        try {
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

            payment.setPayee(pservice.findOrCreateNewPayee("Panera"));
            payment.setAmount(8.85f);
            pservice.addOrUpdatePayment(payment);

            Payment paymentUpdated = pservice.getPaymentByID(payment.getId());
            assertEquals(payment.getAmount(), paymentUpdated.getAmount());
            assertEquals(payment.getPayee(), paymentUpdated.getPayee());

            //test overrides
            assertTrue(paymentUpdated.getPaymentOverrides().size() == 1);

            payment.removePaymentOverride(override);
            pservice.addOrUpdatePayment(payment);
            paymentUpdated = pservice.getPaymentByID(payment.getId());
            assertTrue(paymentUpdated.getPaymentOverrides().size() == 0);


        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testRemovePayment() {
        try {
            Date now = Calendar.getInstance().getTime();
            Label groceries = pservice.findOrCreateNewLabel("Dining");
            Payee walmart = pservice.findOrCreateNewPayee("Chipotle");
            Payment payment = new Payment(6.75f, walmart, now);
            payment.setOccurence("ONCE");
            payment.addLabel(groceries);
            pservice.addOrUpdatePayment(payment);
            System.out.println("Payment was added:" + payment.getId());
            assertTrue("Payment was added.", payment.getId() > 0);

            pservice.removePayment(payment);

            payment = pservice.getPaymentByID(payment.getId());
            assertNull(payment);

            groceries = pservice.getLabelByID(groceries.getId());
            assertNotNull(groceries);

            walmart = pservice.getPayeeByID(walmart.getId());
            assertNotNull(walmart);

        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     @Test
    public void testGetPaymentsWithCriteria() {
        try {
            Date now = Calendar.getInstance().getTime();
            Scenario scenario = new Scenario("Testing");
            pservice.addOrUpdateScenario(scenario);
            
            Label groceries = pservice.findOrCreateNewLabel("Dining");
            Payee walmart = pservice.findOrCreateNewPayee("Chipotle");
            Payment payment = new Payment(6.75f, walmart, now);
            payment.addScenario(scenario);
            payment.setOccurence("NONE");
            payment.addLabel(groceries);
            pservice.addOrUpdatePayment(payment);
            System.out.println("Payment was added:" + payment.getId());
            assertTrue("Payment was added.", payment.getId() > 0);

            scenario = pservice.getScenarioByName(scenario.getName());
            assertNotNull(scenario);
            PaymentSearchCriteria criteria =
                    new PaymentSearchCriteria();
            criteria.setScenario(scenario);
            
            List payments = pservice.getPayments(criteria);
            System.out.println(payments.size());
            assertTrue("Got payments by criteria",payments.size() == 1);

        } catch (Exception ex) {
            Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Scenario getDefaultScenario() {
        Scenario s = new Scenario("Current");
        try {
            pservice.addOrUpdateScenario(s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
    }
}
