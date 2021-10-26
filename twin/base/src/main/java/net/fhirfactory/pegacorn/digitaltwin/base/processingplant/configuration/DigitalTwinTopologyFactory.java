package net.fhirfactory.pegacorn.digitaltwin.base.processingplant.configuration;

import net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.fhirpersistence.im.FHIRIMSubsystemTopologyFactory;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.*;
import net.fhirfactory.pegacorn.digitaltwin.base.processingplant.common.DigitalTwinTopologyNames;
import net.fhirfactory.pegacorn.util.PegacornEnvironmentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DigitalTwinTopologyFactory extends FHIRIMSubsystemTopologyFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DigitalTwinTopologyFactory.class);

    @Inject
    private DigitalTwinTopologyNames patientDTNames;

    @Inject
    private PegacornEnvironmentProperties pegacornEnvironmentProperties;

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected Class specifyPropertyFileClass() {
        return (DigitalTwinConfigurationFile.class);
    }

    @Override
    protected ProcessingPlantTopologyNode buildSubsystemTopology() {
        SubsystemTopologyNode subsystemTopologyNode = addSubsystemNode(getTopologyIM().getSolutionTopology());
        BusinessServiceTopologyNode businessServiceTopologyNode = addBusinessServiceNode(subsystemTopologyNode);
        DeploymentSiteTopologyNode deploymentSiteTopologyNode = addDeploymentSiteNode(businessServiceTopologyNode);
        ClusterServiceTopologyNode clusterServiceTopologyNode = addClusterServiceNode(deploymentSiteTopologyNode);

        PlatformTopologyNode platformTopologyNode = addPlatformNode(clusterServiceTopologyNode);
        ProcessingPlantTopologyNode processingPlantTopologyNode = addPegacornProcessingPlant(platformTopologyNode);
        addPrometheusPort(processingPlantTopologyNode);
        addJolokiaPort(processingPlantTopologyNode);
        addKubeLivelinessPort(processingPlantTopologyNode);
        addKubeReadinessPort(processingPlantTopologyNode);
        addEdgeAnswerPort(processingPlantTopologyNode);
        addIntraZoneIPCJGroupsPort(processingPlantTopologyNode);
        addInterZoneIPCJGroupsPort(processingPlantTopologyNode);

        return(processingPlantTopologyNode);
    }

    protected String specifyPropertyFileName() {
        getLogger().info(".specifyPropertyFileName(): Entry");
        String configurationFileName = pegacornEnvironmentProperties.getMandatoryProperty("DEPLOYMENT_CONFIG_FILE");
        if(configurationFileName == null){
            throw(new RuntimeException("Cannot load configuration file!!!! (SUBSYSTEM-CONFIG_FILE="+configurationFileName+")"));
        }
        getLogger().info(".specifyPropertyFileName(): Exit, filename->{}", configurationFileName);
        return configurationFileName;
    }
}
