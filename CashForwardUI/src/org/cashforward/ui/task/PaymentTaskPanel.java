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
import com.jidesoft.list.AbstractGroupableListModel;
import com.jidesoft.swing.PartialLineBorder;
import com.jidesoft.swing.PartialSide;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
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
    private LoadScheduledPaymentsAction loadScheduledPayments;
    private LoadCurrentPaymentsAction loadCurrentPayments;
    private LoadSpecificPaymentsAction loadSpecificPayments;

    /** Creates new form PaymentTaskPanel */
    public PaymentTaskPanel() {
        initComponents();

        loadCurrentPayments = new LoadCurrentPaymentsAction();
        loadScheduledPayments = new LoadScheduledPaymentsAction();
        loadSpecificPayments = new LoadSpecificPaymentsAction();
        this.scenarioList.setModel(new ScenarioListFilterModel());
        this.scenarioList.setGroupCellRenderer(new GroupCellRenderer());
        this.scenarioList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                } else if (PaymentTaskPanel.this.scenarioList.getSelectedIndex() < 0) {
                    UIContext.getDefault().clearScenario();
                    return;
                }
                
                if (e.getFirstIndex() != e.getLastIndex()){
                    //update graph with scenario
                    
                    return;
                }

                Scenario scenario = (Scenario) scenarioList.getSelectedValue();
                UIContext.getDefault().setScenario(scenario);
                //loadSpecificPayments.actionPerformed(null);
                PaymentFilter filter = (PaymentFilter) filterList.getSelectedValue();
                if (filter != null) {
                    processFilter(filter);
                }
            }
        });

        this.filterList.setModel(new PaymentListFilterModel());
        this.filterList.setGroupCellRenderer(new GroupCellRenderer());
        this.filterList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                } else if (PaymentTaskPanel.this.filterList.getSelectedIndex() < 0) {
                    UIContext.getDefault().clearScenario();
                    return;
                } else if (!(PaymentTaskPanel.this.filterList.getSelectedValue() instanceof PaymentFilter)) {
                    return;
                }

                PaymentFilter filter = (PaymentFilter) filterList.getSelectedValue();
                processFilter(filter);
            }
        });

    //remember what was last selected? //should be an object
    //groupList1.setSelectedIndex(1);

    }

    public JList getLabelListComponent() {
        return this.filterList;
    }
    //alternatively, this would just be filtered automatically
    //using the glazed lists matchers
    private void processFilter(PaymentFilter filter) {
        filter.setScenario(UIContext.getDefault().getScenario());
        UIContext.getDefault().setPaymentFilter(filter);
        if (filter.getPaymentType() == PaymentFilter.TYPE_CURRENT) {
            loadCurrentPayments.actionPerformed(null);
        } else if (filter.getPaymentType() == PaymentFilter.TYPE_SCHEDULED) {
            loadScheduledPayments.actionPerformed(null);
        } else if (filter.getPaymentType() == PaymentFilter.TYPE_CALCULATED) {
            loadSpecificPayments.actionPerformed(null);
        }
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
        filterList = new com.jidesoft.list.GroupList();
        jScrollPane2 = new javax.swing.JScrollPane();
        scenarioList = new com.jidesoft.list.GroupList();

        jScrollPane1.setViewportView(filterList);

        jScrollPane2.setViewportView(scenarioList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.jidesoft.list.GroupList filterList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.jidesoft.list.GroupList scenarioList;
    // End of variables declaration//GEN-END:variables
    static class ScenarioListFilterModel extends AbstractGroupableListModel {

        private static String[] GROUP_NAMES = {
            "Scenarios"
        };

        public ScenarioListFilterModel() {
            scenarios.addListEventListener(new ListEventListener() {

                public void listChanged(ListEvent event) {
                    fireGroupChanged(scenarios);
                }
            });

        }

        public int getSize() {
            //System.out.println("getSize:"+scenarios.length + payments.length + labels.length);
            return scenarios.size();
        }

        public Object getElementAt(int index) {
            return scenarios.get(index);//return scenarios[index];
        }

        @Override
        public Object[] getGroups() {
            return GROUP_NAMES;
        }

        @Override
        public Object getGroupAt(int index) {
            return GROUP_NAMES[0];
        }
    }

    static class PaymentListFilterModel extends AbstractGroupableListModel {

        private static String[] GROUP_NAMES = {
            "Payments", "Labels"
        };
        private static final String[] payments = {
            "Scheduled", "Current", "Projected"
        };
        private static final String[] labels = {
            "Auto", "Fun", "Food"
        };
        private List<PaymentFilter> label = new ArrayList();

        public PaymentListFilterModel() {
            //create our filters, probably should be a customizable by user
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
            label.add(auto);

            PaymentFilter fun = new PaymentFilter("Fun");
            label.add(fun);

            PaymentFilter food = new PaymentFilter("Food");
            label.add(food);

        }

        public int getSize() {
            //System.out.println("getSize:"+scenarios.length + payments.length + labels.length);
            return payments.length + labels.length;
        }

        public Object getElementAt(int index) {
            //System.out.println("getElementAt:"+index);
            if (index < payments.length) {
                //System.out.println("scenarios and payments:"+(index - scenarios.length));
                return types.get(index);//return payments[index - scenarios.length];
            } else {
                //System.out.println("scenarios and payments and labels:"+(index - scenarios.length - payments.length));
                return label.get(index - (payments.length));//labels[index - (scenarios.length + payments.length)];
            }
        }

        @Override
        public Object[] getGroups() {
            return GROUP_NAMES;
        }

        @Override
        public Object getGroupAt(int index) {
            //System.out.println("getGroupAt:"+index);
            if (index < payments.length) {
                //System.out.println("scenarios and payments:"+(index - scenarios.length));
                return GROUP_NAMES[0];
            } else {
                //System.out.println("scenarios and payments and labels:"+(index - scenarios.length - payments.length));
                return GROUP_NAMES[1];
            }
        }
    }

    class GroupCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            label.setBackground(isSelected ? list.getSelectionBackground() : new Color(221, 231, 238));
            label.setForeground(new Color(0, 21, 110));
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            label.setBorder(BorderFactory.createCompoundBorder(new PartialLineBorder(Color.LIGHT_GRAY, 1, PartialSide.SOUTH),
                    BorderFactory.createEmptyBorder(2, 6, 2, 2)));
            return label;
        }
    }
}
