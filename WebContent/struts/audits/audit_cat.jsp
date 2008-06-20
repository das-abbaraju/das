<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />

<SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" SRC="js/validateForms.js"></SCRIPT>
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript"
	src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<script type="text/javascript" src="js/aim.js"></script>

<script type="text/javascript">
<s:if test="mode == 'Edit'">
	function saveAnswer( questionid, elm ) {
		var pars = 'auditData.audit.id=<s:property value="conAudit.id"/>&catDataID=<s:property value="catDataID"/>&auditData.question.questionID=' + questionid + '&auditData.answer=';
		if( elm.type == 'text' || elm.type == 'radio' || elm.type == 'textarea')
		{
			var thevalue = elm.value;
			//if( thevalue != '' ) {
			// Save blanks too
				pars = pars + thevalue;
				
				var divName = 'status_'+questionid;
				var myAjax = new Ajax.Updater('','AuditDataSaveAjax.action', 
				{
					method: 'post', 
					parameters: pars,
					onSuccess: function(transport) {
						$('required_td'+questionid).innerHTML = '';
						new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
					}
				});
			//}
		}
		else if( elm.type == 'checkbox')
		{
			var thevalue = '';
			if (elm.checked)
				thevalue = 'X';
			else
				thevalue = ' ';
			pars = pars + thevalue;
			
			var divName = 'status_'+questionid;
			var myAjax = new Ajax.Updater('','AuditDataSaveAjax.action', 
			{
				method: 'post', 
				parameters: pars,
				onSuccess: function(transport) {
					$('required_td'+questionid).innerHTML = '';
					new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
				}
			});
		}
		else
		{
			alert( elm.type + ' ' +elm.value );
		}
		return true;
	}
	
	function startCallback( theId ) {
		var elm = $('status_upload_' + theId);
		elm.innerHTML="<img src='ajax_process.gif' /> Uploading File...";
		elm = $('file_upload_' + theId);
		elm.hide();
		return true;
	}
	
	function completeCallback(theId ) {
		var elm = $('status_upload_' + theId);
		elm.innerHTML="Upload Complete";
		elm = $('show_upload_'+theId);
	    elm.show();

		elm = $('file_upload_'+theId);
	    elm.hide();
	    

	    elm = $('upload_iframe_' + theId);
	    var doc = elm.contentDocument;
        if (doc == undefined || doc == null)
            doc = elm.contentWindow.document;
	    
	    var ext = doc.getElementById('response').innerHTML;

		elm = $('meta_upload_' + theId);
		elm.innerHTML = "<nobr><a id=\"link_" + theId + "\" href=\"#\" onClick=\"openQuestion('" + theId + "', '" + ext + "'); return false;\">View File</a></nobr>";

	    elm = $('show_button_' + theId);
	    elm.value = 'Edit File';

	    return true;
	}
</s:if>
	
	function openOsha(logID, year) {
		url = 'servlet/showpdf?id=<s:property value="contractor.id"/>&OID='+logID+'&file=osha'+year;
		title = 'Osha300Logs';
		pars = 'scrollbars=yes,resizable=yes,width=700,height=450';
		window.open(url,title,pars);
	}

	function openQuestion(questionID, extension) {
		url = 'servlet/showpdf?id=<s:property value="conAudit.id"/>&file=pqf'+extension+questionID;
		title = 'PICS File Upload';
		pars = 'scrollbars=yes,resizable=yes,width=700,height=450';
		window.open(url,title,pars);
	}
</script>

</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<s:include value="audit_cat_nav.jsp" />
<div id="auditToolbar" class="right">
<s:if test="catDataID > 0">
	<s:if test="mode != 'View'">
		<a class="view" href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>">Switch to View Mode</a>
	</s:if>
	<s:if test="mode != 'Edit' && canEdit">
		<a class="edit" href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>&mode=Edit">Switch to Edit Mode</a>
	</s:if>
	<s:if test="mode != 'Verify' && canVerify && currentCategory.category.id != 29">
		<a class="verify" href="?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataID"/>&mode=Verify">Switch to Verify Mode</a>
	</s:if>
</s:if>
	<s:if test="mode == 'View'">
		<a href="javascript:window.print()" class="print">Print</a>
	</s:if>
</div>
<br clear="all" />
<s:iterator value="categories">
	<s:if test="catDataID == id || catDataID == 0">
		<h2>Category <s:property value="category.number"/> - <s:property value="category.category"/></h2>
		<s:if test="category.id == 29">
				<s:if test="mode == 'View'">
					<s:iterator value="contractor.oshas">
						<s:include value="audit_cat_osha.jsp"></s:include>
					</s:iterator>
				</s:if>
				<s:if test="mode == 'Edit'">
					<s:if test="permissions.admin || permissions.contractor">
						<span class="redMain">You must input at least your corporate statistics. To further assist your clients, please
						enter additional locations that you maintain OSHA/MSHA logs for that may be needed by your clients.<br/>
						</span>
					</s:if>
					<s:if test="permissions.contractor">
						<span style="font-size: 12px;color:#003768;">Provide the following numbers (excluding subcontractors) using your OSHA/MSHA 300 Forms from the past 3 years:</span>
					</s:if>
					<s:iterator value="contractor.oshas">
						<s:include value="audit_cat_osha_edit.jsp"></s:include>
					</s:iterator>
					<s:form action="OshaSave" method="POST" enctype="multipart/form-data">
						<s:hidden name="conID" value="%{conAudit.contractorAccount.id}"></s:hidden>
						<s:hidden name="auditID"></s:hidden>
						<s:hidden name="catDataID"></s:hidden>
						<s:hidden name="oshaID" value="%{id}"></s:hidden>
						<s:submit name="submit" value="Add New Location" cssStyle="padding: 6px;position: relative;left: 380px;"></s:submit>
					</s:form>
				</s:if>
		</s:if>
		<s:else>
			<table class="audit">
			<s:iterator value="category.subCategories">
				<tr class="subCategory">
					<td colspan="3">Sub Category <s:property value="category.number"/>.<s:property value="number"/> - 
					<s:property value="subCategory"/>
					<span style="width: 150px;position:absolute;right:10px;font-size: 14px;">* &nbsp;-&nbsp; Required</span>
					</td>
				</tr>
				<s:iterator value="questions">
					<s:if test="isGroupedWithPrevious.toString() == 'No'">
						<s:set name="shaded" value="!#shaded" scope="action"/>
					</s:if>
					
					<s:if test="title.length() > 0">
						<tr class="group<s:if test="#shaded">Shaded</s:if>">
							<td class="groupTitle" colspan="3"><s:property value="title"/></td>
						</tr>
					</s:if>
					<s:if test="mode == 'View'">
						<s:if test="viewBlanks || answer.answer.length() > 0">
							<s:include value="audit_cat_view.jsp"></s:include>
						</s:if>
					</s:if>
					<s:if test="mode == 'Edit'">
						<s:include value="audit_cat_edit.jsp"></s:include>
					</s:if>
					<s:if test="mode == 'Verify'">
						<s:if test="answer.answer.length() > 0">
							<s:include value="audit_cat_verify.jsp"></s:include>
						</s:if>
					</s:if>
				</s:iterator>
			</s:iterator>
			</table>
		</s:else>
	</s:if>
</s:iterator>

<s:include value="audit_cat_nav.jsp" />

</body>
</html>
