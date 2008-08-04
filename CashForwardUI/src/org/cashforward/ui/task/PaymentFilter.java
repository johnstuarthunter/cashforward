/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.ui.task;

import java.util.Date;
import java.util.List;
import org.cashforward.model.PaymentSearchCriteria;
import org.cashforward.model.Scenario;

/**
 * Utility class for filter the various lists of payments in the UI
 * 
 * @author Bill
 */

public class PaymentFilter {
    public static int TYPE_ANY = 1000;
    public static int TYPE_CURRENT = 100;
    public static int TYPE_SCHEDULED = 200;
    public static int TYPE_CALCULATED = 300;
    
    private String name;
    
    private PaymentSearchCriteria criteria;
    private int paymentType = TYPE_ANY;
    private List<Scenario> scenarios;

    public PaymentFilter(){
        criteria = new PaymentSearchCriteria();
    }
    
    public PaymentFilter(String name){
        this.name = name;
        criteria = new PaymentSearchCriteria();
    }
    
    public PaymentFilter(PaymentSearchCriteria newCriteria){
        this.criteria = newCriteria;
    }
    
    public PaymentFilter(String name, PaymentSearchCriteria newCriteria){
        this.name = name;
        this.criteria = newCriteria;
    }

    public PaymentFilter(List<Scenario> scenarios) {
        this.scenarios = scenarios;
        this.name = scenarios.get(0).getName();
        this.criteria = new PaymentSearchCriteria();
        criteria.setScenario(scenarios.get(0));
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setScenarios(List<Scenario> scenarios){
        this.scenarios = scenarios;
        if (criteria != null && !scenarios.isEmpty())
            criteria.setScenario(scenarios.get(0));
    }
    
    public List<Scenario> getScenarios(){
        return this.scenarios;
    }
    
    public void setDateStart(Date startDate) {
        criteria.setDateStart(startDate);
    }

    public void setDateEnd(Date endDate) {
        criteria.setDateEnd(endDate);
    }
    
    public int getPaymentType(){
        return this.paymentType;
    }
    
    public void setPaymentType(int newType){
        this.paymentType = newType;
    }

    public Date getDateStart() {
        if (criteria != null)
            return criteria.getDateStart();
        else
            return null;
    }
    
    public Date getDateEnd() {
        if (criteria != null)
            return criteria.getDateEnd();
        else
            return null;
    }
    
    public PaymentSearchCriteria getPaymentSearchCriteria(){
        return this.criteria;
    }
    
    public void setPaymentSearchCriteria(PaymentSearchCriteria criteria){
        this.criteria = criteria;
    }
    
    public String toString(){
        return this.name;
    }

}
