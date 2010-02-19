<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Flag Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=20091231" />
<style type="text/css">
#impactDiv {
	float: right;
	clear: none;
	width: 300px;
}

#criteriaDiv {
	float: left;
	clear: none;
	margin-right: 20px;
}

#addCriteria {
	clear: both;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function checkSubmit(criteriaID) {
	var checked = confirm('Are you sure you want to remove this criteria?');
	var data = {
			button: 'delete',
			criteriaID: criteriaID,
			id: <s:property value="account.id" />
		};
	$('#criteriaDiv').load('ManageFlagCriteriaOperatorAjax.action', data);
}
function addCriteria(criteriaID) {
	var hurdle = $('#'+criteriaID).find("[name='newHurdle']").val();
	
	var data = {
			button: 'add',
			criteriaID: criteriaID,
			newFlag: $('#'+criteriaID).find("[name='newFlag']").val(),
			newHurdle: hurdle == null ? '' : hurdle,
			id: <s:property value="account.id" />
		};
	$('#criteriaDiv').load('ManageFlagCriteriaOperatorAjax.action', data);
}
function getImpact(criteriaID) {
	var data = {
			button: 'impact',
			criteriaID: criteriaID,
			id: <s:property value="account.id" />
		};
	startThinking({div:'impact_thinking', message:'Fetching impact...'});
	$('#impactDiv').load('ManageFlagCriteriaOperatorAjax.action', data,
		function() {
			stopThinking({div:'impact_thinking'});
			$(this).show('slow');
		}
	);
}
function getAddQuestions() {
	var layer = '#addCriteria';
	if ($(layer).is(':hidden')) {
		var data= {
			id: <s:property value="account.id" />,
			button: 'questions'
		};
		startThinking({div:'question_thinking', message:'Fetching criteria...'});
		$(layer).load('ManageFlagCriteriaOperatorAjax.action', data, 
			function() {
				stopThinking({div:'question_thinking'});
				$(this).show('slow');
			}
		);
	} else {
		$(layer).hide('slow');
	}
}
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<div id="dialog" style="display:none"></div>

<div style="vertical-align: top">
<s:form id="form1" method="get">
	<div id="mainThinkingDiv" style="position: absolute; top: -15px; left: 20px;"></div>
	<div id="growlBox"></div>
	<div id="impactDiv"></div>
	<div id="criteriaDiv"><s:include value="op_manage_flag_criteria_list.jsp"></s:include></div>
	<div style="clear: both; margin: 10px 0px;">
		<a href="#" onclick="getAddQuestions(); return false;" class="picsbutton">Add New Criteria</a>
		<span id="question_thinking"></span>
		<span id="impact_thinking"></span>
	</div>
	<div id="addCriteria" style="display:none"></div>
</s:form>
</div>

</body>
</html>
