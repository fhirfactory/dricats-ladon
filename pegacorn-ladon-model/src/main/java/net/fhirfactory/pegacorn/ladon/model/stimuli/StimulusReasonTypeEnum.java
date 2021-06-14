package net.fhirfactory.pegacorn.ladon.model.stimuli;

public enum StimulusReasonTypeEnum {
    STIMULUS_REASON_HL7V2_MESSAGE("pegacorn.platform.ladon.statespace.stimulus.reason.hl7v2_message"),
    STIMULUS_REASON_RESTFUL_POST("pegacorn.platform.ladon.statespace.stimulus.reason.restful_post"),
    STIMULUS_REASON_RESTFUL_PUT("pegacorn.platform.ladon.statespace.stimulus.reason.restful_put"),
    STIMULUS_REASON_BUNDLE("pegacorn.platform.ladon.statespace.stimulus.reason.bundle"),
    STIMULUS_REASON_GENERAL("pegacorn.platform.ladon.statespace.stimulus.reason.general");

    private String reasonType;

    private StimulusReasonTypeEnum(String reasonType){
        this.reasonType = reasonType;
    }

    public String getReasonType(){
        return(this.reasonType);
    }
}
