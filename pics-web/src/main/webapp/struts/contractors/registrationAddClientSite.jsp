<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<title><s:text name="ContractorRegistration.title" /></title>

<s:if test="contractor.operatorAccounts.empty">
	<s:set var="display_client_site_right" value="%{'none'}" />
	<s:set var="display_client_site_get_started" value="%{'block'}" />
	<s:set var="class_client_site_info" value="%{'info'}" />
</s:if>
<s:else>
	<s:set var="display_client_site_right" value="%{'block'}" />
	<s:set var="display_client_site_get_started" value="%{'none'}" />
	<s:set var="class_client_site_info" value="%{''}" />
</s:else>

<div class="registration-header">
	<section>
		<s:include value="/struts/contractors/registrationStep.jsp">
			<s:param name="step_current" value="1" />
			<s:param name="step_last" value="1" />
		</s:include>
	</section>
</div>

<s:if test="hasActionErrors()">
	<s:actionerror cssClass="action-error alert-message error" />
</s:if>

<div class="client-site">
	<h1><s:text name="RegistrationAddClientSite.AddClientSites" /></h1>
	
	<s:form id="RegistrationAddClientSiteFilter" cssClass="client-site-filter" method="GET" theme="pics">
		<ul>
			<li class="search">
				<s:textfield name="searchValue" label="%{getText('RegistrationAddClientSite.SearchInstructions')}" autofocus="autofocus" />
			</li>
			<li class="actions">
				<s:submit 
					method="search" 
					key="button.Search" 
					cssClass="btn info" 
				/>
			</li>
		</ul>
	</s:form>
	
	<s:form cssClass="client-site-form" theme="pics">
		<s:hidden name="contractor" />
		
		<section>
			<div class="client-site-left">
				<label><s:text name="RegistrationAddClientSite.Select" /></label>
				<a class="show-all-client-site" href="javascript:;"><s:text name="RegistrationAddClientSite.ShowAll" /></a>
				
				<div class="client-site-list-container">
					<div class="arrow"></div>
					
					<s:set var="client_site_list_position" value="%{'left'}" />
					<s:set var="client_site_list" value="searchResults" />
					<s:include value="/struts/contractors/_registrationAddClientSiteList.jsp" />
				</div>
			</div>
			
			<div class="client-site-right" style="display: ${display_client_site_right}">
				<label><s:text name="RegistrationAddClientSite.Selected" /></label>
				
				<div class="client-site-list-container">
					<s:set var="client_site_list_position" value="%{'right'}" />
					<s:set var="client_site_list" value="contractor.operatorAccounts" />
					<s:include value="/struts/contractors/_registrationAddClientSiteList.jsp" />
				</div>
			</div>
			
			<div class="client-site-get-started" style="display: ${display_client_site_get_started}">
				<section>
					<img src="images/iconset_project_email.png" />
					
					<div class="information-container">
						<h1><s:text name="RegistrationAddClientSite.Invitation" /></h1>
						<h2><s:text name="RegistrationAddClientSite.Requested" /></h2>
					</div>
				</section>
				
				<section>
					<img src="images/iconset_project_search.png" class="search"/>
					
					<div class="information-container">
						<h1><s:text name="RegistrationAddClientSite.FindSites" /></h1>
						<h2><s:text name="RegistrationAddClientSite.OtherSites" /></h2>
					</div>
				</section>
			</div>
			
			<br style="clear:both" />
			
			<ul class="actions">
				<li>
					<s:submit
						method="nextStep" 
						key="button.SaveNext"
						cssClass="btn success" 
					/>
				</li>
			</ul>
		</section>
		
	</s:form>
</div>