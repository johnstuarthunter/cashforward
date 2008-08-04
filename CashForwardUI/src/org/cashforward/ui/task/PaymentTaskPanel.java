package org.cashforward.ui.task;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.EventListModel;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.OverlayableUtils;
import com.jidesoft.swing.StyledLabelBuilder;
import java.util.Date;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.cashforward.model.Scenario;
import org.cashforward.ui.UIContext;
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

        //lblScenarios.setIcon(UIResources.getImage(UIResources.ICON_SCENARIO));
        //lblPaymentTypes.setIcon(UIResources.getImage(UIResources.ICON_PAYMENT));
        //lblLabels.setIcon(UIResources.getImage(UIResources.ICON_LABELS));

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
        this.scenarioList.setModel(new EventListModel(scenarios));
        this.scenarioList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                } else if (scenarioList.getSelectedIndex() < 0) {
                    //keep label/type lists disabled when no Scenario selected
                    UIContext.getDefault().clearScenario();
                    labelList.setEnabled(false);
                    typeList.setEnabled(false);
                    refreshOverlay();
                    return;
                }
                
                labelList.setEnabled(true);
                typeList.setEnabled(true);
                //set the selected scenarios
                Object[] s = scenarioList.getSelectedValues();
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

        this.scenarioList.addListSelectionListener(clearQuickSearch);
        this.typeList.addListSelectionListener(clearQuickSearch);
        this.labelList.addListSelectionListener(clearQuickSearch);

        this.labelList.setModel(new EventListModel(labels));

        setupOverlay();

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
        scenarioList.setModel(new EventListModel(newSenarios));
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        styledLabel1 = new com.jidesoft.swing.StyledLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        scenarioList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        typeList = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        labelList = new javax.swing.JList();
        lblScenarios = new javax.swing.JLabel();
        lblPaymentTypes = new javax.swing.JLabel();
        lblLabels = new javax.swing.JLabel();

        styledLabel1.setText(org.openide.util.NbBundle.getMessage(PaymentTaskPanel.class, "PaymentTaskPanel.styledLabel1.text")); // NOI18N

        jScrollPane1.setViewportView(scenarioList);

        typeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(typeList);

        jScrollPane3.setViewportView(labelList);

        lblScenarios.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblScenarios.setText(org.openide.util.NbBundle.getMessage(PaymentTaskPanel.class, "PaymentTaskPanel.lblScenarios.text")); // NOI18N

        lblPaymentTypes.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblPaymentTypes.setText(org.openide.util.NbBundle.getMessage(PaymentTaskPanel.class, "PaymentTaskPanel.lblPaymentTypes.text")); // NOI18N

        lblLabels.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblLabels.setText(org.openide.util.NbBundle.getMessage(PaymentTaskPanel.class, "PaymentTaskPanel.lblLabels.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblScenarios)
                .addContainerGap(66, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblPaymentTypes)
                .addContainerGap(17, Short.MAX_VALUE))
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblLabels)
                .addContainerGap(86, Short.MAX_VALUE))
            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblScenarios)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblPaymentTypes)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 88, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblLabels)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList labelList;
    private javax.swing.JLabel lblLabels;
    private javax.swing.JLabel lblPaymentTypes;
    private javax.swing.JLabel lblScenarios;
    private javax.swing.JList scenarioList;
    private com.jidesoft.swing.StyledLabel styledLabel1;
    private javax.swing.JList typeList;
    // End of variables declaration//GEN-END:variables
}
