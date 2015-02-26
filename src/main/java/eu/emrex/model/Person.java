package eu.emrex.model;

import java.util.Date;

public class Person {

    private String givenNames;

    private String familyName;

    private Date birthDate;

    private String gender;


    public String getGivenNames() {
        return givenNames;
    }


    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }


    public String getFamilyName() {
        return familyName;
    }


    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }


    public Date getBirthDate() {
        return birthDate;
    }


    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }


    public String getGender() {
        return gender;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }

}
