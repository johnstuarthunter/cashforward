/*
 * PaymentDetailPanel.java
 *
 * Created on May 17, 2008, 9:49 PM
 */
package org.cashforward.ui.payment;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.cashforward.model.Label;
import org.cashforward.model.Payee;
import org.cashforward.model.Payment;
import org.cashforward.ui.internal.options.UIOptions;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;

/**
 *
 * @author  Bill
 */
public class PaymentDetailPanel extends javax.swing.JPanel {

    Payment payment;
    public static Property PROP_paymentValid =
            BeanProperty.create("paymentValid");
    private boolean paymentValid;
    
    /** Creates new form PaymentDetailPanel */
    public PaymentDetailPanel() {
        initComponents();
        this.paymentAmountCombo.getCalculator().setDisplayFormat(
                NumberFormat.getCurrencyInstance());
        labelList.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (UIOptions.paymentsRequireLabel()){
                    Object[] items = labelList.getSelectedObjects();
                    PROP_paymentValid.setValue(
                            PaymentDetailPanel.this,
                            items != null && items.length > 0);
                }
            }
        });
        /*labelList.addgetList().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (UIOptions.paymentsRequireLabel()){
                    int[] items = labelList.getList().getSelectedIndices();
                    setPaymentValid(items != null && items.length > 0);
                }
            }
        });*/

    }

    public boolean getPaymentValid() {
        return paymentValid;
    }

    public void setPaymentValid(boolean valid) {
        System.out.println("setting paymentvalid:"+valid);
        this.paymentValid = valid;
    }

    public void setPayees(EventList<Payee> payees) {
        payeeCombo.setModel(new EventListComboBoxModel(payees));
    }

    public void setLabels(EventList<Label> labels) {
        this.labelList.setModel(new EventListComboBoxModel(labels));
    }

    public void setOccurences(List<Label> occurences) {
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        setIsDeposit(payment.getAmount() >= 0);

        descriptionText.setText(payment.getDescription());
        paymentAmountCombo.getCalculator().setDisplayText(
                Float.toString(payment.getAmount()));
        payeeCombo.setEditable(true);
        payeeCombo.setSelectedItem(payment.getPayee());
        paymentDateChooser.setDate(payment.getStartDate());
        labelList.setSelectedObjects(payment.getLabels().toArray());
    }

    public Payment getPayment() {
        //set up the payment
        payment.setDescription(descriptionText.getText());
        payment.setAmount(getAmount());

        //set payee
        Object payee = payeeCombo.getSelectedItem();
        if (payee instanceof String) {
            payee = new Payee(payee.toString());
        }
        payment.setPayee((Payee) payee);

        //set label
        payment.getLabels().clear();
        Object[] selectedLabels = labelList.getSelectedObjects();
        for (int i = 0; i < selectedLabels.length; i++) {
            if (selectedLabels[i] instanceof Label) {
                payment.addLabel((Label) selectedLabels[i]);
            } else {
                payment.addLabel(new Label((String) selectedLabels[i]));
            }
        }


        payment.setStartDate(paymentDateChooser.getDate());
        return this.payment;
    }

    private void setIsDeposit(boolean isDeposit) {
        cbDeposit.setSelected(isDeposit);
        cbBill.setSelected(!isDeposit);
    }

    private boolean isDeposit() {
        return cbDeposit.isSelected();
    }

    private float getAmount() {
        float amount = Float.parseFloat(
                paymentAmountCombo.getCalculator().getDisplayText());

        if (!isDeposit()) {
            amount = -1 * amount;
        }

        return amount;
    }

    private class EventListComboBoxModel extends DefaultComboBoxModel {

        EventList source;

        public EventListComboBoxModel(EventList source) {
            super();
            this.source = source;
            source.addListEventListener(new ListEventListener<Payee>() {

                public void listChanged(ListEvent<Payee> event) {
                    EventListComboBoxModel.this.fireContentsChanged(
                            EventListComboBoxModel.this, 0, event.getSourceList().size());
                }
            });
        }

        public Object getElementAt(int index) {
            Object retValue;
            retValue = source.get(index);
            return retValue;
        }

        public int getSize() {
            int retValue;
            retValue = source.size();
            return retValue;
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        descriptionText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        labelList = new com.jidesoft.combobox.CheckBoxListComboBox();
        jLabel5 = new javax.swing.JLabel();
        paymentDateChooser = new com.jidesoft.combobox.DateComboBox();
        jLabel3 = new javax.swing.JLabel();
        payeeCombo = new com.jidesoft.swing.AutoCompletionComboBox();
        jLabel4 = new javax.swing.JLabel();
        paymentAmountCombo = new com.jidesoft.combobox.CalculatorComboBox();
        cbDeposit = new javax.swing.JRadioButton();
        cbBill = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(PaymentDetailPanel.class, "PaymentDetailPanel.jLabel1.text")); // NOI18N

        descriptionText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descriptionTextActionPerformed(evt);
            }
        });

        jLabel2.setText(org.openide.util.NbBundle.getMessage(PaymentDetailPanel.class, "PaymentDetailPanel.jLabel2.text")); // NOI18N

        labelList.setPreferredSize(new java.awt.Dimension(119, 20));

        jLabel5.setText(org.openide.util.NbBundle.getMessage(PaymentDetailPanel.class, "PaymentDetailPanel.jLabel5.text")); // NOI18N

        paymentDateChooser.setPreferredSize(new java.awt.Dimension(92, 20));

        jLabel3.setText(org.openide.util.NbBundle.getMessage(PaymentDetailPanel.class, "PaymentDetailPanel.jLabel3.text")); // NOI18N

        payeeCombo.setStrict(false);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(PaymentDetailPanel.class, "PaymentDetailPanel.jLabel4.text")); // NOI18N

        paymentAmountCombo.setPreferredSize(new java.awt.Dimension(119, 20));

        buttonGroup1.add(cbDeposit);
        cbDeposit.setText(org.openide.util.NbBundle.getMessage(PaymentDetailPanel.class, "PaymentDetailPanel.cbDeposit.text")); // NOI18N
        cbDeposit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDepositActionPerformed(evt);
            }
        });

        buttonGroup1.add(cbBill);
        cbBill.setText(org.openide.util.NbBundle.getMessage(PaymentDetailPanel.class, "PaymentDetailPanel.cbBill.text")); // NOI18N

        jLabel6.setText(org.openide.util.NbBundle.getMessage(PaymentDetailPanel.class, "PaymentDetailPanel.jLabel6.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel4)
                    .add(jLabel5)
                    .add(jLabel1)
                    .add(jLabel3)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(paymentDateChooser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(paymentAmountCombo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE))
                    .add(descriptionText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                    .add(labelList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                    .add(payeeCombo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(cbBill)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbDeposit)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(5, 5, 5)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(cbDeposit)
                    .add(cbBill))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jLabel2)
                    .add(paymentAmountCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(paymentDateChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(payeeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(descriptionText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(labelList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void descriptionTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descriptionTextActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_descriptionTextActionPerformed

private void cbDepositActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDepositActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_cbDepositActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton cbBill;
    private javax.swing.JRadioButton cbDeposit;
    private javax.swing.JTextField descriptionText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private com.jidesoft.combobox.CheckBoxListComboBox labelList;
    private com.jidesoft.swing.AutoCompletionComboBox payeeCombo;
    private com.jidesoft.combobox.CalculatorComboBox paymentAmountCombo;
    private com.jidesoft.combobox.DateComboBox paymentDateChooser;
    // End of variables declaration//GEN-END:variables
}
