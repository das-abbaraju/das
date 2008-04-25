<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Schedule &amp; Assign Audits</title>
<script type="text/javascript" src="js/prototype.js" ></script> 
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js?load=effects" ></script>
<SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
<script type="text/javascript">
	function saveAudit(auditId) {
		var pars = "contractorAudit.id=" + auditId;
		
		var auditor = $F($('auditor_' + auditId));
		if( auditor != '' && auditor != 0)
		{
			pars = pars + "&auditor.id=" + auditor;
		} 

		var thisdate = $F($('scheduled_date_' + auditId + '_date'));
		if( thisdate != '' )
		{
			var thisTime = $('scheduled_date_' + auditId + '_time').options[$('scheduled_date_' + auditId + '_time').selectedIndex].text;
			thisdate = thisdate + ' ' + thisTime;
			pars = pars + "&contractorAudit.scheduledDate=" + thisdate;	
		}

		thisdate = $F($('assigned_date_' + auditId + '_date'));
		if( thisdate != '' )
		{
			var thisTime = $('assigned_date_' + auditId + '_time').options[$('assigned_date_' + auditId + '_time').selectedIndex].text;
			thisdate = thisdate + ' ' + thisTime;
			pars = pars + "&contractorAudit.assignedDate=" + thisdate;	
		}

		var location = $F($('auditlocation_' + auditId));
		if( location != '' )
		{
			pars = pars + "&contractorAudit.auditLocation=" + location;
		}

		if( pars != "contractorAudit.id=" + auditId )
		{
			//alert( pars );
			var myAjax = new Ajax.Request('AuditAssignmentUpdateAjax.action', {method: 'post', parameters: pars});	
		}

		var divName = 'audit_'+auditId;
		new Effect.Highlight(divName, {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});

		return false;
	}
</script>
<style>
td.reportDate {
	text-align: center;
}
</style>
</head>
<body>
<h1>Schedule &amp; Assign Audits</h1>

<s:form id="form1" method="post">
	<table>
	<tr>
		<td style="vertical-align: middle;"><s:textfield name="name" cssClass="forms" size="8" onfocus="clearText(this)" />
			<s:select list="auditTypeList" cssClass="forms" name="auditTypeID" listKey="auditTypeID" listValue="auditName" />
			<s:select list="operatorList" cssClass="forms" name="operator" listKey="id" listValue="name" />
		</td>
		<td><s:submit name="imageField" type="image" src="images/button_search.gif" onclick="runSearch( 'form1')" /></td>
	</tr>
	</table>
	<s:hidden name="showPage" value="1"/>
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
</s:form>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr>
	<td><a class="blueMain" href="AuditAssignments.action">Reset</a></td>
	<td align="left"><s:property value="report.startsWithLinksWithDynamicForm" escape="false"/></td>
	<td align="right"><s:property value="report.pageLinksWithDynamicForm" escape="false"/></td>
  </tr>
</table>

<s:form id="assignScheduleAuditsForm" method="post" cssClass="forms">
<table border="0" cellpadding="1" cellspacing="1" align="center" width="100%">
	<tr bgcolor="#003366" class="whiteTitle"> 
		<td><a href="?orderBy=a.name" class="whiteTitle">Contractor</a></td>
		<td align="center"><a href="?orderBy=auditType" class="whiteTitle">Type</a></td>
		<td align="center"><a href="?orderBy=createdDate DESC" class="whiteTitle">Created</a></td>
		<td align="center"><a href="?orderBy=auditorID DESC,name" class="whiteTitle">Auditor</a></td>
		<td align="center"><a href="?orderBy=assignedDate DESC" class="whiteTitle">Assigned</a></td>
		<td align="center"><a href="?orderBy=scheduledDate,a.name" class="whiteTitle">Scheduled</a></td>
		<td align="center"><a href="?orderBy=auditLocation,a.name" class="whiteTitle">Location</a></td>
		<td align="center">Exempt</td>
		<td></td>
	</tr>
	<s:iterator value="data">
	<tr id="audit_<s:property value="[0].get('auditID')"/>" class="blueMain" <s:property value="color.nextBgColor" escape="false" />>
		<td><a href="pqf_view.jsp?auditID=<s:property value="[0].get('auditID')"/>" 
			class="blueMain"><s:property value="[0].get('name')"/></a>
		</td>
		<td><nobr><s:property value="[0].get('auditName')"/></nobr></td>
		<td class="reportDate"><s:date name="[0].get('createdDate')" format="M/d/yy" /></td>
		<td><s:select id="auditor_%{[0].get('auditID')}" list="auditorList" cssClass="forms" value="%{[0].get('auditorID')}" name="operator" listKey="id" listValue="name" /></td>
		<td class="reportDate">
				<nobr>
					<input name="assigned_date_<s:property value="[0].get('auditID')"/>_date" id="assigned_date_<s:property value="[0].get('auditID')"/>_date" cssClass="forms"  size="5" type="text" 
							onClick="cal1.select(this,'assigned_date_<s:property value="[0].get('auditID')"/>_date','M/d/yy'); return false;" 
							value="<s:property value="getBetterDate( [0].get('assignedDate'), 'MM/dd/yy hh:mm:ss a.000')"/>"/>
					  <s:select list="@com.picsauditing.PICS.DateBean@getBusinessTimes()" listKey="key" 
					  		listValue="value" 
					  		name="assigned_date_%{[0].get('auditID')}_time" 
					  		id="assigned_date_%{[0].get('auditID')}_time"
					  		value="%{@com.picsauditing.PICS.DateBean@getIndexForTime(getBetterTime( [0].get('assignedDate'), 'MM/dd/yy hh:mm:ss a.000')) }"
					  		/>
				</nobr>	 		
		
		</td>
		<td>
				<nobr><input name="scheduled_date_<s:property value="[0].get('auditID')"/>_date" id="scheduled_date_<s:property value="[0].get('auditID')"/>_date" cssClass="forms"  size="5" type="text" 
							onClick="cal1.select(this,'scheduled_date_<s:property value="[0].get('auditID')"/>_date','M/d/yy'); return false;" 
							value="<s:property value="getBetterDate( [0].get('scheduledDate'), 'MM/dd/yy hh:mm:ss a.000')"/>"/>
					  <s:select list="@com.picsauditing.PICS.DateBean@getBusinessTimes()" listKey="key" 
					  		listValue="value" 
					  		name="scheduled_date_%{[0].get('auditID')}_time" 
					  		id="scheduled_date_%{[0].get('auditID')}_time"
					  		value="%{@com.picsauditing.PICS.DateBean@getIndexForTime(getBetterTime( [0].get('scheduledDate'), 'MM/dd/yy hh:mm:ss a.000')) }"
					  		/>
				</nobr>
		</td>
		<td><s:textfield id="auditlocation_%{[0].get('auditID')}" value="%{[0].get('auditLocation')}" /></td>
		<td></td>
		<td id="1audit_<s:property value="[0].get('auditID')"/>"><s:submit value="Save" onclick="javascript: return saveAudit('%{[0].get('auditID')}');" cssClass="forms" /></td>
	</tr>
	</s:iterator>
</table>
</s:form>
<center>
<s:property value="report.pageLinksWithDynamicForm" escape="false"/>
</center>
		
</body>
</html>
