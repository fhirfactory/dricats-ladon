package net.fhirfactory.pegacorn.ladon.edge.answer.resourceproxies.common;

import net.fhirfactory.pegacorn.datasets.fhir.r4.base.entities.bundle.BundleContentHelper;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.operations.VirtualDBMethodOutcome;
import net.fhirfactory.pegacorn.ladon.processingplant.LadonProcessingPlant;
import net.fhirfactory.pegacorn.ladon.virtualdb.accessors.common.AccessorBase;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

public abstract class LadonEdgeProxyBase {
    boolean isInitialised;

    public LadonEdgeProxyBase(){
        isInitialised = false;
    }

    @Inject
    private LadonProcessingPlant ladonPlant;

    @Inject
    private BundleContentHelper bundleContentHelper;

    protected BundleContentHelper getBundleContentHelper(){
        return(bundleContentHelper);
    }

    @PostConstruct
    private void initialisePatientProxy(){
        if(!this.isInitialised()) {
            getLogger().info("LadonEdgeProxyBase::initialiseProxy(): Entry, Initialising Services");
            getProcessingPlant().initialisePlant();
            getVirtualDBAccessor().initialiseServices();
            this.setInitialised(true);
            getLogger().debug("LadonEdgeProxyBase::initialiseProxy(): Exit");
        }
    }

    public boolean isInitialised() {
        return isInitialised;
    }

    public void setInitialised(boolean initialised) {
        isInitialised = initialised;
    }

    public LadonProcessingPlant getProcessingPlant() {
        return ladonPlant;
    }

    abstract protected Logger getLogger();
    abstract protected AccessorBase specifyVirtualDBAccessor();
    abstract protected VirtualDBMethodOutcome getResource(IdType id);
    abstract protected VirtualDBMethodOutcome createResource(Resource resource);
    abstract protected VirtualDBMethodOutcome updateResource(Resource resource);
    abstract protected VirtualDBMethodOutcome deleteResource(IdType id);

    protected AccessorBase getVirtualDBAccessor(){
        return(specifyVirtualDBAccessor());
    }

    protected Bundle searchProcessHasFailed(String searchMethod){
        getLogger().warn(searchMethod + ": A resource search has failed (not just not finding any matching resources, but the search process itself)");
        Bundle outputBundle = new Bundle();
        outputBundle.setType(Bundle.BundleType.SEARCHSET);
        outputBundle.setTimestamp(Date.from(Instant.now()));
        outputBundle.setTotal(0);
        getLogger().debug("findByIdentifier(): Exit, search failed (not just not finding any matching resources, but the search process itself faield)!");
        return(outputBundle);
    }

}
