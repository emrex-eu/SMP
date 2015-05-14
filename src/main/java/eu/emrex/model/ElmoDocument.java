package eu.emrex.model;

import java.util.List;

public class ElmoDocument {

    private String personName;

    private String institutionName;

    private List<ElmoResult> results;


    public String getPersonName() {
        return personName;
    }


    public void setPersonName(String personName) {
        this.personName = personName;
    }


    public String getInstitutionName() {
        return institutionName;
    }


    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }


    public List<ElmoResult> getResults() {
        return results;
    }


    public void setResults(List<ElmoResult> results) {
        this.results = results;
    }

}
