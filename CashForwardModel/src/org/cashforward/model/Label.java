package org.cashforward.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Labels are tags that are applied to <code>Payment</code>s. 
 * This provides a simplistic way to categorize each Payment.
 *
 * Labels are also used to add internal metadata to Payments.
 * For now, the only such use is to tag each Payment with a 
 * <code>Scenario</code> label.
 * 
 * @author Bill
 */
@Entity
@Table(name = "LABEL")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Type", discriminatorType = DiscriminatorType.STRING, length = 10)
@NamedQueries({
    @NamedQuery(name = "Label.findById", 
        query = "SELECT l FROM Label l WHERE l.id = :id"), 
    @NamedQuery(name = "Label.findByName", 
        query = "SELECT l FROM Label l WHERE l.name = :name"),
    @NamedQuery(name = "Label.findAll", 
        query = "SELECT l FROM Label l where l.internal = false")})
public class Label implements Comparable,Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private long id;
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "INTERNAL", nullable = false)
    protected boolean internal;

    public Label() {
    }

    public Label(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public void setInternal(boolean internal){
        //no overrriding hack
    }

    /**
     * Internal labels are used by CashForward to set metadata on objects.
     * Otherwise, labels are used to categorize Payments
     * @return true if the labels is used internally by CashForward
     */
    public boolean isInternal(){
        return internal;
    }

    /**
     * Return the name, or text, of the label
     * @return the label text
     */
    public String getName() {
        return name;
    }

    /**
     * Set the text value of the label
     * @param name new name for the label
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (object == null)
            return false;
        
        if (!(object.getClass().isAssignableFrom(getClass()))) {
            return false;
        }
        Label other = (Label) object;
        if (this.id != other.id) {
            return false;
        } else
            return this.name.equals(other.getName());
        
    }

    @Override
    public String toString() {
        return name;
    }

    public int compareTo(Object o) {
        if (o instanceof Label){
            return this.name.compareTo(((Label)o).getName());
        } else 
            return 1;
    }

}
