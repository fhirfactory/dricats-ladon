package net.fhirfactory.pegacorn.ladon.mdr.fhirplace.accessor.common;

import net.fhirfactory.pegacorn.deployment.names.subsystems.FHIRPlaceIMComponentNames;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.fhirplace.dm.FHIRPlaceMDRDMPropertyFile;
import net.fhirfactory.pegacorn.platform.edge.ask.InternalFHIRClientServices;

import javax.inject.Inject;

public abstract class FHIRPlaceMDRDefaultAccessor extends InternalFHIRClientServices {
    @Inject
    private FHIRPlaceIMComponentNames mdrComponentNames;

    @Inject
    private FHIRPlaceMDRDMPropertyFile fhirplaceProperties;

    @Override
    protected String specifyFHIRServerService() {
        return (fhirplaceProperties.getSubsystemInstant().getClusterServiceName());
    }

    @Override
    protected String specifyFHIRServerSubsystemName() {
        return (fhirplaceProperties.getSubsystemInstant().getSubsystemName());
    }

    @Override
    protected String specifyFHIRServerSubsystemVersion() {
        return (fhirplaceProperties.getSubsystemInstant().getSubsystemVersion());
    }

    @Override
    protected String specifyRequiredInterfaceName() {
        return ("FHIR");
    }

    @Override
    protected String specifyRequiredInterfaceVersion() {
        return ("4.0.1");
    }
}
