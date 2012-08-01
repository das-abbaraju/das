<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<div id="search">
	<s:form id="form1">
		<s:hidden name="filter.ajax" />
		<s:hidden name="filter.destinationAction" />
		<s:hidden name="filter.allowMailMerge" />
		<s:hidden name="showPage" value="1" />
		<s:hidden name="filter.startsWith" />
		<s:hidden name="orderBy" />
		<s:hidden name="accountType" />
		
		<div>
			<button id="searchfilter" type="submit" name="button" value="Search"
				onclick="checkCountrySubdivisionAndCountry('form1_state','form1_country'); return clickSearch('form1');"
				class="picsbutton positive"><s:text name="button.Search" /></button>
		</div>
		
		<s:if test="filter.showAccountName">
			<div class="filterOption"><s:textfield name="filter.accountName"
				cssClass="forms" size="18" onfocus="clearText(this)" /></div>
		</s:if>
	
		<s:if test="filter.showStatus">
			<div class="filterOption"><a href="#"
				onclick="toggleBox('form1_status'); return false;"><s:text name="global.Status" /></a> = <span
				id="form1_status_query"><s:text name="JS.Filters.status.All" /></span><br /><span id="form1_status_select"
				style="display: none" class="clearLink"><s:select
				list="filter.statusList" multiple="true" cssClass="forms"
				name="filter.status" id="form1_status" /><br />
			<a class="clearLink" href="#"
				onclick="clearSelected('form1_status'); return false;"><s:text name="Filters.status.Clear" /></a> </span></div>
		</s:if>
		
		<s:if test="filter.showType">
			<div class="filterOption"><a href="#"
				onclick="toggleBox('form1_type'); return false;"><s:text name="global.Type" /></a> = <span
				id="form1_type_query"><s:text name="JS.Filters.status.All" /></span><br /><span id="form1_type_select"
				style="display: none" class="clearLink"><s:select
				list="filter.typeList" multiple="true" cssClass="forms"
				name="filter.type" id="form1_type" /><br />
			<a class="clearLink" href="#"
				onclick="clearSelected('form1_type'); return false;"><s:text name="Filters.status.Clear" /></a> </span></div>
		</s:if>
		
		<s:if test="filter.showAddress">
			<div class="filterOption">
			<table>
				<tr>
					<td rowspan="2" style="vertical-align: top"><s:text name="global.Address" />: <s:textfield name="filter.city"
						cssClass="forms" size="15" onfocus="clearText(this)" /> <s:textfield
						name="filter.zip" cssClass="forms" size="5"
						onfocus="clearText(this)" /></td>
					<td style="padding-left: 1ex;"><a href="#"
						onclick="toggleBox('form1_state'); return false;"><s:text name="CountrySubdivision" /></a> = <span
						id="form1_state_query"><s:text name="JS.Filters.status.All" /></span></td>
					<td style="padding-left: 1ex;"><a href="#"
						onclick="toggleBox('form1_country'); return false;"><s:text name="Country" /></a> = <span
						id="form1_country_query"><s:text name="JS.Filters.status.All" /></span></td>
				</tr>
				<tr>
					<td style="padding-left: 1ex;"><span id="form1_state_select"
						style="display: none" class="clearLink"><s:select
						id="form1_state" name="filter.countrySubdivision" list="filter.countrySubdivisionList"
						listKey="isoCode" listValue="name" cssClass="forms" multiple="true"
						size="15" onclick="clearSelected('form1_country');" /><br />
					<a class="clearLink" href="#"
						onclick="clearSelected('form1_state'); return false;"><s:text name="Filters.status.Clear" /></a> </span></td>
					<td style="padding-left: 1ex; vertical-align: top"><span
						id="form1_country_select" style="display: none" class="clearLink"><s:select
						id="form1_country" name="filter.country" list="filter.countryList"
						listKey="isoCode" listValue="name" cssClass="forms" multiple="true"
						size="15" /><br />
					<a class="clearLink" href="#"
						onclick="clearSelected('form1_country'); return false;"><s:text name="Filters.status.Clear" /></a> </span>
					</td>
				</tr>
			</table>
			</div>
		</s:if>
		
		<s:if test="filter.showIndustry">
			<br clear="all" />
			<div class="filterOption"><a href="#"
				onclick="toggleBox('form1_industry'); return false;"><s:text name="Filters.label.Industry" /></a> = <span
				id="form1_industry_query"><s:text name="JS.Filters.status.All" /></span><br />
			<span id="form1_industry_select" style="display: none"
				class="clearLink"> <s:select id="form1_industry"
				name="filter.industry" list="filter.industryList"
				listValue="description" cssClass="forms" multiple="true" size="5" />
			<br />
			<a class="clearLink" href="#"
				onclick="clearSelected('form1_industry'); return false;"><s:text name="Filters.status.Clear" /></a> </span></div>
		</s:if>
		
		<s:if test="filter.showPrimaryInformation">
			<div class="filterOption"><label><s:checkbox
				name="filter.primaryInformation" /> <s:text name="Filters.label.ShowContactInfo" /></label></div>
		</s:if>
	
		<s:if test="filter.showTradeInformation">
			<div class="filterOption"><label><s:checkbox
				name="filter.tradeInformation" /> <s:text name="Filters.label.ShowTradeInfo" /></label></div>
		</s:if>
	
		<pics:permission perm="DevelopmentEnvironment">
			<div class="filterOption"><label><s:text name="Filters.label.QueryAPI" /></label> <s:textfield
				name="filter.customAPI" /></div>
		</pics:permission>
		
		<br clear="all" />
		<div class="alphapaging">
			<s:property value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
	</s:form>
</div>