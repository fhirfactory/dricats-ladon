package net.fhirfactory.pegacorn.ladon.processingplant;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.workshops.base.PetasosEnabledWorkshop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateSpaceWorkshop extends PetasosEnabledWorkshop {
    private static final Logger LOG = LoggerFactory.getLogger(StateSpaceWorkshop.class);

    private static String STATESPACE_WORKSHOP_NAME = "StateSpace";
    private static String STATESPACE_WORKSHOP_VERSION = "1.0.0";

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyWorkshopName() {
        return (STATESPACE_WORKSHOP_NAME);
    }

    @Override
    protected String specifyWorkshopVersion() {
        return (STATESPACE_WORKSHOP_VERSION);
    }

    @Override
    protected TopologyNodeTypeEnum specifyWorkshopType() {
        return (TopologyNodeTypeEnum.WORKSHOP);
    }

    @Override
    protected void invokePostConstructInitialisation() {

    }


}
