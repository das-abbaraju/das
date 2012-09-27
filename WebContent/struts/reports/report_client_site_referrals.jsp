<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>
			<s:text name="ReportClientSiteReferrals.title" />
		</title>
		<s:include value="reportHeader.jsp" />
	</head>
	<body>
		<h1>
			<s:text name="ReportClientSiteReferrals.title" />
		</h1>
		<s:include value="filters.jsp" />
		<div class="right">
			<a class="excel" rel="<s:property value="report.allRows" />" href="#" 
				title="<s:text name="javascript.DownloadAllRows"><s:param><s:property value="report.allRows" /></s:param></s:text>">
				<s:text name="global.Download" />
			</a>
		</div>
		<div style="padding: 5px;">
			<a href="ReferNewClientSite.action" class="add">
				<s:text name="ReportClientSiteReferrals.link.AddClientSiteReferral" />
			</a>
		</div>
		<s:if test="data.size > 0">
			<div>
				<s:property value="report.pageLinksWithDynamicForm" escape="false" />
			</div>
			<table class="report">
				<thead>
					<tr>
						<td colspan="2">
							<s:text name="global.Account.name" />
						</td>
						<td>
							<s:text name="ClientSiteReferral.referringCompany" />
						</td>
						<td>
							<a href="javascript: changeOrderBy('form1','orl.creationDate');">
								<s:text name="global.CreationDate" />
							</a>
						</td>
						<td>
							<s:text name="ClientSiteReferral.label.ContactedBy" />
						</td>
						<td>
							<a href="javascript: changeOrderBy('form1','orl.lastContactDate DESC');">
								<s:text name="ClientSiteReferral.label.On" />
							</a>
						</td>
						<td>
							<s:text name="ClientSiteReferral.label.Attempts" />
						</td>
						<td>
							<s:text name="ClientSiteReferral.label.InPics" />
						</td>
						<s:if test="filter.referralStatus.empty || filter.referralStatus.contains('Closed')">
							<td>
								<s:text name="ReportClientSiteReferrals.label.ClosedDate" />
							</td>
						</s:if>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="data" status="stat" var="crr">
						<tr>
							<td class="right">
								<s:property value="#stat.index + report.firstRowNumber" />
							</td>
							<td>
								<a href="ReferNewClientSite.action?newClientSite=<s:property value="get('id')"/>">
									<s:property value="get('name')" />
								</a>
							</td>
							<td title="<s:property value="get('sourceContact')"/>">
								<s:property value="get('sourceContractorName')"/>
							</td>
							<td>
								<s:date name="get('creationDate')" />
							</td>
							<td>
								<s:property value="get('ContactedBy')" />
							</td>
							<td>
								<s:date name="get('lastContactDate')" />
							</td>
							<td>
								<s:property value="get('contactCount')" />
							</td>
							<td>
								<s:if test="get('opID') != null">
									<a href="FacilitiesEdit.action?operator=<s:property value="get('opID')"/>">
										<s:property value="get('opName')" />
									</a>			
								</s:if>
							</td>
							<s:if test="filter.referralStatus.empty || filter.referralStatus.contains('Closed')">
								<td>
									<s:date name="get('closedOnDate')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
								</td>
							</s:if>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</s:if>
		<div>
			<s:property value="report.pageLinksWithDynamicForm" escape="false" />
		</div>
		<script type="text/javascript">
			$(function() {
				$('#content').delegate('a.excel', 'click', function(e) {
					e.preventDefault();
					var num = $(this).attr('rel');
					
					var confirmed = false;
					if (num > 500)
						confirmed = confirm(translate('JS.ConfirmDownloadAllRows', ['<s:property value="report.allRows" />']));
					else
						confirmed = true;
					
					if (confirmed) {
						newurl = "ReportClientSiteReferralsCSV.action?" + $('#form1').serialize();
						popupWin = window.open(newurl, 'ReportClientSiteReferrals', '');
					}
				});
			});
		</script>
	</body>
</html>
