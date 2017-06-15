
package com.classes.model;

// Andy Wodarczyk
// CSCE741 2017-02-11
//
// ClassChild
//
// Function: Store all of the class attributes that
// apply to a single class. Note one class list such
// Introduction to Computer Concepts CSCE 101 section 1
// might meet multiple times within a week such as
// Monday, Wednesday, and Friday. Each of these days would have
// their own ClassChild object as they might have different
// instructors and times for different days.
//

public class ClassChild
{
    private String DayOfWeek;
    private String ClassTimeStart;
    private String ClassTimeEnd;
    private String Instructor;
    private String ClassDateStart;
    private String ClassDateEnd;
    private String Location;
    private String Attribute;
    private int DayOrder;

    public ClassChild()
    {
    }

    public void setDayOfWeek(String newDayOfWeek)
    {
        this.DayOfWeek = newDayOfWeek;;
    }

    public void setClassTimeStart (String newClassTimeStart)
    {
        this.ClassTimeStart = newClassTimeStart;
    }

    public void setClassTimeEnd (String newClassTimeEnd)
    {
        this.ClassTimeEnd = newClassTimeEnd;
    }

    public void setInstructor(String newInstructor)
    {
        this.Instructor = newInstructor;
    }

    public void setClassDateStart(String newClassDateStart)
    {
        this.ClassDateStart = newClassDateStart;
    }

    public void setClassDateEnd(String newClassDateEnd)
    {
        this.ClassDateEnd = newClassDateEnd;
    }

    public void setLocation(String newLocation)
    {
        this.Location  = newLocation;
    }

    public void setAttribute(String newAttribute)
    {
        this.Attribute = newAttribute;
    }

    public void setDayOrder(int newDayOrder)
    {
        this.DayOrder = newDayOrder;
    }

    public String getDayOfWeek()
    {
        return this.DayOfWeek;
    }

    public String getClassTimeStart()
    {
        return this.ClassTimeStart;
    }

    public String getClassTimeEnd()
    {
        return this.ClassTimeEnd;
    }

    public String getInstructor()
    {
        return this.Instructor;
    }

    public String getInstructorFormatted()
    {
        if (this.Instructor.equals("TBA"))
        {
            return "TBA";
        }
        else
        {
            String test = this.Instructor.replace("(P)","").trim();
            int loc = test.lastIndexOf(" ");
            return test.substring(loc + 1) + ", " + test.substring(0, loc);
        }
    }

    public String getClassDateStart()
    {
        return this.ClassDateStart;
    }

    public String getClassDateEnd()
    {
        return this.ClassDateEnd;
    }

    public String getLocation()
    {
        return this.Location;
    }

    public String getAttribute()
    {
        return this.Attribute;
    }

    public int getDayOrder()
    {
        return this.DayOrder;
    }

    public String getClassDates()
    {
        return this.ClassDateStart + "-" + this.ClassDateEnd;
    }

    public String getClassTimes()
    {
        String rv = "TBA";
        if (this.ClassTimeStart != null)
        {
            String timeStart = this.ClassTimeStart.replaceAll(":00 ","");
            String timeEnd = this.ClassTimeEnd.replaceAll(":00 ","");
            rv = timeStart.trim() + "-" + timeEnd.trim();
        }
        return rv;
    }

}

