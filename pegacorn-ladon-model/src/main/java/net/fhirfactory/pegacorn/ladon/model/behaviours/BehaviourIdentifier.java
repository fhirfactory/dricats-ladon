package net.fhirfactory.pegacorn.ladon.model.behaviours;

public class BehaviourIdentifier {
    String name;
    String version;

    public BehaviourIdentifier() {
        this.name = null;
        this.version = null;
    }

    public BehaviourIdentifier(String name, String version){
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
