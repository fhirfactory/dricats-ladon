package net.fhirfactory.pegacorn.ladon.processingplant;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.workshops.base.PetasosEnabledWorkshop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MDRRepositoryServicesWorkshop extends PetasosEnabledWorkshop {
    private static final Logger LOG = LoggerFactory.getLogger(MDRRepositoryServicesWorkshop.class);

    private static String MDR_REPOSITORY_SERVICE_WORKSHOP_NAME = "VirtualDB";
    private static String MDR_REPOSITORY_SERVICE_WORKSHOP_VERSION = "1.0.0";

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyWorkshopName() {
        return (MDR_REPOSITORY_SERVICE_WORKSHOP_NAME);
    }

    @Override
    protected String specifyWorkshopVersion() {
        return (MDR_REPOSITORY_SERVICE_WORKSHOP_VERSION);
    }

    @Override
    protected TopologyNodeTypeEnum specifyWorkshopType() {
        return (TopologyNodeTypeEnum.WORKSHOP);
    }

    @Override
    protected void invokePostConstructInitialisation() {

    }


}
