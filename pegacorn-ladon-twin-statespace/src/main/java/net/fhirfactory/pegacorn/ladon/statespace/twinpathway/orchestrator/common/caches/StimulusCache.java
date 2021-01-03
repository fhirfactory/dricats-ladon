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

import net.fhirfactory.pegacorn.ladon.model.stimuli.Stimulus;
import net.fhirfactory.pegacorn.ladon.model.stimuli.StimulusIdentifier;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWIdentifier;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StimulusCache {
    private ConcurrentHashMap<StimulusIdentifier, Stimulus> stimulusPool;
    private ConcurrentHashMap<UoWIdentifier, Set<StimulusIdentifier>> uow2StimulusMap;

    public StimulusCache(){
        this.stimulusPool = new ConcurrentHashMap<>();
        this.uow2StimulusMap = new ConcurrentHashMap<>();
    }

    public void addStimulus(Stimulus newStimulus){
        if(newStimulus == null){
            return;
        }
        stimulusPool.put(newStimulus.getId(), newStimulus);
        if(newStimulus.getOriginalUoWIdentifier() != null){
            addStimulusAssociation2UoW(newStimulus.getId(), newStimulus.getOriginalUoWIdentifier());
        }
    }

    public void removeStimulus(StimulusIdentifier stimulusToRemove){
        if(stimulusToRemove == null){
            return;
        }
        if(stimulusPool.containsKey(stimulusToRemove)){
            Stimulus workingStimulus = stimulusPool.get(stimulusToRemove);
            removeStimulusAssociation2UoW(stimulusToRemove,workingStimulus.getOriginalUoWIdentifier());
            stimulusPool.remove(stimulusToRemove);
        }
    }

    public Stimulus getStimulus(StimulusIdentifier stimulusId){
        if(stimulusPool.containsKey(stimulusId)){
            return(stimulusPool.get(stimulusId));
        }
        return(null);
    }

    public void addStimulusAssociation2UoW(StimulusIdentifier stimulusId, UoWIdentifier uowId){
        if(stimulusId == null || uowId == null){
            return;
        }
        if(!uow2StimulusMap.containsKey(uowId)){
            HashSet<StimulusIdentifier> idSet = new HashSet<StimulusIdentifier>();
            uow2StimulusMap.put(uowId, idSet);
        }
        uow2StimulusMap.get(uowId).add(stimulusId);
    }

    public Set<StimulusIdentifier> getStimulusAssociatedWithUoW(UoWIdentifier uowId){
        if(uowId == null){
            return(new HashSet<StimulusIdentifier>());
        }
        if(!uow2StimulusMap.containsKey(uowId)){
            return(new HashSet<StimulusIdentifier>());
        }
        return(uow2StimulusMap.get(uowId));
    }

    public void removeStimulusAssociation2UoW(StimulusIdentifier stimulusId, UoWIdentifier uowId){
        if(stimulusId == null || uowId == null){
            return;
        }
        if(!uow2StimulusMap.containsKey(uowId)){
            return;
        }
        Set<StimulusIdentifier> uowStimulus = uow2StimulusMap.get(uowId);
        if(uowStimulus.contains(stimulusId)){
            uowStimulus.remove(stimulusId);
        }
        if(uowStimulus.isEmpty()){
            uow2StimulusMap.remove(uowId);
        }
    }

}
