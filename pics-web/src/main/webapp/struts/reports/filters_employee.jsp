<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<div id="search">
	<s:if test="allowCollapsed">
		<div id="showSearch" onclick="showSearch()"<s:if test="filtered"> style="display: none"</s:if>>
			<a href="#"><s:text name="Filters.button.ShowFilterOptions" /></a>
		</div>
		<div id="hideSearch"<s:if test="!filtered"> style="display: none"</s:if>>
			<a href="#" onclick="hideSearch()"><s:text name="Filters.button.HideFilterOptions" /></a>
		</div>
	</s:if>
	<s:form id="form1" action="%{filter.destinationAction}">
		<s:hidden name="filter.ajax" />
		<s:hidden name="filter.destinationAction" />
		<s:hidden name="showPage" value="1" />
		<s:hidden name="orderBy" />
		
		<div>
			<button id="searchfilter" type="submit" name="button" value="Search" onclick="return clickSearch('form1');" class="picsbutton positive">
				<s:text name="button.Search" />
			</button>
		</div>
		
		<s:if test="filter.showAccountName">
			<div class="filterOption">
				<s:text name="global.CompanyName" />: <s:textfield name="filter.accountName" size="35" onclick="clearText(this)" />
			</div>
		</s:if>
		
		<div class="clear"></div>
		
		<s:if test="filter.showFirstName">
			<div class="filterOption">
				<s:text name="Employee.firstName" />: <s:textfield name="filter.firstName" />
			</div>
		</s:if>
		
		<s:if test="filter.showLastName">
			<div class="filterOption">
				<s:text name="Employee.lastName" />: <s:textfield name="filter.lastName" />
			</div>
		</s:if>
		
		<s:if test="filter.showEmail">
			<div class="filterOption">
				<s:text name="Employee.email" />: <s:textfield name="filter.email" />
			</div>
		</s:if>
		
		<div class="clear"></div>
		
		<s:if test="filter.showOperators">
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="global.Operators" /></a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span class="clearLink q_box select">
					<s:textfield rel="Operator" name="filter.operators" cssClass="tokenAuto" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
					<s:radio 
						list="#{'false':getTextNullSafe('JS.Filters.status.All'),'true':getTextNullSafe('Filters.status.Any')}" 
						name="filter.showAnyOperator"
						theme="pics"
						cssClass="inline"
					/>
				</span>
			</div>
		</s:if>
		
		<s:if test="filter.showProjects">
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_projects'); return false;"><s:text name="Filters.label.Projects" /></a> =
				<span id="form1_projects_query"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span id="form1_projects_select" style="display: none" class="clearLink">
					<s:select list="filter.projectList" multiple="true" cssClass="forms" name="filter.projects" id="form1_projects" listKey="id" listValue="%{operator.name + ': ' + name}" />
					<br />
					<a class="clearLink" href="#" onclick="clearSelected('form1_projects'); return false;"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
		
		<s:if test="filter.showAssessmentCenter">
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_assessmentCenters'); return false;"><s:text name="global.AssessmentCenter" /></a> =
				<span id="form1_assessmentCenters_query"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span id="form1_assessmentCenters_select" style="display: none" class="clearLink">
					<s:select list="filter.assessmentCenterList" multiple="true" cssClass="forms" name="filter.assessmentCenters" id="form1_assessmentCenters" listKey="id" listValue="name" />
					<br />
					<a class="clearLink" href="#" onclick="clearSelected('form1_assessmentCenters'); return false;"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
		
		<s:if test="filter.showJobRoles">
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_jobRoles'); return false;"><s:text name="Filters.label.JobRoles" /></a> =
				<span id="form1_jobRoles_query"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span id="form1_jobRoles_select" style="display: none" class="clearLink">
					<s:select list="filter.jobRoleList" multiple="true" cssClass="forms" name="filter.jobRoles" id="form1_jobRoles" listKey="id" listValue="name" />
					<br />
					<a class="clearLink" href="#" onclick="clearSelected('form1_jobRoles'); return false;"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
		
		<s:if test="filter.showCompetencies">
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_competencies'); return false;"><s:text name="global.HSECompetencies" /></a> =
				<span id="form1_competencies_query"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span id="form1_competencies_select" style="display: none" class="clearLink">
					<s:select list="filter.competencyList" multiple="true" cssClass="forms" name="filter.competencies" id="form1_competencies" listKey="id" listValue="label" />
					<br />
					<a class="clearLink" href="#" onclick="clearSelected('form1_competencies'); return false;"><s:text name="Filters.status.Clear" /></a>
				</span>
			</div>
		</s:if>
		
		<div class="clear"></div>
		
		<s:if test="filter.showLimitEmployees && permissions.operatorCorporate">
			<div class="filterOption">
				<s:checkbox name="filter.limitEmployees" />
				<s:text name="Filters.label.ShowOnlyMyEmployees" />
			</div>
		</s:if>
	</s:form>
	
	<div class="clear"></div>
</div>