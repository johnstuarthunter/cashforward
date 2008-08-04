package org.cashforward.ui.internal.filter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.cashforward.model.Label;
import org.cashforward.model.Payment;
import org.cashforward.ui.internal.UILogger;
import org.cashforward.ui.task.PaymentFilter;
import org.cashforward.ui.task.PaymentTaskPanel;
import org.cashforward.ui.task.TaskTopComponent;

/**
 * Creates Matchers used by GlazedLists dynamic filtering capabilities.
 *
 * @author Bill
 */
public class MatcherFactory {

    private static MatcherFactory instance;
    private JTextField temp = new JTextField();

    private MatcherFactory() {
    }

    public static synchronized MatcherFactory getInstance() {
        if (instance == null) {
            instance = new MatcherFactory();
        }
        return instance;
    }

    /**
     * Delegate for the CashForward QuickSearchProvider. Used by
     * the EventList pipeline to filter out Payments based on what is
     * typed in the filter box
     *
     * @return
     */
    public JTextField getQuickSearchProxy() {
        return temp;
    }

    /**
     * Creates a MatcherEditor for the existing Labels.
     *
     * @return the Labels matcher
     */
    public MatcherEditor createLabelMatcher() {
        TaskTopComponent ttC = TaskTopComponent.findInstance();
        PaymentTaskPanel ptP = ttC.getPaymentTaskPanel();
        return new LabelSelect(ptP.getLabelListComponent());
    }

    /**
     * Creates a MatcherEditor for the existing Scenarios
     *
     * @return the Scenario matcher
     */
    public MatcherEditor createScenarioMatcher() {
        TaskTopComponent ttC = TaskTopComponent.findInstance();
        PaymentTaskPanel ptP = ttC.getPaymentTaskPanel();
        return new LabelSelect(ptP.getScenarioListComponent());
    }

    /**
     * Creates a MatcherEditor for the Payment types
     *
     * @return the Payment type matcher
     */
    public MatcherEditor createTypeMatcher() {
        TaskTopComponent ttC = TaskTopComponent.findInstance();
        PaymentTaskPanel ptP = ttC.getPaymentTaskPanel();
        return new TypeSelect(ptP.getTypeListComponent());
    }

    /**
     * @return a joint Label and Scenario matcher
     */
    public MatcherEditor createLabelAndScenarioMatcher() {
        EventList matchers = new BasicEventList();
        matchers.add(createLabelMatcher());
        matchers.add(createScenarioMatcher());
        matchers.add(createTypeMatcher());
        matchers.add(new TextComponentMatcherEditor(getQuickSearchProxy(), new PaymentFilterator()));

        CompositeMatcherEditor matcher =
                new CompositeMatcherEditor(matchers);
        matcher.setMode(CompositeMatcherEditor.AND);

        return matcher;
    }

    private class LabelSelect extends AbstractMatcherEditor implements ListSelectionListener {

        /** a list of labels */
        EventList labelsSelectedList = new BasicEventList();
        /** a widget for selecting labels */
        JList target;

        /**
         * Create a {@link IssuesForLabelsMatcherEditor} that matches Labels from the
         * specified {@link EventList} of {@link Issue}s.
         */
        public LabelSelect(JList target) {
            this.target = target;
            target.addListSelectionListener(this);
        }

        /**
         * When the JList selection changes, create a new Matcher and fire 
         * an event.
         */
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            Matcher newMatcher =
                    new PaymentsForLabelsMatcher(getSelectedLabels());
            fireChanged(newMatcher);
        }

        private EventList getSelectedLabels() {
            labelsSelectedList.clear();
            Object[] selected = target.getSelectedValues();
            PaymentFilter f;

            for (int i = 0; i < selected.length; i++) {
                if (Label.class.isAssignableFrom(selected[i].getClass())) {
                    labelsSelectedList.add(((Label) selected[i]).getName());
                    UILogger.LOG.finest("filtering s " + ((Label) selected[i]).getName());
                } else {
                    f = (PaymentFilter) selected[i];
                    UILogger.LOG.finest("filtering t " + f.getName());
                    labelsSelectedList.add(f.getName());
                }
            }

            return labelsSelectedList;
        }
    }

    private class PaymentsForLabelsMatcher implements Matcher {

        /** the users to match */
        private Set labels = new HashSet();

        /**
         * Create a new {@link PaymentsForLabelsMatcher} that matches only 
         * {@link Issue}s that have one or more user in the specified list.
         */
        public PaymentsForLabelsMatcher(Collection labels) {
            // make a defensive copy of the users
            this.labels.addAll(labels);
        }

        /**
         * Test whether to include or not include the specified issue based
         * on whether or not their user is selected.
         */
        public boolean matches(Object o) {
            if (o == null) {
                return false;
            }
            if (labels.isEmpty()) {
                return true;
            }
            Payment payment = (Payment) o;
            UILogger.LOG.finest(payment.toString());
            List<Label> plabels = payment.getLabels();
            for (Label label : plabels) {
                UILogger.LOG.finest("matches: " + label.getName());
                if (labels.contains(label.getName())) {
                    return true;
                }
            }
            return false;
        }
    }

    private class TypeSelect extends AbstractMatcherEditor implements ListSelectionListener {

        /** a list of labels */
        EventList labelsSelectedList = new BasicEventList();
        /** a widget for selecting labels */
        JList target;

        /**
         * Create a {@link IssuesForLabelsMatcherEditor} that matches Labels from the
         * specified {@link EventList} of {@link Issue}s.
         */
        public TypeSelect(JList target) {
            this.target = target;
            target.addListSelectionListener(this);
        }

        /**
         * When the JList selection changes, create a new Matcher and fire
         * an event.
         */
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            Matcher newMatcher =
                    new ScheduledPaymentMatcher(isScheduled());
            fireChanged(newMatcher);
        }

        private boolean isScheduled() {
            Object o = target.getSelectedValue();
            PaymentFilter p = (PaymentFilter) o;
            if (p == null) {
                return false;
            }

            return (p.getPaymentType() == PaymentFilter.TYPE_SCHEDULED);
        }
    }

    private class ScheduledPaymentMatcher implements Matcher {

        private boolean scheduled;

        /**
         * Create a new {@link PaymentsForLabelsMatcher} that matches only
         * {@link Issue}s that have one or more user in the specified list.
         */
        public ScheduledPaymentMatcher(boolean scheduled) {
            // make a defensive copy of the users
            this.scheduled = scheduled;
        }

        /**
         * Test whether to include or not include the specified issue based
         * on whether or not their user is selected.
         */
        public boolean matches(Object o) {
            if (o == null) {
                return false;
            }

            Payment payment = (Payment) o;
            UILogger.LOG.finest(payment + " is " + payment.isScheduled());
            if (scheduled) {
                return payment.isScheduled();
            } else {
                return !payment.isScheduled();
            }
        }
    }
}
