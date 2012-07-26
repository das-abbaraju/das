<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
	<title>
		<s:property value="contractor.name" />
		<s:text name="global.Facilities" />
	</title>

	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
    
    <style>
        .operatorlocation
        {
            padding-left: 10px;
            font-size: x-small;
            color: gray;
        }
        
        #results
        {
            padding-top: 10px;
        }
    </style>
</head>
<body>
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<s:include value="conHeader.jsp" />
	
		<s:if test="permissions.contractor && contractor.status.pendingDeactivated">
			<s:if test="msg != null && msg.length() > 0">
				<div class="error">
					<s:property value="msg" />
				</div>
			</s:if>
		</s:if>
	
		<br clear="all" />
	
		<table width="100%">
			<tr>
				<td style="width: 44%; vertical-align: top;">
					<h3>
						<s:text name="ContractorFacilities.ContractorFacilities.SelctedFacilities" />
					</h3>
					<div id="thinkingDiv"></div>
	
					<div id="facilities">
						<s:include value="contractor_facilities_assigned.jsp" />
					</div>
	
					<s:if test="permissions.admin">
						<s:if test="contractor.hasAuditWithOnlyInvisibleCaos()">
							<div class="alert">
								This contractor has some audits with no visible
								caos on them. When you disassociate a contractor with an operator some
								data is kept in our system, but is not visible to external users. Audits
								that fall under this case are marked as such in the audit.
							</div>
						</s:if>
					</s:if>
	
					<pics:permission perm="EditNotes" type="Edit">
						<div id="notesList">
							<s:include value="../notes/account_notes_embed.jsp"></s:include>
						</div>
					</pics:permission>
				</td>
				<td style="width: 2%"></td>
				<td style="width: 44%; vertical-align: top;">
					<h3>
						<s:text name="ContractorFacilities.ContractorFacilities.AddFacilities" />
					</h3>
					<form id="facility_search">
						<s:hidden name="id" />
						<div id="search">
							<div class="buttons" style="min-height: 30px;">
								<button class="picsbutton positive">
									<s:text name="global.Search" />
								</button>
								<nobr>
									<s:text name="ContractorFacilities.ContractorFacilities.Search.Name" />:
									<s:textfield cssClass="forms clearable" name="operator.name" id="search_operator" />
								</nobr>
	
								<nobr>
									<s:text name="global.Location" />
									:
									<s:select
										cssClass="forms clearable"
										headerKey=""
										headerValue="- %{getText('ContractorFacilities.StateOrProvince')} -"
										id="search_location"
										list="getStateList('US|CA')"
										listKey="isoCode"
										listValue="english"
										name="state" />
								</nobr>
							</div>
						</div>
					</form>
	
					<div id="results"></div>
				</td>
			</tr>
		</table>
		<br clear="all" />
	</div>
</body>