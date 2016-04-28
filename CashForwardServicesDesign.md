# Introduction #

This document will take a brief look at the services module implemented in the NetBeans Grant Milestone Release.

CashForwardServices holds both the Payment object model and the services need to calculate, manage, and persist Payment information.

# The Payment Model #

The most import players in the CashForward domain model are :

  * the `Payment` - holds the information about the transaction, `Payee`, amount, start date, and `Occurrence`.
  * `Payee` - tells the `Payment` who to pay
  * `Label` - categorizes the `Payment`; may be a system or user tag

## `Payment` ##

`Payment}}s exists to track the income and expense transaction. The {{{Payment` object represents a generic transaction, and is considered a Bill if the amount is negative, and a Deposit if the amount is positive. The `Payee}} handling is simplistic, with one {{{Payee` attached to each `Payment`. CashForward does not support (yet) the idea of Payor - where the `Payment` would come from. Though that would be trivial to add and allow support for managing multiple Bank Account/Funds.

The trickiest part about the `Payment` is handling its schedule. A basic set of enumerated types exists to help track the various occurrences. The types are used by the `PaymentCalculator` to create projected `Paymnent`s.

## `Label` ##

The other primary player in the services model is the `Label`. `Label`s are tags that can be applied to each `Payment`. User (or category) tags are public (seen on the interface); while `Scenario`s tags are private, and handled differently.

# Persistence #

CashForward uses the Java Persistence API for storing the Payment model. Hibernate is the underlying persistence provider. Currently, the Hibernate configuration is driven entirely by the persistence.xml; but it would make sense to handle this programmatically.

Calls to the `EntityManager` are wrapped in the `PersistenceService`.`PersistenceService` should contain the essential CRUD operations for the model. More complex logic and handling should be done in the `PaymentService`/

# Services #

`Payment`s are manipulated through the `PaymentService` class. It is expected that external clients will do all work on `Payment`s via the `PaymentService`.