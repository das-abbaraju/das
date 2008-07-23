<%@ taglib prefix="s" uri="/struts-tags"%>

<div id="search">
<div id="showSearch" onclick="showSearch()"
	<s:if test="filtered">style="display: none"</s:if>><a href="#">Show
Filter Options</a></div>
<div id="hideSearch" <s:if test="!filtered">style="display: none"</s:if>><a
	href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<s:form id="form1" method="post"
	cssStyle="background-color: #F4F4F4;"
	onsubmit="runSearch( 'form1')">
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />

	<div style="text-align: center; width: 100%">
	<div class="buttons"><a href="#" class="positive"
		onclick="$('form1').submit(); return false;">Search</a></div>
	</div>
	<br clear="all" />
	

	<s:if test="filterUser">
		<div class="filterOption"><s:textfield name="UserName"
			cssClass="forms" size="15" onfocus="clearText(this)" title="must be 9 digits" /></div>
	</s:if>

	<s:if test="filterContact">
		<div class="filterOption"><s:textfield name="ContactName"
			cssClass="forms" size="15" onfocus="clearText(this)" title="must be 9 digits" /></div>
	</s:if>
	
	<s:if test="filterPhone">
		<div class="filterOption"><s:textfield name="PhoneNumber"
			cssClass="forms" size="15" onfocus="clearText(this)" title="must be 9 digits" /></div>
	</s:if>
	
	<s:if test="filterEmail">
		<div class="filterOption"><s:textfield name="EmailAddress"
			cssClass="forms" size="15" onfocus="clearText(this)" title="must be 9 digits" /></div>
	</s:if>
	
	<br clear="all" />
	<div class="alphapaging"><s:property
		value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form></div>

