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
package net.fhirfactory.pegacorn.ladon.model.outcomes;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourIdentifier;
import net.fhirfactory.pegacorn.ladon.model.twin.DigitalTwinIdentifier;
import org.hl7.fhir.r4.model.Resource;

import net.fhirfactory.pegacorn.ladon.model.stimuli.StimulusIdentifier;

public class Outcome {
	private Resource outputResource;
	private Date creationDate;
	private StimulusIdentifier sourceStimulus;
	private OutcomeIdentifier id;
	private BehaviourIdentifier sourceBehaviour;
	private DigitalTwinIdentifier affectingTwin;
	private OutcomeStatusEnum status;
	private boolean forwardToRealMe;

	
	public Outcome() {
		this.outputResource = null;
		this.creationDate = Date.from(Instant.now());;
		this.sourceStimulus = null;
		this.sourceBehaviour = null;
		this.affectingTwin = null;
		this.status = null;
		this.id = new OutcomeIdentifier(UUID.randomUUID().toString());
		this.forwardToRealMe = false;
	}
	
	public Outcome(Outcome ori) {
		this.outputResource = ori.getOutputResource();
		this.creationDate = ori.getCreationDate();
		this.sourceStimulus = ori.getSourceStimulus();
		this.id = ori.getId();
		this.sourceBehaviour = ori.getSourceBehaviour();
		this.affectingTwin = ori.getAffectingTwin();
		this.status = ori.getStatus();
		this.forwardToRealMe = false;
	}
	
	public Outcome(Resource outResource, OutcomeStatusEnum outcomeStatus) {
		this.outputResource = outResource;
		this.creationDate = Date.from(Instant.now());
		this.sourceStimulus = null;
		this.sourceBehaviour = null;
		this.affectingTwin = null;
		this.status = outcomeStatus;
		this.id = new OutcomeIdentifier(UUID.randomUUID().toString());
		this.forwardToRealMe = false;
	}

	public OutcomeStatusEnum getStatus() {
		return status;
	}

	public void setStatus(OutcomeStatusEnum status) {
		this.status = status;
	}

	public Resource getOutputResource() {
		return outputResource;
	}
	public void setOutputResource(Resource outputResource) {
		this.outputResource = outputResource;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	private void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public StimulusIdentifier getSourceStimulus() {
		return sourceStimulus;
	}

	public void setSourceStimulus(StimulusIdentifier sourceStimulus) {
		this.sourceStimulus = sourceStimulus;
	}

	public OutcomeIdentifier getId() {
		return id;
	}

	private void setId(OutcomeIdentifier id) {
		this.id = id;
	}

	public BehaviourIdentifier getSourceBehaviour() {
		return sourceBehaviour;
	}

	public void setSourceBehaviour(BehaviourIdentifier sourceBehaviour) {
		this.sourceBehaviour = sourceBehaviour;
	}

	public DigitalTwinIdentifier getAffectingTwin() {
		return affectingTwin;
	}

	public void setAffectingTwin(DigitalTwinIdentifier affectingTwin) {
		this.affectingTwin = affectingTwin;
	}

	public boolean isForwardToRealMe() {
		return forwardToRealMe;
	}

	public void setForwardToRealMe(boolean forwardToRealMe) {
		this.forwardToRealMe = forwardToRealMe;
	}
}
