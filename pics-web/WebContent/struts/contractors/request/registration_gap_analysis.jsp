<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- URLs --%>
<s:url action="RegistrationGapAnalysis" var="registration_gap_analysis" />

<head>
	<title>
		<s:text name="RegistrationGapAnalysis.title" />
	</title>
	
	<link rel="stylesheet" type="text/css" href="css/reports.css" />
	
	<style type="text/css">
		a.copy {
			padding-left: 18px;
			margin-left: 2px;
			background: url('../images/copy.png') no-repeat left center;
			zoom: 1;
		}
	</style>
</head>
<body>
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<h1>
			<s:text name="RegistrationGapAnalysis.title" />
		</h1>
		
		<div id="search">
			<s:form>
				<div class="filterOption">
					Find recently registered contractors in the past <s:textfield size="5" name="daysAgo" /> days
				</div>
				<s:submit value="%{getText('button.Search')}" cssClass="picsbutton positive" />
			</s:form>
			<div class="clear"></div>
		</div>
		
		<br class="clear" />
		
		<s:if test="matches.isEmpty()">
			<div class="info">
				<s:text name="Filters.paging.NoResultsFound" />
			</div>
		</s:if>
		<s:else>
			<table class="report">
				<thead>
					<tr>
						<th></th>
						<th>
							<s:text name="global.Contractor" />
						</th>
						<th>
							<s:text name="RequestNewContractor.title" />
						</th>
						<th>
							<s:text name="ContractorRegistrationRequest.matchCount" />
						</th>
						<th>
							<s:text name="RegistrationGapAnalysis.CopyDataOver" />
						</th>
						<th>
							<s:text name="RegistrationGapAnalysis.DeactivateAsDuplicate" />
						</th>
					</tr>
				</thead>
				<tbody>
					<s:set name="position" value="1" />
					<s:iterator value="matches.keySet()" var="registered">
						<s:url action="ContractorView" var="contractor_view">
							<s:param name="id">
								${registered.id}
							</s:param>
						</s:url>
						<s:iterator value="matches.get(#registered)" var="match">
						
							<%-- URLS --%>
							<s:url action="RequestNewContractorAccount" var="request_contractor">
								<s:param name="contractor">
									${match.requested.id}
								</s:param>
							</s:url>

							<s:url action="CopyContractorInfo" var="copy_contractor">
								<s:param name="fromRequestedContractor">
									${match.requested.id}
								</s:param>
								<s:param name="toContractorAccount">
									${registered.id}
								</s:param>
								<s:param name="deactivateWhenCopied" value="true" />
								<s:param name="url">
									${registration_gap_analysis}
								</s:param>
							</s:url>
							
							<s:url method="deactivateDuplicate" var="deactivate_duplicate">
								<s:param name="original">
									${registered.id}
								</s:param>
								<s:param name="duplicate">
									${match.requested.id}
								</s:param>
							</s:url>
							<%-- URLS --%>
							
							<tr>
								<td>
									${position}
								</td>
								<td>
									<a href="${contractor_view}">
										${registered.name}
									</a>
								</td>
								<td>
									<a href="${request_contractor}">
										${match.requested.name}
									</a>
								</td>
								<td>
									${match.matchedOn}
								</td>
								<td class="center">
									<a
										href="${copy_contractor}"
										class="copy"
										data-contractor="${registered.name}"
										data-request="${match.requested.name}"></a>
								</td>
								<td class="center">
									<a href="${deactivate_duplicate}" class="remove"></a>
								</td>
							</tr>
							<s:set name="position" value="#position + 1" />
						</s:iterator>
					</s:iterator>
				</tbody>
			</table>
		</s:else>
	</div>
</body>