package net.fhirfactory.pegacorn.ladon.model.virtualdb.searches;

public enum SearchNameEnum {
    DOCUMENT_REFERENCE_DATE_AND_TYPE("DocumentReference: date=[Start,End], type=[xxx]"),
    PATIENT_QRY_A19("Pateint: identifier, queryString"),
    PROCEDURE_PATIENT_AND_DATE("Procedure: subject.where(resolve() is Patient), performed=[Start,End]");

    private String searchName;

    private SearchNameEnum(String name){
        this.searchName = name;
    }

    public String getSearchName(){
        return(this.searchName);
    }
}
