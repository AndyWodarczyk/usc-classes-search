
package com.classes.service;

import com.classes.model.ClassChild;
import com.classes.model.ClassParent;

import org.joda.time.LocalTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Service;

// Andy Wodarczyk
// CSCE741 2017-02-11
//
// ClassListFieldSetMapper
//
// Function: provide mapping between ClassParent / ClassChild and CsvImport
// 
// After finding search results the table data is written to a csv file
// (tilda deliminted). This classs will parse the file row by row and 
// place data in the ClassParent and ClassChild objects

@Service
public class ClassFieldMapper implements FieldSetMapper<ClassParent>
{
    public static String Term;
    public static String SemesterYear;
    public static int DayOrder = 0;

    private static DateTimeFormatter timeFormatOut = DateTimeFormat.forPattern("HH:mm");
    private static DateTimeFormatter timeFormatIn = DateTimeFormat.forPattern("hh:mm a");
    private static DateTimeFormatter dateFormatOut = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static DateTimeFormatter dateFormatIn = DateTimeFormat.forPattern("MM/dd/yyyy");

    public ClassParent mapFieldSet(FieldSet fieldSet)
    {  
        ClassParent classParent = new ClassParent();

        if (fieldSet == null || fieldSet.getFieldCount() != 19)
            return null;

        if (!fieldSet.readString(0).equals(""))
        {
            // fields numbers are 0 zero based, but the 0 column
            // is not needed so we start with column 1

            classParent.setTerm(Term);
            
            if (NumberUtils.isNumber(fieldSet.readString(1)))
            {
                DayOrder = 0;
                classParent.setCrnNbr(fieldSet.readInt(1));
            }
            else
            {
                DayOrder += 1;
            }
            classParent.setSubject(fieldSet.readString(2));
            classParent.setCourseNbr(fieldSet.readString(3));

            classParent.setSectionNbr(fieldSet.readString(4));
            classParent.setCmp(fieldSet.readString(5));

            //
            // Position 6 = credit hours
            //
            // Extract the credits format with possible variable of 1.000-3.000
            String[] credits = fieldSet.readString(6).split("-");

            if (NumberUtils.isNumber(credits[0])) 
                classParent.setMinCreditHrs(Double.parseDouble(credits[0]));
            
            // if a min - max credit range doees not exist then we will set the max = min
            if (NumberUtils.isNumber(credits[credits.length - 1]))
                classParent.setMaxCreditHrs(Double.parseDouble(credits[credits.length - 1]));

            // if we have a var credit range such as 1-3 then we will end up up with a length of 
            // the array of size 2. Size of 2 - 1 will set the database flag equal to 1 denoting 
            // that a min - max range exists
            classParent.setVarCreditHrs(credits.length - 1);
          
            //classList.setCreditHrs(fieldSet.readString(6));
            classParent.setPartOfTerm(fieldSet.readString(7));
            classParent.setTitle(fieldSet.readString(8));
        
            if (classParent.getCrnNbr() > 0)
            {
                classParent.setCapacity(fieldSet.readInt(11));
                classParent.setActual(fieldSet.readInt(12));
                classParent.setRemaining(fieldSet.readInt(13));
            }           
        }

        // in case we have 2 instructors listed like "joe smith, bob brown"
        // each will get credit
        String[] instructors = fieldSet.readString(14).split(",");

        for (String name : instructors)
        {
            ClassChild classChild = new ClassChild();

            classChild.setDayOfWeek(fieldSet.readString(9));
            classChild.setDayOrder(DayOrder);

            // Extract the classtimes formated as 10:50 am-09:20 am
            if (fieldSet.readString(10).contains("-"))
            {
                String[] times = fieldSet.readString(10).split("-");
                classChild.setClassTimeStart(timeFormatOut.print(timeFormatIn.parseDateTime(times[0])));
                classChild.setClassTimeEnd(timeFormatOut.print(timeFormatIn.parseDateTime(times[1])));
            }
                
            classChild.setInstructor(name);
                    
            // Extract the classdates formatted as 08/18-12/02
            if (fieldSet.readString(15).contains("-"))
            {
                String[] dates = fieldSet.readString(15).split("-");
                classChild.setClassDateStart(dateFormatOut.print(dateFormatIn.parseDateTime(dates[0] + "/" + SemesterYear)));
                classChild.setClassDateEnd(dateFormatOut.print(dateFormatIn.parseDateTime(dates[1] + "/" + SemesterYear)));
            }

            classChild.setLocation(fieldSet.readString(16));
            classChild.setAttribute(fieldSet.readString(17));
            classParent.addClassDay(classChild);

        }

        return classParent;

    } // End of mapFieldSet method

} // End of class ClassFieldMapper

