<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>


<title>Reset Selenium</title>

<h2> Accounts available for deletion: </h2>
<hr/>
<s:if test="%{DBAccounts.size > 0}">
	<s:form action="ResetSelenium!delete" method="post" theme="pics">
		<s:iterator var="act" value="dBAccounts">
			<ul>
				<s:if test="%{Type == 'USER'}">
					<input type="checkbox" name="DBUsers" value="${act.getID()}" />
				</s:if>
				<s:elseif test="%{Type == 'EMPLOYEE'}">
					<input type="checkbox" name="DBEmployees" value="${act.getID()}" />
				</s:elseif>
				<s:else>
					<input type="checkbox" name="DBAccounts" value="${act.getID()}" />
				</s:else>
				<strong><s:property value="Name" /></strong>
				<s:property value="Type" />
			</ul>
		</s:iterator>
		<br /> 
		<s:submit cssClass="btn danger" value="Delete Selected" />
		<a href="ResetSelenium!deleteAll.action" class="btn danger picsbutton">Delete All</a>
	</s:form>
</s:if>
<s:else><h4>There are no accounts available for deletion.</h4></s:else>