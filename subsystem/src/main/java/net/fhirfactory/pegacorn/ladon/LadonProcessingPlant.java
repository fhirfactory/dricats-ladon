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
package net.fhirfactory.pegacorn.ladon;

import net.fhirfactory.pegacorn.core.constants.systemwide.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.core.model.topology.role.ProcessingPlantRoleEnum;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.PetasosEnabledSubsystemPropertyFile;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.FHIRElementTopicFactory;
import net.fhirfactory.pegacorn.ladon.configuration.LadonConfigurationFile;
import net.fhirfactory.pegacorn.ladon.configuration.LadonTopologyFactory;
import net.fhirfactory.pegacorn.ladon.interfaces.configuration.LadonDataGridTopologyFactoryInterface;
import net.fhirfactory.pegacorn.processingplant.ProcessingPlant;

import javax.inject.Inject;


public abstract class LadonProcessingPlant extends ProcessingPlant implements LadonDataGridTopologyFactoryInterface {

    @Inject
    private FHIRElementTopicFactory fhirElementTopicFactory;

    @Inject
    private PegacornReferenceProperties pegacornReferenceProperties;

    @Inject
    private LadonTopologyFactory topologyFactory;

    public LadonProcessingPlant(){
        super();

    }

    //
    // Implemented Methods
    //

    @Override
    public PetasosEnabledSubsystemPropertyFile getPropertyFile() {
        LadonConfigurationFile ladonConfigurationFile = (LadonConfigurationFile)topologyFactory.getPropertyFile();
        return (ladonConfigurationFile);
    }

    @Override
    public ProcessingPlantRoleEnum getProcessingPlantCapability() {
        return (ProcessingPlantRoleEnum.PETASOS_SERVICE_PROVIDER_DIGITAL_TWIN_WORKFLOW);
    }
}