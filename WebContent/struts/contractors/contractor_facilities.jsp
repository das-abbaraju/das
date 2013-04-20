<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
	<title>
		<s:property value="contractor.name" />
		<s:text name="global.Facilities" />
	</title>

	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=${version}" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=${version}" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=${version}" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=${version}" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/contractor/contractor_facilities.css?v=${version}" />
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
								<nobr>
									<input type="text"
                                           id="contractor_facilities_search_box"
                                           name="search"
                                           placeholder="<s:text name="ContractorFacilities.SearchNameOrLocation" />"
                                           value="${search}"
                                    />
                                    <input type="button"
                                           class="picsbutton positive"
                                           name="method:search"
                                           value="<s:text name="button.Search" />"
                                    />
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