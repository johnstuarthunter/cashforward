/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.service;

import org.cashforward.model.Label;
import org.cashforward.model.PaymentSearchCriteria;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.cashforward.model.Payee;
import org.cashforward.model.Payment;
import org.cashforward.model.Scenario;
import org.cashforward.persistence.PersistenceService;
import org.cashforward.service.internal.PaymentCalculator;

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

    public void addOrUpdateScenario(Scenario newScenario) throws Exception  {
        persistenceService.addOrUpdateScenario(newScenario);
    }

    public boolean createScenario(Scenario currentScenario, 
            Scenario newScenario) throws Exception {
        //payment serivce then copies all payments from base to new
        /*
         * impl:
         * get all payments where scenario.id = baseScenario.id
         * then just ADD the newScenario label to that!!!
         * 
         */
        
         PaymentSearchCriteria baseLookup = 
                 new PaymentSearchCriteria();
         baseLookup.getLabels().add(currentScenario);
         
         List<Payment> basePayments = //needs fixing, labels, occurences
                 persistenceService.getPayments(baseLookup);
          
         return persistenceService.applyLabel(newScenario, basePayments);
         
        /*
         * then, when 
         * adding, just add scenarion (from context) to payment
         * updating, just keep scenarios the same
         * deleting - trickiest - 
         *      delete payment only if scenario is only scenario
         *      otherwise, just delete the scenario label
         * 
         */
    }

    public List<Scenario> getScenarios() throws Exception{
        return persistenceService.getScenarios();
    }
    
    public List<Payment> getScheduledPayments() 
        throws Exception {
            return persistenceService.getSchdeuledPayments();
    }
    
    public List<Payment> getCurrentPayments() 
        throws Exception {
            return persistenceService.getCurrentPayments();
    }
    
    public List<Payment> getPayments(PaymentSearchCriteria criteria) 
        throws Exception {
        
        Date start = criteria.getDateStart();
        Date end = criteria.getDateEnd();
        
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
        
        return allPayments;
    }
    
    public boolean addOrUpdatePayment(Payment newPayment) throws Exception {
        return persistenceService.addOrUpdatePayment(newPayment);
    }
    
    public boolean removePayment(Payment oldPayment) throws Exception {
        return persistenceService.removePayment(oldPayment);
    }
    
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
