package org.cashforward.ui.task;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.EventListModel;
import com.jidesoft.swing.CheckBoxList;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.OverlayableUtils;
import com.jidesoft.swing.StyledLabelBuilder;
import java.awt.Component;
import java.util.Date;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.cashforward.model.Scenario;
import org.cashforward.ui.UIContext;
import org.cashforward.ui.UIResources;
import org.cashforward.ui.action.LoadCurrentPaymentsAction;
import org.cashforward.ui.action.LoadScheduledPaymentsAction;
import org.cashforward.ui.action.LoadSpecificPaymentsAction;
import org.cashforward.ui.internal.UILogger;
import org.cashforward.ui.internal.filter.LabelsToPaymentFilterList;
import org.cashforward.ui.internal.filter.MatcherFactory;
import org.cashforward.util.DateUtilities;

/**
 *  Shows the Scenario, Payment type, and Payment label lists/filters.
 *
 * @author  Bill
 */
public class PaymentTaskPanel extends javax.swing.JPanel {

    private DefaultOverlayable labelOverlay;
    private EventList<Scenario> scenarios = UIContext.getDefault().getScenarios();
    private EventList<PaymentFilter> types = new BasicEventList();
    private EventList<PaymentFilter> labels =
            new LabelsToPaymentFilterList(UIContext.getDefault().getLabels());
    private LoadScheduledPaymentsAction loadScheduledPayments;
    private LoadCurrentPaymentsAction loadCurrentPayments;
    private LoadSpecificPaymentsAction loadSpecificPayments;

    public PaymentTaskPanel() {
        initComponents();

        lblScenarios.setIcon(UIResources.getImage(UIResources.ICON_SCENARIO));
        typeList.setCellRenderer(new PaymentTypeCellRenderer());

        //the types can be hard wired data
        PaymentFilter scheduled = new PaymentFilter("Scheduled");
        scheduled.setPaymentType(PaymentFilter.TYPE_SCHEDULED);
        types.add(scheduled);

        PaymentFilter current = new PaymentFilter("Current");
        current.setPaymentType(PaymentFilter.TYPE_CURRENT);
        types.add(current);

        PaymentFilter projected = new PaymentFilter("Projected");
        projected.getPaymentSearchCriteria().setDateStart(
                new Date());

        projected.getPaymentSearchCriteria().setDateEnd(
                DateUtilities.endOfThisYear());
        projected.setPaymentType(PaymentFilter.TYPE_CALCULATED);
        types.add(projected);


        loadCurrentPayments = new LoadCurrentPaymentsAction();
        loadScheduledPayments = new LoadScheduledPaymentsAction();
        loadSpecificPayments = new LoadSpecificPaymentsAction();
        this.scenarioList2.setModel(new EventListModel(scenarios));
        this.scenarioList2.getCheckBoxListSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                } else if (scenarioList2.getCheckBoxListSelectionModel().getMaxSelectionIndex() < 0) {
                    //keep label/type lists disabled when no Scenario selected
                    UIContext.getDefault().clearScenario();
                    labelList2.setEnabled(false);
                    typeList.setEnabled(false);
                    refreshOverlay();
                    return;
                }
                
                labelList2.setEnabled(true);
                typeList.setEnabled(true);
                //set the selected scenarios
                Object[] s = scenarioList2.getCheckBoxListSelectedValues();
                EventList l = new BasicEventList();
                for (int i = 0; i < s.length; i++) {
                    l.add(s[i]);
                }
                UIContext.getDefault().setSelectedScenarios(l);
                
                refreshOverlay();

                //update the filter
                PaymentFilter filter =
                        (PaymentFilter) typeList.getSelectedValue();
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
                }

                //update the filter
                processFilter((PaymentFilter) typeList.getSelectedValue());

                //...and the overlay
                refreshOverlay();
            }
        });

        /**
         * The PaymentQuickSearch sets permanent filters on the Payment list
         *
         * When we get focus back on the scenario/type/list
         * clear the QuickSearch filter
         */
        ListSelectionListener clearQuickSearch = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                clearQuickSearch();
            }
        };

        this.scenarioList2.addListSelectionListener(clearQuickSearch);
        this.typeList.addListSelectionListener(clearQuickSearch);
        this.labelList2.addListSelectionListener(clearQuickSearch);

        this.labelList2.setModel(new EventListModel(labels));

        setupOverlay();

    }

    public JList getLabelListComponent() {
        return this.labelList2;
    }

    public CheckBoxList getScenarioListComponent() {
        return this.scenarioList2;
    }

    public JList getTypeListComponent() {
        return this.typeList;
    }

    //alternatively, this could just be filtered automatically
    //using the glazed lists matchers. But this seems a little more performant
    private void processFilter(PaymentFilter filter) {
        if (filter == null) {
            return;
        }

        long start = System.currentTimeMillis();
        UILogger.LOG.finest("processFilter start:" + start);
        int currentType =
                UIContext.getDefault().getPaymentFilter().getPaymentType();

        filter.setScenarios(UIContext.getDefault().getSelectedScenarios());
        UIContext.getDefault().setPaymentFilter(filter);
        if (currentType == PaymentFilter.TYPE_CALCULATED &&
                filter.getPaymentType() == PaymentFilter.TYPE_CURRENT) {
            loadCurrentPayments.actionPerformed(null);
        } else if (currentType == PaymentFilter.TYPE_CALCULATED &&
                filter.getPaymentType() == PaymentFilter.TYPE_SCHEDULED) {
            loadScheduledPayments.actionPerformed(null);
        } else if (currentType != PaymentFilter.TYPE_CALCULATED &&
                filter.getPaymentType() == PaymentFilter.TYPE_CALCULATED) {
            loadSpecificPayments.actionPerformed(null);
        }
        UILogger.LOG.finest("processFilter elapsed:" +
                (System.currentTimeMillis() - start));
    }

    public void setScenarios(EventList<Scenario> newSenarios) {
        scenarioList2.setModel(new EventListModel(newSenarios));
    }

    private void setupOverlay() {
        jScrollPane3.remove(labelList);
        labelOverlay =
                new DefaultOverlayable(labelList);
        labelOverlay.addOverlayComponent(
                StyledLabelBuilder.createStyledLabel(
                "{Labels added to Payments will appear here.  :f:gray}"));
        labelOverlay.setOverlayVisible(labels.size() == 0);
        jScrollPane3.setViewportView(labelOverlay);

        labels.addListEventListener(new ListEventListener() {
            public void listChanged(ListEvent event) {
                refreshOverlay();
            }
        });
    }

    private void refreshOverlay() {
        labelOverlay.getOverlayComponents()[0].setVisible(
                labels.size() == 0);
        OverlayableUtils.repaintOverlayable(labelOverlay);
    }

    private void clearQuickSearch() {
        if (MatcherFactory.getInstance().getQuickSearchProxy().getText() != null) {
            MatcherFactory.getInstance().getQuickSearchProxy().setText(null);
        }
    }
    
    private class PaymentTypeCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel cell = (JLabel)super.getListCellRendererComponent(list,
                    value, index, isSelected, cellHasFocus);
                    
            PaymentFilter filter = (PaymentFilter)value;
            if (filter.getPaymentType() == PaymentFilter.TYPE_CALCULATED)
                cell.setIcon(UIResources.getImage(UIResources.ICON_PAYMENT_PROJECTED));
            else if (filter.getPaymentType() == PaymentFilter.TYPE_CURRENT)
                cell.setIcon(UIResources.getImage(UIResources.ICON_PAYMENT_CURRENT));
            else if (filter.getPaymentType() == PaymentFilter.TYPE_SCHEDULED)
                cell.setIcon(UIResources.getImage(UIResources.ICON_PAYMENT_SCHEDULED));
            
            return cell;
        }
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        labelList = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        scenarioList = new javax.swing.JList();
        styledLabel1 = new com.jidesoft.swing.StyledLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        typeList = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        scenarioList2 = new com.jidesoft.swing.CheckBoxList();
        jScrollPane5 = new javax.swing.JScrollPane();
        labelList2 = new com.jidesoft.swing.CheckBoxList();
        lblLabels = new javax.swing.JLabel();
        lblScenarios = new javax.swing.JLabel();
        lblPaymentTypes = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();

        jScrollPane3.setBorder(null);

        labelList.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        labelList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(PaymentTaskPanel.class, "PaymentTaskPanel.labelList.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(0, 0, 0))); // NOI18N
        jScrollPane3.setViewportView(labelList);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(PaymentTaskPanel.class, "PaymentTaskPanel.jScrollPane1.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(0, 0, 0))); // NOI18N

        scenarioList.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jScrollPane1.setViewportView(scenarioList);

        styledLabel1.setText(org.openide.util.NbBundle.getMessage(PaymentTaskPanel.class, "PaymentTaskPanel.styledLabel1.text")); // NOI18N

        jScrollPane2.setBorder(null);

        typeList.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        typeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(typeList);

        jScrollPane4.setBorder(null);

        scenarioList2.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jScrollPane4.setViewportView(scenarioList2);

        jScrollPane5.setBorder(null);

        labelList2.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jScrollPane5.setViewportView(labelList2);

        lblLabels.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblLabels.setText(org.openide.util.NbBundle.getMessage(PaymentTaskPanel.class, "PaymentTaskPanel.lblLabels.text")); // NOI18N

        lblScenarios.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblScenarios.setText(org.openide.util.NbBundle.getMessage(PaymentTaskPanel.class, "PaymentTaskPanel.lblScenarios.text")); // NOI18N

        lblPaymentTypes.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblPaymentTypes.setText(org.openide.util.NbBundle.getMessage(PaymentTaskPanel.class, "PaymentTaskPanel.lblPaymentTypes.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(lblLabels)
                        .addContainerGap(142, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lblScenarios)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lblPaymentTypes)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .add(jSeparator3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblScenarios)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 58, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(lblPaymentTypes)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(2, 2, 2)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(lblLabels)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JList labelList;
    private com.jidesoft.swing.CheckBoxList labelList2;
    private javax.swing.JLabel lblLabels;
    private javax.swing.JLabel lblPaymentTypes;
    private javax.swing.JLabel lblScenarios;
    private javax.swing.JList scenarioList;
    private com.jidesoft.swing.CheckBoxList scenarioList2;
    private com.jidesoft.swing.StyledLabel styledLabel1;
    private javax.swing.JList typeList;
    // End of variables declaration//GEN-END:variables
}
