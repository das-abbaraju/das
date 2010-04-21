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
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
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

	<br/>
	<br/>

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
</s:form>

<div class="clear"></div>
</div>