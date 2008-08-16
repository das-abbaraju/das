<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
function toggleBox(name) {
	var box = $(name+'_select');
	var result = $(name+'_query');
	result.hide();
	box.toggle();
	if (box.visible())
		return;

	updateQuery(name);
	result.show();
}

function updateQuery(name) {
	var box = $(name);
	var result = $(name+'_query');
	var queryText = '';
	var values = $F(box);
	for(i=0; i < box.length; i++) {
		if (box.options[i].selected) {
			if (queryText != '') queryText = queryText + ", ";
			queryText = queryText + box.options[i].text;
		}
	}
	
	if (queryText == '') {
		queryText = 'ALL';
	}
	result.update(queryText);
}

</script>

<div id="search">

<s:form id="form1" method="post"
	cssStyle="background-color: #F4F4F4;"
	onsubmit="runSearch('form1')">
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />

	<div style="text-align: center; width: 100%">
	<div class="buttons"><a href="#" class="positive"
		onclick="$('form1').submit(); return false;">Search</a></div>
	</div>
	<br clear="all" />

	<div class="filterOption"><a href="#"
		onclick="toggleBox('auditorId'); return false;">Auditors</a> = <span
		id="auditorId_query">ALL</span><br />
	<span id="auditorId_select" style="display: none" class="clearLink">
	<s:action name="AuditorsGet" executeResult="true">
		<s:param name="controlName" value="%{'auditorId'}" />
		<s:param name="presetValue" value="auditorId" />
	</s:action> 
	<script type="text/javascript">updateQuery('auditorId');</script> <br />
	<a class="clearLink" href="#"
		onclick="clearSelected('auditorId'); return false;">Clear</a> </span></div>

	<div class="filterOption"><a href="#"
		onclick="toggleBox('form1_auditTypeID'); return false;">Audit Type</a> = <span 
		id="form1_auditTypeID_query">ALL</span><br />
	<span id="form1_auditTypeID_select" style="display: none" class="clearLink">
	<s:select list="auditTypeList"
		cssClass="forms" name="auditTypeID" listKey="auditTypeID"
		listValue="auditName" multiple="true" size="5" /> 
		<script	type="text/javascript">updateQuery('form1_auditTypeID');</script> <br />
	<a class="clearLink" href="#"
		onclick="clearSelected('form1_auditTypeID'); return false;">Clear</a>
	</span></div>
	
	<br clear="all" />
</s:form></div>

