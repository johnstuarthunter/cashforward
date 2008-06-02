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
 *
 * The <code>Payee</code> is the entity that recieves the
 * <code>Payment</code>. 
 * 
 * @author Bill
 */
@Entity
@Table(name = "PAYEE")
@NamedQueries({@NamedQuery(name = "Payee.findById", query = "SELECT p FROM Payee p WHERE p.id = :id"), @NamedQuery(name = "Payee.findByName", query = "SELECT p FROM Payee p WHERE p.name = :name")})
public class Payee implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private long id;
    @Column(name = "NAME", nullable = false)
    private String name;

    public Payee() {
    }

    public Payee(String name) {
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
        if (!(object instanceof Payee)) {
            return false;
        }
        Payee other = (Payee) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.cashforward.Payee[id=" + id + 
                ";name:"+name+"]";
    }

}
