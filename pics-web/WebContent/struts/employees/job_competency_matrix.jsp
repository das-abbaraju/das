<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
	<head>
		<title>
			<s:text name="JobCompetencyMatrix.title" />
		</title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		
		<style>
			.selected {
				background-color: LightBlue;
				text-align: center;
				font-weight: bold;
				color: #003768;
				padding: 0;
				vertical-align: middle;
			}
			table.report td.notselected {
				background-color: white;
				padding: 0;
				vertical-align: middle;
			}
			table.legend {
				clear: both;
				margin: 5px 0px;
			}
			table.legend td {
				padding: 3px;
				vertical-align: middle;
			}
			div.box {
				width: 20px;
				height: 20px;
				border: 1px solid #012142;
			}
		</style>
		
		<s:include value="../jquery.jsp" />
	</head>
	
	<body>
		<s:if test="audit.id > 0">
			<div class="info">
				<s:text name="JobCompetencyMatrix.Step2">
					<s:param>
						<s:property value="audit.id" />
					</s:param>
					<s:param>
						<s:text name="AuditType.99.name" />
					</s:param>
				</s:text>
			</div>
		</s:if>
		
		<h1>
			<s:property value="account.name" />
			<span class="sub">
				<s:text name="JobCompetencyMatrix.title" />
			</span>
		</h1>
		
		<div class="right">
			<s:url action="JobCompetencyMatrix" method="download" var="download">
				<s:param name="account" value="%{account.id}" />
			</s:url>
			<a class="excel" href="${download}" target="_BLANK" 
				title="<s:text name="javascript.DownloadAllRows"><s:param value="%{competencies.size}" /></s:text>">
				<s:text name="global.Download" />
			</a>
		</div>
		
		<s:if test="permissions.contractor || permissions.admin">
			<s:url action="ManageJobRoles" var="jobRoles" escapeAmp="false">
				<s:param name="account" value="%{account.id}" />
				<s:param name="audit" value="%{audit.id}" />
				<s:param name="questionId" value="%{questionId}" />
			</s:url>
			<a href="${jobRoles}">
				<s:if test="permissions.contractor">
					<s:text name="JobCompetencyMatrix.ReturnToEditJobRoles" />
				</s:if>
				<s:else>
					<s:text name="ManageJobRoles.title" />
				</s:else>
			</a>
			<br />
		</s:if>
		
		<table class="report">
			<thead>
				<tr>
					<th colspan="2"><s:text name="JobCompetencyMatrix.header.HSECompetency" /></th>
					<s:iterator value="roles">
						<th><s:property value="name" /></th>
					</s:iterator>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="competencies" id="competency">
					<s:if test="getRoles(#competency) != null">
						<tr>
							<td><s:property value="#competency.label" /></td>
							<td><img src="images/help.gif" alt="<s:property value="#competency.label" />" title="<s:property value="#competency.category" />: <s:property value="#competency.description" />" /></td>
							<s:iterator value="getRoles(#competency)" id="role">
								<s:if test="getJobCompetency(#role, #competency).id > 0">
									<td class="selected"><img alt="X" src="images/checkBoxTrue.gif"></td>
								</s:if>
								<s:else>
									<td class="notselected"></td>
								</s:else>
							</s:iterator>
						</tr>
					</s:if>
				</s:iterator>
			</tbody>
		</table>
		
		<table class="legend">
			<tr>
				<td><div class="box selected"><img alt="X" src="images/checkBoxTrue.gif"></div></td>
				<td><s:text name="JobCompetencyMatrix.help.CompetencyRequired" /></td>
			</tr>
		</table>
		<br />
		<div class="info">
			<a href="resources/HSECompetencyReview.pdf">
				<s:text name="ManageJobRoles.link.QuestionReviewPDF" />
			</a>
			<br />
			<s:text name="ManageJobRoles.help.QuestionReviewPDF" />
		</div>
	</body>
</html>