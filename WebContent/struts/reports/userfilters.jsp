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
			onclick="clickSearch('form1')">Find Recipients</button>
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

	<br clear="all" />
	<div class="alphapaging"><s:property
		value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form></div>
<div id="caldiv2"
	style="position: absolute; visibility: hidden; background-color: white; layer-background-color: white;"></div>
