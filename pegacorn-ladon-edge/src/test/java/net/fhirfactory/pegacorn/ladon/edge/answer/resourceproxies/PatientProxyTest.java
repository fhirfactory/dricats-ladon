package net.fhirfactory.pegacorn.ladon.edge.answer.resourceproxies;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import net.fhirfactory.pegacorn.fhir.r4.samples.PatientSetFactory;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;

import org.hl7.fhir.r4.model.Patient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@RunWith(Arquillian.class)
public class PatientProxyTest {
    private static final Logger LOG = LoggerFactory.getLogger(PatientProxyTest.class);

    private static final String WEBAPP_SRC="src/main/webapp";

    @Inject
    private PatientSetFactory patientSet;

    @Inject
    private FHIRContextUtility FHIRContextUtility;
    
    private IParser parserR4;
    
    private IGenericClient ladonEdgeClient;

    @Deployment(testable=true)
    public static WebArchive createDeployment() {

        WebArchive ladonEdgeTestWAR;

        PomEquippedResolveStage pomEquippedResolver = Maven.resolver().loadPomFromFile("pom.xml");
        PomEquippedResolveStage pomEquippedResolverWithRuntimeDependencies = pomEquippedResolver.importRuntimeDependencies();
        MavenStrategyStage mavenResolver = pomEquippedResolverWithRuntimeDependencies.resolve();
        MavenFormatStage mavenFormat = mavenResolver.withTransitivity();
        File[] fileSet = mavenFormat.asFile();
        LOG.debug(".createDeployment(): ShrinkWrap Library Set for run-time equivalent, length --> {}", fileSet.length);
        for (int counter = 0; counter < fileSet.length; counter++) {
            File currentFile = fileSet[counter];
            LOG.trace(".createDeployment(): Shrinkwrap Entry --> {}", currentFile.getName());
        }
        ladonEdgeTestWAR = ShrinkWrap.create(WebArchive.class, "ladon.war")
                .addAsLibraries(fileSet)
                .addPackages(true, "net.fhirfactory.pegacorn.ladon.edge")
                .addPackages(true, "net.fhirfactory.pegacorn.deployment.topology.map.standalone")
                .addPackages(true, "net.fhirfactory.pegacorn.fhir.r4.samples")
                .addAsWebInfResource(new File(WEBAPP_SRC, "WEB-INF/jboss-web.xml"))
                .setWebXML( new File(WEBAPP_SRC, "WEB-INF/web.xml"))
                .addAsManifestResource("META-INF/beans.xml", "WEB-INF/beans.xml");
        if (LOG.isDebugEnabled()) {
            Map<ArchivePath, Node> content = ladonEdgeTestWAR.getContent();
            Set<ArchivePath> contentPathSet = content.keySet();
            Iterator<ArchivePath> contentPathSetIterator = contentPathSet.iterator();
            while (contentPathSetIterator.hasNext()) {
                ArchivePath currentPath = contentPathSetIterator.next();
                LOG.debug(".createDeployment(): pegacorn-ladon-edge-test Entry Path --> {}", currentPath.get());
            }
        }
        return (ladonEdgeTestWAR);
    }

    @Before
    public void setUp() {
        LOG.debug(".setup(): Entry");
        FhirContext contextR4 = FHIRContextUtility.getFhirContext();
        parserR4 = FHIRContextUtility.getJsonParser();
        contextR4.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        ladonEdgeClient = contextR4.newRestfulGenericClient("http://localhost:8080/pegacorn/fhir/r4/");
        LOG.trace(".setUp(): Exit!");
    }

    @org.junit.Test
    @RunAsClient
    public void createPatient() {
        LOG.trace(".createPatient(): Entry!");
        HashSet<MethodOutcome> patientIdSet = new HashSet<MethodOutcome>();
        LOG.trace(".createPatient(): Creating Test Patient Resource");
        Patient newPatient = (Patient) parserR4.parseResource(patient0);
        LOG.trace(".createPatient(): Adding Patient To Cache (via RESTful API) --> {}", newPatient);
        MethodOutcome newId = ladonEdgeClient.create().resource(newPatient).prettyPrint().encodedJson().execute();
        LOG.trace(".createPatient(): outcome --> {}", newId);
        patientIdSet.add(newId);

    }

    @org.junit.Test
    @RunAsClient
    public void readPatient() {
    }

    private String patient0 = "{\n" +
            "  \"resourceType\": \"Patient\",\n" +
            "  \"id\": \"f001\",\n" +
            "  \"text\": {\n" +
            "    \"status\": \"generated\",\n" +
            "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: f001</p><p><b>identifier</b>: 738472983 (USUAL), ?? (USUAL)</p><p><b>active</b>: true</p><p><b>name</b>: Pieter van de Heuvel </p><p><b>telecom</b>: ph: 0648352638(MOBILE), p.heuvel@gmail.com(HOME)</p><p><b>gender</b>: male</p><p><b>birthDate</b>: 17/11/1944</p><p><b>deceased</b>: false</p><p><b>address</b>: Van Egmondkade 23 Amsterdam 1024 RJ NLD (HOME)</p><p><b>maritalStatus</b>: Getrouwd <span>(Details : {http://terminology.hl7.org/CodeSystem/v3-MaritalStatus code 'M' = 'Married', given as 'Married'})</span></p><p><b>multipleBirth</b>: true</p><h3>Contacts</h3><table><tr><td>-</td><td><b>Relationship</b></td><td><b>Name</b></td><td><b>Telecom</b></td></tr><tr><td>*</td><td>Emergency Contact <span>(Details : {http://terminology.hl7.org/CodeSystem/v2-0131 code 'C' = 'Emergency Contact)</span></td><td>Sarah Abels </td><td>ph: 0690383372(MOBILE)</td></tr></table><h3>Communications</h3><table><tr><td>-</td><td><b>Language</b></td><td><b>Preferred</b></td></tr><tr><td>*</td><td>Nederlands <span>(Details : {urn:ietf:bcp:47 code 'nl' = 'Dutch', given as 'Dutch'})</span></td><td>true</td></tr></table><p><b>managingOrganization</b>: <a>Burgers University Medical Centre</a></p></div>\"\n" +
            "  },\n" +
            "  \"identifier\": [\n" +
            "    {\n" +
            "      \"use\": \"usual\",\n" +
            "      \"system\": \"urn:oid:2.16.840.1.113883.2.4.6.3\",\n" +
            "      \"value\": \"738472983\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"use\": \"usual\",\n" +
            "      \"system\": \"urn:oid:2.16.840.1.113883.2.4.6.3\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"active\": true,\n" +
            "  \"name\": [\n" +
            "    {\n" +
            "      \"use\": \"usual\",\n" +
            "      \"family\": \"van de Heuvel\",\n" +
            "      \"given\": [\n" +
            "        \"Pieter\"\n" +
            "      ],\n" +
            "      \"suffix\": [\n" +
            "        \"MSc\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"telecom\": [\n" +
            "    {\n" +
            "      \"system\": \"phone\",\n" +
            "      \"value\": \"0648352638\",\n" +
            "      \"use\": \"mobile\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"system\": \"email\",\n" +
            "      \"value\": \"p.heuvel@gmail.com\",\n" +
            "      \"use\": \"home\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"gender\": \"male\",\n" +
            "  \"birthDate\": \"1944-11-17\",\n" +
            "  \"deceasedBoolean\": false,\n" +
            "  \"address\": [\n" +
            "    {\n" +
            "      \"use\": \"home\",\n" +
            "      \"line\": [\n" +
            "        \"Van Egmondkade 23\"\n" +
            "      ],\n" +
            "      \"city\": \"Amsterdam\",\n" +
            "      \"postalCode\": \"1024 RJ\",\n" +
            "      \"country\": \"NLD\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"maritalStatus\": {\n" +
            "    \"coding\": [\n" +
            "      {\n" +
            "        \"system\": \"http://terminology.hl7.org/CodeSystem/v3-MaritalStatus\",\n" +
            "        \"code\": \"M\",\n" +
            "        \"display\": \"Married\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"text\": \"Getrouwd\"\n" +
            "  },\n" +
            "  \"multipleBirthBoolean\": true,\n" +
            "  \"contact\": [\n" +
            "    {\n" +
            "      \"relationship\": [\n" +
            "        {\n" +
            "          \"coding\": [\n" +
            "            {\n" +
            "              \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0131\",\n" +
            "              \"code\": \"C\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ],\n" +
            "      \"name\": {\n" +
            "        \"use\": \"usual\",\n" +
            "        \"family\": \"Abels\",\n" +
            "        \"given\": [\n" +
            "          \"Sarah\"\n" +
            "        ]\n" +
            "      },\n" +
            "      \"telecom\": [\n" +
            "        {\n" +
            "          \"system\": \"phone\",\n" +
            "          \"value\": \"0690383372\",\n" +
            "          \"use\": \"mobile\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"communication\": [\n" +
            "    {\n" +
            "      \"language\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"urn:ietf:bcp:47\",\n" +
            "            \"code\": \"nl\",\n" +
            "            \"display\": \"Dutch\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"text\": \"Nederlands\"\n" +
            "      },\n" +
            "      \"preferred\": true\n" +
            "    }\n" +
            "  ],\n" +
            "  \"managingOrganization\": {\n" +
            "    \"reference\": \"Organization/f001\",\n" +
            "    \"display\": \"Burgers University Medical Centre\"\n" +
            "  }\n" +
            "}";
}
