
package com.classes.service;

import com.classes.model.ClassParent;
import com.classes.model.Selection;

import java.util.List;
import java.util.Map;

// Andy Wodarczyk
// CSCE741 2017-02-11
//
// ClassesDao
//
// Interface for ClassesDaoJdbcImpl
// 

public interface ClassesDao
{
    public Map<String,String> getSubjects();
    public List<String> getClassNumbers();
    public void insertClassName (ClassParent classParent);
    public void insertClassSemester (List<ClassParent> masterClassList);
    public void deleteClassSemester (Selection selection);
    public List<ClassParent> findSelection (Selection selection);
    public List<String> getInstructors();
    public List<String> getTerms();
}

