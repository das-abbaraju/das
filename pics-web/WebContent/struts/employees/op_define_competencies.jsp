<%@ page language="java" errorPage="/exception_handler.jsp" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<head>
	<title><s:text name="DefineCompetencies.title"/></title>
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=${version}"/>
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=${version}"/>
	<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=${version}"/>
	<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=${version}"/>
	<style type="text/css">
		fieldset.bottom {
			float: left;
		}

		#item input[type=text], #item textarea {
			font-size: normal;
		}

		#item {
			display: none;
		}
	</style>
</head>
<body>
<div class="DefineCompetencies-page page" id="${actionName}_${methodName}_page">
	<h1>
		<s:text name="DefineCompetencies.title"/>
			<span class="sub">
				${operator.name}
			</span>
	</h1>
	<a href="javascript:;" id="add_competency_link" class="add" data-operator="${operator.id}">
		<s:text name="DefineCompetencies.link.AddHSECompetency"/>
	</a>
	<table class="report">
		<thead>
		<tr>
			<th></th>
			<th><s:text name="OperatorCompetency.category"/></th>
			<th><s:text name="OperatorCompetency.label"/></th>
			<th><s:text name="OperatorCompetency.description"/></th>
			<th><s:text name="OperatorCompetency.courses"/></th>
			<th><s:text name="button.Edit"/></th>
		</tr>
		</thead>
		<tbody>
		<s:iterator value="operator.competencies" var="operator_competency" status="operator_competency_status">
			<tr id="comp_${operator_competency.id}">
				<td>${operator_competency_status.count}</td>
				<td>${operator_competency.category}</td>
				<td>${operator_competency.label}</td>
				<td>${operator_competency.description}</td>
				<td>
					<s:iterator value="#operator_competency.courses" var="course" status="course_iterator">
						<s:text name="%{#course.courseType.i18nKey}"/><s:if test="!#course_iterator.last">, </s:if>
					</s:iterator>
				</td>
				<td class="center">
					<a href="javascript:;" class="edit" data-competency="${operator_competency.id}"
					   data-operator="${operator.id}"></a>
				</td>
			</tr>
		</s:iterator>
		</tbody>
	</table>

	<div id="competency_form"></div>
</div>
</body>