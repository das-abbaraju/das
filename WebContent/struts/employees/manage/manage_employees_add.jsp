<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:url action="ManageEmployees" var="list_employees">
	<s:param name="account">${account.id}</s:param>
	<s:param name="audit">${audit.id}</s:param>
	<s:param name="questionId">${questionId}</s:param>
</s:url>

<s:include value="_manage-employees-head.jsp" />

<body>
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">

        <s:include value="_manage-employees-header.jsp" />


        <!-- This id is kept only for styling purposes -->
        <a href="${list_employees}" id="addNewEmployee">
            <s:text name="ManageEmployees.list" />
        </a>
        <br />
        <br />

        <s:form>
        	<s:hidden name="account" />
        	<s:hidden name="audit" />
        	<s:hidden name="employee" />
        	<s:hidden name="questionId" />
        	<s:hidden name="employeeId" />

        	<fieldset class="form">
        		<h2 class="formLegend">
        			<s:text name="ManageEmployees.header.EmployeeDetails" />
        		</h2>

        		<ol>

                    <s:include value="_manage-employees-forms.jsp" />

        			<s:if test="account.contractor && allEmployeeGUARDOperators.size > 0">
        				<li>
        					<label>
        						<s:text name="ManageEmployees.EmployeesFacilities" />
        					</label>

        					<s:iterator value="allEmployeeGUARDOperators" var="client">
        						<s:if test="#client.site == null">
        							<%-- HSE Operators --%>
        							<input type="checkbox" name="initialClients" value="${client.id}" id="site_${client.id}" />
        							<label for="site_${client.id}" class="sites-label">
        								${client.name}
        							</label>
        							<br />
        						</s:if>
        						<s:else>
        							<%-- OQ Operators --%>
        							<input type="checkbox" name="initialJobSites" value="${client.site.id}" id="site_${client.site.id}" />
        							<label for="site_${client.id}" class="sites-label">
        								${client.name}: ${client.site.name}
        							</label>
        							<br />
        						</s:else>
        					</s:iterator>
        				</li>
        			</s:if>

        		</ol>
        	</fieldset>

        	<fieldset class="form submit">
    			<s:submit method="save" cssClass="picsbutton positive" value="%{getText('button.Add')}" />
    			<s:submit method="cancel" cssClass="picsbutton" value="%{getText('button.Cancel')}" />
        	</fieldset>

        </s:form>
    </div>
</body>
