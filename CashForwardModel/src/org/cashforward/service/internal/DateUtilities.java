/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cashforward.service.internal;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author wsnyder
 */
public class DateUtilities {

    public static int daysBetween(Date start, Date end){
        int result = 0;
        
        Calendar s = Calendar.getInstance();
        s.setTime(start);
        Calendar e = Calendar.getInstance();
        e.setTime(end);
        
        //need to account for different years of calendars...later for now
        result = e.get(Calendar.DAY_OF_YEAR) - 
            s.get(Calendar.DAY_OF_YEAR);
        
        return result;
        
    }
    
    public static Date getDateAfterDays(Date date, int daysAfter) {
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        start.add(Calendar.DAY_OF_YEAR, daysAfter);
        
        return start.getTime();
    }
}
