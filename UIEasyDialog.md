# Introduction #

As a more flexible alternative to `JOptionPane`, the NetBeans Platform provides the [Dialogs API](http://bits.netbeans.org/dev/javadoc/index.html) for handling alerts, notifications, and messages to the user.

# In CashForward #

DialogDescriptor and NotifyDescriptor are used to ask for information, confirmation, and display problems.

The Dialogs API provides simple defaults:

```
        //NotifyDescriptor will tell the dialog what to display. 
        //in this case, just a localized message for removing payments.
        //Note that this is a Confirmation dialog
        NotifyDescriptor notify =
                new NotifyDescriptor.Confirmation(
                getMessage("CTL_confirm_remove_payment"));

        //DialogDisplayer does the work of showing the Descriptor
        Object result = DialogDisplayer.getDefault().notify(notify);
        //
        if (result == DialogDescriptor.YES_OPTION) {
          //do something
        }
```

or it is flexible enough to take a custom component

```
        //So we use DialogDescriptor for custom dialogs
        final DialogDescriptor dd =
                new DialogDescriptor(paymentDetailPanel,
                getMessage("CTL_NewPaymentTitle"));
        dd.setModal(true);
        dd.setLeaf(true);//don't allow nested dialogs
        dd.setOptionType(DialogDescriptor.OK_CANCEL_OPTION);
```
