package net.fhirfactory.pegacorn.ladon.model.stimuli;

import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementIdentifier;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

public class StimulusReason {
	private NodeElementIdentifier pegacornEntryPoint;
	private Resource why;
	private StimulusReasonTypeEnum reasonType;
	private String originalSource;
	private String originalDestination;
	
	public StimulusReason(StimulusReasonTypeEnum reasonType, Resource why) {
		this.why = why;
		this.reasonType = reasonType;
		this.pegacornEntryPoint = null;
		this.originalDestination = null;
		this.originalDestination = null;
	}
	
	public StimulusReason(StimulusReason ori) {
		this.pegacornEntryPoint = ori.getPegacornEntryPoint();
		this.why = ori.getWhy();
		this.reasonType = ori.getReasonType();
		this.originalDestination = ori.getOriginalDestination();
		this.originalSource = ori.getOriginalSource();
	}
	
	public NodeElementIdentifier getPegacornEntryPoint() {
		return pegacornEntryPoint;
	}
	public void setPegacornEntryPoint(NodeElementIdentifier source) {
		this.pegacornEntryPoint = source;
	}
	public Resource getWhy() {
		return why;
	}
	public void setCode(Resource why) {
		this.why = why;
	}
	public StimulusReasonTypeEnum getReasonType() {
		return reasonType;
	}
	public void setReasonType(StimulusReasonTypeEnum reasonType) {
		this.reasonType = reasonType;
	}

	public void setWhy(Resource why) {
		this.why = why;
	}

	public String getOriginalSource() {
		return originalSource;
	}

	public void setOriginalSource(String originalSource) {
		this.originalSource = originalSource;
	}

	public String getOriginalDestination() {
		return originalDestination;
	}

	public void setOriginalDestination(String originalDestination) {
		this.originalDestination = originalDestination;
	}
}
