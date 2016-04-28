## Introduction ##

QuickSearch is introduced in NetBeans 6.5 M1/M2. Using the QuickSearchProvider, CashForward plugs into the search pipeline.


## Details ##

CashForward uses the QuickSearch APIs to search `Payment` data and to search (this) the CashForward website.

A [tutorial already exists](http://blogs.sun.com/geertjan/entry/search_the_guardian_from_a) for hooking into the website search. But to reiterate, this Bundle:

```
quicksearch.web.site=http://code.google.com/p/cashforward
quicksearch.web.url_patterns=.*,*
```

is placed in the folder branding/org-netbeans-spi-quicksearch.jar/org/modules/netbeans/quicksearch/web.

The other way to hook into the QuickSearch is via the provider interface. By implementing and registering a provider (via a Wizard) we can search for Payments.

Looking at the only method to implement in the `SearchProvider` interface:

```
    private static final String PREFIX = "Show Payments matching ";
    private RevealPayment filterPayments = new RevealPayment();
    private String source;

    public void evaluate(SearchRequest request, SearchResponse response) {
        source = request.getText();
        if (source.startsWith(PREFIX))
            source = source.substring(PREFIX.length());
        
        response.addResult(filterPayments, PREFIX + source);
    }
```

the `RevealPayment` runnable will fire off filtering on the EventList, and so filter out the payments on the screen.