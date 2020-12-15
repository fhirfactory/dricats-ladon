package net.fhirfactory.pegacorn.ladon.model.stimuli;

import net.fhirfactory.pegacorn.petasos.model.topics.TopicToken;

public class StimulusType extends TopicToken {

    public StimulusType(TopicToken topic){
        super(topic);
    }

    public StimulusType(){
        super();
    }

    public TopicToken getAsTopicToken(){
        TopicToken asToken = new TopicToken();
        asToken.setIdentifier(this.getIdentifier());
        asToken.setVersion(this.getVersion());
        return(asToken);
    }
}
