package org.cashforward.service.internal;

import java.util.Comparator;
import org.cashforward.model.Payment;

/**
 * Helper for sorting Payments by ascending start date
 *
 * @author Bill
 */
public class PaymentComparator implements Comparator{

    public int compare(Object o1, Object o2) {
        Payment p1 = (Payment)o1;
        Payment p2 = (Payment)o2;

        return p1.getStartDate().compareTo(p2.getStartDate());
    }

}
