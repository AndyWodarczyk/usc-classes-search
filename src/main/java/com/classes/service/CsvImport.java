
package com.classes.service;

import java.util.ArrayList;
import java.util.List;

import com.classes.model.ClassParent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

// Andy Wodarczyk
// CSCE741 2017-02-11
//
// CsvImport
//
// Function: read a data file (tilda deliminted) for insertion into db
//
// FlatFileItemReader / LineMapper based on 
// http://docs.spring.io/spring-batch/reference/html/readersAndWriters.html

@Service
public class CsvImport
{
    @Autowired
    ClassesDao classesDao;

    public void CsvImport(String term, String semesterYear, String fileName)
    {
        List<ClassParent> classList = new ArrayList<ClassParent>();

        ApplicationContext appContext =
                  new ClassPathXmlApplicationContext();
        try
        {
            List<String> loadedClassNames = classesDao.getClassNumbers();

            FlatFileItemReader<ClassParent> itemReader =
                   new FlatFileItemReader<ClassParent>();
            Resource resource = appContext.getResource("file:" + fileName);

            itemReader.setResource(resource);
            DefaultLineMapper<ClassParent> lineMapper =
                   new DefaultLineMapper<ClassParent>();

            // set tilda as the delimiter
            // use the ClassListFieldSetMapper.java class to convert fields
            // and populate the ClassList and ClassDay objects
            lineMapper.setLineTokenizer(new DelimitedLineTokenizer("~"));
            lineMapper.setFieldSetMapper(new ClassFieldMapper());
            itemReader.setLineMapper(lineMapper);
            itemReader.setLinesToSkip(0);
            itemReader.open(new ExecutionContext());

            ClassFieldMapper.Term = term;
            ClassFieldMapper.SemesterYear = semesterYear;

            ClassParent classParent = null;

            do
            {
                classParent = itemReader.read();

                if (classParent != null)
                {
                    classParent.setTerm(term);

                    // if the class number is 0, then this is a second
                    // (or more) line that just lists additional days
                    // that the class meets. In that case we just append
                    // this extra day to the previous ClassList
                    if (classParent.getCrnNbr() == 0)
                    {
                        classList.get(classList.size() - 1).addClassDayRange(classParent.getClassDays());
                    }
                    else
                    {
                        classList.add(classParent);

                        if (!loadedClassNames.contains(classParent.getSubject() + classParent.getCourseNbr()))
                        {
                            classesDao.insertClassName(classParent);
                            loadedClassNames.add(classParent.getSubject() + classParent.getCourseNbr());
                        }
                    }
                }

            } while (classParent != null);

            classesDao.insertClassSemester(classList);

        }
        catch (Exception e)   
        {
            e.printStackTrace();

        } // End of try / catch
    }
}
