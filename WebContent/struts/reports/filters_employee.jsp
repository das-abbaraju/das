<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<div id="search">
<s:if test="allowCollapsed">
	<div id="showSearch" onclick="showSearch()"<s:if test="filtered"> style="display: none"</s:if>>
		<a href="#">Show Filter Options</a>
	</div>
	<div id="hideSearch"<s:if test="!filtered"> style="display: none"</s:if>>
		<a href="#" onclick="hideSearch()">Hide Filter Options</a>
	</div>
</s:if>
<s:form id="form1" action="%{filter.destinationAction}">
	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="orderBy" />
	
	<div>
		<button id="searchfilter" type="submit" name="button" value="Search"
				onclick="checkStateAndCountry('form1_state','form1_country'); return clickSearch('form1');"
				class="picsbutton positive">Search</button>
	</div>
	
	<s:if test="filter.showAccountName">
		<div class="filterOption">
			Company Name: <s:textfield name="filter.accountName" size="35" />
		</div>
	</s:if>
	<s:if test="filter.showFirstName">
		<div class="filterOption">
			First Name: <s:textfield name="filter.firstName" />
		</div>
	</s:if>
	<s:if test="filter.showLastName">
		<div class="filterOption">
			Last Name: <s:textfield name="filter.lastName" />
		</div>
	</s:if>
	<s:if test="filter.showEmail">
		<div class="filterOption">
			Email: <s:textfield name="filter.email" />
		</div>
	</s:if>
	<s:if test="filter.showSsn">
		<div class="filterOption">
			SSN: <s:textfield name="filter.ssn" cssClass="ssn"/>
		</div>
	</s:if>
	<s:if test="filter.showLimitEmployees && permissions.operatorCorporate">
		<div class="filterOption">
			<s:checkbox name="filter.limitEmployees" /> Show Only My Employees
		</div>
	</s:if>
	
	<s:if test="filter.showProjects">
		<div class="filterOption">
			<a href="#" onclick="toggleBox('form1_projects'); return false;">Projects</a> =
			<span id="form1_projects_query">ALL</span>
			<br />
			<span id="form1_projects_select" style="display: none" class="clearLink">
				<s:select list="filter.getProjectList(permissions)" multiple="true" cssClass="forms"
					name="filter.projects" id="form1_projects" listKey="id" listValue="label" />
				<br />
				<script type="text/javascript">updateQuery('form1_projects');</script>
				<a class="clearLink" href="#" onclick="clearSelected('form1_projects'); return false;">Clear</a>
			</span>
		</div>
	</s:if>
</s:form>

<div class="clear"></div>
</div>