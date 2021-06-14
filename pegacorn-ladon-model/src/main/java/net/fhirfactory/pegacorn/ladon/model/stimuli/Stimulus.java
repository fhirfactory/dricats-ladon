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
package net.fhirfactory.pegacorn.ladon.model.stimuli;

import net.fhirfactory.pegacorn.petasos.model.uow.UoWIdentifier;
import org.hl7.fhir.r4.model.Resource;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class Stimulus {
    private Resource stimulusResource;
    private StimulusIdentifier id;
    private UoWIdentifier originalUoWIdentifier;
    private Date creationDate;
    private StimulusReason reason;

    public Stimulus(UoWIdentifier oriUoW, Resource resource){
        this.stimulusResource = resource;
        this.originalUoWIdentifier = oriUoW;
        this.id = new StimulusIdentifier(UUID.randomUUID().toString());
        this.creationDate = Date.from(Instant.now());
        this.reason = null;
    }

    public Stimulus(){
        this.stimulusResource = null;
        this.originalUoWIdentifier = null;
        this.id = null;
        this.creationDate = Date.from(Instant.now());
        this.reason = null;
    }

    public Resource getStimulusResource() {
        return (this.stimulusResource);
    }

    public void setStimulusResource(Resource stimulusResource) {
        this.stimulusResource = stimulusResource;
    }

    public StimulusIdentifier getId() {
        return id;
    }

    public void setId(StimulusIdentifier id) {
        this.id = id;
    }

    public UoWIdentifier getOriginalUoWIdentifier() {
        return originalUoWIdentifier;
    }

    public void setOriginalUoWIdentifier(UoWIdentifier originalUoW) {
        this.originalUoWIdentifier = originalUoW;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

	public StimulusReason getReason() {
		return reason;
	}

	public void setReason(StimulusReason reason) {
		this.reason = reason;
	}

}
