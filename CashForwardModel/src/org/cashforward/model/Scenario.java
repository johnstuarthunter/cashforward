/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Bill
 */
@Entity
@DiscriminatorValue("Scenario")
@NamedQueries({
    @NamedQuery(name = "Scenario.findById", 
        query = "SELECT s FROM Scenario s WHERE s.id = :id"), 
    @NamedQuery(name = "Scenario.findByName", 
        query = "SELECT s FROM Scenario s WHERE s.name = :name"),
    @NamedQuery(name = "Scenario.findAll", 
        query = "SELECT s FROM Scenario s")})
public class Scenario extends Label{

    public Scenario(){
        super();
        this.internal = true;
    }
    
    public Scenario(String name) {
        super(name);
        this.internal = true;
    }
    
    @Override
    public boolean isInternal(){
       return true;
    }

    
}
