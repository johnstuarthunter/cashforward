/*
 * PaymentTaskPanel.java
 *
 * Created on May 17, 2008, 9:18 PM
 */
package org.cashforward.ui.task;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.EventListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.cashforward.model.Scenario;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.action.LoadCurrentPaymentsAction;
import org.cashforward.ui.action.LoadScheduledPaymentsAction;
import org.cashforward.ui.action.LoadSpecificPaymentsAction;
import org.cashforward.util.DateUtilities;

/**
 *
 * @author  Bill
 */
public class PaymentTaskPanel extends javax.swing.JPanel {

    private static EventList<Scenario> scenarios = new BasicEventList();
    private static EventList<PaymentFilter> types = new BasicEventList();
    private static EventList<PaymentFilter> labels = new BasicEventList();
    private LoadScheduledPaymentsAction loadScheduledPayments;
    private LoadCurrentPaymentsAction loadCurrentPayments;
    private LoadSpecificPaymentsAction loadSpecificPayments;

    /** Creates new form PaymentTaskPanel */
    public PaymentTaskPanel() {
        initComponents();

        //temp data
        PaymentFilter scheduled = new PaymentFilter("Scheduled");
        scheduled.setPaymentType(PaymentFilter.TYPE_SCHEDULED);
        types.add(scheduled);

        PaymentFilter current = new PaymentFilter("Current");
        current.setPaymentType(PaymentFilter.TYPE_CURRENT);
        types.add(current);

        PaymentFilter projected = new PaymentFilter("Projected");
        projected.getPaymentSearchCriteria().setDateStart(
                DateUtilities.firstOfThisYear());
        projected.getPaymentSearchCriteria().setDateEnd(
                DateUtilities.endOfThisYear());
        projected.setPaymentType(PaymentFilter.TYPE_CALCULATED);
        types.add(projected);

        PaymentFilter auto = new PaymentFilter("Auto");
        labels.add(auto);

        PaymentFilter fun = new PaymentFilter("Fun");
        labels.add(fun);

        PaymentFilter food = new PaymentFilter("Food");
        labels.add(food);

        loadCurrentPayments = new LoadCurrentPaymentsAction();
        loadScheduledPayments = new LoadScheduledPaymentsAction();
        loadSpecificPayments = new LoadSpecificPaymentsAction();
        this.scenarioList.setModel(new EventListModel(scenarios));
        this.scenarioList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                } else if (PaymentTaskPanel.this.scenarioList.getSelectedIndex() < 0) {
                    UIContext.getDefault().clearScenario();
                    return;
                }

                Object[] s = scenarioList.getSelectedValues();
                EventList l = new BasicEventList();
                for (int i = 0; i < s.length; i++) {
                    l.add(s[i]);
                }

                Scenario scenario = (Scenario) scenarioList.getSelectedValue();
                UIContext.getDefault().setSelectedScenarios(l);
                //loadSpecificPayments.actionPerformed(null);
                PaymentFilter filter = (PaymentFilter) typeList.getSelectedValue();
                if (filter != null) {
                    processFilter(filter);
                }
            }
        });

        this.typeList.setModel(new EventListModel(types));
        this.typeList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                } else if (PaymentTaskPanel.this.typeList.getSelectedIndex() < 0) {
                    UIContext.getDefault().clearScenario();
                    return;
                } else if ((PaymentTaskPanel.this.typeList.getSelectedValue() instanceof Scenario)) {
                    Scenario scenario = (Scenario) scenarioList.getSelectedValue();
                    UIContext.getDefault().setScenario(scenario);
                    return;
                }

                PaymentFilter filter = (PaymentFilter) typeList.getSelectedValue();
                processFilter(filter);
            }
        });

        this.labelList.setModel(new EventListModel(labels));
    //remember what was last selected? //should be an object
    //groupList1.setSelectedIndex(1);

    }

    public JList getLabelListComponent() {
        return this.labelList;
    }

    public JList getScenarioListComponent() {
        return this.scenarioList;
    }

    public JList getTypeListComponent() {
        return this.typeList;
    }

    //alternatively, this would just be filtered automatically
    //using the glazed lists matchers
    private void processFilter(PaymentFilter filter) {
        long start = System.currentTimeMillis();
        System.out.println("processFilter start:" + start);
        filter.setScenario(UIContext.getDefault().getScenario());
        UIContext.getDefault().setPaymentFilter(filter);
        if (filter.getPaymentType() == PaymentFilter.TYPE_CURRENT) {
            loadCurrentPayments.actionPerformed(null);
        } else if (filter.getPaymentType() == PaymentFilter.TYPE_SCHEDULED) {
            loadScheduledPayments.actionPerformed(null);
        } else if (filter.getPaymentType() == PaymentFilter.TYPE_CALCULATED) {
            loadSpecificPayments.actionPerformed(null);
        }
        System.out.println("processFilter elapsed:" + (System.currentTimeMillis() - start));
    }

    public void setScenarios(EventList<Scenario> scenarios) {
        for (Scenario scenario : scenarios) {
            PaymentTaskPanel.scenarios.add(scenario);
        }
        scenarios.addListEventListener(new ListEventListener() {
            //TODO handle remove

            public void listChanged(ListEvent event) {
                while (event.next()) {
                    if (event.getType() == ListEvent.INSERT) {
                        EventList<Scenario> source = event.getSourceList();
                        Scenario scenario = source.get(event.getIndex());
                        PaymentTaskPanel.scenarios.add(scenario);
                    } else if (event.getType() == ListEvent.DELETE) {
                        EventList<Scenario> source = event.getSourceList();
                        Scenario scenario = source.get(event.getIndex());
                        PaymentTaskPanel.scenarios.remove(scenario);
                    } else if (event.getType() == ListEvent.UPDATE) {
                    }
                }

            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        scenarioList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        typeList = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        labelList = new javax.swing.JList();

        jScrollPane1.setViewportView(scenarioList);

        jScrollPane2.setViewportView(typeList);

        jScrollPane3.setViewportView(labelList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList labelList;
    private javax.swing.JList scenarioList;
    private javax.swing.JList typeList;
    // End of variables declaration//GEN-END:variables
}
