
package com.classes.model;

import java.util.ArrayList;
import java.util.List;

// Andy Wodarczyk
// CSCE741 2017-02-11
//
// ClassParent
//
// Function: Store all of the class attributes that
// apply to a single class. This is the parent of the
// ClassChild. This class will store attributes of the
// class that are common for all days for a given
// section. 
// 

public class ClassParent extends ClassChild
{

    List<ClassChild> ClassDays = new ArrayList<ClassChild>();

    private String Term = "";
    private int CrnNbr;
    private String Subject = "";
    private String CourseNbr = "";
    private String SectionNbr = "";
    private String Cmp = "";
    private double MinCreditHrs;
    private double MaxCreditHrs;
    private int VarCreditHrs;
    private String CreditHrs = "";
    private String PartOfTerm ="";
    private String Title = "";
    private int Capacity;
    private int Actual;
    private int Remaining;

    public void setTerm(String newTerm)
    {
        this.Term = newTerm;
    }

    public void setCrnNbr(int newCrnNbr)
    {
        this.CrnNbr = newCrnNbr;
    }

    public void setSubject(String newSubject)
    {
        this.Subject = newSubject;
    }

    public void setCourseNbr(String newCourseNbr)
    {
        this.CourseNbr = newCourseNbr;
    }

    public void setSectionNbr(String newSectionNbr)
    {
        this.SectionNbr = newSectionNbr;
    }

    public void setCmp(String newCmp)
    {
        this.Cmp = newCmp;
    }

    public void setMinCreditHrs(double newMinCreditHrs)
    {
        this.MinCreditHrs = newMinCreditHrs;
    }

    public void setMaxCreditHrs(double newMaxCreditHrs)
    {
        this.MaxCreditHrs = newMaxCreditHrs;
    }

    public void setVarCreditHrs(int newVarCreditHrs)
    {
        this.VarCreditHrs = newVarCreditHrs;
    }

    public void setCreditHrs(String newCreditHrs)
    {
        this.CreditHrs = newCreditHrs;
    }

    public void setPartOfTerm(String newPartOfTerm)
    {
        this.PartOfTerm = newPartOfTerm;
    }

     public void setTitle(String newTitle)
    {
        this.Title = newTitle;
    }

    public void setCapacity(int newCapacity)
    {
        this.Capacity = newCapacity;
    }

    public void setActual(int newActual)
    {
        this.Actual = newActual;
    }

    public void setRemaining(int newRemaining)
    {
        this.Remaining = newRemaining;
    }

    public void addClassDay(ClassChild newClassDay)
    {
        this.ClassDays.add(newClassDay);
    }

    public void addClassDayRange(List<ClassChild> newClassDays)
    {
        this.ClassDays.addAll(newClassDays);
    }

    public String getTerm()
    {
        return this.Term;
    }

    public int getCrnNbr()
    {
        return this.CrnNbr;
    }

    public String getSubject()
    {
        return this.Subject;
    }

    public String getCourseNbr()
    {
        return this.CourseNbr;
    }

    public String getSectionNbr()
    {
        return this.SectionNbr;
    }

    public String getCmp()
    {
        return this.Cmp;
    }

    public double getMinCreditHrs()
    {
        return this.MinCreditHrs;
    }

    public double getMaxCreditHrs()
    {
        return this.MaxCreditHrs;
    }

    public int getVarCreditHrs()
    {
        return this.VarCreditHrs;
    }

    public String getCreditHrs()
    {
        return this.CreditHrs;
    }

    public String getPartOfTerm()
    {
        return this.PartOfTerm;
    }

    public String getTitle()
    {
        return this.Title;
    }

    public int getCapacity()
    {
        return this.Capacity;
    }

    public int getActual()
    {
        return this.Actual;
    }

    public int getRemaining()
    {
        return this.Remaining;
    }

    public List<ClassChild> getClassDays()
    {
        return this.ClassDays;
    }

} 

