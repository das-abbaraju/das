<%@ taglib prefix="s" uri="/struts-tags"%>

<div id="search">
<s:form id="form1" method="post"
	cssStyle="background-color: #F4F4F4;"
	onsubmit="runSearch( 'form1')">
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />

	<div style="text-align: center; width: 100%">
	<div class="buttons"><a href="#" class="positive"
		onclick="$('form1').submit(); return false;">Search</a></div>
	</div>
	<br clear="all" />
	
	<s:if test="filter.showContact">
		<div class="filterOption"><s:textfield name="filter.contactName"
			cssClass="forms" size="15" onfocus="clearText(this)" title="must be 9 digits" /></div>
	</s:if>

	<s:if test="filter.showUser">
		<div class="filterOption"><s:textfield name="filter.userName"
			cssClass="forms" size="15" onfocus="clearText(this)" title="must be 9 digits" /></div>
	</s:if>
	
	<s:if test="filter.showPhone">
		<div class="filterOption"><s:textfield name="filter.phoneNumber"
			cssClass="forms" size="15" onfocus="clearText(this)" title="must be 9 digits" /></div>
	</s:if>
	
	<s:if test="filter.showEmail">
		<div class="filterOption"><s:textfield name="filter.emailAddress"
			cssClass="forms" size="15" onfocus="clearText(this)" title="must be 9 digits" /></div>
	</s:if>
	
	<br clear="all" />
	<div class="alphapaging"><s:property
		value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form></div>

