package org.cashforward;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * <code>PaymentOverride</code>s allow individual <code>Payment</code> 
 * occurences to be changed. Either the date or the amount can be modified.
 * 
 * @author Bill
 */
@Entity
@Table(name = "PAYMENT_OVERRIDE")
//@NamedQueries({@NamedQuery(name = "PaymentOverride.findById", query = "SELECT p FROM PaymentOverride p WHERE p.id = :id"), @NamedQuery(name = "PaymentOverride.findByPaymentId", query = "SELECT p FROM PaymentOverride p WHERE p.paymentOverridePK.paymentId = :paymentId"), @NamedQuery(name = "PaymentOverride.findByPaymentDate", query = "SELECT p FROM PaymentOverride p WHERE p.paymentDate = :paymentDate"), @NamedQuery(name = "PaymentOverride.findByAmount", query = "SELECT p FROM PaymentOverride p WHERE p.amount = :amount")})
public class PaymentOverride implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private long id;
    @ManyToOne
    private Payment payment;
    @Column(name = "PAYMENT_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date paymentDate;
    @Column(name = "AMOUNT", nullable = false)
    private float amount;
    @Column(name = "SEQUENCE_INDEX", nullable = false)
    private int sequenceIndex;

    public PaymentOverride() {
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public int getSequenceIndex() {
        return sequenceIndex;
    }

    public void setSequenceIndex(int sequenceIndex) {
        this.sequenceIndex = sequenceIndex;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
    
    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
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
        if (!(object instanceof PaymentOverride)) {
            return false;
        }
        PaymentOverride other = (PaymentOverride) object;
        if (this.id != other.id)
            return false;
        //if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
        //    return false;
        //}
        return true;
    }

    @Override
    public String toString() {
        return "cashfxpersistence.PaymentOverride[id=" + id + "]";
    }

}
