/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cashforward.ui.payment;

import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Logger;
import org.cashforward.model.Payee;
import org.cashforward.model.Payment;
import org.cashforward.ui.UIContext;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.OverlayableUtils;
import com.jidesoft.swing.StyledLabelBuilder;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import org.cashforward.model.Scenario;
import org.cashforward.ui.task.PaymentFilter;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
final class PaymentTopComponent extends TopComponent {

    private static PaymentTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "PaymentTopComponent";
    
    private DefaultOverlayable tableOverlay;
    private Lookup.Result paymentNotifier =
            UIContext.getDefault().lookupResult(Payment.class);
    private Lookup.Result payeeNotifier =
            UIContext.getDefault().lookupResult(Payee.class);
    private Lookup.Result scenarioNotifier =
            UIContext.getDefault().lookupResult(Scenario.class);
    private Lookup.Result filterNotifier =
            UIContext.getDefault().lookupResult(PaymentFilter.class);
    private JTable paymentTable;

    private PaymentTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(PaymentTopComponent.class, "CTL_PaymentTopComponent"));
        setToolTipText(NbBundle.getMessage(PaymentTopComponent.class, "HINT_PaymentTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));

        Icon icon = new ImageIcon(
                Utilities.loadImage("/org/cashforward/ui/payment/payment-detail.png"));
        JLabel infoLabel = new JLabel(icon);
        paymentDetailContainer.setTitleComponent(infoLabel);

        paymentTable = paymentListPanel.getTableComponent();
        paymentTable.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (paymentTable.columnAtPoint(e.getPoint()) == 4) {
                    try {
                        if (paymentDetailContainer.isCollapsed()) {
                            paymentDetailContainer.setCollapsed(false);
                        } else {
                            paymentDetailContainer.setCollapsed(true);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();/*who cares*/
                    }
                }
            }
        });

        paymentCompositePanel.setPayees(UIContext.getDefault().getPayees());
        paymentCompositePanel.setLabels(UIContext.getDefault().getLabels());
        payeeNotifier.addLookupListener(new LookupListener() {

            public void resultChanged(LookupEvent arg0) {
                paymentCompositePanel.setPayees(UIContext.getDefault().getPayees());
            }
        });

        paymentNotifier.addLookupListener(new LookupListener() {

            public void resultChanged(LookupEvent event) {
                Lookup.Result r = (Lookup.Result) event.getSource();
                Collection c = r.allInstances();
                if (!c.isEmpty()) {
                    PaymentFilter filter =
                            UIContext.getDefault().getPaymentFilter();
                    boolean calculated = filter.getPaymentType() ==
                            PaymentFilter.TYPE_CALCULATED;
                    Payment payment = (Payment) c.iterator().next();
                    paymentCompositePanel.setPayment(payment);
                    paymentCompositePanel.allowUpdate(!calculated);
                } else {
                    paymentCompositePanel.setPayment(new Payment());
                    paymentCompositePanel.allowUpdate(false);
                }
            }
        });

        setupOverlay();
    }

    public PaymentCompositePanel getPaymentCompositePanel() {
        return paymentCompositePanel;
    }

    private void setupOverlay() {
        remove(paymentContainer);
        tableOverlay =
                new DefaultOverlayable(paymentContainer);

        OverlayLookupListener oll = new OverlayLookupListener();
        scenarioNotifier.addLookupListener(oll);
        filterNotifier.addLookupListener(oll);

        tableOverlay.addOverlayComponent(
                StyledLabelBuilder.createStyledLabel("{No payments available for this Scenario. " +
                "Add a new Payment or change the filters. :f:gray}"),
                SwingConstants.CENTER);

        tableOverlay.addOverlayComponent(
                StyledLabelBuilder.createStyledLabel("{Please select at " +
                "least one Scenario to view Payments. :f:gray}"),
                SwingConstants.CENTER);

        add(tableOverlay,BorderLayout.CENTER);

        refreshOverlay();
    }

    private void refreshOverlay() {
        boolean scenarioSelected =
                UIContext.getDefault().getSelectedScenarios().size() > 0;
        boolean paymentsInTable = UIContext.getDefault().getPayments().size() > 0;

        if (scenarioSelected) {
            if (!paymentsInTable) { //show no payments message
                paymentContainer.setVisible(false);
                tableOverlay.getOverlayComponents()[0].setVisible(true);
                tableOverlay.getOverlayComponents()[1].setVisible(false);
                UIContext.getDefault().clearPayment();
            } else { //just show the table
                paymentContainer.setVisible(true);
                tableOverlay.getOverlayComponents()[0].setVisible(false);
                tableOverlay.getOverlayComponents()[1].setVisible(false);
            }
        } else { //show select scenario message
            paymentContainer.setVisible(false);//for now
            tableOverlay.getOverlayComponents()[0].setVisible(true);
            tableOverlay.getOverlayComponents()[1].setVisible(false);
            UIContext.getDefault().clearPayment();
        }

        OverlayableUtils.repaintOverlayable(tableOverlay);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paymentListPanel = new org.cashforward.ui.payment.PaymentListPanel();
        paymentCompositePanel = new org.cashforward.ui.payment.PaymentCompositePanel();
        paymentContainer = new javax.swing.JPanel();
        paymentListContainer = new com.jidesoft.pane.CollapsiblePane();
        paymentDetailContainer = new com.jidesoft.pane.CollapsiblePane();

        setLayout(new java.awt.BorderLayout());

        paymentListContainer.setShowExpandButton(false);
        paymentListContainer.setShowTitleBar(false);
        paymentListContainer.setStyle(CollapsiblePane.PLAIN_STYLE);
        paymentListContainer.setSlidingDirection(7);

        org.jdesktop.layout.GroupLayout paymentListContainerLayout = new org.jdesktop.layout.GroupLayout(paymentListContainer.getContentPane());
        paymentListContainer.getContentPane().setLayout(paymentListContainerLayout);
        paymentListContainerLayout.setHorizontalGroup(
            paymentListContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 302, Short.MAX_VALUE)
        );
        paymentListContainerLayout.setVerticalGroup(
            paymentListContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 243, Short.MAX_VALUE)
        );

        try {
            paymentDetailContainer.setCollapsed(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        paymentDetailContainer.setShowExpandButton(false);
        paymentDetailContainer.setStyle(CollapsiblePane.PLAIN_STYLE);
        paymentDetailContainer.setSlidingDirection(SwingConstants.WEST);

        org.jdesktop.layout.GroupLayout paymentDetailContainerLayout = new org.jdesktop.layout.GroupLayout(paymentDetailContainer.getContentPane());
        paymentDetailContainer.getContentPane().setLayout(paymentDetailContainerLayout);
        paymentDetailContainerLayout.setHorizontalGroup(
            paymentDetailContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 4, Short.MAX_VALUE)
        );
        paymentDetailContainerLayout.setVerticalGroup(
            paymentDetailContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        paymentListContainer.setSlidingDirection(CollapsiblePane.WEST);
        paymentListContainer.setContentPane(paymentListPanel);
        paymentListContainer.setContentAreaFilled(true);
        paymentDetailContainer.setSlidingDirection(SwingConstants.EAST);
        paymentDetailContainer.setContentPane(paymentCompositePanel);

        org.jdesktop.layout.GroupLayout paymentContainerLayout = new org.jdesktop.layout.GroupLayout(paymentContainer);
        paymentContainer.setLayout(paymentContainerLayout);
        paymentContainerLayout.setHorizontalGroup(
            paymentContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, paymentContainerLayout.createSequentialGroup()
                .add(paymentListContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(paymentDetailContainer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        paymentContainerLayout.setVerticalGroup(
            paymentContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, paymentContainerLayout.createSequentialGroup()
                .addContainerGap()
                .add(paymentDetailContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(paymentListContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        add(paymentContainer, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.cashforward.ui.payment.PaymentCompositePanel paymentCompositePanel;
    private javax.swing.JPanel paymentContainer;
    private com.jidesoft.pane.CollapsiblePane paymentDetailContainer;
    private com.jidesoft.pane.CollapsiblePane paymentListContainer;
    private org.cashforward.ui.payment.PaymentListPanel paymentListPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized PaymentTopComponent getDefault() {
        if (instance == null) {
            instance = new PaymentTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the PaymentTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized PaymentTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(PaymentTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof PaymentTopComponent) {
            return (PaymentTopComponent) win;
        }
        Logger.getLogger(PaymentTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        paymentListPanel.setPayments(
                UIContext.getDefault().getPayments());
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return PaymentTopComponent.getDefault();
        }
    }
    
     class OverlayLookupListener implements LookupListener {
        public void resultChanged(LookupEvent arg0) {
            refreshOverlay();
        }
    }
}
