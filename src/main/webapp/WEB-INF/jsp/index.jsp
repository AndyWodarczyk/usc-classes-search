
<!-- Andy Wodarczyk -->
<!-- CSCE741 2017-02-11 -->
<!-- html page to allow user to perform searches on class schedules -->

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">

<head>
    <spring:url value="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js" var="jqueryJs" />
    <script src="${jqueryJs}"></script>

    <spring:url value="/js/search.js" var="searchJs" />
    <script src="${searchJs}"></script>

    <spring:url value="/css/search.css" var="searchCss" />
    <link href="${searchCss}" rel="stylesheet" />
</head>

<body>

    <h1>University of South Carolina Class Search </h1>
    
    <form:form method="post" commandName="userForm">

        <div id="search-option">
            <div style="float:left;display:block">
                <h6>Search Option</h6>
                <label>
                    <form:radiobutton path="searchOption" cssClass="radio-button" 
                                      name="searchOption" value="L" /> Local
                </label>
                <label>
                    <form:radiobutton path="searchOption" cssClass="radio-button" 
                                      name="searchOption" value="R" /> Remote
                </label>
            </div>

            <div style="float:right;display:block" >
                <h6>User Name <span class="required-text"></span> </h6>
                <form:input path="username" cssClass="input-box" 
                            maxlength="12" disabled="true" />
                <h6>Password <span class="required-text"></span> </h6>
                <form:password path="password" cssClass="input-box" 
                               maxlength="16" disabled="true" />
            </div>
        </div>
   
        <div id ="term-campus-block">
            <h6>Term <span class="required-text"></span> </h6>
            <form:select path="term" cssClass="selectNarrow" 
                         size="8" items="${termList}" />
            <h6>Campus <span class="required-text"></span> </h6>
            <form:select path="campus" cssClass="selectNarrow" 
                         size="8" items="${campusList}" />
        </div>
               
        <div id="subject-block">
            <h6> Subject <span class="required-text"></span> </h6>
            <form:select multiple="true" cssClass="selectWide" size="18" 
                         path="Subjects" items="${subjectList}" />
        </div>
                 
        <div id="days-instructor-block">
            <h6> Days </h6>
            <form:checkboxes path="Days" cssClass="check-box-vert" items="${daysList}" />
            <h6>Instructor</h6>
            <form:select path="instructor" cssClass="selectWide" 
                         size="12" items="${instructorList}" />
        </div>

        <div id="optional-fields-block">
            <h6> Optional Table Fields </h6>
            <form:checkboxes path="tableFields" delimiter="</br>" 
                  cssClass="check-box-horz" items="${fieldsList}" />
        </div>
     
        <div id="button-controls">
            <table id="table-button">
                <tr>
                    <td> <input type="submit" name="command" value="Search" /> </td>
                    <td> <input type="submit" name="command" value="Clear" /> </td>
                    <td> <form:errors path="*" id="error-block" element="div" /> </td>
                </tr>
            </table>
        </div>

        <c:set var="errorList"> <form:errors path="*"/> </c:set>
       
    </form:form>

    <c:if test="${empty errorList}"> 

        <c:set var="listCRN"  value="false" />
        <c:set var="listCamp" value="false" />
        <c:set var="listCred" value="false" />
        <c:set var="listPart" value="false" />
        <c:set var="listCap"  value="false" />
        <c:set var="listAct"  value="false" />
        <c:set var="listRem"  value="false" />
        <c:set var="listInst" value="false" />
        <c:set var="listDate" value="false" />
        <c:set var="listLoc"  value="false" />

        <c:forEach var="item" items="${userForm.tableFields}">
            <c:choose>
                <c:when test="${item=='CRN'}">  <c:set var="listCRN"  value="true"/> </c:when>
                <c:when test="${item=='Camp'}"> <c:set var="listCamp" value="true"/> </c:when> 
                <c:when test="${item=='Cred'}"> <c:set var="listCred" value="true"/> </c:when>   
                <c:when test="${item=='Part'}"> <c:set var="listPart" value="true"/> </c:when>           
                <c:when test="${item=='Cap'}">  <c:set var="listCap"  value="true"/> </c:when>           
                <c:when test="${item=='Act'}">  <c:set var="listAct"  value="true"/> </c:when>
                <c:when test="${item=='Rem'}">  <c:set var="listRem"  value="true"/> </c:when>
                <c:when test="${item=='Inst'}"> <c:set var="listInst" value="true"/> </c:when>           
                <c:when test="${item=='Date'}"> <c:set var="listDate" value="true"/> </c:when>
                <c:when test="${item=='Loc'}">  <c:set var="listLoc"  value="true"/> </c:when>
            </c:choose>
        </c:forEach>
   
        <table id ="table-data">
            <thead>
                <tr>
                    <th>Term</th>
                    <th width = "55px"> Subject</th>
                    <th width = "50px"> Course</th>
                    <th width = "55px"> Section</th>
                    <th width = "300px">Title</th>
                    <c:if test="${listCRN}">  <th>CRN</th> </c:if>
                    <c:if test="${listCamp}"> <th width = "50px">Campus</th> </c:if>
                    <c:if test="${listCred}"> <th>Credits</th> </c:if>
                    <c:if test="${listPart}"> <th width = "190px">Part Of Term</th> </c:if>
                    <th>Days</th> 
                    <th width = "115px">Time</th>
                    <c:if test="${listCap}">  <th width = "50px">Capacity</th> </c:if>
                    <c:if test="${listAct}">  <th width = "50px">Actual</th> </c:if>
                    <c:if test="${listRem}">  <th width = "50px">Remain</th> </c:if>
                    <c:if test="${listInst}"> <th>Instructor</th> </c:if>
                    <c:if test="${listDate}"> <th width = 110px">Date</th> </c:if>
                    <c:if test="${listLoc}">  <th width = "95px">Location</th> </c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${classList}">
                    <tr>
                        <td> ${item.term} </td>
                        <td> ${item.subject} </td>
                        <td> ${item.crnNbr == 0 ? " " : item.courseNbr} </td>
                        <td> ${item.sectionNbr} </td>
                        <td> ${item.title} </td>
                        <c:if test="${listCRN}">  <td> ${item.crnNbr == 0 ? " " : item.crnNbr} </td> </c:if>
                        <c:if test="${listCamp}"> <td> ${item.cmp} </td> </c:if>
                        <c:if test="${listCred}"> <td> ${item.creditHrs} </td> </c:if>
                        <c:if test="${listPart}"> <td> ${item.partOfTerm} </td> </c:if>
                        <td> ${item.dayOfWeek}  </td>
                        <td> ${item.classTimes} </td>
                        <c:if test="${listCap}">  <td> ${item.crnNbr == 0 ? " " : item.capacity} </td> </c:if>
                        <c:if test="${listAct}">  <td> ${item.crnNbr == 0 ? " " : item.actual} </td> </c:if>
                        <c:if test="${listRem}">  <td> ${item.crnNbr == 0 ? " " : item.remaining} </td> </c:if>
                        <c:if test="${listInst}"> <td> ${item.instructor} </td> </c:if>
                        <c:if test="${listDate}"> <td> ${item.classDates} </td> </c:if>
                        <c:if test="${listLoc}">  <td> ${item.location} </td> </c:if>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

</body>
</html>

