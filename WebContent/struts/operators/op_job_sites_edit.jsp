<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:form id="editJobSite" method="POST" enctype="multipart/form-data" cssStyle="clear: both;">
	<s:hidden name="operator" />
	<s:hidden name="jobSite" />
	<div>
	<fieldset class="form">
		<h2 class="formLegend"><s:text name="%{scope}.label.EditProject" /></h2>
		<ol>
			<li><label><s:text name="JobSite.label" /><span class="redMain">*</span>:</label>
				<input type="text" name="siteLabel" value="<s:property value="jobSite.label" />" size="20" maxlength="15" />
			</li>
			<li><label><s:text name="JobSite.name" /><span class="redMain">*</span>:</label>
				<input type="text" name="siteName" value="<s:property value="jobSite.name" />" size="20" />
			</li>
			<li><label><s:text name="global.City" />:</label>
				<input type="text" name="siteCity" value="<s:property value="jobSite.city" />" size="20" />
			</li>
			<li><label><s:text name="Country" />:</label>
				<s:select list="countryList" name="siteCountry.isoCode" listKey="isoCode" headerKey=""
					headerValue="- Country -" listValue="name" value="jobSite.country.isoCode"></s:select>
			</li>
			<li class="loadCountrySubdivisions">
				<s:if test="jobSite.country.isoCode != ''">
					<label><s:text name="CountrySubdivision" />:</label>
					<s:select list="getCountrySubdivisionList(jobSite.country.isoCode)" name="countrySubdivision.isoCode" listKey="isoCode"
						listValue="name" value="jobSite.countrySubdivision.isoCode"></s:select>
				</s:if>
			</li>
			<li><label><s:text name="JobSite.projectStart" />:</label>
				<input type="text" name="siteStart" value="<s:property value="maskDateFormat(jobSite.projectStart)" />"
					size="10" class="datepicker" />
			</li>
			<li><label><s:text name="JobSite.projectStop" />:</label>
				<input type="text" name="siteEnd" value="<s:property value="maskDateFormat(jobSite.projectStop)" />" size="10"
				class="datepicker" />
			</li>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<s:submit method="update" value="%{getText(scope + '.button.Update')}" cssClass="picsbutton positive" />
		<input type="button" class="picsbutton cancelButton" value="<s:text name="button.Cancel" />" />
		<s:if test="siteID != 0">
			<s:submit value="%{getText('button.Remove')}" cssClass="picsbutton negative" method="remove" id="removeSiteButton" />
		</s:if>
	</fieldset>
	</div>
</s:form>