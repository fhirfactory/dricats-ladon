/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.ladon.statespace.twinpathwaycontroller.common;

import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourIdentifier;
import net.fhirfactory.pegacorn.ladon.model.twin.DigitalTwinIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public abstract class TwinPathwayControllerBase {
    private static final Logger LOG = LoggerFactory.getLogger(TwinPathwayControllerBase.class);

    private TwinRegentResourceTypeEnum twinRegentType;
    private TwinResourceTypeEnum twinResourceType;
    private ConcurrentHashMap<DigitalTwinIdentifier, BehaviourIdentifier> twinInstanceActiveStatus;

    public TwinPathwayControllerBase(){
        twinInstanceActiveStatus = new ConcurrentHashMap<>();
    }

    // Abstract methods

    abstract protected TwinResourceTypeEnum specifyTwinResourceType();
    abstract protected TwinRegentResourceTypeEnum specifyTwinRegentType();

    // Twin Instance Active Status

    public void lockTwinInstance(DigitalTwinIdentifier twinIdentifier, BehaviourIdentifier behaviourIdentifier){
        if(twinInstanceActiveStatus.containsKey(twinIdentifier)){
            return;
        }
        twinInstanceActiveStatus.put(twinIdentifier, behaviourIdentifier);
    }

    public void unlockTwinInstance(DigitalTwinIdentifier twinIdentifier){
        if(twinInstanceActiveStatus.containsKey(twinIdentifier)){
            twinInstanceActiveStatus.remove(twinIdentifier);
        }
    }

    public boolean isTwinLocked(DigitalTwinIdentifier twinIdentifier){
        if(twinInstanceActiveStatus.containsKey(twinIdentifier)){
            return(true);
        } else {
            return(false);
        }
    }

    public BehaviourIdentifier getTwinActiveBehaviour(DigitalTwinIdentifier twinIdentifier){
        if(twinInstanceActiveStatus.containsKey(twinIdentifier)){
            return(twinInstanceActiveStatus.get(twinIdentifier));
        } else {
            return(null);
        }
    }
}
