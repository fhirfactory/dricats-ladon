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
package net.fhirfactory.pegacorn.ladon.statespace.inputs.staging.task;

import net.fhirfactory.pegacorn.ladon.statespace.inputs.staging.operationoutcome.OperationOutcomeSSTopicProcessorBean;
import net.fhirfactory.pegacorn.petasos.model.topics.TopicToken;
import net.fhirfactory.pegacorn.petasos.wup.archetypes.MOAStandardWUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class TaskSSTopic extends MOAStandardWUP {
    private static final Logger LOG = LoggerFactory.getLogger( TaskSSTopic.class);
    @Override
    protected Logger getLogger(){return(LOG);}

    private static String STATE_SPACE_TOPIC_WUP_VERSION = "1.0.0";
    private static String STATE_SPACE_TOPIC_RESOURCE_ID = "Task";
    private static String STATE_SPACE_FHIR_VERSION = "4.0.1";


    @Override
    protected Set<TopicToken> specifySubscriptionTopics() {
        TopicToken topicId = getFHIRTopicIDBuilder().createTopicToken(STATE_SPACE_TOPIC_RESOURCE_ID, STATE_SPACE_FHIR_VERSION);
        topicId.addDescriminator("Source", "Ladon.StateSpace.Normaliser" );
        HashSet<TopicToken> topicSet = new HashSet<TopicToken>();
        topicSet.add(topicId);
        return(topicSet);
    }

    @Override
    protected String specifyWUPInstanceName() {
        return ("StateSpaceInputs"+STATE_SPACE_TOPIC_RESOURCE_ID+"TopicWUP");
    }

    @Override
    protected String specifyWUPVersion() {
        return (STATE_SPACE_TOPIC_WUP_VERSION);
    }

    @Override
    protected String specifyWUPWorkshop() {
        return "StateSpace";
    }

    @Override
    public void configure() throws Exception {
        // This is truly a do-nothing WUP for the initial release and is really only here to
        // separate the topics into their own queue.
        from(ingresFeed())
                .routeId(this.getNameSet().getWupTypeName())
                .bean(TaskSSTopicProcessorBean.class,"toPubSub(*)")
                .to(egressFeed());
    }
}
