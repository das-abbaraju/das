<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Flag Status for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<style type="text/css">
table.report a {
	text-decoration: underline;
}

.hide {
	display: none;
}

.hover {
	margin-left: 10px;
	display: none;
}
small {
	font-size: x-small;
}
.flaggedCriteria {
	float: left;
	width: 33%;
}
</style>
<s:include value="../jquery.jsp" />
<script type="text/javascript">
function checkSubmit(buttonName, dataID) {
	var conID = $('#form_override_id').val();
	var opID = $('#form_override_opID').val();
	var forceFlag = $('#override_flagdata_'+dataID).find("[name='forceFlag']").val();
	var forceEnd = $('#override_flagdata_'+dataID).find("[name='forceEnd']").val();

	var data = {
		button: buttonName,
		dataID: dataID,
		id: conID,
		opID: opID,
		forceFlag: (forceFlag == null) ? '' : forceFlag,
		forceEnd: (forceEnd == null) ? '' : forceEnd
	}

	$('#contractor_flag_data').load('ContractorFlagAjax.action?id='+conID+'&opID='+opID, data,
		function() {
			startThinking({div:'thinking', message:'Updating Flags...'});
			$.ajax({ url:'ContractorCronAjax.action?conID='+conID+'&opID='+opID+'&steps=Flag&steps=WaitingOn',
				success: function () {
					stopThinking({div:'thinking'});
					if (buttonName == "Cancel Data Override")
						$('#contractor_flag_data').load('ContractorFlagAjax.action?id='+conID+'&opID='+opID);
				}}
			);
		}
	);
}
</script>
</head>
<body>

<s:push value="#subHeading='Flag Status'" />
<s:include value="conHeader.jsp" />

<div style="text-align: center; width: 100%">
<s:if test="co.waitingOn.ordinal() > 0"><div class="info" style="float: right; width: 25%;">Currently waiting on <b><s:property value="co.waitingOn"/></b></div></s:if>
<table style="text-align: center;">
	<tr>
		<td rowspan="2" style="vertical-align: middle;"><s:property
			value="co.flagColor.bigIcon" escape="false" /></td>
		<td style="vertical-align: middle;"><b>Overall Flag Status at <s:property value="co.operatorAccount.name"/></b>
		<br/><a href="http://help.picsauditing.com/wiki/Reviewing_Flag_Status" class="help">What does this mean?</a><br/></td>
	</tr>
	<tr>
		<td>
		<s:if test="opID == permissions.getAccountId() || permissions.corporate">
			<s:if test="co.forcedFlag">
				<s:form cssStyle="border: 2px solid #A84D10; background-color: #FFC; padding: 10px;">
					Manual Force Flag <s:property value="co.forceFlag.smallIcon" escape="false" /> until <s:date name="co.forceEnd" format="MMM d, yyyy" />
					<br/>
					<s:hidden name="id" />
					<s:hidden name="opID" 	/>
					<pics:permission perm="EditForcedFlags">
						<s:if test="permissions.corporate">
							<s:checkbox name="overrideAll"/><label>Check to Cancel the Force the Flag Color at all your Facilities in your database</label><br/>
						</s:if>
						Reason:<br><s:textarea name="forceNote" value="" rows="3" cols="15"></s:textarea>
						<div>
							<button class="picsbutton positive" type="submit" name="button" value="Cancel Override">Cancel Override</button>
						</div>
						<br />
					</pics:permission>
				</s:form>
			</s:if>
			<s:else>
				<pics:permission perm="EditForcedFlags">
					<div id="override" style="display: none">
					<s:form id="form_override">
						<s:hidden name="id" />
						<s:hidden name="opID" />
						<s:select list="flagList" name="forceFlag" />
						until 
						<input id="forceEnd" name="forceEnd" size="8" type="text" class="datepicker"/>
						<br/>
						<s:if test="permissions.corporate">
							<s:checkbox name="overrideAll"/><label>Check to Force the Flag Color for all your Facilities in your database</label><br/>
						</s:if>
						Reason: <s:textarea name="forceNote" value="" rows="4" cols="15"></s:textarea><br />
						<span class="redMain">* All Fields are required</span>
						
						<div>
							<button class="picsbutton positive" type="submit" name="button" value="Force Flag">Force Flag</button>
						</div>
					</s:form>
					<a href="#" onclick="$('#override_link').show(); $('#override').hide(); return false;">Nevermind</a>
					</div>
					<a id="override_link" href="#" onclick="$('#override').show(); $('#override_link').hide(); return false;">Manually Force Flag Color</a>
				</pics:permission>
			</s:else>
		</s:if>
		<s:else>
			<s:if test="co.forcedFlag">
			<s:form cssStyle="border: 2px solid #A84D10; background-color: #FFC; padding: 10px;">
				Manual Force Flag <s:property value="co.forceFlag.smallIcon" escape="false" /> until <s:date name="co.forceEnd" format="MMM d, yyyy" />
			</s:form>
			</s:if>
		</s:else>
		</td>
	</tr>
</table>
</div>

<s:if test="permissions.contractor">
<div class="helpOnRight" style="clear: right;">
		The minimum requirements set by <s:property value="co.operatorAccount.name"/> are listed in this page. 
		If any requirements exceed the acceptable threshold or answer, those requirements will be flagged. The overall flag color is set to Red if any requirement is flagged Red. 
		It is set to Amber if any requirement is flagged Amber. If no requirement is Red or Amber, then the overall flag color will be Green.
</div>
</s:if>

<span id="thinking"></span>
<div id="contractor_flag_data"><s:include value="contractor_flag_data.jsp"></s:include></div>

<s:if test="co.operatorAccount.approvesRelationships.toString() == 'Yes'">
	<s:if test="co.workStatusPending">
		<div class="alert">The operator has not approved this contractor yet.</div>
	</s:if>
	<s:if test="co.workStatusRejected">
		<div class="alert">The operator did not approve this contractor.</div>
	</s:if>
</s:if>

<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

</body>
</html>
								
