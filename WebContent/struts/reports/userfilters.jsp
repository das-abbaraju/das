<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />

<div id="search"><s:form id="form1"
	action="%{filter.destinationAction}"
	cssStyle="background-color: #F4F4F4;">

	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />

	<div><s:if test="filter.allowMailMerge">
		<button type="submit" id="write_email_button" name="button"
			value="Write Email" onclick="clickSearchSubmit('form1')"
			class="picsbutton positive" style="display: none">Write
		Email</button>
		<button type="button" name="button" value="Find Recipients"
			onclick="clickSearch('form1')" class="picsbutton">Find Recipients</button>
	</s:if> <s:else>
		<button type="submit" name="button" value="Search"
			onclick="return clickSearch('form1');" class="picsbutton positive">Search</button>
		<br clear="all" />
	</s:else></div>

	<s:if test="filter.showContact">
		<div class="filterOption">Contact Name: <s:textfield
			name="filter.contactName" cssClass="forms" size="15"
			onfocus="clearText(this)" /></div>
	</s:if>

	<s:if test="filter.showUser">
		<div class="filterOption">User Name: <s:textfield
			name="filter.userName" cssClass="forms" size="15"
			onfocus="clearText(this)" /></div>
	</s:if>

	<s:if test="filter.showCompanyName">
		<div class="filterOption">Company Name: <s:textfield
			name="filter.companyName" cssClass="forms" size="15"
			onfocus="clearText(this)" /></div>
	</s:if>

	<s:if test="filter.showPhone">
		<div class="filterOption">Phone Number: <s:textfield
			name="filter.phoneNumber" cssClass="forms" size="15"
			onfocus="clearText(this)" title="must be 9 digits" /></div>
	</s:if>

	<s:if test="filter.showEmail">
		<div class="filterOption">Email Address: <s:textfield
			name="filter.emailAddress" cssClass="forms" size="15"
			onfocus="clearText(this)" /></div>
	</s:if>
	
	<s:if test="filter.showActive">
		<div class="filterOption">Active: 
			<s:select cssClass="forms" list="#{'Yes':'Yes','No':'No','':'ALL'}" name="filter.active" />
		</div>
	</s:if>
	
	<s:if test="filter.showCompanyStatus">
		<div class="filterOption"> 
			<a href="#" onclick="toggleBox('form1_company_status'); return false;">Company Status</a> =
			<span id="form1_company_status_query">ALL</span><br />
			<span id="form1_company_status_select" style="display: none" class="clearLink">
				<s:select list="filter.companyStatusList" multiple="true" cssClass="forms" name="filter.companyStatus" id="form1_company_status" /><br />
				<script type="text/javascript">updateQuery('form1_company_status');</script>
				<a class="clearLink" href="#" onclick="clearSelected('form1_company_status'); return false;">Clear</a>
			</span>
		</div>
	</s:if>
	
	<s:if test="filter.showCompanyType">
		<div class="filterOption"> 
			<a href="#" onclick="toggleBox('form1_company_type'); return false;">Company Type</a> =
			<span id="form1_company_type_query">ALL</span><br />
			<span id="form1_company_type_select" style="display: none" class="clearLink">
				<s:select list="#{'Contractor':'Contractor','Operator':'Operator','Corporate':'Corporate'}" multiple="true" 
					cssClass="forms" name="filter.companyType" id="form1_company_type" /><br />
				<script type="text/javascript">updateQuery('form1_company_type');</script>
				<a class="clearLink" href="#" onclick="clearSelected('form1_company_type'); return false;">Clear</a>
			</span>
		</div>
	</s:if>
	
	<br clear="all" />
	<div class="alphapaging"><s:property
		value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form></div>
<div id="caldiv2"
	style="position: absolute; visibility: hidden; background-color: white; layer-background-color: white;"></div>
