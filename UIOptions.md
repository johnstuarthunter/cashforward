## Introduction ##

The [Dialog and Service SPI](http://bits.netbeans.org/dev/javadoc/org-netbeans-modules-options-api/allclasses-frame.htmlOptions) provide support for creating customizable Option interfaces as well storing the User Preferences

## In CashForward ##

CashForward takes advantage of the tooling to install default Option interfaces for Payments and for General System settings. See the New File->Modular Development->Options Panel wizard.

After creating the two Options Panels. The CashForwardUI layer.xml was modified to only show those two Option Categories.

Preferences displayed in the Options Panels are backed by `NbPreferences`. (A wrapper around `java.util.Preferences`. `rg.cashforward.ui.internal.options.UIOptions` handles the storing and retrieval of CashForwardUI specific preferences.