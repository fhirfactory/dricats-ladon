package net.fhirfactory.pegacorn.ladon.edge.forward.common;

import net.fhirfactory.pegacorn.ladon.model.twin.DigitalTwinIdentifier;
import net.fhirfactory.pegacorn.petasos.ipc.beans.sender.InterProcessingPlantHandoverFinisherBean;
import net.fhirfactory.pegacorn.petasos.ipc.beans.sender.InterProcessingPlantHandoverPacketEncoderBean;
import net.fhirfactory.pegacorn.petasos.ipc.beans.sender.InterProcessingPlantHandoverPacketGenerationBean;
import net.fhirfactory.pegacorn.petasos.ipc.beans.sender.InterProcessingPlantHandoverPacketResponseDecoder;
import net.fhirfactory.pegacorn.petasos.model.topology.EndpointElement;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElement;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementTypeEnum;
import net.fhirfactory.pegacorn.petasos.wup.archetypes.EdgeEgressMessagingGatewayWUP;
import net.fhirfactory.pegacorn.platform.edge.forward.common.EdgeIPCForwarderWUPTemplate;
import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;

import javax.inject.Inject;

public abstract class LadonEdgeForwarderWUPTemplate extends EdgeEgressMessagingGatewayWUP {

    @Inject
    CamelContext camelCTX;

//    @Inject
//    TwinInstance2EdgeForwarderMap twinInstance2ForwarderMap;

    @Override
    public void configure() throws Exception {
        getLogger().info("LadonEdgeForwarderWUPTemplate :: WUPIngresPoint/ingresFeed --> {}", this.ingresFeed());
        getLogger().info("LadonEdgeForwarderWUPTemplate :: WUPEgressPoint/egressFeed --> {}", this.egressFeed());

        fromWithStandardExceptionHandling(ingresFeed())
                .routeId(getNameSet().getRouteCoreWUP())
                .log(LoggingLevel.INFO, "Incoming Raw Message --> ${body}")
                .bean(InterProcessingPlantHandoverPacketGenerationBean.class, "constructInterProcessingPlantHandoverPacket(*,  Exchange," + this.getWupTopologyNodeElement().extractNodeKey() + ")")
                .bean(InterProcessingPlantHandoverPacketEncoderBean.class, "handoverPacketEncode")
                .to(specifyEgressEndpoint())
                .transform(simple("${bodyAs(String)}"))
                .log(LoggingLevel.INFO, "Response Raw Message --> ${body}")
                .bean(InterProcessingPlantHandoverPacketResponseDecoder.class, "contextualiseInterProcessingPlantHandoverResponsePacket(*,  Exchange," + this.getWupTopologyNodeElement().extractNodeKey() + ")")
                .bean(InterProcessingPlantHandoverFinisherBean.class, "ipcSenderNotifyActivityFinished(*, Exchange," + this.getWupTopologyNodeElement().extractNodeKey() + ")");
    }

    protected abstract String specifyTargetSubsystem();
    protected abstract String specifyTargetSubsystemVersion();
    protected abstract String specifyTargetEndpointName();
    protected abstract String specifyTargetEndpointVersion();
    protected abstract String specifyTargetService();
    protected abstract String specifyTargetProcessingPlant();
//    protected abstract DigitalTwinIdentifier specifyDigitalTwin();

    @Override
    protected String deriveTargetEndpointDetails(){
        getLogger().debug(".deriveTargetEndpointDetails(): Entry");
        getLogger().trace(".deriveTargetEndpointDetails(): Target Subsystem Name --> {}, Target Subsystem Version --> {}", specifyTargetSubsystem(), specifyTargetSubsystemVersion());
        NodeElement targetSubsystem = getTopologyServer().getNode(specifyTargetSubsystem(), NodeElementTypeEnum.SUBSYSTEM, specifyTargetSubsystemVersion());
        getLogger().trace(".deriveTargetEndpointDetails(): Target Subsystem (NodeElement) --> {}", targetSubsystem);
        NodeElement targetNode;
        switch(targetSubsystem.getResilienceMode()){
            case RESILIENCE_MODE_MULTISITE:
            case RESILIENCE_MODE_KUBERNETES_MULTISITE:
            case RESILIENCE_MODE_CLUSTERED:
            case RESILIENCE_MODE_KUBERNETES_CLUSTERED:
            case RESILIENCE_MODE_KUBERNETES_STANDALONE:{
                getLogger().trace(".deriveTargetEndpointDetails(): Running in Multisite, Clustered or Kubernetes mode");
                targetNode = getTopologyServer().getNode(specifyTargetService(), NodeElementTypeEnum.SERVICE, specifyTargetSubsystemVersion());
                break;
            }
            case RESILIENCE_MODE_STANDALONE:
            default:{
                getLogger().trace(".deriveTargetEndpointDetails(): Running in Standalone mode");
                targetNode = getTopologyServer().getNode(specifyTargetProcessingPlant(), NodeElementTypeEnum.PROCESSING_PLANT, specifyTargetSubsystemVersion());
            }
        }
        getLogger().trace(".deriveTargetEndpointDetails(): targetNode --> {}", targetNode);
        getLogger().trace(".deriveTargetEndpointDetails(): targetEndpointName --> {}, targetEndpointVersion --> {}", specifyTargetEndpointName(), specifyTargetEndpointVersion());
        EndpointElement endpoint = getTopologyServer().getEndpoint(targetNode, specifyTargetEndpointName(), specifyTargetEndpointVersion());
        getLogger().trace(".deriveTargetEndpointDetails(): targetEndpoint (EndpointElement) --> {}", endpoint);
        String endpointDetails = endpoint.getHostname() + ":" + endpoint.getExposedPort();
        getLogger().debug(".deriveTargetEndpointDetails(): Exit, endpointDetails --> {}", endpointDetails);
        return(endpointDetails);
    }

    @Override
    protected String specifyWUPWorkshop(){
        return("Edge");
    }

    @Override
    protected void executePostInitialisationActivities(){
        EdgeIPCForwarderWUPTemplate.executePostInitialisationActivities(camelCTX);
//        twinInstance2ForwarderMap.addForwarderAssociation2DigitalTwin(specifyTargetSubsystem(), specifyDigitalTwin());
    }
}
