
package com.classes.model;

import java.util.ArrayList;
import java.util.List;

// Andy Wodarczyk
// CSCE741 2017-02-11
//
// Selection
//
// Function:
//   -- Store the user selected criteria to be used for searching
//   -- Store the user name and password for remote searches
//   -- Store the type of search to be performed (remote vs local)

public class Selection
{
    private String Term;
    private String Campus;
    private List<String> Subjects;
    private String Username = "";
    private String Password = "";

    private String SearchOption; // L = local or R = remote
    private List<String> Days;
    private String Instructor;
    private List<String> TableFields;

    public Selection()
    {
        this.Days = new ArrayList<String>();
    }

    public void setTerm(String newTerm)
    {
        this.Term = newTerm;
    }

    public void setCampus(String newCampus)
    {
        this.Campus = newCampus;
    }

    public void setSubjects(List<String> newSubjects)
    {
        this.Subjects = newSubjects;
    }

    public void setSearchOption(String newSearchOption)
    {
        this.SearchOption = newSearchOption;
    }

    public void setDays(List<String> newDays)
    {
        this.Days = newDays;
    }

    public void setInstructor(String newInstructor)
    {
        this.Instructor = newInstructor;
    }

    public void setUsername(String newUsername)
    {
        this.Username = newUsername;
    }

    public void setPassword(String newPassword)
    {
        this.Password = newPassword;
    }

    public void setTableFields(List<String> newTableFields)
    {
        this.TableFields = newTableFields;
    }

    public String getTerm()
    {
        return this.Term;
    }

    public String getCampus()
    {
        return this.Campus;
    }

    public List<String> getSubjects()
    {
        return this.Subjects;
    }

    public String getSearchOption()
    {
        return this.SearchOption;
    }

    public List<String> getDays()
    {
        return this.Days;
    }

    public String getInstructor()
    {
        return this.Instructor;
    }

    public String getUsername()
    {
        return this.Username;
    }

    public String getPassword()
    {
       return this.Password;
    }

    public List<String> getTableFields()
    {
       return this.TableFields;
    }

}

