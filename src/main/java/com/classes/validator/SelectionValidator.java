
package com.classes.validator;

import com.classes.model.Selection;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

// Andy Wodarczyk
// CSCE741 2017-02-11
//
// SelectionValidator
//
// Function: ensure that the selections made by the user 
//           are valid or return error message to be displayed on form
//
// Validator based on 
// http://docs.spring.io/spring/docs/current/javadoc-api//org/springframework/validation/Validator.html

@Component
public class SelectionValidator implements Validator
{
    public boolean supports(Class clazz)
    {
        return Selection.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors e)
    {
        Selection s = (Selection) obj;

        // For REMOTE selection option ensure that we have the required
        // parameters that the school web sites requires 
        // - username
        // - password
        // - a selected term ie Fall 2017
        // - a selected campus ie Columbia
        // - one or more selected subjects
        // this class does NOT validate the entered username / password is valid
        // it just validates that they are not empty
        if (s.getSearchOption().equals("R"))
        {
            ValidationUtils.rejectIfEmptyOrWhitespace(e, "Username", "empty.username");
            ValidationUtils.rejectIfEmptyOrWhitespace(e, "Password", "empty.password");
            ValidationUtils.rejectIfEmpty(e, "Term", "empty.term");
            ValidationUtils.rejectIfEmpty(e, "Campus", "empty.campus");

            if (s.getSubjects() == null)
            {
                e.rejectValue("Subjects", "empty.subject");
            }
        }
    }
}

