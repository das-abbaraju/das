<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
	<title>
		<s:text name="RegistrationGapAnalysis.title" />
	</title>
	
	<link rel="stylesheet" type="text/css" href="css/reports.css" />
</head>
<body>
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
	
	<s:if test="possibleMatches.empty">
		<s:text name="Filters.paging.NoResultsFound" />
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
				</tr>
			</thead>
			<tbody>
				<s:set name="position" value="1" />
				<s:iterator value="possibleMatches.keySet()" var="registered">
					<s:url action="ContractorView" var="contractor_view">
						<s:param name="id">
							${registered.id}
						</s:param>
					</s:url>
					<s:iterator value="possibleMatches.get(#registered)" var="requested">
						<s:url action="RequestNewContractorAccount" var="request_contractor">
							<s:param name="requestedContractor">
								${requested.id}
							</s:param>
						</s:url>
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
									${requested.name}
								</a>
							</td>
						</tr>
						<s:set name="position" value="#position + 1" />
					</s:iterator>
				</s:iterator>
			</tbody>
		</table>
		
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
						<s:text name="RegistrationGapAnalysis.ContractorMatchedOn" />
					</th>
					<th>
						<s:text name="RegistrationGapAnalysis.RequestMatchedOn" />
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
						<s:url action="RequestNewContractorAccount" var="request_contractor">
							<s:param name="requestedContractor">
								${match.requested.id}
							</s:param>
						</s:url>
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
							<td>
								${match.getMatchingValues(registered)}
							</td>
							<td>
								${match.getMatchingValues(match.requested)}
							</td>
						</tr>
						<s:set name="position" value="#position + 1" />
					</s:iterator>
				</s:iterator>
			</tbody>
		</table>
	</s:else>
</body>