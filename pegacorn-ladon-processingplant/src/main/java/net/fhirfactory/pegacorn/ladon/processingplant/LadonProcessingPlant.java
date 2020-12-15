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
package net.fhirfactory.pegacorn.ladon.processingplant;

import net.fhirfactory.pegacorn.common.model.FDN;
import net.fhirfactory.pegacorn.common.model.RDN;
import net.fhirfactory.pegacorn.ladon.mdr.fhirplace.FHIRPlaceMDRWorkshop;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElement;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementFunctionToken;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementIdentifier;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementTypeEnum;
import net.fhirfactory.pegacorn.processingplatform.CoreInternalSubsystemProcessingPlatform;
import net.fhirfactory.pegacorn.processingplatform.common.StandardProcessingPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


public abstract class LadonProcessingPlant extends CoreInternalSubsystemProcessingPlatform {

    /**
     * This class creates the Processing Plant "Workshops" for Ladon. "Workshops" are used to segregate
     * the processing functionality (much like packaging) and support ease of integration and monitoring.
     * 
     * The Workshops are presented as part of the Watchdog service and collate/aggregate information for
     * performance and usage metrics.
     * 
     * 
     * There are five Workshops in Ladon:
     * 		1. DTCache
     * 		2. Edge
     * 		3. StateSpace
     * 		4. MasterDataRepositoryServices
     * 		5. Behaviours
     */
    @Override
    protected void buildProcessingPlantWorkshops() {
        getLogger().debug(".buildLadonWorkshops(): Entry");
        getLogger().trace(".buildLadonWorkshops(): 1st, the VirtualDB!");
        if(getLogger().isTraceEnabled()) {
            getLogger().trace(".buildLadonWorkshops(): ProcessingPlant Identifier --> {}", this.getProcessingPlantNodeId());
            getLogger().trace(".buildLadonWorkshops(): ProcessingPlant NodeElement --> {}", this.getProcessingPlantNodeElement());
        }
        FDN virtualDBFDN = new FDN(this.getProcessingPlantNodeId());
        virtualDBFDN.appendRDN(new RDN(NodeElementTypeEnum.WORKSHOP.getNodeElementType(), "VirtualDB"));
        NodeElementIdentifier dtcacheId = new NodeElementIdentifier(virtualDBFDN.getToken());
        NodeElement virtualDB = new NodeElement();
        virtualDB.setVersion(getVersion());
        virtualDB.setNodeInstanceID(dtcacheId);
        FDN dtcacheFunctionFDN = new FDN(this.getProcessingPlantNodeElement().getNodeFunctionID());
        dtcacheFunctionFDN.appendRDN(new RDN(NodeElementTypeEnum.WORKSHOP.getNodeElementType(), "VirtualDB"));
        virtualDB.setNodeFunctionID(dtcacheFunctionFDN.getToken());
        virtualDB.setConcurrencyMode(this.getProcessingPlantNodeElement().getConcurrencyMode());
        virtualDB.setResilienceMode(this.getProcessingPlantNodeElement().getResilienceMode());
        virtualDB.setInstanceInPlace(true);
        virtualDB.setContainingElementID(this.getProcessingPlantNodeId());
        this.getDeploymentIM().registerNode(virtualDB);
        this.getDeploymentIM().addContainedNodeToNode(this.getProcessingPlantNodeId(),virtualDB);

        getLogger().trace(".buildLadonWorkshops(): 2nd, the Edge");
        FDN edgeFDN = new FDN(this.getProcessingPlantNodeId());
        edgeFDN.appendRDN(new RDN(NodeElementTypeEnum.WORKSHOP.getNodeElementType(), "Edge"));
        NodeElementIdentifier edgeId = new NodeElementIdentifier(edgeFDN.getToken());
        NodeElement edge = new NodeElement();
        edge.setVersion(getVersion());
        edge.setNodeInstanceID(edgeId);
        FDN edgeFunctionFDN = new FDN(this.getProcessingPlantNodeElement().getNodeFunctionID());
        edgeFunctionFDN.appendRDN(new RDN(NodeElementTypeEnum.WORKSHOP.getNodeElementType(), "Edge"));
        edge.setNodeFunctionID(edgeFunctionFDN.getToken());
        edge.setConcurrencyMode(this.getProcessingPlantNodeElement().getConcurrencyMode());
        edge.setResilienceMode(this.getProcessingPlantNodeElement().getResilienceMode());
        edge.setInstanceInPlace(true);
        edge.setContainingElementID(this.getProcessingPlantNodeId());
        this.getDeploymentIM().registerNode(edge);
        this.getDeploymentIM().addContainedNodeToNode(this.getProcessingPlantNodeId(),edge);

        getLogger().trace(".buildLadonWorkshops(): 3nd, the StateSpace");
        FDN ssFDN = new FDN(this.getProcessingPlantNodeId());
        ssFDN.appendRDN(new RDN(NodeElementTypeEnum.WORKSHOP.getNodeElementType(), "StateSpace"));
        NodeElementIdentifier statespaceIdentifier = new NodeElementIdentifier(ssFDN.getToken());
        NodeElement statespace = new NodeElement();
        statespace.setVersion(getVersion());
        statespace.setNodeInstanceID(statespaceIdentifier);
        FDN ssFunctionFDN = new FDN(this.getProcessingPlantNodeElement().getNodeFunctionID());
        ssFunctionFDN.appendRDN(new RDN(NodeElementTypeEnum.WORKSHOP.getNodeElementType(), "StateSpace"));
        statespace.setNodeFunctionID(ssFunctionFDN.getToken());
        statespace.setConcurrencyMode(this.getProcessingPlantNodeElement().getConcurrencyMode());
        statespace.setResilienceMode(this.getProcessingPlantNodeElement().getResilienceMode());
        statespace.setInstanceInPlace(true);
        statespace.setContainingElementID(this.getProcessingPlantNodeId());
        this.getDeploymentIM().registerNode(statespace);
        this.getDeploymentIM().addContainedNodeToNode(this.getProcessingPlantNodeId(),statespace);

        getLogger().trace(".buildLadonWorkshops(): 4th, the MDR");
        FDN mdrFDN = new FDN(this.getProcessingPlantNodeId());
        mdrFDN.appendRDN(new RDN(NodeElementTypeEnum.WORKSHOP.getNodeElementType(), "MasterDataRepositoryServices"));
        NodeElementIdentifier mdrIdentifier = new NodeElementIdentifier(mdrFDN.getToken());
        NodeElement mdr = new NodeElement();
        mdr.setVersion(getVersion());
        mdr.setNodeInstanceID(mdrIdentifier);
        FDN mdrFunctionFDN = new FDN(this.getProcessingPlantNodeElement().getNodeFunctionID());
        mdrFunctionFDN.appendRDN(new RDN(NodeElementTypeEnum.WORKSHOP.getNodeElementType(), "MasterDataRepositoryServices"));
        mdr.setNodeFunctionID(mdrFunctionFDN.getToken());
        mdr.setConcurrencyMode(this.getProcessingPlantNodeElement().getConcurrencyMode());
        mdr.setResilienceMode(this.getProcessingPlantNodeElement().getResilienceMode());
        mdr.setInstanceInPlace(true);
        mdr.setContainingElementID(this.getProcessingPlantNodeId());
        this.getDeploymentIM().registerNode(mdr);
        this.getDeploymentIM().addContainedNodeToNode(this.getProcessingPlantNodeId(),mdr);

        getLogger().trace(".buildLadonWorkshops(): 5th, the Behaviours");
        FDN behavioursFDN = new FDN(this.getProcessingPlantNodeId());
        behavioursFDN.appendRDN(new RDN(NodeElementTypeEnum.WORKSHOP.getNodeElementType(), "Behaviours"));
        NodeElementIdentifier behavioursIdentifier = new NodeElementIdentifier(behavioursFDN.getToken());
        NodeElement behaviours = new NodeElement();
        behaviours.setVersion(getVersion());
        behaviours.setNodeInstanceID(behavioursIdentifier);
        FDN behaivoursFunctioNFDN = new FDN(this.getProcessingPlantNodeElement().getNodeFunctionID());
        behaivoursFunctioNFDN.appendRDN(new RDN(NodeElementTypeEnum.WORKSHOP.getNodeElementType(), "Behaviours"));
        behaviours.setNodeFunctionID(behaivoursFunctioNFDN.getToken());
        behaviours.setConcurrencyMode(this.getProcessingPlantNodeElement().getConcurrencyMode());
        behaviours.setResilienceMode(this.getProcessingPlantNodeElement().getResilienceMode());
        behaviours.setInstanceInPlace(true);
        behaviours.setContainingElementID(this.getProcessingPlantNodeId());
        this.getDeploymentIM().registerNode(behaviours);
        this.getDeploymentIM().addContainedNodeToNode(this.getProcessingPlantNodeId(),behaviours);

        getLogger().debug(".buildLadonWorkshops(): Exit");
    }
}
