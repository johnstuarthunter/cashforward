/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.service;

import org.cashforward.model.Label;
import org.cashforward.model.PaymentSearchCriteria;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.cashforward.model.Payee;
import org.cashforward.model.Payment;
import org.cashforward.model.Scenario;
import org.cashforward.persistence.PersistenceService;
import org.cashforward.service.internal.PaymentCalculator;
import org.cashforward.service.internal.PaymentComparator;

/**
 *
 * @author Bill 
 * 
 * NOTE:
 * The functionality exposed by this class will evolve and the UI evolves.
 * 
 * Yet to see:
 * -filtering payments by amount, payee
 * -considering payment overrides when query payments
 * 
 */
public class PaymentService {
    
    PersistenceService persistenceService;
    PaymentCalculator paymentCalculator;
    
     public PaymentService(){
         //TODO read db from config
         persistenceService = 
                 PersistenceService.getInstance(PersistenceService.STORAGE_DEV);
         paymentCalculator = new PaymentCalculator();
     }
    
    protected PaymentService(PersistenceService persistenceService){
        if (persistenceService == null)
            throw new IllegalArgumentException("PersistenceService cannot be null.");
        this.persistenceService = persistenceService;
    }

    public Scenario getDefaultScenario() {
        Scenario s = new Scenario("Current");
        try {
            addOrUpdateScenario(s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
    }

    public void addOrUpdateScenario(Scenario newScenario) throws Exception  {
        persistenceService.addOrUpdateScenario(newScenario);
    }

    /**
     * Creates a new Scenario based off an existing one.
     * 
     * @param currentScenario Existing Scenario to use as base
     * @param newScenario The new Scenario to create
     * @return true of the new Scenario was created successfully
     * @throws java.lang.Exception if there was a problem creating the Scenario
     */
    public boolean createScenario(Scenario currentScenario, 
            Scenario newScenario) throws Exception {
         PaymentSearchCriteria baseLookup = 
                 new PaymentSearchCriteria();
         baseLookup.getLabels().add(currentScenario);
         
         List<Payment> basePayments = //needs fixing, labels, occurences
                 persistenceService.getPayments(baseLookup);
          
         return persistenceService.applyLabel(newScenario, basePayments);
         
    }

    public List<Scenario> getScenarios() throws Exception{
        return persistenceService.getScenarios();
    }

    /**
     * Get all the Payments that reoccur
     *
     * @return
     * @throws java.lang.Exception
     */
    public List<Payment> getScheduledPayments() 
        throws Exception {
            return persistenceService.getSchdeuledPayments();
    }

    /**
     * Get all the existing non-scheduled Payments
     *
     * @return
     * @throws java.lang.Exception
     */
    public List<Payment> getCurrentPayments() 
        throws Exception {
            return persistenceService.getCurrentPayments();
    }

    /**
     * Get Payments based on specific criteria
     * @param criteria The criteria to search by
     * @return the payments matching the given criteria
     * @throws java.lang.Exception
     */
    public List<Payment> getPayments(PaymentSearchCriteria criteria) 
        throws Exception {

        if (criteria == null)
            return persistenceService.getPayments(null);
        
        Date start = criteria.getDateStart();
        Date end = criteria.getDateEnd();
        
        Scenario scenario = criteria.getScenario();
        
        List<Payment> allPayments = new ArrayList();    
        List<Payment> payments = persistenceService.getSchdeuledPayments();
        System.out.println(payments.size() + " scheduled payments");
        for (Payment payment : payments) {
            List newPayments = 
                    paymentCalculator.calculatePayments(payment, start, end);
            System.out.println("generated " + newPayments.size());
            allPayments.addAll(newPayments);
        }
        
        //now add current for now
        allPayments.addAll(persistenceService.getPayments(criteria));

        Collections.sort(allPayments, new PaymentComparator());

        return allPayments;
    }
    
    public boolean addOrUpdatePayment(Payment newPayment) throws Exception {
        return persistenceService.addOrUpdatePayment(newPayment);
    }
    
    public boolean removePayment(Payment oldPayment) throws Exception {
        return persistenceService.removePayment(oldPayment);
    }

    /**
     * Enters the next scheduled Payment into the register. This payment is
     * created as a non-scheduled entry.
     *
     * @param scheduledPayment Scheduled Payment to enter
     * @return the created and persisted new Payment
     * @throws java.lang.Exception if there is a problem creating the Payment
     */
    public Payment enterNextPayment(Payment scheduledPayment) throws Exception {
        Payment newPayment = new Payment(scheduledPayment.getAmount(), 
                scheduledPayment.getPayee(), scheduledPayment.getStartDate());
        newPayment.setOccurence(Payment.Occurence.NONE.name());
        newPayment.setEndDate(newPayment.getStartDate());
        
        if (persistenceService.addOrUpdatePayment(newPayment)){
            scheduledPayment.setStartDate(
                    paymentCalculator.getNextPaymentDate(scheduledPayment, 
                    new Date()));
            persistenceService.addOrUpdatePayment(scheduledPayment);
                
        }
        
        return newPayment;
        
    }
    
    public boolean skipNextPayment(Payment scheduledPayment) throws Exception {
            scheduledPayment.setStartDate(
                    paymentCalculator.getNextPaymentDate(scheduledPayment, 
                    new Date()));
            return persistenceService.addOrUpdatePayment(scheduledPayment);
        
    }

    /**
     * Returns a List of project Payments for a scheduled Payment
     * based on the given date range.
     *
     * @param payment the Scheduled Payment to project
     * @param startDate
     * @param endDate
     * @return the List of calculated Payments
     * @throws java.lang.Exception if there is a problem calculating the Payments
     */
    public List<Payment> getCalculatedPayments(Payment payment, 
            Date startDate, Date endDate) throws Exception {
        return paymentCalculator.calculatePayments(payment, startDate, endDate);
    }
    
    public List<Payee> getPayees() throws Exception {
        return persistenceService.getPayees();
    }
    
    public List<Label> getLabels() throws Exception {
        return persistenceService.getLabels();
    }
    
}
