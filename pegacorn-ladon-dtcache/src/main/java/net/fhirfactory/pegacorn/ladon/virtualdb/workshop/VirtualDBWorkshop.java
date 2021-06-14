package net.fhirfactory.pegacorn.ladon.virtualdb.workshop;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.workshops.base.PetasosEnabledWorkshop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VirtualDBWorkshop extends PetasosEnabledWorkshop {
    private static final Logger LOG = LoggerFactory.getLogger(VirtualDBWorkshop.class);

    private static String DTCACHE_WORKSHOP_NAME = "VirtualDB";
    private static String DTCACHE_WORKSHOP_VERSION = "1.0.0";

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyWorkshopName() {
        return (DTCACHE_WORKSHOP_NAME);
    }

    @Override
    protected String specifyWorkshopVersion() {
        return (DTCACHE_WORKSHOP_VERSION);
    }

    @Override
    protected TopologyNodeTypeEnum specifyWorkshopType() {
        return (TopologyNodeTypeEnum.WORKSHOP);
    }

    @Override
    protected void invokePostConstructInitialisation() {

    }


}
