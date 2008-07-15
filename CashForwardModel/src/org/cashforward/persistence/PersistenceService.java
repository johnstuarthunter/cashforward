/*
 * PersistenceManager.java
 * 
 * Created on Oct 23, 2007, 11:07:01 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.persistence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.cashforward.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.cashforward.model.Label;
import org.cashforward.model.Payee;
import org.cashforward.model.Payment;
import org.cashforward.model.PaymentOverride;
import org.cashforward.model.PaymentSearchCriteria;
import org.cashforward.model.Scenario;

/**
 *
 * @author TWXS025
 */
public class PersistenceService {

    public static String STORAGE_MAIN = "CashForwardPersistence";
    public static String STORAGE_DEV = "CashForwardPersistenceDev";
    public static String STORAGE_TEST = "CashForwardPersistenceTest";
    EntityManagerFactory factory;
    EntityManager manager;
    private static PersistenceService instance;
    private final static String DOES_PAYEE_EXIST =
            "select a from Payee a where name = ?1";
    private final static String DOES_PAYMENT_EXIST =
            "select s from Payment s where amount = ?1";

    /**
     * Creates a new instance of PersistenceService
     */
    private PersistenceService(String unit) {
        startup(unit);
    }

    public synchronized static PersistenceService getInstance(String unit) {
        if (instance == null) {
            instance = new PersistenceService(unit);
        }

        return instance;
    }

    public void startup(String unit) {
        if (factory != null) {
            return;
        }

        factory = Persistence.createEntityManagerFactory(unit);
        manager = factory.createEntityManager();
    //manager.setFlushMode(FlushModeType.COMMIT);
    }

    public void shutdown() {
        manager.close();
        factory.close();
    }

    public boolean addOrUpdateLabel(Label label) {
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            if (label.getId() < 1) {
                manager.persist(label);
            } else {
                manager.merge(label);
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean addOrUpdatePayment(Payment payment) throws Exception {
        EntityTransaction tx = manager.getTransaction();
        try {
            if (payment.getPayee().getId() < 1) {
                addOrUpdatePayee(payment.getPayee());
            }
            tx.begin();
            if (payment.getId() < 1) {
                manager.persist(payment);
            } else {
                manager.merge(payment);
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean removePayment(Payment payment) throws Exception {
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        try {
            manager.remove(payment);
            tx.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Payment> getCurrentPayments(Scenario scenario) throws Exception {
        List<Payment> payments = new ArrayList();
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        try {
            Query query = manager.createNamedQuery("Payment.findAll");
            query.setParameter("scenario", scenario);
            payments = query.getResultList();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return payments;
        }

        return payments;
    }

    public List<Payment> getSchdeuledPayments(Scenario scenario) {
        List<Payment> payments = new ArrayList();
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        try {
            Query query = manager.createNamedQuery("Payment.findAllScheduled");
            query.setParameter("scenario", scenario);
            payments = query.getResultList();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return payments;
        }

        return payments;
    }

    public List<Payment> getPayments(PaymentSearchCriteria criteria)
            throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Payment> payments = new ArrayList();
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        try {
            StringBuffer queryString = new StringBuffer();
            queryString.append("SELECT p FROM Payment p WHERE ");

            if (criteria != null) {
                if (criteria.getDateStart() != null) {
                    queryString.append("p.startDate >= '" + sdf.format(criteria.getDateStart()) + "'");
                }
                if (criteria.getDateEnd() != null) {
                    if (criteria.getDateStart() != null) {
                        queryString.append(" and ");
                    }
                    queryString.append(" p.endDate <= '" + sdf.format(criteria.getDateEnd()) + "'");
                }

                if (criteria.getDateStart() != null ||
                        criteria.getDateEnd() != null) {
                    queryString.append(" and ");
                }
                queryString.append(" p.occurence = '" + Payment.Occurence.NONE.name() + "'");
                //filter out labels
                //if (criteria.getLabels().size() > 0)
                //    queryString.append(" and :labels MEMBER OF p.labels");
                
                if (criteria.getScenario() != null)
                    queryString.append(" and :scenario MEMBER OF p.labels");
                //queryString.append(" and p.occurence = '" + Payment.Occurence.NONE.name() + "'");
            }

            Query query = manager.createQuery(queryString.toString());
            if (criteria.getScenario() != null)
                query.setParameter("scenario", criteria.getScenario());
            payments = query.getResultList();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
            return payments;
        }

        return payments;
    }

    public Payee getPayeeByID(long id) throws Exception {
        EntityTransaction tx = manager.getTransaction();
        Payee a = null;
        tx.begin();
        try {
            a = manager.find(Payee.class, id);
            tx.commit();
            return a;
        } catch (Exception e) {
            e.printStackTrace();
            return a;
        }
    }

    public Payee findOrCreateNewPayee(String payeeName) throws Exception {
        EntityTransaction tx = manager.getTransaction();
        Payee a = null;
        tx.begin();
        try {
            Query query = manager.createQuery("SELECT p FROM Payee p WHERE p.name = :name");
            query.setParameter("name", payeeName);
            List results = query.getResultList();
            if (results != null && results.size() > 0) {
                a = (Payee) results.get(0);
            } else {
                a = new Payee(payeeName);
                manager.persist(a);
            }
            tx.commit();
            return a;
        } catch (Exception e) {
            e.printStackTrace();
            return a;
        }
    }

    public Label getLabelByID(long id) throws Exception {
        EntityTransaction tx = manager.getTransaction();
        Label a = null;
        tx.begin();
        try {
            a = manager.find(Label.class, id);
            tx.commit();
            return a;
        } catch (Exception e) {
            e.printStackTrace();
            return a;
        }
    }
    
    public Scenario getScenarioByID(long id) throws Exception {
        EntityTransaction tx = manager.getTransaction();
        Scenario a = null;
        tx.begin();
        try {
            a = manager.find(Scenario.class, id);
            tx.commit();
            return a;
        } catch (Exception e) {
            e.printStackTrace();
            return a;
        }
    }
    
    public Scenario getScenarioByName(String name) throws Exception {
        EntityTransaction tx = manager.getTransaction();
        Scenario a = null;
        tx.begin();
        try {
            Query query = manager.createQuery("SELECT p FROM Scenario p WHERE p.name = :name");
            query.setParameter("name", name);
            List results = query.getResultList();
            
            if (results != null && results.size() > 0) 
                a = (Scenario) results.get(0);
             
            tx.commit();
            return a;
        } catch (Exception e) {
            e.printStackTrace();
            return a;
        }
    }

    public Label findOrCreateNewLabel(String labelName) throws Exception {
        EntityTransaction tx = manager.getTransaction();
        Label a = null;
        tx.begin();
        try {
            Query query = manager.createNamedQuery("Label.findByName");
            query.setParameter("name", labelName);
            List results = query.getResultList();
            if (results != null && results.size() > 0) {
                a = (Label) results.get(0);
            } else {
                a = new Label(labelName);
                manager.persist(a);
            }
            tx.commit();
            return a;
        } catch (Exception e) {
            e.printStackTrace();
            return a;
        }
    }

    public List<Label> getLabels() {
        List<Label> labels = new ArrayList();
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        try {
            Query query = manager.createNamedQuery("Label.findAll");
            labels = query.getResultList();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return labels;
        }

        return labels;
    }

    public Payment getPaymentByID(long id) throws Exception {
        EntityTransaction tx = manager.getTransaction();
        Payment a = null;
        tx.begin();
        try {
            a = manager.find(Payment.class, id);
            tx.commit();
            return a;
        } catch (Exception e) {
            e.printStackTrace();
            return a;
        }
    }

    public boolean addOrUpdatePayee(Payee payee) throws Exception {
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            if (payee.getId() < 1) {
                manager.persist(payee);
            } else {
                manager.merge(payee);
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public List<Payee> getPayees() {
        List<Payee> payees = new ArrayList();
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        try {
            Query query = manager.createNamedQuery("Payee.findAll");
            payees = query.getResultList();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return payees;
        }

        return payees;
    }
    //bulk operation
    public boolean applyLabel(Label newLabel, List<Payment> payments) {
       if (!addOrUpdateLabel(newLabel)){
                return false;
        } 
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        try {
            for (Payment payment : payments) {
                payment.addLabel(newLabel);
                if (payment.getId() < 1) {
                    manager.persist(payment);
                } else {
                    manager.merge(payment);
                }
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public List<Scenario> getScenarios() {
        List<Scenario> scenarios = new ArrayList();
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        try {
            Query query = manager.createNamedQuery("Scenario.findAll");
            scenarios = query.getResultList();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return scenarios;
        }

        return scenarios;
    }

    public boolean addOrUpdateScenario(Scenario scenario) throws Exception {
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            if (scenario.getId() < 1) {
                manager.persist(scenario);
            } else {
                manager.merge(scenario);
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        PersistenceService service =
                PersistenceService.getInstance(PersistenceService.STORAGE_MAIN);
//        Payee walmart = new Payee();
//        walmart.setName("Wal-Mart");
//        boolean ok = service.addOrUpdatePayee(walmart);
//        System.out.println("added ok." + " id is " + walmart.getId());
//        walmart = service.getPayeeByID(1);
//        Payment firstPayment = new Payment();
//        firstPayment.setPayee(walmart);
//        firstPayment.setAmount(30.40);
        Date now = Calendar.getInstance().getTime();
//        firstPayment.setStartDate(now);
//        firstPayment.setEndDate(now);
//        firstPayment.setOccurence("ONCE");
//        ok = service.addOrUpdatePayment(firstPayment);
//        System.out.println("added first payment: " + firstPayment.getId());
        Payment firstPayment = service.getPaymentByID(1);
        System.out.println("found first payment: " + firstPayment.getAmount());
        List<Label> labels = firstPayment.getLabels();
        if (labels != null) {
            for (Label label : labels) {
                System.out.println(label.getName());
            }
        }
        List<PaymentOverride> overrides = firstPayment.getPaymentOverrides();
        if (labels != null) {
            for (PaymentOverride override : overrides) {
                System.out.println(override.getAmount());
            }
        }

//    PaymentOverride override = new PaymentOverride();
//    override.setAmount(23.00);
//    override.setPaymentDate(now);
//    firstPayment.addPaymentOverride(override);
//          Label groceries = new Label();
//        groceries.setName("Groceries");
//        firstPayment.addLabel(groceries);
//        service.addOrUpdatePayment(firstPayment);
        service.shutdown();
    }
}
