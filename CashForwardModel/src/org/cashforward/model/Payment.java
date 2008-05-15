package org.cashforward.model;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
//@NamedQueries({@NamedQuery(name = "Payment.findById", query = "SELECT p FROM Payment p WHERE p.id = :id"), @NamedQuery(name = "Payment.findByPayeeId", query = "SELECT p FROM Payment p WHERE p.payeeId = :payeeId"), @NamedQuery(name = "Payment.findByAmount", query = "SELECT p FROM Payment p WHERE p.amount = :amount"), @NamedQuery(name = "Payment.findByStartDate", query = "SELECT p FROM Payment p WHERE p.startDate = :startDate"), @NamedQuery(name = "Payment.findByEndDate", query = "SELECT p FROM Payment p WHERE p.endDate = :endDate"), @NamedQuery(name = "Payment.findByOccurence", query = "SELECT p FROM Payment p WHERE p.occurence = :occurence")})
public class Payment implements Serializable {
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
    @Column(name = "END_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Column(name = "OCCURENCE", nullable = false)
    private String occurence;
    @OneToMany(mappedBy="payment", cascade=CascadeType.ALL)
    private List<PaymentOverride> overrides = new ArrayList();
    @ManyToMany(
        targetEntity=Label.class,
        cascade={CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
        name="PAYMENT_LABEL",
        joinColumns={@JoinColumn(name="LABEL_ID")},
        inverseJoinColumns={@JoinColumn(name="PAYMENT_ID")}
    )
    private List<Label> labels = new ArrayList();

    public Payment() {
    }
    
    public Payment(float amount, Payee payee, Date date){
        this.amount = amount;
        this.payee = payee;
        this.startDate = date;
        this.endDate = date;
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


    @Override
    public int hashCode() {
        int hash = 0;
        //hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Payment)) {
            return false;
        }
        Payment other = (Payment) object;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cashfxpersistence.Payment[id=" + id + "]";
    }

    public void addLabel(Label label) {
        this.labels.add(label);
    }
    
    public void addPaymentOverride(PaymentOverride override){
        override.setPayment(this);
        this.overrides.add(override);
    }
    
    public void removePaymentOverride(PaymentOverride override){
        override.setPayment(null);
        this.overrides.remove(override);
    }

}
