<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<head>
	<title>
		<s:text name="SubcontractorFacilities.title" />
	</title>
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=${version}" />
</head>
<body>
	<s:include value="../contractors/conHeader.jsp" />
	
	<div class="info">
		<s:text name="SubcontractorFacilities.RemovingSitesHelp">
			<s:param>${picsPhoneNumber}</s:param>
		</s:text>
	</div>
	
	<s:form id="save" method="POST" enctype="multipart/form-data">
		<fieldset class="form">
			<s:hidden name="contractor" />
			<h2 class="formLegend">
				<s:text name="SubcontractorFacilities.title" />
			</h2>
			<ol>
				<li id="linked_clients" style="${display_linked_client}">
					<label>
						<s:text name="FacilitiesEdit.LinkedClientAccount" />:
					</label>
					<s:optiontransferselect
						label="Selected Clients"
						name="selectedClientsLeft"
						list="notSelectedClientSites"
						listKey="id"
						listValue="name"
						doubleName="clients"
						doubleList="selectedClientSites"
						doubleListKey="id"
						doubleListValue="name"
						leftTitle="%{getText('FacilitiesEdit.OperatorsList')}"
						rightTitle="%{getText('FacilitiesEdit.SelectedClients')}"
						allowAddToLeft="false"
						addToRightLabel="%{getText('FacilitiesEdit.Assign')}"
						allowAddAllToLeft="false"
						allowAddAllToRight="false"
						allowSelectAll="false"
						allowUpDownOnLeft="false"
						allowUpDownOnRight="false"
						buttonCssClass="arrow"
						theme="pics"
					/>
				</li>
			</ol>
		</fieldset>
		<fieldset class="form submit">
			<s:submit cssClass="picsbutton positive" method="save" value="%{getText('button.Save')}" />
		</fieldset>
	</s:form>
</body>