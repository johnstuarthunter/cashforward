package org.cashforward.ui.internal.search;

import ca.odell.glazedlists.TextFilterator;
import java.util.List;
import org.cashforward.model.Payment;

/**
 * Filterator for <code>Payment</code>s. Likely to be used in
 * conjunction with the SearchProvider SPI.
 *
 * @author Bill
 */
public class PaymentFilterator implements TextFilterator {
    public void getFilterStrings(List baseList, Object source) {
        Payment payment = (Payment)source;
        baseList.add(payment.getAmount());
        baseList.add(payment.getDescription());
        baseList.add(payment.getPayee());
        baseList.add(payment.getEndDate());
        baseList.add(payment.getStartDate());
    }

}
