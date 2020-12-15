package net.fhirfactory.pegacorn.ladon.statespace.twinpathway.stimulicollector.common;

import net.fhirfactory.pegacorn.datasets.fhir.r4.internal.topics.FHIRElementTopicIDBuilder;
import net.fhirfactory.pegacorn.petasos.model.topics.TopicToken;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWProcessingOutcomeEnum;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TwinStimuliCollectorProcessingBean {

    @Inject
    private FHIRElementTopicIDBuilder fhirTopicIDBuilder;

    public UoW collectForQueueing(UoW incomingUoW, String twinType){
        String version = incomingUoW.getPayloadTopicID().getVersion();
        TopicToken topicId = fhirTopicIDBuilder.createTopicToken("Bundle", version);
        topicId.addDescriminator("Source", "Ladon.StateSpace.StimuliCollector." + twinType );
        UoWPayload outgoingPayload = new UoWPayload();
        outgoingPayload.setPayload(incomingUoW.getIngresContent().getPayload());
        outgoingPayload.setPayloadTopicID(topicId);
        incomingUoW.getEgressContent().addPayloadElement(outgoingPayload);
        incomingUoW.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
        return(incomingUoW);
    }
}
