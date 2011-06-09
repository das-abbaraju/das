<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="%{conAudit.auditType.getI18nKey('name')}" /> for <s:property value="conAudit.contractorAccount.name" /></title> 
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css" />

<pics:permission perm="ManageCategoryRules">
	<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
</pics:permission>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
	var messageLoadingRequirements = '<s:text name="Audit.javascript.LoadingRequirements" />';
	var messageLoadingCategory = '<s:text name="Audit.javascript.LoadingCategory" />';
	var messageLoadingAllCategories = '<s:text name="Audit.javascript.LoadingAllCategories" />';
	var messageLoadingAnsweredQuestions = '<s:text name="Audit.javascript.LoadingAnsweredQuestions" />';
	var messageLoadingPreview = '<s:text name="Audit.javascript.LoadingPreview" />';
</script>
<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js"></script>
<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
<script type="text/javascript" src="js/con_audit.js?v=<s:property value="version"/>"></script>
<script type="text/javascript" src="js/audit_data_save.js?v=<s:property value="version"/>"></script>
<script type="text/javascript">
	var auditID = '<s:property value="conAudit.id"/>';
	var conID = '<s:property value="id"/>';
	<s:if test="conAudit.categories.size == 0">
	$(function() {
		updateCategoriesNow();
	});
	</s:if>
	$(function() {
		<s:if test="!permissions.operatorCorporate">
			$('a.passAudit').live('click', function() {
				window.location.href = $(this).attr('href') + ($(this).attr('href').indexOf("?") > 0 ? "&" : "?") + "auditID=" + auditID;
				return false;
			});
		</s:if>
		<s:else>
			$('a.operatorViewable').live('click', function() {
				window.location.href = $(this).attr('href') + "?id=" + conID;
				return false;
			});
		</s:else>
		$('#auditProblems, #problemsHide').live('click', function(){
			$('#problems').slideDown();
			$('#problemsHide').hide();
		});
		$('#problems .bottom').live('click', function(){
			$('#problems').slideUp();
			$('#problemsHide').show();
		});

		$('#importPQFCluetipLink').cluetip({
			arrows: true,
			cluetipClass: 'jtip',
			local: true,
			clickThrough: false,
			activation: 'click',
			sticky: true,
			showTitle: false,
			closeText: "<img src='images/cross.png' width='16' height='16'>",
			width: 675
		});
	});
	
	function clearLinks() {
		<s:if test="permissions.operatorCorporate">
			$('a.passAudit').not('.operatorViewable').each(function() {
				var plaintext = $(this);
				plaintext.after(plaintext.text());
				plaintext.remove();
			});
		</s:if>
		<s:else>
			return false;
		</s:else>
	}
</script>
</head>
<body>
<s:include value="../audits/audit_catHeader.jsp"/>

<s:if test="policy">
	<a href="PolicyVerification.action?button=getFirst" class="picsbutton"><s:text name="Audit.button.FirstPolicy" /></a>
	<a href="PolicyVerification.action?button=showNext&auditID=<s:property value="auditID" />" class="picsbutton positive"><s:text name="Audit.button.NextPolicy" /> &gt;&gt;</a>
	<br clear="all" />
</s:if>

<s:if test="conAudit.auditType.classType.policy && conAudit.hasCaoStatus('Incomplete') && problems.size() > 0">
	<div id="auditProblems">
		There are problems with your audit, please click here to review them.  You can also click on the status for each operator with an <span class="problemCao">icon</span> to see more info.
	</div>
	<div id="problems">
		<table class="report" style="margin: 0 auto;">
			<thead>
				<tr>
					<th>Operator</th>
					<th>Problem</th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="problems" var="pro">
					<tr>
						<td><s:property value="getViewableCaops(#pro.key).iterator.next.name"/></td>
						<td> <s:property value="#pro.value"/></td>
					</tr>
				</s:iterator>	
			</tbody>
		</table>
		
		<div class="bottom">Hide</div>
	</div>
	<div id="problemsHide">
	</div>
</s:if>
<div id="submitRemind"></div>

<div class="right noprint" id="modes">
	<s:if test="canEditAudit">
		<a class="edit modeset" href="#mode=Edit"><s:text name="button.Edit" /></a>
	</s:if>
	<a class="view modeset" href="#mode=View"><s:text name="button.View" /></a>
	<s:if test="canVerifyAudit">
		<a class="verify modeset" href="#mode=Verify"><s:text name="button.Verify" /></a> 
	</s:if>
	<span style="display: none;" id="printReqButton"><a class="print" href="javascript:window.print();"><s:text name="button.Print" /></a></span>
</div>

<table id="audit-layout">
	<tr>
		<td class="auditHeaderSideNav noprint">
			<div id="auditHeaderSideNav">
				<s:include value="con_audit_sidebar.jsp"/>
			</div>
		</td>
		<td style="width: 100%; height: 100%;">
			<div id="auditViewArea"></div>
		</td>
	</tr>
</table>

<s:if test="!@com.picsauditing.util.Strings@isEmpty(auditorNotes)">
	<div class="info">
		<b><s:text name="Audit.message.SafetyProfessionalNotes" />:</b> <s:property value="auditorNotes"/>
	</div>
</s:if>
<br clear="all"/>
</body>
</html>
