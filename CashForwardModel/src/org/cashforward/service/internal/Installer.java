package org.cashforward.service.internal;

import java.util.List;
import org.cashforward.model.Scenario;
import org.cashforward.service.PaymentService;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 *
 */
public class Installer extends ModuleInstall {
    private PaymentService paymentService;
    @Override
    public void restored() {
        //make sure there is at least one scenario 
       if (paymentService == null)
           paymentService = new PaymentService();

       try {
            List<Scenario> scenarios = paymentService.getScenarios();
            if (scenarios.size() < 1) {
                //create the default
                Scenario defaultScenario = new Scenario("Current");
                paymentService.addOrUpdateScenario(defaultScenario);
            }
       } catch (Exception ex) {
            ServicesLogger.LOG.fine(ex.getMessage());
        }
    }
}
