/*
 * The MIT License
 *
 * Copyright 2020 mhunter.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.fhirfactory.pegacorn.ladon.processingplant;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import net.fhirfactory.pegacorn.deployment.topology.map.standalone.StandaloneSampleDeploymentSolution;
import org.jboss.arquillian.container.test.api.Deployment;
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
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mark A. Hunter
 */
@RunWith(Arquillian.class)
public class LadonProcessingPlantTest {

    private static final Logger LOG = LoggerFactory.getLogger(LadonProcessingPlantTest.class);

    @Inject
    LadonProcessingPlant ladonPlatform;
    
    @Inject
    StandaloneSampleDeploymentSolution sampleSolution;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive testWAR;

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
        testWAR = ShrinkWrap.create(WebArchive.class, "pegacorn-ladon-platform-test.war")
                .addAsLibraries(fileSet)
                .addPackages(true, "net.fhirfactory.pegacorn.ladon.processingplant")
                .addPackages(true, "net.fhirfactory.pegacorn.deployment.topology.map.standalone")
                .addAsManifestResource("META-INF/beans.xml", "WEB-INF/beans.xml");
        if (LOG.isDebugEnabled()) {
            Map<ArchivePath, Node> content = testWAR.getContent();
            Set<ArchivePath> contentPathSet = content.keySet();
            Iterator<ArchivePath> contentPathSetIterator = contentPathSet.iterator();
            while (contentPathSetIterator.hasNext()) {
                ArchivePath currentPath = contentPathSetIterator.next();
                LOG.trace(".createDeployment(): pegacorn-ladon-platform-test.war Entry Path --> {}", currentPath.get());
            }
        }
        return (testWAR);
    }

    public LadonProcessingPlantTest() {
    }

    @Before
    public void setUp() {
      if(sampleSolution.hasBeenInitialised()){
          LOG.debug(".setUp(): All Good!");
      } else {
          LOG.debug(".setUp(): Oh no! something is wrong with the sample topology data generation!");
      }
    }

    /**
     * Test of getNodeIdentifier method, of class LadonPlatformFramework.
     */
    @Test
    public void testGetNodeIdentifier() {
        if(ladonPlatform.getProcessingPlantNodeId() != null){
            assertTrue(true);
        }
    }
}
