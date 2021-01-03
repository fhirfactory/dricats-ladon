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

import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa.ParcelStatusElement;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWIdentifier;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;



public class UoWCache {
    private ConcurrentHashMap<UoWIdentifier, UoW> uowPool;
    private ConcurrentHashMap<UoWIdentifier, WUPJobCard> jobCardPool;
    private ConcurrentHashMap<UoWIdentifier, ParcelStatusElement> statusElementPool;
    private ConcurrentHashMap<UoWIdentifier, String> wupKeyPool;

    public UoWCache() {
        this.jobCardPool = new ConcurrentHashMap<>();
        this.uowPool = new ConcurrentHashMap<>();
        this.statusElementPool = new ConcurrentHashMap<>();
        this.wupKeyPool = new ConcurrentHashMap<>();
    }

    public void addUoW(UoW newUoW, WUPJobCard jobCard, ParcelStatusElement statusElement, String wupKey) {
        if(uowPool.containsKey(newUoW.getInstanceID())) {
            uowPool.remove(newUoW.getInstanceID());
        }
        uowPool.put(newUoW.getInstanceID(), newUoW);
        jobCardPool.put(newUoW.getInstanceID(), jobCard);
        statusElementPool.put(newUoW.getInstanceID(), statusElement);
        wupKeyPool.put(newUoW.getInstanceID(), wupKey);
    }

    public void removeUoW(UoWIdentifier uowToRemove){
        if(uowPool.containsKey(uowToRemove)){
            uowPool.remove(uowToRemove);
            jobCardPool.remove(uowToRemove);
            statusElementPool.remove(uowToRemove);
            wupKeyPool.remove(uowToRemove);
        }
    }

    public UoW getUoW(UoWIdentifier uowId){
        if(uowPool.containsKey(uowId)){
            return(uowPool.get(uowId));
        }
        return(null);
    }

    public WUPJobCard getAssociatedJobCard(UoWIdentifier uowId){
        if(jobCardPool.containsKey(uowId)){
            return(jobCardPool.get(uowId));
        }
        return(null);
    }

    public ParcelStatusElement getAssociatedStatusElement(UoWIdentifier uowId){
        if(statusElementPool.containsKey(uowId)){
            return(statusElementPool.get(uowId));
        }
        return(null);
    }

    public String getAssociatedWUPKey(UoWIdentifier uowId){
        if(wupKeyPool.containsKey(uowId)){
            return(wupKeyPool.get(uowId));
        }
        return(null);
    }
}
