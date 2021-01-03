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

package net.fhirfactory.pegacorn.ladon.behaviours.framework.worker.twintypecentrc;

import net.fhirfactory.pegacorn.ladon.behaviours.framework.worker.common.BehaviourEgressConduit;
import net.fhirfactory.pegacorn.ladon.model.outcomes.OutcomeSet;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.orchestrator.CareTeamTwinOrchestrator;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.orchestrator.PersonTwinOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * @author Mark A. Hunter
 * @since 2020-09-20
 */

@Dependent
public class PersonCentricEgressConduit extends BehaviourEgressConduit {
    private static final Logger LOG = LoggerFactory.getLogger(PersonCentricEgressConduit.class);

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Inject
    PersonTwinOrchestrator orchestrator;

    /**
     * This function does the post-Behaviour-Stimulus-Processing processing.
     *
     * @param outcomes The incoming Stimulus (Stimumus) received as output from the actual Work Unit Processor (Business Logic)
     * @return A Stimulus object for injecting into other Behaviours
     */
    public String extractOutcomes(OutcomeSet outcomes) {
        LOG.debug(".extractOutcomes(): Entry, outcomes (OutcomeSet) --> {}", outcomes);

        orchestrator.registerBehaviourCompletion(outcomes);

        return (null);
    }
}