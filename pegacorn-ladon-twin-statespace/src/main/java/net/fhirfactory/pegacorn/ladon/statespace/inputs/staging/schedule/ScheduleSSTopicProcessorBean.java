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
package net.fhirfactory.pegacorn.ladon.statespace.inputs.staging.schedule;

import net.fhirfactory.pegacorn.datasets.fhir.r4.internal.topics.FHIRElementTopicIDBuilder;
import net.fhirfactory.pegacorn.petasos.model.topics.TopicToken;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWProcessingOutcomeEnum;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ScheduleSSTopicProcessorBean {
    @Inject
    private FHIRElementTopicIDBuilder fhirTopicIDBuilder;

    /**
     * This functions does nothing to the actual incoming (Ingres) payload - merely copying it to the Egress payload
     * and assigning the processing outcome as a Success. It also modifies the TopicID of the Egress payload.
     * @param incomingUoW The incoming UoW of work
     * @return A unit of work with the Egress Content matching the Ingres Content and a descriminator --> "Source":"Ladon.StateSpace.PubSub"
     */
    public UoW toPubSub(UoW incomingUoW){
        String version = incomingUoW.getPayloadTopicID().getVersion();
        TopicToken topicId = fhirTopicIDBuilder.createTopicToken("Schedule", version);
        topicId.addDescriminator("Source", "Ladon.StateSpace.PubSub" );
        UoWPayload outgoingPayload = new UoWPayload();
        outgoingPayload.setPayload(incomingUoW.getIngresContent().getPayload());
        outgoingPayload.setPayloadTopicID(topicId);
        incomingUoW.getEgressContent().addPayloadElement(outgoingPayload);
        incomingUoW.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
        return(incomingUoW);
    }
}
