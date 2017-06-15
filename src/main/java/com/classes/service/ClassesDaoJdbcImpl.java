
package com.classes.service;

import com.classes.model.ClassChild;
import com.classes.model.ClassParent;
import com.classes.model.Selection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

// Andy Wodarczyk
// CSCE741 2017-02-11
//
// ClassesDaoJdbcImpl
// Function: Perform all db transactions
//
// Jdbc operations based on examples given from
// https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/html/jdbc.html

@Service
public class ClassesDaoJdbcImpl implements ClassesDao
{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<String> getClassNumbers()
    {
        return jdbcTemplate.queryForList("SELECT CONCAT(SUBJECT,COURSE_NBR) FROM CLASS_NAME", String.class);
    }

    public List<String> getInstructors()
    {
        return this.jdbcTemplate.queryForList("SELECT DISTINCT INSTRUCTOR_FORMAT " +
                                              "FROM CLASS_DAY ORDER BY INSTRUCTOR_FORMAT", String.class);
    }

    public Map<String,String> getSubjects()
    {
       Map<String,String> mapRet= new TreeMap<String,String>();
 
       this.jdbcTemplate.query("SELECT SUBJECT_VALUE, SUBJECT_TEXT " +
                               "FROM CLASS_SUBJECT ORDER BY SUBJECT_VALUE",
            (rs, rowNum) -> mapRet.put(rs.getString("SUBJECT_VALUE"),rs.getString("SUBJECT_TEXT")));
       return mapRet;
    }

    public List<String> getTerms()
    {
        return this.jdbcTemplate.queryForList("SELECT TERM_NAME FROM CLASS_TERM " +
                                              "ORDER BY TERM_SORT DESC", String.class);
    }

    public void insertClassName(ClassParent classParent)
    {
        int count = jdbcTemplate
             .update("INSERT INTO CLASS_NAME(SUBJECT,COURSE_NBR,TITLE,VAR_CREDIT_HRS," +
                     "MIN_CREDIT_HRS,MAX_CREDIT_HRS) VALUES (?,?,?,?,?,?)",
             classParent.getSubject(), classParent.getCourseNbr(), classParent.getTitle(), 
             classParent.getVarCreditHrs(), classParent.getMinCreditHrs(), 
             classParent.getMaxCreditHrs());

        if (count != 1)
            throw new InsertFailedException("Cannot insert class names");
    
    } // End of insertClassName method


    public void deleteClassSemester(Selection selection)
    {
        String term = selection.getTerm();
        String campus = selection.getCampus();
    
        if (selection.getSubjects() != null)
        {
            for (String subject : selection.getSubjects())
            {
                jdbcTemplate.update("DELETE FROM CLASS_DAY " +
                                    "WHERE SEMESTER_TERM=? AND CMP=? AND SUBJECT=?",
                                     term, campus, subject );
                jdbcTemplate.update("DELETE FROM CLASS_SEMESTER " +
                                    "WHERE SEMESTER_TERM=? AND CMP=? AND SUBJECT=?",
                                     term, campus, subject);
            }
        }
    } // End of deleteClassSemester method


    public void insertClassSemester(final List<ClassParent> masterClassList)
    {
        String sql = "INSERT INTO CLASS_SEMESTER " +
                     "(SEMESTER_TERM,CRN_NBR,SUBJECT,COURSE_NBR,SECTION_NBR," +
                     "CMP,PART_OF_TERM,CAPACITY,ACTUAL,REMAINING) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?)";

        int[] counts = jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter()
        {
            public void setValues(PreparedStatement ps, int i) throws SQLException
            {
                ClassParent classParent = masterClassList.get(i);
                ps.setString(1,classParent.getTerm());
                ps.setInt(2,classParent.getCrnNbr());
                ps.setString(3,classParent.getSubject());
                ps.setString(4,classParent.getCourseNbr());
                ps.setString(5,classParent.getSectionNbr());
                ps.setString(6,classParent.getCmp());
                ps.setString(7,classParent.getPartOfTerm());
                ps.setInt(8,classParent.getCapacity());
                ps.setInt(9,classParent.getActual());
                ps.setInt(10,classParent.getRemaining());
            }

            public int getBatchSize()
            {
                return masterClassList.size();
            }
        });

        int i = 0;
        for(int count : counts)
        {
            if (count == 0) throw new InsertFailedException("Row not updated :" + i);
            i++;
        }

        for (ClassParent classParent : masterClassList)
        {
            sql = "INSERT INTO CLASS_DAY(SEMESTER_TERM,SUBJECT,CMP,CRN_NBR," +
                                        "DAY_ORDER,DAY_OF_WEEK,TIME_START," +
                                        "TIME_END,INSTRUCTOR,INSTRUCTOR_FORMAT," +
                                        "CLASS_DATE_START,CLASS_DATE_END," +
                                        "LOCATION,CLASS_ATTRIBUTE) " +
                                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            counts = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter()
            {
                public void setValues(PreparedStatement ps, int i) throws SQLException
                {
                    ClassChild classChild = classParent.getClassDays().get(i);
                    ps.setString(1,classParent.getTerm());
                    ps.setString(2,classParent.getSubject());
                    ps.setString(3,classParent.getCmp());
                    ps.setInt(4,classParent.getCrnNbr());
                    ps.setInt(5,classChild.getDayOrder());
                    ps.setString(6,classChild.getDayOfWeek());
                    ps.setString(7,classChild.getClassTimeStart());
                    ps.setString(8,classChild.getClassTimeEnd());
                    ps.setString(9,classChild.getInstructor());
                    ps.setString(10,classChild.getInstructorFormatted());
                    ps.setString(11,classChild.getClassDateStart());
                    ps.setString(12,classChild.getClassDateEnd());
                    ps.setString(13,classChild.getLocation());
                    ps.setString(14,classChild.getAttribute());
                }

                public int getBatchSize()
                {
                    return classParent.getClassDays().size();
                }
            });

            i = 0;
            for(int count : counts)
            {
                if (count == 0) throw new InsertFailedException("Row not updated :" + i);
                i++;
            }
        }

    } // End of insertClassSemester method


    public List<ClassParent> findSelection(Selection selection)
    {
        MapSqlParameterSource params = new MapSqlParameterSource();

        String sql = "SELECT a.SEMESTER_TERM, a.TITLE, a.CRN_NBR, a.SUBJECT, " +
                            "a.COURSE_NBR, a.SECTION_NBR, a.CMP, a.PART_OF_TERM, " +
                            "a.CREDIT_HRS, a.CAPACITY, a.ACTUAL, a.REMAINING, " +
                            "a.DAY_OF_WEEK, a.TIME_START, a.TIME_END, a.INSTRUCTOR, " +
                            "a.DATE_START_FORMATTED, a.DATE_END_FORMATTED, " +
                            "a.LOCATION, a.DAY_ORDER  " +
                     "FROM (SELECT * FROM CLASS_SEARCH) a " ;
        String where = " WHERE ";
       
        if (selection.getDays() != null && selection.getDays().size() > 0)
        {
            String orTerm = " ";

            // build a series of OR statements for each day in the input
            for (int i = 0; i < selection.getDays().size(); i ++)
            {
                if (i > 0)
                    orTerm += " OR " ;

                orTerm += " DAY_OF_WEEK LIKE CONCAT('%',:DAY" + i + ",'%') ";
                params.addValue("DAY" + i, selection.getDays().get(i));
            }
 
            // Create a self join 
            // (CLASS_DAY is actually a subset of CLASS_SEARCH which is 'a' of the join)
            String sub  = ", (SELECT DISTINCT SEMESTER_TERM, CMP, SUBJECT, CRN_NBR " +
                             "FROM CLASS_DAY WHERE ";
                  sub +=  orTerm;
                  sub += ") b ";
                  sub += "WHERE a.SEMESTER_TERM = b.SEMESTER_TERM AND a.CMP = b.CMP AND " +
                               "a.SUBJECT = b.SUBJECT AND a.CRN_NBR = b.CRN_NBR ";
             sql += sub;  

            where = " AND ";
        }
        
        where += " 1=1 ";

        // Add the instructor name to the where clause
        if (selection.getInstructor() != null)
        {
            where += " AND a.INSTRUCTOR_FORMAT = :INSTRUCTOR_FORMAT ";
            params.addValue("INSTRUCTOR_FORMAT", selection.getInstructor() );
        }

        // Add the term name to the where clause
        if (selection.getTerm() != null)
        {
            where += " AND a.SEMESTER_TERM = :SEMESTER_TERM ";
            params.addValue("SEMESTER_TERM", selection.getTerm() );
        }

        // Add the campus name to the where clause
        if (selection.getCampus() != null)
        {
            where += " AND a.CMP = :CMP "; 
            params.addValue("CMP", selection.getCampus() );
        }
  
        // Add the subjects (1 or more) to the where clause 
        if (selection.getSubjects() != null && selection.getSubjects().size() > 0)
        {
            String inTerm = " AND a.SUBJECT IN ( ";

            for (int i = 0; i < selection.getSubjects().size(); i ++)
            {
                if (i > 0)
                    inTerm += "," ;

                inTerm += ":SUBJECT" + i;
                params.addValue("SUBJECT" + i, selection.getSubjects().get(i));
            }

            inTerm += ") ";
            where += inTerm;
        }
 
        sql += where;

        sql += "ORDER BY a.SEMESTER_TERM, a.SUBJECT, a.COURSE_NBR, a.SECTION_NBR, a.DAY_ORDER ";     
   
	return this.namedParameterJdbcTemplate.query(sql, params, new RowMapper<ClassParent>() 
        {
            public ClassParent mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                ClassParent classList = new ClassParent();
                             
                if (rs.getInt("DAY_ORDER") == 0)
                {
                    classList.setTerm(rs.getString("SEMESTER_TERM"));
                    classList.setTitle(rs.getString("TITLE"));
                    classList.setCrnNbr(rs.getInt("CRN_NBR"));
                    classList.setSubject(rs.getString("SUBJECT"));
                    classList.setCourseNbr(rs.getString("COURSE_NBR"));
                    classList.setSectionNbr(rs.getString("SECTION_NBR"));
                    classList.setCmp(rs.getString("CMP"));
                    classList.setPartOfTerm(rs.getString("PART_OF_TERM"));
                    classList.setCreditHrs(rs.getString("CREDIT_HRS"));
                    classList.setCapacity(rs.getInt("CAPACITY"));
                    classList.setActual(rs.getInt("ACTUAL"));
                    classList.setRemaining(rs.getInt("REMAINING"));
                }
                classList.setDayOfWeek(rs.getString("DAY_OF_WEEK"));
                classList.setClassTimeStart(rs.getString("TIME_START"));
                classList.setClassTimeEnd(rs.getString("TIME_END"));
                classList.setInstructor(rs.getString("INSTRUCTOR"));
                classList.setClassDateStart(rs.getString("DATE_START_FORMATTED"));
                classList.setClassDateEnd(rs.getString("DATE_END_FORMATTED"));
                classList.setLocation(rs.getString("LOCATION"));
                      
                return classList;
            }
        });
    
    } // End of findSelection method
}

