/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.ladon.behaviours.framework.manager;

import net.fhirfactory.pegacorn.ladon.behaviours.framework.worker.BehaviourRouteFrameworkTemplate;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BehaviourRouteManager {
    private static final Logger LOG = LoggerFactory.getLogger(BehaviourRouteManager.class);

    @Inject
    CamelContext camelctx;

    public void builtBehaviourRoutes(String behaviourName){
        LOG.debug(".buildBehaviourRoutes(): Entry, behaviourName (String) --> {}", behaviourName);
        try {
            BehaviourRouteFrameworkTemplate newRoute = new BehaviourRouteFrameworkTemplate(camelctx, behaviourName);
            LOG.trace(".buildBehaviourRoutes(): Attempting to install new Route");
            camelctx.addRoutes(newRoute);
            LOG.trace(".buildBehaviourRoutes(): Route installation successful");
        } catch (Exception Ex) {
            LOG.debug(".buildBehaviourRoutes(): Route install failed! Exception --> {}", Ex);
            // TODO Need to handle this better - potentially failing entire Processing Plant
        }
    }

}
