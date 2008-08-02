package org.cashforward.ui.internal.filter;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.event.ListEvent;
import org.cashforward.model.Label;
import org.cashforward.ui.task.PaymentFilter;

/**
 * Allows us to keep track of all the labels as they are added to the
 * application so we can filter with them
 * 
 * @author Bill
 */
public class LabelsToPaymentFilterList extends TransformedList {

    public LabelsToPaymentFilterList(EventList source) {
        super(source);
        source.addListEventListener(this);
    }

    /**
     * Gets the user at the specified index.
     */
    public Object get(int index) {
        Label label = (Label) source.get(index);
        return new PaymentFilter(label.getName());
    }

    /**
     * When the source issues list changes, propogate the exact same changes
     * for the users list.
     */
    public void listChanged(ListEvent listChanges) {
        updates.forwardEvent(listChanges);
    }
}
