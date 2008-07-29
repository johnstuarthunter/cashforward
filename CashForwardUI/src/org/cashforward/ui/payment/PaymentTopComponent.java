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
//import org.openide.util.Utilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import org.cashforward.ui.task.PaymentFilter;
/**
 * Top component which displays something.
 */
final class PaymentTopComponent extends TopComponent {
   
    private static PaymentTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "PaymentTopComponent";
    
    private Lookup.Result paymentNotifier =
            UIContext.getDefault().lookupResult(Payment.class);
    private Lookup.Result payeeNotifier = 
            UIContext.getDefault().lookupResult(Payee.class);
    private Lookup.Result paymentFilterNotifier = 
            UIContext.getDefault().lookupResult(PaymentFilter.class);
    
    private PaymentTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(PaymentTopComponent.class, "CTL_PaymentTopComponent"));
        setToolTipText(NbBundle.getMessage(PaymentTopComponent.class, "HINT_PaymentTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
        
        Icon icon = new ImageIcon(
                PaymentListPanel.class.getResource(
                    "/org/cashforward/ui/payment/payment-detail.png"));
        JLabel infoLabel = new JLabel(icon);
        paymentDetailContainer.setTitleComponent(infoLabel);
        
        final JTable paymentTable = paymentListPanel.getTableComponent();
        paymentTable.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (paymentTable.columnAtPoint(e.getPoint()) == 4){
                    try {
                        if (PaymentTopComponent.this.paymentDetailContainer.isCollapsed())
                            PaymentTopComponent.this.paymentDetailContainer.setCollapsed(false);
                        else 
                            PaymentTopComponent.this.paymentDetailContainer.setCollapsed(true);
                    } catch (Exception ex) {ex.printStackTrace();/*who cares*/}
                }
            }

        });
        
        paymentCompositePanel.setPayees(UIContext.getDefault().getPayees());
        paymentCompositePanel.setLabels(UIContext.getDefault().getLabels());
        /*
        paymentFilterNotifier.addLookupListener(new LookupListener() {
            public void resultChanged(LookupEvent event) {
                Lookup.Result r = (Lookup.Result) event.getSource();
                Collection c = r.allInstances();
                if (!c.isEmpty()) {
                    PaymentFilter filter = (PaymentFilter) c.iterator().next();
                    int type = filter.getPaymentType();
                    if (type == PaymentFilter.TYPE_CALCULATED)
                        paymentListPanel.setPayments(
                                UIContext.getDefault().getFilteredPayments());
                    else if (type == PaymentFilter.TYPE_CURRENT)
                        paymentListPanel.setPayments(
                                UIContext.getDefault().getCurrentPayments());
                    else if (type == PaymentFilter.TYPE_SCHEDULED)
                        paymentListPanel.setPayments(
                                UIContext.getDefault().getScheduledPayments());
                } 
            }
        });
        */
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
                    Payment payment = (Payment) c.iterator().next();
                    paymentCompositePanel.setPayment(payment);
                }                
            }
        });
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
        paymentListContainer = new com.jidesoft.pane.CollapsiblePane();
        paymentDetailContainer = new com.jidesoft.pane.CollapsiblePane();

        paymentListContainer.setShowExpandButton(false);
        paymentListContainer.setShowTitleBar(false);
        paymentListContainer.setStyle(CollapsiblePane.PLAIN_STYLE);
        paymentListContainer.setSlidingDirection(7);

        org.jdesktop.layout.GroupLayout paymentListContainerLayout = new org.jdesktop.layout.GroupLayout(paymentListContainer.getContentPane());
        paymentListContainer.getContentPane().setLayout(paymentListContainerLayout);
        paymentListContainerLayout.setHorizontalGroup(
            paymentListContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 272, Short.MAX_VALUE)
        );
        paymentListContainerLayout.setVerticalGroup(
            paymentListContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 210, Short.MAX_VALUE)
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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(paymentListContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(paymentDetailContainer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(paymentListContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, paymentDetailContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.cashforward.ui.payment.PaymentCompositePanel paymentCompositePanel;
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
}
