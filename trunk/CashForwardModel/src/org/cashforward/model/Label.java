package org.cashforward.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Labels are tags that are applied to <code>Payment</code>s. 
 * This provides a simplistic way to categorize each Payment.
 * 
 * @author Bill
 */
@Entity
@Table(name = "LABEL")
@NamedQueries({
    @NamedQuery(name = "Label.findById", 
        query = "SELECT l FROM Label l WHERE l.id = :id"), 
    @NamedQuery(name = "Label.findByName", 
        query = "SELECT l FROM Label l WHERE l.name = :name"),
    @NamedQuery(name = "Label.findAll", 
        query = "SELECT l FROM Label l")})
public class Label implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private long id;
    @Column(name = "NAME", nullable = false)
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(object instanceof Label)) {
            return false;
        }
        Label other = (Label) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cashfxpersistence.Label[id=" + id + "]";
    }

}
