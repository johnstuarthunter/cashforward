## Introduction ##

The [Actions API](http://bits.netbeans.org/dev/javadoc/index.html) manages the actions available to the Platform modules. Menus, Toolbars, and code can access and invoke registered actions.

## In CashForward ##

Only CallableSystemAction is used. This is an always-on action that is modified to be context-aware (through listening for objects on the UIContext).

One of the nice things about the Actions API is the registry.  I can call
`SystemAction.get(myClass);` to get the global instance of the action. So in the `PaymentCompositePanel`, the update button behavior comes from `SystemAction.get(SavePaymentAction.class);`

`CallableSystemActions` can also be asynchronous, simply by returning `true` from `asynchronous`.

Actions can be created with the New File->Module Development->Action wizard.