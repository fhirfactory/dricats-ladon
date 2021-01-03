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

import net.fhirfactory.pegacorn.ladon.behaviours.framework.worker.common.StimulusBasedBehaviourRouteFrameworkTemplate;
import net.fhirfactory.pegacorn.ladon.behaviours.framework.worker.common.TimerBasedBehaviourRouteFrameworkTemplate;
import net.fhirfactory.pegacorn.ladon.behaviours.framework.worker.twintypecentrc.*;
import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourTypeEnum;
import net.fhirfactory.pegacorn.ladon.model.twin.TwinTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElement;
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

    public void buildBehaviourRoutes(NodeElement behaviourNode, TwinTypeEnum twinType, BehaviourTypeEnum behaviourArchetype) {
        LOG.debug(".buildBehaviourRoutes(): Entry, behaviourNode --> {}, subscribedTopics --> {}, behaviourArchetype --> {}", behaviourNode, behaviourArchetype);
        try {
            switch (behaviourArchetype) {
                case STIMULI_BASED_BEHAVIOUR: {
                    LOG.trace(".buildBehaviourRoutes(): Building a STIMULI_BASED_BEHAVIOUR route");
                    switch (twinType) {
                        case BUSINESS_UNIT_TWIN: {
                            BusinessUnitBehaviourFrameworkTemplate newRoute = new BusinessUnitBehaviourFrameworkTemplate(camelctx, behaviourNode.getFriendlyName());
                            LOG.trace(".buildBehaviourRoutes(): BusinessUnitBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case CARE_TEAM_TWIN: {
                            CareTeamBehaviourFrameworkTemplate newRoute = new CareTeamBehaviourFrameworkTemplate(camelctx, behaviourNode.getFriendlyName());
                            LOG.trace(".buildBehaviourRoutes(): CareTeamBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case ORGANIZATION_TWIN: {
                            OrganizationCentricBehaviourFrameworkTemplate newRoute = new OrganizationCentricBehaviourFrameworkTemplate(camelctx, behaviourNode.getFriendlyName());
                            LOG.trace(".buildBehaviourRoutes(): OrganizationCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case ICTSYSTEM_TWIN: {
                            ICTSystemCentricBehaviourFrameworkTemplate newRoute = new ICTSystemCentricBehaviourFrameworkTemplate(camelctx, behaviourNode.getFriendlyName());
                            LOG.trace(".buildBehaviourRoutes(): ICTSystemCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case GROUP_TWIN: {
                            GroupCentricBehaviourFrameworkTemplate newRoute = new GroupCentricBehaviourFrameworkTemplate(camelctx, behaviourNode.getFriendlyName());
                            LOG.trace(".buildBehaviourRoutes(): GroupCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case ENDPOINT_TWIN: {
                            EndpointCentricBehaviourFrameworkTemplate newRoute = new EndpointCentricBehaviourFrameworkTemplate(camelctx, behaviourNode.getFriendlyName());
                            LOG.trace(".buildBehaviourRoutes(): EndpointCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case HEALTHCARE_SERVICE_TWIN: {
                            HealthcareServiceCentricBehaviourFrameworkTemplate newRoute = new HealthcareServiceCentricBehaviourFrameworkTemplate(camelctx, behaviourNode.getFriendlyName());
                            LOG.trace(".buildBehaviourRoutes(): HealthcareServiceCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case LOCATION_TWIN: {
                            LocationCentricBehaviourFrameworkTemplate newRoute = new LocationCentricBehaviourFrameworkTemplate(camelctx, behaviourNode.getFriendlyName());
                            LOG.trace(".buildBehaviourRoutes(): LocationCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case PATIENT_TWIN: {
                            PatientCentricBehaviourFrameworkTemplate newRoute = new PatientCentricBehaviourFrameworkTemplate(camelctx, behaviourNode.getFriendlyName());
                            LOG.trace(".buildBehaviourRoutes(): PatientCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case PERSON_TWIN: {
                            PersonCentricBehaviourFrameworkTemplate newRoute = new PersonCentricBehaviourFrameworkTemplate(camelctx, behaviourNode.getFriendlyName());
                            LOG.trace(".buildBehaviourRoutes(): PersonCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case PRACTITIONER_TWIN: {
                            PractitionerCentricBehaviourFrameworkTemplate newRoute = new PractitionerCentricBehaviourFrameworkTemplate(camelctx, behaviourNode.getFriendlyName());
                            LOG.trace(".buildBehaviourRoutes(): PractitionerCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                        case PRACTITIONER_ROLE_TWIN: {
                            PractitionerRoleCentricBehaviourFrameworkTemplate newRoute = new PractitionerRoleCentricBehaviourFrameworkTemplate(camelctx, behaviourNode.getFriendlyName());
                            LOG.trace(".buildBehaviourRoutes(): PractitionerRoleCentricBehaviourFrameworkTemplate created, now adding it to he CamelContext!");
                            camelctx.addRoutes(newRoute);
                            break;
                        }
                    }
                    LOG.trace(".buildBehaviourRoutes(): Subscribed to Topics, work is done!");
                    break;
                }
                case TIMER_BASED_BEHAVIOUR: {
                    LOG.trace(".buildBehaviourRoutes(): Building a TIMER_BASED_BEHAVIOUR route");
                    TimerBasedBehaviourRouteFrameworkTemplate timerRoute = new TimerBasedBehaviourRouteFrameworkTemplate(camelctx, behaviourNode.getFriendlyName());
                    camelctx.addRoutes(timerRoute);
                    LOG.trace(".buildBehaviourRoutes(): Note, this type of WUP/Route does not subscribe to Topics (it is purely a producer)");
                    break;
                }
            }
        } catch(Exception Ex){
            LOG.error(".buildBehaviourRoutes(): Route install failed! Exception --> {}", Ex);
            // TODO Need to handle this better - potentially failing entire Processing Plant
        }
    }
}
