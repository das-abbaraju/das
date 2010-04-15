<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Define Competencies</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<style type="text/css">
fieldset.bottom {
	float:left;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
</head>
<body>
<s:include value="../operators/opHeader.jsp"/>

				<a onclick="$('#addCompetencyMenu').show(); $('#addCompetencyLink').hide(); return false;"
					href="#" id="addCompetencyLink" class="add">Add Competency</a>
				<div id="addCompetencyMenu" style="display: none; clear: both;">
					<s:form id="newCompetencyMenu" method="POST" enctype="multipart/form-data">
						<s:hidden name="id" />
						<fieldset class="form bottom">
							<legend><span>Define Competency</span></legend>
							<ol>
								<li>
									<label>Category:</label>
									<s:textfield maxlength="50" id="category_autocomplete" name="category" />
										
									<script type="text/javascript"> 
										$('#category_autocomplete').autocomplete('CategorySuggestAjax.action');
									</script>
								</li>
								<li>
									<label>Label:</label>
									<s:textfield name="label" maxlength="15" size="15"/>
								</li>
								<li>
									<label>Help Page:</label>
									<s:textfield maxlength="100" size="52" name="helpPage" />
								</li>
								<li>
									<label>Description:</label>
									<s:textarea name="description" cols="40" rows="5" />
								</li>
								<li class="right">
									<input type="submit" value="Save" class="picsbutton positive" name="button" />
									<button onclick="$('#addCompetencyLink').show(); $('#addCompetencyMenu').hide(); return false;"
											class="picsbutton negative">Cancel</button>
								</li>
							</ol>
						</fieldset>
					</s:form>
				</div>
				<s:if test="competencies.size > 0">
					<table class="report">
						<thead>
							<tr>
								<th>Category</th>
								<th>Label</th>
								<th>Description</th>
								<th>Percent Used</th>
								<th>Help Page</th>
								<th>Edit</th>
								<th>Delete</th>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="competencies" id="competency">
								<tr>
									<td><s:property value="#competency.category"/></td>
									<td><s:property value="#competency.label"/></td>
									<td><s:property value="#competency.description"/></td>
									<td class="center">0%</td>
									<td><a href="#">www.wikipedia.org</a></td>
									<td class="center">
										<a href="#" onclick="editCompetency(<s:property value="competency.id" />); return false;" class="edit"></a>
									</td>
									<td class="center">
										<a href="DefineCompetencies.action?id=<s:property value="operator.id" />&competencyID=<s:property value="#competency.id" />&button=Remove"
											class="remove"></a>
									</td>
								</tr>			
							</s:iterator>
						</tbody>
					</table>
				</s:if>
				<s:else>
					<div class="info">No Competencies Exist. Please Add a Competency.</div>
				</s:else>
	</body>
</html>
