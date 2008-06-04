/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Bill 
 */
public class PaymentSearchCriteria {
    Date dateStart;
    Date dateEnd;
    List labels;
    float amountLow;
    float amountHigh;

    public float getAmountHigh() {
        return amountHigh;
    }

    public void setAmountHigh(float amountHigh) {
        this.amountHigh = amountHigh;
    }

    public float getAmountLow() {
        return amountLow;
    }

    public void setAmountLow(float amountLow) {
        this.amountLow = amountLow;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public List getLabels() {
        return labels;
    }

    public void setLabels(List labels) {
        this.labels = labels;
    }
    
}
