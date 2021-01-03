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
package net.fhirfactory.pegacorn.ladon.statespace.twinpathway.orchestrator.common.caches;

import net.fhirfactory.pegacorn.ladon.model.stimuli.StimulusPackage;
import net.fhirfactory.pegacorn.ladon.model.twin.DigitalTwinIdentifier;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TwinInstanceWorkQueues {
    private ConcurrentHashMap<DigitalTwinIdentifier, ConcurrentLinkedQueue<StimulusPackage>> twinInstanceStimulusQueue;
    private ConcurrentHashMap<DigitalTwinIdentifier, Object> twinInstanceQueueLock;

    public TwinInstanceWorkQueues(){
        twinInstanceStimulusQueue = new ConcurrentHashMap<>();
        this.twinInstanceQueueLock = new ConcurrentHashMap<>();
    }

    /**
     * This method supports the addition of new activity elements (StimulusPackage).
     *
     * @param twinInstanceIdentifier The Twin Instance unique Identifier for the Digital Twin Type.
     * @param newStimuli The StimulusPackage to be added the the TwinInstance's activity queue.
     */
    public void addStimulus2Queue(DigitalTwinIdentifier twinInstanceIdentifier, StimulusPackage newStimuli) {
        if (!twinInstanceQueueLock.containsKey(twinInstanceIdentifier)) {
            twinInstanceQueueLock.put(twinInstanceIdentifier, new Object());
        }
        Object twinQueueLock = twinInstanceQueueLock.get(twinInstanceIdentifier);
        synchronized(twinQueueLock) {
            if (twinInstanceStimulusQueue.containsKey(twinInstanceIdentifier)) {
                Queue<StimulusPackage> twinStimuli = twinInstanceStimulusQueue.get(twinInstanceIdentifier);
                if (!twinStimuli.contains(newStimuli)) {
                    twinStimuli.add(newStimuli);
                }
            } else {
                ConcurrentLinkedQueue<StimulusPackage> stimuliQueue = new ConcurrentLinkedQueue<>();
                stimuliQueue.add(newStimuli);
                twinInstanceStimulusQueue.put(twinInstanceIdentifier, stimuliQueue);
            }
        }
    }

    /**
     * This method supports the extraction (pop'ing) of the next-in-line activity element from the queue. It also
     * removes that element from the queue.
     *
     * @param twinInstanceIdentifier The Twin Instance unique Identifier for the Digital Twin Type.
     * @return A StimulusPackage from the FIFO queue for the identified Digital Twin.
     */
    public StimulusPackage getNextStimulusPackage(DigitalTwinIdentifier twinInstanceIdentifier) {
        if (!twinInstanceQueueLock.containsKey(twinInstanceIdentifier)) {
            twinInstanceQueueLock.put(twinInstanceIdentifier, new Object());
        }
        Object twinQueueLock = twinInstanceQueueLock.get(twinInstanceIdentifier);
        StimulusPackage nextStimulusPackage = null;
        synchronized(twinQueueLock) {
            if (twinInstanceStimulusQueue.containsKey(twinInstanceIdentifier)) {
                Queue<StimulusPackage> twinStimuli = twinInstanceStimulusQueue.get(twinInstanceIdentifier);
                if (!twinStimuli.isEmpty()){
                    nextStimulusPackage = twinStimuli.poll();
                    if(twinStimuli.isEmpty()){
                        twinInstanceStimulusQueue.remove(twinInstanceIdentifier);
                    }
                }
            }
        }
        return(nextStimulusPackage);
    }


    /**
     * This method is used to ascertain if their is activity (StimulusPackage) items within the
     * Digital Twin's activity queue.
     *
     * @param twinInstanceIdentifier The Twin Instance unique Identifier for the Digital Twin Type.
     * @return A count of the number of StimulusPackage elements (Activity) waiting for the Twin Instance to process.
     */
    public int getStimulusCount(DigitalTwinIdentifier twinInstanceIdentifier){
        if (twinInstanceStimulusQueue.containsKey(twinInstanceIdentifier)) {
            Queue<StimulusPackage> twinStimuli = twinInstanceStimulusQueue.get(twinInstanceIdentifier);
            if (!twinStimuli.isEmpty()) {
                return (twinStimuli.size());
            }
        }
        // If we get to this point, there is no Stimuli for the identifier twinInstanceIdentifier
        // so return ( 0 ).
        return(0);
    }

    public Set<DigitalTwinIdentifier> getTwinsWithQueuedWork(){
        if(twinInstanceStimulusQueue.isEmpty()) {
            return (new HashSet<>());
        }
        HashSet<DigitalTwinIdentifier> twinIdSet = new HashSet<>();
        Enumeration<DigitalTwinIdentifier> twinsWithQueuedTraffic = twinInstanceStimulusQueue.keys();
        while(twinsWithQueuedTraffic.hasMoreElements()){
            twinIdSet.add(twinsWithQueuedTraffic.nextElement());
        }
        return(twinIdSet);
    }

}
