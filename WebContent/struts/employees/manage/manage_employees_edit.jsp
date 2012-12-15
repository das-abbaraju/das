<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:url action="ManageEmployees" var="list_employees">
	<s:param name="account">${account.id}</s:param>
	<s:param name="audit">${audit.id}</s:param>
	<s:param name="questionId">${questionId}</s:param>
</s:url>

<s:url action="EmployeePhotoUpload" var="employee_photo_upload">
	<s:param name="employee">
		${employee.id}
	</s:param>
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

    	<a href="EmployeeDetail.action?employee=<s:property value="employee.id" />">
    		<s:text name="ManageEmployees.link.ViewProfile" />
    	</a>

    	<br clear="all" />

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

    				<s:if test="employee.photo.length() > 0">
    					<li>
    						<label><s:text name="Employee.photo" />:</label>
    						<s:url action="EmployeePhotoStream" var="employee_photo_crop">
    							<s:param name="employeeID">
    								${employee.id}
    							</s:param>
    						</s:url>
    						<a href="${employee_photo_upload}" class="edit">
    							<img id="cropPhoto" src="${employee_photo_crop}" style="width: 25px; height: 25px; vertical-align: bottom;" />
    						</a>
    					</li>
    				</s:if>
    				<s:else>
    					<li>
    						<label><s:text name="ManageEmployees.label.UploadPhoto" />:</label>
    						<a href="${employee_photo_upload}" class="add">
    							<s:text name="button.Add" />
    						</a>
    					</li>
    				</s:else>

        		</ol>
        	</fieldset>

    		<s:if test="showJobRolesSection">
    			<fieldset class="form">
    				<h2 class="formLegend">
    					<s:text name="ManageEmployees.header.JobRoles" />
    				</h2>
    				<div id="employee_role">
    					<s:include value="manage_employees_roles.jsp" />
    				</div>
    			</fieldset>
    		</s:if>

    		<div id="employee_site">
    			<s:include value="manage_employees_sites.jsp" />
    		</div>

    		<s:if test="employee.account.requiresOQ">
                <s:include value="_manage-employees-edit-oq.jsp" />
    		</s:if>

        	<fieldset class="form submit">
        		<s:if test="employee.status.toString().equals('Active')">
        			<s:submit method="save" cssClass="picsbutton positive" value="%{getText('button.Save')}" />
        			<s:submit method="cancel" cssClass="picsbutton" value="%{getText('button.Cancel')}" />

    				<s:submit method="inactivate" cssClass="picsbutton negative" value="%{getText('button.Inactivate')}" />
        		</s:if>
        		<s:else>
        			<s:submit method="activate" cssClass="picsbutton positive" value="%{getText('button.Activate')}" />

        			<s:submit method="delete" cssClass="picsbutton negative" id="deleteEmployee" value="%{getText('button.Delete')}" />
        		</s:else>
        	</fieldset>

        </s:form>
    </div>
</body>
