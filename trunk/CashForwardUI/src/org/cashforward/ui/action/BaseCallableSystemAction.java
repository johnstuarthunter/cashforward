/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.ui.action;

import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 *
 * @author Bill
 */
public abstract class BaseCallableSystemAction extends CallableSystemAction{

    protected String getMessage(String messageKey){
        return NbBundle.getMessage(getClass(),messageKey);
    }
    
    protected String getMessage(Class clazz,String messageKey){
        return NbBundle.getMessage(clazz,messageKey);
    }
}
