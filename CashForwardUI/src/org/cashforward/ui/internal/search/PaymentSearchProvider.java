package org.cashforward.ui.internal.search;

import org.cashforward.ui.internal.UILogger;
import org.cashforward.ui.internal.filter.MatcherFactory;
import org.netbeans.spi.quicksearch.SearchProvider;
import org.netbeans.spi.quicksearch.SearchRequest;
import org.netbeans.spi.quicksearch.SearchResponse;

/**
 * The aook into the global platform search; provides the mechanism
 * for finding payments meeting certain criteria.
 *
 * See <code>PaymentFilterator</code>
 *
 * @author Bill
 */
public class PaymentSearchProvider implements SearchProvider {

    private RevealPayment filterPayments = new RevealPayment();
    private String source ;
    
    /**
     * Method is called by infrastructure when search operation was requested.
     * Implementors should evaluate given request and fill response object with
     * apropriate results
     *
     * @param request Search request object that contains information what to search for
     * @param response Search response object that stores search results. Note that it's important to react to return value of SearchResponse.addResult(...) method and stop computation if false value is returned.
     */
    public void evaluate(SearchRequest request, SearchResponse response) {
        source = request.getText();
        
        response.addResult(filterPayments, "Show Payments matching " + source);

    }

    private class RevealPayment implements Runnable {

        public void run() {
            //filter out the payments
            if (source != null || !"".equals(source.trim())) {
                MatcherFactory.getInstance().getTemp().setText(source);
            } else {
                MatcherFactory.getInstance().getTemp().setText(null);
            }
        }
        
    }

}
