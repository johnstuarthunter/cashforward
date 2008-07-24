/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.cashforward.model.Label;
import org.cashforward.model.Payee;
import org.cashforward.model.Payment;
import org.cashforward.model.Scenario;
import org.cashforward.ui.task.PaymentFilter;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * The UIContext is kind of a central desktop application bus.
 * From here the Payment (and other object) selections are dispatched to other
 * parts of the application.
 *
 * In addition to use the Lookup NetBeans APIs, the UIContext also keeps
 * instances of GlazedLists <code>EventList</code>s around for usage by
 * the list and table based interface components.
 *
 * Both the Lookup and EventList mechanisms are useful for broadcasting
 * model changes to the interface.
 *
 * @author Bill 
 */
public class UIContext extends AbstractLookup {

    private InstanceContent content = null;
    private static UIContext ctx = new UIContext();
    private EventList<Payment> filteredPayments = new BasicEventList();
    private EventList<Payment> scheduledPayments = new BasicEventList();
    private EventList<Payment> currentPayments = new BasicEventList();
    private EventList<Payee> payees = new BasicEventList();
    private EventList<Label> labels = new BasicEventList();
    private EventList<Scenario> scenarios = new BasicEventList();

    private UIContext() {
        this(new InstanceContent());
    }

    private UIContext(InstanceContent content) {
        super(content);
        this.content = content;
    }

    /**
     * Add a <code>List</code> of <code>Label</code>s to the application.
     *
     * @param labels the new <code>Label</code>s to add.
     */
    public synchronized void addLabels(List<Label> labels) {
        this.labels.getReadWriteLock().writeLock().lock();
        for (Label label : labels) {
            if (label.isInternal()) {
                continue;
            }
            this.labels.add(label);
        }
        //this.labels.addAll(labels);
        this.labels.getReadWriteLock().writeLock().unlock();
    }

    /**
     * Get the current <code>EventList</code> of <code>Label</code>s.
     *
     * @return
     */
    public synchronized EventList getLabels() {
        return this.labels;
    }

    /**
     * Get <code>Payment</code> that is the focus of the application.
     *
     * @return the currently selected <code>Payment</code>
     */
    public Payment getPayment() {
        Collection all =
                lookupAll(Payment.class);
        if (all != null) {
            Iterator ia = all.iterator();
            if (ia.hasNext()) {
                return (Payment) ia.next();
            }
        }
        return null;
    }

    /**
     * Set the <code>Payment</code> being used by the appplication
     *
     * @param payment the new <code>Payment</code> to use.
     */
    public synchronized void setPayment(Payment payment) {
        Collection all =
                lookupAll(Payment.class);
        if (all != null) {
            Iterator ia = all.iterator();
            while (ia.hasNext()) {
                remove(ia.next());
            }
        }
        add(payment);
    }

    /**
     * Add a new Scenario to the application
     *
     * @param newScenario the new Scenario to add
     */
    public void addScenario(Scenario newScenario) {
        this.scenarios.add(newScenario);
    }

    /**
     * Add a <code>List</code> of <code>Scenario</code>s to the application
     *
     * @param scenarios the new <code>Scenario</code>s to add.
     */
    public void addScenarios(List scenarios) {
        this.scenarios.addAll(scenarios);
    }

    /**
     * Get the available <code>Scenario</code>s available to the application
     *
     * @return the observable EventList of Scenarios
     */
    public EventList<Scenario> getScenarios() {
        return scenarios;
    }

    /**
     * Remove a scenario from the application.
     *
     * @param scenario
     */
    public void removeScenario(Scenario scenario) {
        scenarios.remove(scenario);
    }

    /**
     * Set the active Scenario
     *
     * @param scenario
     */
    /*public synchronized void setScenario(Scenario scenario) {
        Collection all =
                lookupAll(Scenario.class);
        if (all != null) {
            Iterator ia = all.iterator();
            while (ia.hasNext()) {
                remove(ia.next());
            }
        }
        add(scenario);
    }*/

    /**
     * Set the active selected <code>Scenario</code>s
     *
     * @param scenarios new Scenarios to set
     */
    public synchronized void setSelectedScenarios(List<Scenario> scenarios) {
        Collection all =
                lookupAll(Scenario.class);
        if (all != null) {
            Iterator ia = all.iterator();
            while (ia.hasNext()) {
                remove(ia.next());
            }
        }
        for (Scenario scenario : scenarios) {
            add(scenario);
        }
    }

    /**
     * Get the active <code>Scenario</code>s
     *
     * @return the active Scenarios
     */
     public synchronized List<Scenario> getSelectedScenarios() {
        Collection all =
                lookupAll(Scenario.class);
        return new ArrayList(all);
    }

    /*
    public Scenario getScenario() {
        Collection all =
                lookupAll(Scenario.class);
        if (all != null) {
            Iterator ia = all.iterator();
            if (ia.hasNext()) {
                return (Scenario) ia.next();
            }
        }
        return null;
    }
    */

    /**
     * Clear the active Scenario from the context
     *
     */
    public void clearScenario() {
        Collection all =
                lookupAll(Scenario.class);
        if (all != null) {
            Iterator ia = all.iterator();
            if (ia.hasNext()) {
                remove(ia.next());
            }
        }
    }

    /**
     * Clear the active Payment from the context
     *
     */
    public void clearPayment() {
        Collection all =
                lookupAll(Payment.class);
        if (all != null) {
            Iterator ia = all.iterator();
            while (ia.hasNext()) {
                remove(ia.next());
            }
        }
    }

    /**
     * Set the active <code>PaymentFilter</code>.
     *
     * @param filter new PaymentFilter
     */
    public synchronized void setPaymentFilter(PaymentFilter filter) {
        Collection all =
                lookupAll(PaymentFilter.class);
        if (all != null) {
            Iterator ia = all.iterator();
            while (ia.hasNext()) {
                remove(ia.next());
            }
        }
        add(filter);
    }

    /**
     *
     * Get the active PaymentFilter
     *
     * @return current PaymentFilter
     */
    public synchronized PaymentFilter getPaymentFilter() {
        Collection all =
                lookupAll(PaymentFilter.class);
        if (all != null) {
            Iterator ia = all.iterator();
            if (ia.hasNext()) {
                return (PaymentFilter) ia.next();
            }
        }

        return new PaymentFilter();
    }

    //---proto
    public synchronized void addPayment(Payment payment) {
        filteredPayments.getReadWriteLock().writeLock().lock();
        filteredPayments.add(payment);
        filteredPayments.getReadWriteLock().writeLock().unlock();
    }
    public synchronized void removePayment(Payment payment) {
        filteredPayments.getReadWriteLock().writeLock().lock();
        filteredPayments.remove(payment);
        filteredPayments.getReadWriteLock().writeLock().unlock();
    }
    public synchronized void addPayments(List payments) {
        filteredPayments.getReadWriteLock().writeLock().lock();
        filteredPayments.addAll(payments);
        filteredPayments.getReadWriteLock().writeLock().unlock();
    }
    public synchronized void removePayments(List payments) {
        filteredPayments.getReadWriteLock().writeLock().lock();
        filteredPayments.removeAll(payments);
        filteredPayments.getReadWriteLock().writeLock().unlock();
    }
    //---end

    /*
    public synchronized void addCurrentPayments(List payments) {
        currentPayments.getReadWriteLock().writeLock().lock();
        currentPayments.addAll(payments);
        currentPayments.getReadWriteLock().writeLock().unlock();
    }

    public synchronized void addCurrentPayment(Payment payment) {
        currentPayments.getReadWriteLock().writeLock().lock();
        currentPayments.add(payment);
        currentPayments.getReadWriteLock().writeLock().unlock();
    }

    public synchronized void removeCurrentPayments(List payments) {
        currentPayments.getReadWriteLock().writeLock().lock();
        currentPayments.removeAll(payments);
        currentPayments.getReadWriteLock().writeLock().unlock();
    }

    public synchronized void removeCurrentPayment(Payment payment) {
        currentPayments.getReadWriteLock().writeLock().lock();
        currentPayments.remove(payment);
        currentPayments.getReadWriteLock().writeLock().unlock();
    }

    public synchronized EventList getCurrentPayments() {
        return currentPayments;
    }

    public synchronized void addScheduledPayments(List payments) {
        scheduledPayments.getReadWriteLock().writeLock().lock();
        scheduledPayments.addAll(payments);
        scheduledPayments.getReadWriteLock().writeLock().unlock();
    }

    public synchronized void addScheduledPayment(Payment payment) {
        scheduledPayments.getReadWriteLock().writeLock().lock();
        scheduledPayments.add(payment);
        scheduledPayments.getReadWriteLock().writeLock().unlock();
    }

    public synchronized void removeScheduledPayments(List payments) {
        scheduledPayments.getReadWriteLock().writeLock().lock();
        scheduledPayments.removeAll(payments);
        scheduledPayments.getReadWriteLock().writeLock().unlock();
    }

    public synchronized void removeScheduledPayment(Payment payment) {
        scheduledPayments.getReadWriteLock().writeLock().lock();
        scheduledPayments.remove(payment);
        scheduledPayments.getReadWriteLock().writeLock().unlock();
    }

    public synchronized EventList getScheduledPayments() {
        return scheduledPayments;
    }
    */

    /**
     * Get all the Payments we are dealing with
     *
     * @return the entire list of Payments
     */
    public synchronized EventList getPayments() {
        return filteredPayments;
    }

    /**
     * Add a Payee to application
     *
     * @param payee new Payee
     */
    public synchronized void addPayee(Payee payee) {
        if (!payees.contains(payee)) {
            this.payees.add(payee);
        }
    }

    /**
     * Remove a Payee from the context
     *
     * @param payee to remove
     */
    public synchronized void removePayee(Payee payee) {
        if (payees.contains(payee)) {
            this.payees.remove(payee);
        }
    }

    /**
     * Add a List of Payees to the context
     *
     * @param payees
     */
    public synchronized void addPayees(List<Payee> payees) {
        this.payees.addAll(payees);
    }

    /**
     *
     * Get the List of available Payees
     *
     * @return
     */
    public synchronized EventList getPayees() {
        return payees;
    }

    public void add(Object instance) {
        content.add(instance);
    }

    public void remove(Object instance) {
        content.remove(instance);
    }

    /**
     * Get the current UIContext
     *
     * @return
     */
    public static synchronized UIContext getDefault() {
        return ctx;
    }
}
