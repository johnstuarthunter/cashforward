/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui;

import java.util.Collection;
import java.util.Iterator;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
/**
 * 
 * @author Bill 
 */
public class Context extends AbstractLookup {

    private InstanceContent content = null;
    private static Context ctx = new Context();

    public Context() {
        this(new InstanceContent());
    }

    public Context(InstanceContent content) {
        super(content);
        this.content = content;
    }

    public void add(Object instance) {
        content.add(instance);
    }

    public void remove(Object instance) {
        content.remove(instance);
    }

    public static Context getDefault() {
        return ctx;
    }

}
