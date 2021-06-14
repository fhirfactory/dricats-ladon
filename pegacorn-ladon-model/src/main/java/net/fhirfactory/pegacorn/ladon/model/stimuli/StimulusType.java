package net.fhirfactory.pegacorn.ladon.model.stimuli;

import net.fhirfactory.pegacorn.common.model.topicid.DataParcelToken;

public class StimulusType extends DataParcelToken {

    public StimulusType(DataParcelToken topic){
        super(topic);
    }

    public StimulusType(){
        super();
    }

    public DataParcelToken getAsTopicToken(){
        DataParcelToken asToken = new DataParcelToken();
        asToken.setToken(this.getToken());
        asToken.setVersion(this.getVersion());
        return(asToken);
    }
}
