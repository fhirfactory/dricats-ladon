package net.fhirfactory.pegacorn.ladon.behaviours.archetypes.common;

import net.fhirfactory.pegacorn.camel.BaseRouteBuilder;
import net.fhirfactory.pegacorn.common.model.FDN;
import net.fhirfactory.pegacorn.common.model.RDN;
import net.fhirfactory.pegacorn.deployment.topology.manager.DeploymentTopologyIM;
import net.fhirfactory.pegacorn.ladon.behaviours.framework.manager.BehaviourRouteManager;
import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourRouteNames;
import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourTypeEnum;
import net.fhirfactory.pegacorn.ladon.model.twin.TwinTypeEnum;
import net.fhirfactory.pegacorn.ladon.processingplant.LadonProcessingPlant;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.orchestrator.common.TwinOrchestratorBase;
import net.fhirfactory.pegacorn.petasos.datasets.manager.TopicIM;
import net.fhirfactory.pegacorn.petasos.model.topics.TopicToken;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElement;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementIdentifier;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementTypeEnum;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public abstract class GenericBehaviour extends BaseRouteBuilder {
    protected abstract Logger getLogger();

    private static final String BEHAVIOUR_WORKSHOP = "Behaviours";
    private NodeElement behaviourNodeElement;
    private BehaviourRouteNames nameSet;

    abstract protected String specifyBehaviourName();
    abstract protected String specifyBehaviourVersion();
    abstract protected TwinOrchestratorBase getMyTwinOrchestrationService();
    abstract protected BehaviourTypeEnum specifyBehaviourType();
    abstract protected void executePostInitialisationActivities();
    abstract protected TwinTypeEnum specifyTwinType();

    @Inject
    private BehaviourRouteManager routeManager;

    @Inject
    private LadonProcessingPlant ladonPlant;

    @Inject
    private DeploymentTopologyIM topologyServer;

    public String getBehaviourName(){
        return(specifyBehaviourName());
    }

    public String getBehaviourVersion(){
        return(specifyBehaviourVersion());
    }

    public String getBehaviourWorkshop(){
        return(BEHAVIOUR_WORKSHOP);
    }

    public DeploymentTopologyIM getTopologyServer(){
        return(topologyServer);
    }

    public NodeElement getBehaviourNodeElement(){
        return(this.behaviourNodeElement);
    }

    public TwinTypeEnum getTwinType(){return(specifyTwinType());}

    public BehaviourTypeEnum getBehaviourType(){
        return(specifyBehaviourType());
    }


    /**
     * This function essentially establishes the WUP itself, by first calling all the (abstract classes realised within subclasses)
     * and setting the core attributes of the WUP. Then, it executes the buildWUPFramework() function, which invokes the Petasos
     * framework around this WUP.
     *
     * It is automatically called by the CDI framework following Constructor invocation (see @PostConstruct tag).
     */
    @PostConstruct
    protected void initialise(){
        getLogger().debug(".initialise(): Entry, Default Post Constructor function to setup the WUP");
        getLogger().trace(".initialise(): BehaviourName --> {}", this.specifyBehaviourName());
        getLogger().trace(".initialise(): BehaviourVersion --> {}", this.specifyBehaviourVersion());
        getLogger().trace(".initialise(): Setting up the wupTopologyElement (NodeElement) instance, which is the Topology Server's representation of this WUP ");
        buildBehaviourNodeElement();
        getLogger().trace(".initialise(): Setting the WUP nameSet, which is the set of Route EndPoints that the WUP Framework will use to link various enablers");
        nameSet = new BehaviourRouteNames(this.specifyBehaviourName());
        getLogger().trace(".initialise(): Now call the WUP Framework constructure - which builds the Petasos framework around this WUP");
        buildBehaviourFramework(this.getContext());
        getLogger().trace(".initialise(): Now invoking subclass initialising function(s)");
        executePostInitialisationActivities();
        getLogger().debug(".initialise(): Exit");
    }

    private void buildBehaviourNodeElement(){
        getLogger().debug(".buildBehaviourNodeElement(): Entry, Workshop --> {}", getBehaviourName());
        NodeElement workshopNode = ladonPlant.getWorkshop(getBehaviourWorkshop());
        getLogger().trace(".buildBehaviourNodeElement(): Entry, Workshop NodeElement--> {}", workshopNode);
        NodeElement newWUPNode = new NodeElement();
        getLogger().trace(".buildBehaviourNodeElement(): Create new FDN/Identifier for WUP");
        FDN newWUPNodeFDN = new FDN(workshopNode.getNodeInstanceID());
        newWUPNodeFDN.appendRDN(new RDN(NodeElementTypeEnum.WUP.getNodeElementType(), getBehaviourName()));
        NodeElementIdentifier newWUPNodeID = new NodeElementIdentifier(newWUPNodeFDN.getToken());
        getLogger().trace(".buildBehaviourNodeElement(): WUP NodeIdentifier --> {}", newWUPNodeID);
        newWUPNode.setNodeInstanceID(newWUPNodeID);
        getLogger().trace(".buildBehaviourNodeElement(): Create new Function Identifier for WUP");
        FDN newWUPNodeFunctionFDN = new FDN(workshopNode.getNodeFunctionID());
        newWUPNodeFunctionFDN.appendRDN(new RDN(NodeElementTypeEnum.WUP.getNodeElementType(), getBehaviourName()));
        getLogger().trace(".buildBehaviourNodeElement(): WUP Function Identifier --> {}", newWUPNodeFunctionFDN.getToken());
        newWUPNode.setNodeFunctionID(newWUPNodeFunctionFDN.getToken());
        newWUPNode.setVersion(getBehaviourVersion());
        newWUPNode.setConcurrencyMode(workshopNode.getConcurrencyMode());
        newWUPNode.setResilienceMode(workshopNode.getResilienceMode());
        newWUPNode.setFriendlyName(getBehaviourName());
        newWUPNode.setInstanceInPlace(true);
        newWUPNode.setNodeArchetype(NodeElementTypeEnum.WUP);
        NodeElement processingPlantNode = ladonPlant.getProcessingPlantNodeElement();
        getLogger().trace(".buildWUPNodeElement(): parent ProcessingPlant --> {}", processingPlantNode);
        this.getTopologyServer().registerNode(newWUPNode);
        getLogger().debug(".buildWUPNodeElement(): Node Registered --> {}", newWUPNode);
        this.getTopologyServer().addContainedNodeToNode(workshopNode.getNodeInstanceID(), newWUPNode);
        this.behaviourNodeElement = newWUPNode;
    }

    public void buildBehaviourFramework(CamelContext routeContext) {
        getLogger().debug(".buildBehaviourFramework(): Entry");
        // By default, the set of Topics this WUP subscribes to will be empty - as we need to the Behaviours to initialise first to tell us.
        Set<TopicToken> emptyTopicList = new HashSet<TopicToken>();
        routeManager.buildBehaviourRoutes(this.getBehaviourNodeElement(), this.getTwinType(), this.getBehaviourType());
        getLogger().debug(".buildBehaviourFramework(): Exit");
    }

    protected String ingresFeed(){
        return(nameSet.getBehaviourIngresPoint());
    }

    protected String egressFeed(){
        return(nameSet.getBehaviourEgressPoint());
    }
}
