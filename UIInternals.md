# Introduction #

Typically when building complex applications it is important to maintain a modular structure. Reasons for adopting a modular style include: flexibility in upgrades, flexibility in changing the implementation, and reducing the dependencies of components.

Perhaps the main strength of the NetBeans platform is its support for creating modular, pluggable architecutres.

# Modules and Suites #

There are really just two components in the CashFoward desgin: the services and the client.

CashForward is thus comprised of two primary modules: CashForwardUI and CashForwardServices. CashForward is then technically a collection, or suite, of these modules.

The client (CashForwardUI) depends on the service (CashForwardServices) module; while the services module knows nothing of the client module. These dependencies are set though the Project Properties->Library view.

One cool thing about NetBeans modules is the ability to control who sees the module APIs.
The CashForwardService module is configured in the Project Properties->API Versioning viw to expose the `org.cashforward.model`, `org.cashforward.service`, and `org.cashforward.util` packages.


Note: this module structure will likely change in future releases. The services API will be abstracted to a separated module to provide alternate implementations (perhaps using WebServices instead of direct-Hibernate-access).

Likewise, the adapters in the client module will be abstracted to an API module with alternate implementations based on service type.

# Application Utilities #

The Platform's logging API uses JDK logging; as such all application logging route through `java.util.Logger` gets included in the Platform logs.

The `ServicesLogger` and `UILogger` expose logging to there respective modules.


Getting tired...must sleep....zzzz