<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage SubCategory</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects,dragdrop,controls" type="text/javascript"></script>
<style type="text/css">
#list {
	margin:0;
	margin-top:10px;
	padding:0;
	list-style-type: none;
	width:350px;
}
#list li {
	margin:0;
	margin-bottom:4px;
	padding:5px;
	border:1px solid #888;
	cursor:move;
}
</style>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />

<table>
	<tr>
		<td style="vertical-align: top">
		<s:form id="save">
		<s:hidden name="id" />
		<s:hidden name="parentID" value="%{subCategory.category.id}" />
		<s:hidden name="subCategory.category.id" />
			<div>
				<fieldset>
				<legend><span>Details</span></legend>
				<ol>
					<li><label>ID:</label>
						<s:if test="category.id > 0">
							<s:property value="category.id" />
						</s:if>
						<s:else>
							NEW
						</s:else>
					</li>
					<li>Sub Category Name:
						<s:textfield name="subCategory.subCategory" size="50" />
					</li>				
				</ol>
				</fieldset>
				<br clear="all">
			</div>
			<br clear="all">
			<div class="buttons">
				<button class="positive" name="button" type="submit" value="save">Save</button>
			<s:if test="subCategory.questions.size == 0">
				<button name="button" type="submit" value="delete">Delete</button>
			</s:if>
			</div>			
		</s:form>
		</td>
	<s:if test="id != 0">
		<td style="vertical-align: top">
			<div>
				<ul id="list">
				<s:iterator value="subCategory.questions">
				    <li id="item_<s:property value="questionID"/>" title="Drag and drop to change order"><s:property value="number"/>. <a href="ManageQuestion.action?id=<s:property value="questionID"/>"><s:property value="question.length()>50 ? question.substring(0,47) + '...' : question"/></a></li>
				</s:iterator>
				</ul>
			
				<a href="ManageQuestion.action?button=AddNew&parentID=<s:property value="subCategory.id"/>&question.subCategory.id=<s:property value="subCategory.id"/>">Add New Question</a>
				<script type="text/javascript">
				//<![CDATA[
				Sortable.create("list", 
					{onUpdate:function(){new Ajax.Updater('list-info', 'OrderAuditChildrenAjax.action?id=<s:property value="subCategory.id"></s:property>&type=AuditSubCategory', {asynchronous:true, evalScripts:true, onComplete:function(request){new Effect.Highlight("list",{});}, parameters:Sortable.serialize("list")})}})
				//]]>
				</script>
				<div id="list-info"></div>
			</div>
		</td>
	</s:if>
</tr>
</table>
</body>
</html>