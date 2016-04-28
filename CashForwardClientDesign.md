# Introduction #

This document will take a brief look at the client module implemented in the NetBeans Grant Milestone Release.

CashForwardUI? contains the core user interface for working with `Payment`s and [the services need to calculate, manage, and persist Payment information](CashForwardServicesDesign.md).


# User Interface Structure #

The user interface is organized into a few important layers.

  * Form/Presentation
  * Actions
  * Adapter

## Forms/Presentation ##

The presentation/form layers are divided up between several components - `Payment}, {{{Scenario`, and `Task`. These are also the three main `TopComponent`s you see in the interface.

Each of these packages, `org.cashforward.ui.payment`,`org.cashforward.ui.scenario`,`org.cashforward.ui.task` also contain supporting forms for input and display.

## Actions ##

The `org.cashforward.ui.action` package contains the actions for the menu/toolbars (usually inheriting off of `org.cashforward.action.BaseCallableSystemAction`) and other system actions. The actions control the flow of information between the Forms/Presentation and the Payment service adapters.


## Service Adapters ##

Adapters are responsible for taking presentation data and shuttling it to the `PaymentService` in the Model/Services module. Most of the time, actions should be using the adapters (`org.cashforward.ui.adapter` to connect to the services module.

Perhaps not evident immediately, one benefit of an adapter approach is future modularity - the ability to connect to different service providers without the rest of the user interface knowing about it. The client can work with the model data, and the adapter handles the communication with the service.

In the future adapters will also act as a bridge/converter between Presentation objects (e.g. Nodes) and the actual domain data.

# Internal #

There are other classes and packages used for internal activities :

  * Filtering
  * Searching
  * Options/Preferences

## Filtering ##

`org.cashforward.ui.internal.filter` contains helper classes for structuring the Payment event pipeline.

## Searching ##

Payments (and the CashForward website) can be searched via the QuickSearch; support for this is found in the `org.cashforward.ui.internal.search` package.

## Options ##

User preferences are stored using `org.cashforward.ui.internal.options.UIOptions` `UIOptions` is a wrapper around NbPreferences.

