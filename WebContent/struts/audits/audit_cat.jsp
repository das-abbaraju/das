<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp" pageEncoding="UTF-8"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<meta name="help" content="<s:property value="conAudit.auditType.classType"/>">

<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />

<script type="text/javascript" src="js/validateForms.js"></script>
<script type="text/javascript" src="js/audit_cat_edit.js?v=<s:property value="version"/>"></script>

<script type="text/javascript">
	var auditID = <s:property value="auditID"/>;
	var catDataID = <s:property value="catDataID"/>;
	var conID = <s:property value="conAudit.contractorAccount.id"/>;
	var mode = '<s:property value="#parameters.mode"/>';

	function openOsha(logID) {
		url = 'DownloadOsha.action?id='+logID;
		title = 'Osha300Logs';
		pars = 'scrollbars=yes,resizable=yes,width=700,height=450';
		window.open(url,title,pars);
	}
</script>
<style>
label.policy {
	color:#003768;
	font-weight:bold;
	margin-right:0.3em;
	margin-left: 1em;
}
</style>
</head>
<body>

<s:include value="audit_catHeader.jsp"/>

<s:if test="auditID > 0">
	<s:if test="!singleCat">
		<s:include value="audit_cat_nav.jsp" />
	</s:if>
</s:if>

<div id="auditToolbar" class="right">
<s:if test="catDataID > 0">	
	<s:if test="mode != 'View'">
		<a class="view" href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>&mode=View">Switch to View Mode</a>
	</s:if>
	<s:if test="mode != 'Edit' && canEdit">
		<a class="edit" href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>&mode=Edit">Switch to Edit Mode</a>
	</s:if>
	<s:if test="mode != 'Verify' && canVerify">
		<a class="verify" href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>&mode=Verify">Switch to Verify Mode</a>
	</s:if>
</s:if>
	<s:if test="mode == 'View' || mode == 'ViewQ' || catDataID == 0">
		<a href="javascript:window.print()" class="print">Print</a>
	</s:if>
</div>

<br clear="all"/>
<s:if test="mode == 'Edit'">
	<div
		<s:if test="'done' == button">
			class="alert"
		</s:if>
	>
	<div class="requiredLegend">Starred questions are required to continue from this page</div>
	</div>
</s:if>
<s:iterator value="categories">
	<s:if test="catDataID == id || (catDataID == 0 && appliesB)">
		<s:if test="category.id in { 151, 157, 158 }">
			<s:include value="audit_cat_sha.jsp"></s:include>
		</s:if>
		<s:else>
			<s:if test="category.validSubCategories.size() > 0">
				<h2>Category <s:property value="category.number"/> - <s:property value="category.category"/></h2>
				<s:set name="shaded" value="true" scope="action"/>
				<s:iterator value="category.validSubCategories">
					<div class="subCategory">
						<s:if test="category.validSubCategories.size() > 1">
							<h3 class="subCategory">
								Sub Category <s:property value="category.number"/>.<s:property value="number"/> - 
								<s:property value="subCategory" escape="false"/>
							</h3>
						</s:if>
						<s:if test="helpText != null && helpText.length() > 0">
							<div class="alert"><s:property value="helpText" escape="false"/></div>
						</s:if>
						<s:if test="id == 461"><a href="JobCompetencyMatrix.action?id=<s:property value="contractor.id"/>" target="_BLANK" title="opens in new window">Job Competency Matrix</a></s:if>
						
						<s:iterator value="questions">
							<s:if test="valid">
								<s:if test="title != null && title.length() > 0">
									<h4 class="groupTitle">
										<s:property value="title" escape="false"/>
									</h4>
								</s:if>
								<s:if test="mode == 'ViewQ'">
									<div class="question <s:if test="shaded">shaded</s:if>">
										<s:include value="audit_cat_questions.jsp"></s:include>
									</div>
								</s:if>
								
								<s:set name="q" value="[0]" />
								<!-- Single Leaf Question -->
								<s:set name="a" value="answerMap.get(#q.id)" />
								<s:set name="visible" value="#q.visible" />
								<s:if test="onlyReq && !#a.hasRequirements">
									<s:set name="visible" value="false" />
								</s:if>
								<s:if test="!viewBlanks && (#a == null || #a.answer == null || #a.answer.length() == 0)">
									<s:set name="visible" value="false" />
								</s:if>
								<s:if test="#visible">
									<s:if test="#q.isGroupedWithPrevious.toString() == 'No'">
										<s:set name="shaded" value="!#shaded" scope="action"/>
									</s:if>
									<div id="node_<s:property value="#q.id"/>" class="clearfix question <s:if test="#shaded">shaded</s:if>">
										<s:include value="audit_cat_question.jsp"></s:include>
									</div>
								</s:if>
							</s:if>
						</s:iterator>
					</div>
				</s:iterator>

			</s:if>
		</s:else>
	</s:if>
</s:iterator>

<s:if test="conAudit.auditType.classType.policy">
<!-- Once we remove the Sub Category 1.3 above, we can start displaying this header
	<div class="subCategory">
		<h3 class="subCategory">
			Sub Category 1.3 - Operator Requirements &amp; File Uploads
		</h3>
	</div>
 -->
	<s:if test="conAudit.operators.size() > 0">
		<s:sort source="conAudit.operators" comparator="caoComparator">
		<s:iterator id="cao">
			<s:if test="#cao.isVisibleTo(permissions)">
				<s:if test="#cao.visible">
					<div class="caoGroup" id="cao<s:property value="id"/>">
						<h4 style="margin-left: 40px"><s:property value="#cao.operator.name" /></h4>
						<div style="position: absolute; right: 0; top: 0; float: left;" id="thinking_<s:property value="#cao.id"/>"></div>
						<div id="cao_<s:property value="#cao.id"/>" style="margin-left: 20px; margin-bottom: 20px; background-color: #F9F9F9;">
							<s:include value="audit_cat_cao.jsp"/>
						</div>
					</div>
				</s:if>
				<s:else>
					<div class="caoGroup" id="cao<s:property value="id"/>">
						<h3 style="margin-left: 40px"><s:property value="#cao.operator.name" /></h3>
						<div style="position: absolute; right: 0; top: 0; float: left;" id="thinking_<s:property value="#cao.id"/>"></div>
						<div id="cao_<s:property value="#cao.id"/>" style="margin-left: 20px; margin-bottom: 20px;">
							<div class="info">
								This policy is not required for this operator.
								<s:form id="cao_form%{#cao.id}">
									<s:hidden name="auditID" value="%{#cao.audit.id}"/>
									<s:hidden name="cao.id" value="%{#cao.id}"/>
									<s:hidden name="mode"/>
									<input type="submit" class="picsbutton positive" name="button" value="Make Required"
										onclick="saveCao('#cao_form<s:property value="#cao.id"/>', 'visible', 'cao_<s:property value="#cao.id"/>');return false;"/>
								</s:form>
							</div>
						</div>
					</div>
				</s:else>
			</s:if>
		</s:iterator>
		</s:sort>
	</s:if>
	<s:else>
		<div class="alert">No operators are currently requesting your insurance.</div>
	</s:else>
</s:if>

<s:if test="catDataID > 0">
	<br clear="all"/>
	<pics:permission perm="InsuranceVerification">
		<s:if test="conAudit.auditType.classType.policy">
			<div class="buttons" style="float:right">
				<s:if test="nextPolicyID > 0">
					<a class="picsbutton button" href="AuditCat.action?auditID=<s:property value="nextPolicyID"/>"> Next Policy &gt;</a>
				</s:if>
				<a class="picsbutton button" href="PolicyVerification.action?filter.visible=Y&filter.caoStatus=Submitted&button=getFirst"> Oldest Policy &gt;&gt;</a>
			</div>
			<br clear="all"/>
		</s:if>
	</pics:permission>
	<s:if test="!singleCat">
		<div class="buttons" style="float: right;">
			<s:if test="nextCategory == null">
				<a href="Audit.action?auditID=<s:property value="auditID"/>" class="picsbutton positive">Next &gt;&gt;</a>
			</s:if>
			<s:else>
				<a href="AuditCat.action?auditID=<s:property value="auditID"/>&catDataID=<s:property value="nextCategory.id"/>&mode=<s:property value="mode"/>" class="picsbutton positive">Next &gt;&gt;</a>
			</s:else>
		</div>
		<br clear="all"/>
		<s:include value="audit_cat_nav.jsp" />
	</s:if>
	<s:else>
		<s:if test="conAudit.percentComplete < 100 && conAudit.auditStatus.pending && !conAudit.auditType.classType.policy">
			<div class="info" class="buttons" style="">
				<a href="Audit.action?auditID=<s:property value="auditID"/>" class="picsbutton positive">Done</a>
			Click Done when you're ready to submit the <s:property value="conAudit.auditType.auditName"/>
			</div>
		</s:if>
	</s:else>
	<s:if test="canClose">
                <div class="alert" class="buttons" style="">
                        <s:hidden name="auditStatus" value="Active" />
                        <s:submit value="%{'Close '.concat(conAudit.auditType.auditName)}"/>
                </div>
        </s:if>

</s:if>
</body>
</html>
