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
package net.fhirfactory.pegacorn.ladon.statespace.twinpathway.twinactivityqueue.common;

import net.fhirfactory.pegacorn.ladon.model.twin.DigitalTwinIdentifier;
import net.fhirfactory.pegacorn.ladon.statespace.stimuli.model.StimulusPackage;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class TwinInstanceActivityQueue {
    private ConcurrentHashMap<DigitalTwinIdentifier, ConcurrentLinkedQueue<StimulusPackage>> twinInstanceQueue;

    public TwinInstanceActivityQueue() {
        this.twinInstanceQueue = new ConcurrentHashMap<>();
    }

    public void addStimulus2Queue(DigitalTwinIdentifier twinInstanceIdentifier, StimulusPackage newStimuli) {
        if (twinInstanceQueue.containsKey(twinInstanceIdentifier)) {
            Queue<StimulusPackage> twinStimuli = twinInstanceQueue.get(twinInstanceIdentifier);
            if (!twinStimuli.contains(newStimuli)) {
                twinStimuli.add(newStimuli);
            }
        } else {
            ConcurrentLinkedQueue<StimulusPackage> stimuliQueue = new ConcurrentLinkedQueue<>();
            stimuliQueue.add(newStimuli);
            twinInstanceQueue.put(twinInstanceIdentifier, stimuliQueue);
        }
    }

    public StimulusPackage getNextStimulus(DigitalTwinIdentifier twinInstanceIdentifier) {
        if (twinInstanceQueue.containsKey(twinInstanceIdentifier)) {
            StimulusPackage nextStimulusPackage;
            Queue<StimulusPackage> twinStimuli = twinInstanceQueue.get(twinInstanceIdentifier);
            if (!twinStimuli.isEmpty()){
                nextStimulusPackage = twinStimuli.poll();
                if(twinStimuli.isEmpty()){
                    twinInstanceQueue.remove(twinInstanceIdentifier);
                }
                return(nextStimulusPackage);
            }
            return(null);
        }
        return(null);
    }

    public int getStimulusCount(DigitalTwinIdentifier twinInstanceIdentifier){
        if (twinInstanceQueue.containsKey(twinInstanceIdentifier)) {
            StimulusPackage nextStimulusPackage;
            Queue<StimulusPackage> twinStimuli = twinInstanceQueue.get(twinInstanceIdentifier);
            if (!twinStimuli.isEmpty()) {
                return (twinStimuli.size());
            }
        }
        return(0);
    }
}
