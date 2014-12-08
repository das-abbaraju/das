<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>Klark User Manager</title>
    <style type="text/css">
        body {
            font-family: sans-serif;
        }
        .data, .data td {
            border-collapse: collapse;
            width: 100%;
            border: 1px solid #aaa;
            margin: 2px;
            padding: 2px;
        }
        .data th {
            font-weight: bold;
            background-color: #5C82FF;
            color: white;
        }
    </style>
</head>
<body>

<h2>User Manager</h2>

<form:form method="post" action="add.html" commandName="contact">
    <table>
    <tr>
        <td><form:label path="firstname"><spring:message code="label.firstname"/></form:label></td>
        <td><form:input path="firstname" /></td> 
    </tr>
    <tr>
        <td><form:label path="lastname"><spring:message code="label.lastname"/></form:label></td>
        <td><form:input path="lastname" /></td>
    </tr>
    <tr>
        <td><form:label path="email"><spring:message code="label.email"/></form:label></td>
        <td><form:input path="email" /></td>
    </tr> 
    <tr>
        <td><form:label path="password"><spring:message code="label.password"/></form:label></td>
        <td><form:input path="password" /></td>
    </tr>
       <tr>
        <td><form:label path="birthDate"><spring:message code="label.birthdate"/></form:label></td>
        <td><form:input path="birthDate" /></td>
    </tr>
     <tr>
        <td><form:label path="gender"><spring:message code="label.gender"/></form:label></td>
        <td><form:input path="gender" /></td>
    </tr>
       
        <tr>
        <td><form:label path="zipcode"><spring:message code="label.zipcode"/></form:label></td>
        <td><form:input path="zipcode" /></td>
        </tr>
    
    <tr>
        <td colspan="2">
            <input type="submit" value="<spring:message code="label.addcontact"/>"/>
        </td>
    </tr>
</table>    
</form:form>

    
<h3>Users</h3>
<c:if  test="${!empty contactList}">
<table class="data">
<tr>
    <th>Name</th>
    <th>Email</th>
    <th>Telephone</th>
    <th>&nbsp;</th>
</tr>
<c:forEach items="${contactList}" var="contact">
    <tr>
        <td>${contact.lastname}, ${contact.firstname} </td>
        <td>${contact.email}</td>
        <td>${contact.zipcode}</td>
        <td><a href="delete/${contact.id}">delete</a></td>
    </tr>
</c:forEach>
</table>
</c:if>


</body>
</html>
