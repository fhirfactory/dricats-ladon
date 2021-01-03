/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.ladon.statespace.twinpathway.stimulusbased.encapsulatorroutes.common;

import net.fhirfactory.pegacorn.camel.BaseRouteBuilder;
import net.fhirfactory.pegacorn.common.model.FDN;
import net.fhirfactory.pegacorn.common.model.RDN;
import net.fhirfactory.pegacorn.datasets.fhir.r4.internal.topics.FHIRElementTopicIDBuilder;
import net.fhirfactory.pegacorn.deployment.names.PegacornLadonComponentNames;
import net.fhirfactory.pegacorn.deployment.topology.manager.DeploymentTopologyIM;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.orchestrator.common.TwinOrchestratorBase;
import net.fhirfactory.pegacorn.petasos.core.moa.brokers.PetasosMOAServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.datasets.manager.TopicIM;
import net.fhirfactory.pegacorn.petasos.model.processingplant.ProcessingPlantServicesInterface;
import net.fhirfactory.pegacorn.petasos.model.topics.TopicToken;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElement;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementFunctionToken;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementIdentifier;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPIdentifier;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class TwinTypeBaseBehaviourEncapsulatorRouteWUP extends BaseRouteBuilder{
    abstract protected Logger getLogger();

    private NodeElement topologyNode;
    private WUPJobCard wupInstanceJobCard;
    private RouteElementNames nameSet;

    @Inject
    private PetasosMOAServicesBroker servicesBroker;

    @Inject
    private DeploymentTopologyIM wupTopologyProxy;

    @Inject
    private PegacornLadonComponentNames subsystemNames;

    @Inject
    private FHIRElementTopicIDBuilder fhirTopicIDBuilder;

    @Inject
    private ProcessingPlantServicesInterface processingPlantServices;
    
    @Inject 
    private TopicIM topicServer;

    public TwinTypeBaseBehaviourEncapsulatorRouteWUP() {
        super();
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
        getLogger().trace(".initialise(): wupInstanceName --> {}", this.getWUPInstanceName());
        getLogger().trace(".initialise(): wupInstanceVersion --> {}", this.getWUPVersion());
//        getLogger().trace(".iniitalise(): wupFunctionToken --> {}", this.getWUPFunctionToken());
        getLogger().trace(".initialise(): Setting if the WUP uses the Petasos generated Ingres/Egress Endpoints");
        getLogger().trace(".initialise(): Setting up the wupTopologyElement (NodeElement) instance, which is the Topology Server's representation of this WUP ");
        buildWUPNodeElement();
        getLogger().trace(".initialise(): Setting the WUP nameSet, which is the set of Route EndPoints that the WUP Framework will use to link various enablers");
        nameSet = new RouteElementNames(getWUPFunctionToken());
        getLogger().trace(".initialise(): Now call the WUP Framework constructure - which builds the Petasos framework around this WUP");
        buildWUPFramework(this.getContext());
        getLogger().trace(".initialise(): Now invoking subclass initialising function(s)");
        executePostInitialisationActivities();
        getLogger().debug(".initialise(): Exit");
    }

    // To be implemented methods (in Specialisations)

    abstract protected String specifyTwinTypeName();
    abstract protected String specifyTwinTypeVersion();
    abstract protected TwinOrchestratorBase getOrchestrator();
    
    public String getWUPInstanceName() {
    	return(specifyTwinTypeName());
    }
    
    public String getWUPVersion() {
    	return(specifyTwinTypeVersion());
    }
    
    private String getWUPWorkshopName() {
    	return("StateSpace");
    }
    
    
    public WUPIdentifier getWUPIdentifier() {
    	WUPIdentifier wupID = new WUPIdentifier(this.getWUPTopologyNodeElement().getNodeInstanceID());
    	return(wupID);
    }

    protected void executePostInitialisationActivities(){
        // Subclasses can optionally override
    }

    public void registerNodeInstantiation(){
        getLogger().debug(".registerTopologyElementInstantiation(): Entry");
//        getOrchestrator().reg(this.topologyNode);
        getLogger().debug(".registerTopologyElementInstantiation(): Exit");
    }

    public void buildWUPFramework(CamelContext routeContext) {
        getLogger().debug(".buildWUPFramework(): Entry");
        // By default, the set of Topics this WUP subscribes to will be empty - as we need to the Behaviours to initialise first to tell us.
        Set<TopicToken> emptyTopicList = new HashSet<TopicToken>();
        servicesBroker.registerWorkUnitProcessor(this.topologyNode, emptyTopicList, this.getWUPArchetype());
        getLogger().debug(".buildWUPFramework(): Exit");
    }

    public NodeElementFunctionToken getWUPFunctionToken() {
        return (this.getWUPTopologyNodeElement().getNodeFunctionToken());
    }

    public String ingresFeed() {
        return (getRouteElementNameSet().getEndPointWUPContainerIngresProcessorIngres());
    }

    public String egressFeed() {
        return ("BeanDriven-DynamicRouter");
    }

    public PetasosMOAServicesBroker getServicesBroker(){
        return(this.servicesBroker);
    }

    public DeploymentTopologyIM getTopologyServer(){
        return(this.wupTopologyProxy);
    }

    public NodeElement getWUPTopologyNodeElement() {
        return topologyNode;
    }

    public void setWUPTopologyNodeElement(NodeElement wupTopologyNodeElement) {
        this.topologyNode = wupTopologyNodeElement;
    }

    public RouteElementNames getRouteElementNameSet() {
        return nameSet;
    }

    public WUPArchetypeEnum getWUPArchetype() {
        return (WUPArchetypeEnum.WUP_NATURE_LADON_BEHAVIOUR_WRAPPER);
    }

    public PegacornLadonComponentNames getSubsystemComponentNamesService(){
        return(this.subsystemNames);
    }

    public FHIRElementTopicIDBuilder getFHIRTopicIDBuilder(){
        return(this.fhirTopicIDBuilder);
    }
    
    public void requestSubscription(List<TopicToken> tokenList) {
    	
    }

    private void buildWUPNodeElement(){
        getLogger().debug(".buildWUPNodeElement(): Entry, Workshop --> {}", getWUPWorkshopName());
        NodeElement workshopNode = processingPlantServices.getWorkshop(getWUPWorkshopName());
        getLogger().trace(".buildWUPNodeElement(): Entry, Workshop NodeElement--> {}", workshopNode);
        NodeElement newWUPNode = new NodeElement();
        getLogger().trace(".buildWUPNodeElement(): Create new FDN/Identifier for WUP");
        FDN newWUPNodeFDN = new FDN(workshopNode.getNodeInstanceID());
        newWUPNodeFDN.appendRDN(new RDN(NodeElementTypeEnum.WUP.getNodeElementType(), getWUPInstanceName()));
        NodeElementIdentifier newWUPNodeID = new NodeElementIdentifier(newWUPNodeFDN.getToken());
        getLogger().trace(".buildWUPNodeElement(): WUP NodeIdentifier --> {}", newWUPNodeID);
        newWUPNode.setNodeInstanceID(newWUPNodeID);
        getLogger().trace(".buildWUPNodeElement(): Create new Function Identifier for WUP");
        FDN newWUPNodeFunctionFDN = new FDN(workshopNode.getNodeFunctionID());
        newWUPNodeFunctionFDN.appendRDN(new RDN(NodeElementTypeEnum.WUP.getNodeElementType(), getWUPInstanceName()));
        getLogger().trace(".buildWUPNodeElement(): WUP Function Identifier --> {}", newWUPNodeFunctionFDN.getToken());
        newWUPNode.setNodeFunctionID(newWUPNodeFunctionFDN.getToken());
        newWUPNode.setVersion(getWUPVersion());
        newWUPNode.setConcurrencyMode(workshopNode.getConcurrencyMode());
        newWUPNode.setResilienceMode(workshopNode.getResilienceMode());
        newWUPNode.setInstanceInPlace(true);
        newWUPNode.setNodeArchetype(NodeElementTypeEnum.WUP);
        NodeElement processingPlantNode = processingPlantServices.getProcessingPlantNodeElement();
        getLogger().trace(".buildWUPNodeElement(): parent ProcessingPlant --> {}", processingPlantNode);
        this.getTopologyServer().registerNode(newWUPNode);
        getLogger().debug(".buildWUPNodeElement(): Node Registered --> {}", newWUPNode);
        this.getTopologyServer().addContainedNodeToNode(workshopNode.getNodeInstanceID(), newWUPNode);
        this.setWUPTopologyNodeElement(newWUPNode);
        getOrchestrator().registerEncapsulatorWUPNode(newWUPNode);
        this.topologyNode = newWUPNode;
    }
    
    
    public void subscribeToTopics(Set<TopicToken> subscribedTopics){
        getLogger().debug(".uowTopicSubscribe(): Entry, subscribedTopics --> {}, wupNode --> {}", subscribedTopics, getWUPIdentifier() );
        if(subscribedTopics.isEmpty()){
        	getLogger().debug(".uowTopicSubscribe(): Not topics provided as input, exiting");
            return;
        }
        NodeElementFunctionToken wupFunctionToken = new NodeElementFunctionToken();
        wupFunctionToken.setFunctionID(getWUPTopologyNodeElement().getNodeFunctionID());
        wupFunctionToken.setVersion(getWUPTopologyNodeElement().getVersion());
        Iterator<TopicToken> topicIterator = subscribedTopics.iterator();
        while(topicIterator.hasNext()) {
            TopicToken currentTopicID = topicIterator.next();
            getLogger().trace(".uowTopicSubscribe(): wupNode --> {} is subscribing to UoW Content Topic --> {}", wupFunctionToken, currentTopicID);
            topicServer.addTopicSubscriber(currentTopicID, getWUPTopologyNodeElement().getNodeInstanceID() );
        }
        getLogger().debug(".uowTopicSubscribe(): Exit");
    }
    
 
}
