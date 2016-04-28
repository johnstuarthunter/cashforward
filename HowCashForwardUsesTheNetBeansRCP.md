# Overview #

The NetBeans Platform provides excellent out of the box support for building Swing applications. If you've ever built custom Swing applications, you understand the complexity and time it takes to make an application function.

The NetBeans Platform provides frameworks and tooling that help deal with that complexity. These are just some of my notes and observations as I've built CashForward.

## [Get up and Running: Quick and easy interface creation](UICreation.md) ##
  * Using Window and Forms for interface layout and design.
    * Its a process of continual prototyping
  * Matisse for form construction
    * Expanding the default palette

## [Stay in sync: Using Lookup as a central application messaging bus](UIContext.md) ##
  * Central context for managing state (current selections, shared data)

## [Lets Dialog: Advanced Windowing and Dialog support](UIEasyDialog.md) ##
  * Using Notifier for handling error, warning, and info displays

## [Call to Action: Effectively using Actions](UIActionManagement.md) ##
  * Easy action creation via wizards
  * Customize menus and toolbars with the layer.xml
  * Working with Context aware actions

## [What's your Preference: Managing user Preferences and settings](UIOptions.md) ##
  * Customizing the Options dialog
  * Handling user and internal UI options

## [Find It: Integrating with the QuickSearch](UIQuickSearch.md) ##
  * Enabling searching of application data and help files via the Search Provider

## [Nuts and Bolts: Infrastructure support: Module management, Logging, etc](UIInternals.md) ##
  * Module development and reducing dependencies
  * Logging facilities