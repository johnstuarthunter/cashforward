package org.cashforward.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.cashforward.model.Payment.Occurence;

/**
 * The <code>Payment</code> represents a single or recurring transaction. 
 * A <code>Payment</code> is used to represent either and income or expense.
 * The <code>Payee</code> is the recepient of the transaction amount. 
 * The occurence determines the frequence at which the transaction occurs. 
 * 
 * @author Bill
 */
@Entity
@Table(name = "PAYMENT")
@NamedQueries({
    @NamedQuery(name = "Payment.findAll",
    query = "SELECT p FROM Payment p where p.occurence = 'NONE' order by p.startDate asc"),
    @NamedQuery(name = "Payment.findAllScheduled",
    query = "SELECT p FROM Payment p where p.occurence != 'NONE' order by p.startDate asc"),
    @NamedQuery(name = "Payment.findById",
    query = "SELECT p FROM Payment p WHERE p.id = :id"),
    @NamedQuery(name = "Payment.findByPayeeId",
    query = "SELECT p FROM Payment p WHERE p.payee.id = :payeeId"),
    @NamedQuery(name = "Payment.findByAmount",
    query = "SELECT p FROM Payment p WHERE p.amount = :amount"),
    @NamedQuery(name = "Payment.findByStartDate",
    query = "SELECT p FROM Payment p WHERE p.startDate = :startDate"),
    @NamedQuery(name = "Payment.findByEndDate",
    query = "SELECT p FROM Payment p WHERE p.endDate = :endDate"),
    @NamedQuery(name = "Payment.findByOccurence",
    query = "SELECT p FROM Payment p WHERE p.occurence = :occurence")
})
public class Payment implements Serializable {

    public enum Occurence {

        NONE("", -1, -1),
        ONCE("Once", 0, Calendar.DAY_OF_YEAR),
        DAILY("Daily", 1, Calendar.DAY_OF_YEAR),
        WEEKLY("Weekly", 7, Calendar.DAY_OF_YEAR),
        BIWEEKLY("Every two weeks", 14, Calendar.DAY_OF_MONTH),
        MONTHLY("Monthly", 1, Calendar.MONTH),
        BIMONTHLY("Twice a month", 15, Calendar.DAY_OF_MONTH),
        ANNUALLY("Yearly", 12, Calendar.MONTH),
        BIANNUALLY("Twice a year", 6, Calendar.MONTH),
        TRIANNUALLY("Three times a year", 3, Calendar.MONTH);
        private final int period;
        private final int unit;
        private String label;

        Occurence(String label, int unit, int period) {
            this.label = label;
            this.period = period;
            this.unit = unit;
        }

        public String getLabel() {
            return label;
        }

        public int unit() {
            return unit;
        }

        public int period() {
            return period;
        }
    }
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private long id;
    @ManyToOne
    private Payee payee;
    @Column(name = "AMOUNT", nullable = false)
    private float amount;
    @Column(name = "START_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Column(name = "END_DATE", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Column(name = "DESCRIPTION", nullable = true)
    private String description;
    @Column(name = "OCCURENCE", nullable = false)
    private String occurence;
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<PaymentOverride> overrides = new ArrayList();
    
    @ManyToMany(targetEntity = Label.class,
    cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "PAYMENT_LABEL",
    joinColumns = {@JoinColumn(name = "LABEL_ID")},
    inverseJoinColumns = {@JoinColumn(name = "PAYMENT_ID")})
    private List<Label> labels = new ArrayList();
    
    private transient List<Scenario> scenarios = new ArrayList();

    public Payment() {
    }

    public Payment(float amount, Payee payee, Date date) {
        this.amount = amount;
        this.payee = payee;
        this.startDate = date;
        this.endDate = date;
        this.occurence = Occurence.NONE.name();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * Get the date of the first occurence
     * @return the Date of the first occurence
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Set the Date of the first occurence
     * @param startDate the Date of the first occurence
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Set the Date of the last occurence
     * @return the Date of the last occurence
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Set the Date of the last occurence
     * @param endDate the Date of the last occurence
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Get the type of occurence
     * @return the occurence of the payment
     */
    public String getOccurence() {
        return occurence;
    }

    /**
     * Set type of occurence
     * @param occurence the new occurence
     */
    public void setOccurence(String occurence) {
        this.occurence = occurence;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public List<Label> getLabels() {
        return labels;
    }
    
    public List<Scenario> getScenarios() {
        scenarios.clear();
        for (Label label : labels) {
            if (label instanceof Scenario)
                scenarios.add((Scenario) label);
        }
        return scenarios;
    }

    /**
     * Override specific occurences of the <code>Payment</code>
     * @param overrides the <code>PaymentOverride</code>s to apply
     */
    public void setPaymentOverrides(List<PaymentOverride> overrides) {
        this.overrides = overrides;
    }

    /**
     * Get the list of overriden payment occurences.
     * @return the list of payment overrides 
     */
    public List<PaymentOverride> getPaymentOverrides() {
        return overrides;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isScheduled() {
        return Occurence.valueOf(occurence) != Occurence.NONE;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        //hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    //TODO complete this 
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Payment)) {
            return false;
        }
        Payment other = (Payment) object;
        if (id != other.id) {
            return false;
        } else if (other.getAmount() != getAmount()) {
            return false;
        } else if (other.getStartDate() != null &&
                !other.getStartDate().equals(getStartDate())) {
            return false;
        } else if (other.getEndDate() != null &&
                !other.getEndDate().equals(getEndDate())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.cashforward.Payment[id=" + id + ";" +
                ";payee=" + payee + ";amount:" + amount +
                ";occurence:" + occurence +
                ";start:" + startDate +
                ";end:" + endDate +
                "]";
    }

    public void addLabel(Label label) {
        if (!labels.contains(label)) {
            this.labels.add(label);
        }
    }

    public void removeLabel(Label label) {
        this.labels.remove(label);
    }
    
    public void addScenario(Scenario scenario) {
        addLabel(scenario);
    }

    public void removeScenario(Scenario scenario) {
        removeLabel(scenario);
    }

    public void addPaymentOverride(PaymentOverride override) {
        if (!overrides.contains(override)) {
            override.setPayment(this);
            this.overrides.add(override);
        }
    }

    public void removePaymentOverride(PaymentOverride override) {
        if (overrides.contains(override)){
            this.overrides.remove(override);
            override.setPayment(null);
        }
    }
    
    public boolean inMulipleScenarios(){
        return scenarios != null ? scenarios.size() > 1 : false;
    }
    
}
