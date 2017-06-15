
package com.classes.web;

import com.classes.model.Selection;
import com.classes.service.ClassesDao;
import com.classes.service.CsvImport;
import com.classes.validator.SelectionValidator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

// Andy Wodarczyk
// CSCE741 2017-02-11
//
// ClassesController
//
// Function: populate form objects and perform selenium based
//           web retrieval of desired classes

@Controller
@ComponentScan({"com.classes.service","com.classes.validator"})
public class ClassesController
{
    @Autowired
    ClassesDao classesDao;

    @Autowired
    SelectionValidator selectionValidator;

    @Autowired
    CsvImport csvImport;

    // these are the days of the week that can be used to narrow/filter searches
    // selected via checkboxes on the form
    @ModelAttribute("daysList")
    public Map<String,String> populateDays()
    {
        Map<String,String> daysList = new LinkedHashMap<String,String>();
        daysList.put("M","Mon ");
        daysList.put("T","Tue ");
        daysList.put("W","Wed ");
        daysList.put("R","Thur ");
        daysList.put("F","Fri ");
        daysList.put("S","Sat ");
        daysList.put("U","Sun ");
        return daysList;
    }

   
    // These are the optional fields that will show on the results table
    // selected via checkboxes on form
    @ModelAttribute("fieldsList")
    public Map<String,String> populateFieldsList()
    {
        Map<String,String> fieldsList = new LinkedHashMap<String,String>();
        fieldsList.put("CRN","CRN");
        fieldsList.put("Camp","CMP");
        fieldsList.put("Cred","Credits");
        fieldsList.put("Part","Part Of Term");
        fieldsList.put("Cap","Capacity");
        fieldsList.put("Act","Actual");
        fieldsList.put("Rem","Remaining");
        fieldsList.put("Inst","Instructor");
        fieldsList.put("Date","Date");
        fieldsList.put("Loc","Location");
        return fieldsList;
    }

    // Available campuses to search. 
    // Will be shown in option list box
    @ModelAttribute("campusList")
    public Map<String,String> populateCampusList()  
    {
        Map<String,String> campusList = new HashMap<String,String>();
        campusList.put("AIK","USC Aiken");
        campusList.put("BFT","USC Beaufort");
        campusList.put("COL","USC Columbia");
        campusList.put("LAN","USC Lancaster");
        campusList.put("SAL","USC Salkehatchie");
        campusList.put("SMT","USC Sumter");
        campusList.put("UNI","USC Union");
        campusList.put("UPS","USC Upstate");
        return campusList;
    }

    @ModelAttribute("termList")
    public List<String> populateTerms()
    {
        return classesDao.getTerms();
    }


    @ModelAttribute("instructorList")
    public List<String> populateInstructors()
    {
        return classesDao.getInstructors();
    }


    @ModelAttribute("subjectList")
    public Map<String,String> populateSubjectList()
    {
        return classesDao.getSubjects();
    }


    @RequestMapping(value = "classes-search", method = RequestMethod.GET)
    public ModelAndView searchNew()
    {
        ModelAndView model = new ModelAndView("index");
        Selection userForm = new Selection();		
        userForm.setSearchOption("L");
        model.addObject("userForm", userForm);
        return model;
    }	

	
    @RequestMapping(value = "classes-search", method = RequestMethod.POST )
    public ModelAndView search(@ModelAttribute("userForm") Selection selection, 
                               @RequestParam String command, 
                               BindingResult results) throws Exception
    {
        ObjectError error;
        String term = selection.getTerm(); 
        String campus = selection.getCampus();
        String searchOption = selection.getSearchOption();
        ModelAndView model = new ModelAndView("index");

        // Clear user form and exit
        if (command.equals("Clear"))
        {
            Selection userForm = new Selection();
            userForm.setSearchOption("L");
            model.addObject("userForm", userForm);
            return model;
        }
  
        selectionValidator.validate(selection, results);
        if (!results.hasErrors())
        {
            if (searchOption.equals("R"))
            { 
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless"); // comment out to run with gui
                WebDriver driver = new ChromeDriver(chromeOptions); 
                Wait<WebDriver> wait = new WebDriverWait(driver, 120);

                WebElement element;

                // Start of My SC web site
                driver.get("https://my.sc.edu/");
                element = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sign in to Self Service Carolina (SSC)")));
                element.click();

                // wait for username field
                element = wait.until(ExpectedConditions.elementToBeClickable(By.id("generic-username")));
                element.sendKeys(selection.getUsername());

                // enter the password
                element = driver.findElement(By.id("generic-password"));
                element.sendKeys(selection.getPassword());

                // select the submit button
                driver.findElement(By.name("submit")).click();

                // wait for either an invalid username/password (text contains invalid) OR 
                //                  a successful logon (welcome message found) OR
                //                 duo authoriztion (duo frame found)
                wait.until(ExpectedConditions.or(
                           ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'Invalid')]")),
                           ExpectedConditions.presenceOfElementLocated(By.cssSelector("#welcomemessage")), 
                           ExpectedConditions.presenceOfElementLocated(By.id("duo_iframe"))));
                        //   ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(), 'Push')]"))));
                
                // first use a try/catch to see if the invalid message was displayed. 
                // No catch is needed as that means a invalid id was NOT entered and we can just continue
                try
                {
                    element = driver.findElement(By.xpath("//*[contains(text(), 'Invalid')]"));
                    error = new ObjectError("Username","Invalid username/password!");
                    results.addError(error);
                    return model;
                }
                catch (Exception ex)
                {}      
          
                // Use try/catch to see if the duo authorization is needed. 
                // No catch is needed as that means none was needed and 
                // we can continue to process as normal
                try
                {
                    //element = driver.findElement(By.xpath("//button[contains(text(), 'Push')]"));
                    element = driver.findElement(By.id("duo_iframe"));
                    driver.switchTo().frame("duo_iframe");
                    element = driver.findElement(By.xpath("//button[contains(text(), 'Push')]"));
                    element.click();
                }
                catch (Exception ex)
                {}    

                // wait for Student button
                element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[./text() = 'Student']")));
                element.click();

                // wait for Registration button
                element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='bmenu--P_RegMnu___UID1']/p")));
                element.click();

                // wait for Lookup Classes selection
                element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='contentItem12']/h3")));
                element.click();

                // select the term - we can not select the term directly since we store
                // the terms generically with the Season - Year. Eventually the terms on 
                // the web site change from "Fall 2017" -> "Fall 2017 (view only)".
                // therefor we loop through the list until we get a match via contains
                //  so that the "view only" change will not break code
                Select select= new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name("p_term"))));
          
                boolean foundTerm = false;           
                List<WebElement> options = select.getOptions();
                for (WebElement option : options)
                {
                    if (option.getText().contains(term))
                    {
                        select.selectByVisibleText(option.getText());
                        foundTerm = true;
                        break;
                    }   
                }

                // Eventually USC might remove older Terms for the web site for searching. 
                // In this case we might have a Term stored locally which is not findable - return message
                if (foundTerm == false)
                {
                    error = new ObjectError("Term","Selected Term is not present on the USC web site!");
                    results.addError(error);
                    return model;
                }

                // submit button
                element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[./text() = 'Submit']")));
                element.click();

                // wait for advanced search
                element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[./text() = 'Advanced Search']")));
                element.click();

                // since the campus selection is visible on the previous page - wait for an element unique to advanced page
                element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='crse_id']")));

                // now get the campus selection
                select = new Select(wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='camp_id']"))));
                select.selectByValue(campus);

                // select the subject
                // if the selected subject is not valid for any campus (for the selected term),
                //   then we will raise an exception here.
                // Note that if the subject is not valid for the selected campus, BUT is valid for
                // any campus, then we will pass here and get no results returned in the actual search
                try
                {
                    select = new Select(driver.findElement(By.id("subj_id")));
                    for (String subject : selection.getSubjects())
                    {
                        select.selectByValue(subject); 
                    }
                }
                catch (Exception ex)
                {
                    error = new ObjectError("Subject","Selected Subject(s) is not an option for this term on any campus!");
                    results.addError(error);
                    return model;
                }

                // select the submit button to get the search results
                element = driver.findElement(By.xpath("//div[./text() = 'Section Search']"));
                element.click();

                // wait for either no search results message or the search results table 
                wait.until(ExpectedConditions.or(
                           ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'No classes')]")),
                           ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.datadisplaytable"))));

                // first use a try/catch to see if the "no classes" message was displayed.
                // No catch is needed as that means the other
                // element was located and we can continue to process as normal
                try
                {
                    element = driver.findElement(By.xpath("//*[contains(text(), 'No classes')]"));
                    error = new ObjectError("Subjects","No classes found for selected Term/Campus/Subject!");
                    results.addError(error);
                    return model;
                }
                catch (Exception ex)
                {}

                element = driver.findElement(By.cssSelector("table.datadisplaytable"));

                String content = element.getAttribute("outerHTML");

                Document doc = Jsoup.parse(content, "UTF-8");

                String csvRows = ""; 
                String csvRow = "";
                int colSpan = 1;

                List<String> yyy = new ArrayList<String>();
              
                // loop through all of the rows within this table and extract all of the td elements (skip headers)
                for (Element htmlRow : doc.select("tr"))
                {
                    csvRow = "";

                    for (Element value : htmlRow.select("td"))
                    {
                        // If the web site has columns joined then we need to write the same value that many
                        // times as the csv importer is looking for certain columns to be X position
                        // default to no spanning column ie 1
                        colSpan = 1;
        
                        if (value.hasAttr("colspan"))
                            colSpan = Integer.parseInt(value.attr("colspan"));
 
                        for (int i = 1; i <= colSpan; i ++)
                        {
                            csvRow += value.text() + "~";
                        }
                    }
                    
                    // skip the blank lines    
                    if (!csvRow.equals(""))
                    {   
                        csvRows += csvRow + "\n";
                        yyy.add(csvRow);
                    }
                }

                driver.quit();

                String fileName = term + "_" + campus + ".csv";   
                try
                {
                    FileWriter f = new FileWriter(new File(fileName));
                    f.write (csvRows);
                    f.close();
                }
                catch (Exception ex)
                {
                }
            
                String[] parts = term.split(" ");
                String semesterYear = parts[1];
                        
                classesDao.deleteClassSemester(selection);
            
                csvImport.CsvImport(term, semesterYear,fileName);
            
            } // End of if for remote searches

        // Get the actual search results to pass to our web page
        model.addObject("classList", classesDao.findSelection(selection));
 
        } // End of if for no validation errors

        return model;
     
    } // End of method		

}

