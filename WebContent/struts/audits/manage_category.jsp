<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Category</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/mcdropdown/css/jquery.mcdropdown.css" />
<style type="text/css">
.subCategories {
	position: relative;
	padding-left: 2em;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/jquery.bgiframe.min.js"></script>
<script type="text/javascript" src="js/jquery/mcdropdown/jquery.mcdropdown.min.js"></script>
<script type="text/javascript">
$(function(){
	var sortList = $('#list').sortable({
		update: function() {
			$('#list-info').load('OrderAuditChildrenAjax.action?id=<s:property value="category.id"/>&type=AuditCategory', 
				sortList.sortable('serialize').replace(/\[|\]/g,''), 
				function() {sortList.effect('highlight', {color: '#FFFF11'}, 1000);}
			);
		}
	});

	var sortListQ = $('#listQ').sortable({
		update: function() {
			$('#listQ-info').load('OrderAuditChildrenAjax.action?id=<s:property value="category.id"/>&type=AuditCategoryQuestions', 
				sortListQ.sortable('serialize').replace(/\[|\]/g,''), 
				function() {sortListQ.effect('hig+hlight', {color: '#FFFF11'}, 1000);}
			);
		}
	});
});

function copyCategory(atypeID) {
	$('#copy_audit').load('ManageCategoryCopyAjax.action', {button: 'text', 'id': atypeID},
		function() {
			$(this).dialog({
				modal: true, 
				title: 'Copy Category',
				width: '55%',
				close: function(event, ui) {
					$(this).dialog('destroy');
					location.reload();
				},
				buttons: {
					Cancel: function() {
						$(this).dialog('close');
					},
					'Copy All': function() {
						var data = $('form#textForm').serialize();
						data += "&button=CopyAll&originalID="+atypeID;
						startThinking( {div: 'copy_audit', message: 'Copying Category...' } );
						$.ajax(
							{
								url: 'ManageCategoryCopyAjax.action',
								data: data,
								complete: function() {
									stopThinking( {div: 'copy_audit' } );
									$(this).dialog('close');
									location.reload();
								}
							}
						);
					},
					'Copy Only Category': function() {
						var data = $('form#textForm').serialize();
						data += "&button=Copy&originalID="+atypeID;
						startThinking( {div: 'copy_audit', message: 'Copying Category...' } );
						$.ajax(
							{
								url: 'ManageCategoryCopyAjax.action',
								data: data,
								complete: function() {
									stopThinking( {div: 'copy_audit' } );
									$(this).dialog('close');
									location.reload();
								}
							}
						);
					}
				}
			});
		}
	);
}

function moveCategory(atypeID) {
	$('#copy_audit').load('ManageCategoryMoveAjax.action', {button: 'text', 'id': atypeID},
		function() {
			$(this).dialog({
				modal: true, 
				title: 'Move Category',
				width: '55%',
				close: function(event, ui) {
					$(this).dialog('destroy');
					location.reload();
				},
				buttons: {
					Cancel: function() {
						$(this).dialog('close');
					},
					'Move Category': function() {
						var data = $('form#textForm').serialize();
						data += "&button=Move&originalID="+atypeID;
						startThinking( {div: 'copy_audit', message: 'Moving Category...' } );
						$.ajax(
							{
								url: 'ManageCategoryMoveAjax.action',
								data: data,
								complete: function() {
									stopThinking( {div: 'copy_audit' } );
									$(this).dialog('close');
									location.reload();
								}
							}
						);
					}
				}
			});
		}
	);
}
</script>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />


<s:form id="save">
	<s:hidden name="id" />
	<s:if test="category.auditType != null">
		<s:hidden name="category.auditType.id" />
		<s:hidden name="parentID" value="%{category.auditType.id}" />
	</s:if>
	<s:if test="categoryParent != null">
		<s:hidden name="categoryParent.id" />
		<s:hidden name="parentID" value="%{categoryParent.ancestors.get(0).auditType.id}" />
	</s:if>
	<fieldset class="form">
	<h2 class="formLegend">Category</h2>
		<ol>
			<li><label>ID:</label>
				<s:if test="category.id > 0">
					<s:property value="category.id" />
				</s:if>
					<s:else>NEW</s:else>
			</li>
			<li><label>Category Name:</label>
				<s:textfield name="category.name" size="30" />
			</li>
			<li><label># of Questions:</label>
				<s:property value="category.numQuestions"/>
			</li>
			<li><label># Required:</label>
				<s:property value="getNumberRequired(category)"/>
			</li>
			<s:if test="category.auditType.dynamicCategories">
				<li><label>Apply on Question:</label>
					<s:textfield name="applyOnQuestionID" />
					<s:if test="applyOnQuestionID > 0"><a href="ManageQuestion.action?id=<s:property value="applyOnQuestionID" />">Show</a></s:if>
					<div class="fieldhelp">
					<h3>Apply on Question</h3>
					<p>This field is only available on audits with dynamic categories</p>
					</div>
				</li>
				<li><label>When Answer is:</label>
					<s:textfield name="category.applyOnAnswer" />
				</li>
			</s:if>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<div>
			<input type="submit" class="picsbutton positive" name="button" value="Save"/>
			<input type="button" class="picsbutton" value="Copy" onclick="copyCategory(<s:property value="id"/>)"/>
			<input type="button" class="picsbutton" value="Move" onclick="moveCategory(<s:property value="id"/>)"/>
				<s:if test="category.subCategories.size() == 0">
					<input type="submit" class="picsbutton negative" name="button" value="Delete"/>
				</s:if>
			<input type="submit" class="picsbutton" name="button" value="UpdateAllAuditsCategories"/>	
		</div>
	</fieldset>
</s:form>

<s:if test="id != 0">
	<div>
		<h3>Subcategories</h3>
		<ul class="list" id="list" title="Drag and drop to change order">
			<s:iterator value="category.subCategories">
				<li id="item_<s:property value="id"/>" title="Drag and drop to change order">
					<s:property value="number" />.
					<a href="ManageCategory.action?id=<s:property value="id"/>">
						<s:property value="name" />
					</a>
				</li>
			</s:iterator>
		</ul>
		<s:if test="category.subCategories.size() > 0">
			<a class="preview" href="AuditCat.action?catID=<s:property value="category.id" />">Preview Category</a>&nbsp;&nbsp;
		</s:if>
		<a class="add" href="ManageCategory.action?button=AddNew&parentID=<s:property value="category.parentAuditType.id"/>&categoryParent.id=<s:property value="category.id" />">Add New Sub Category</a>
		<div id="list-info"></div>
	</div>
	<br clear="all" />
	<div>
		<h3>Questions</h3>
		<ul class="list" id="listQ" title="Drag and drop to change order">
		<s:iterator value="category.questions">
		    <li id="item_<s:property value="id"/>"><s:property value="number"/>.
		    <a href="ManageQuestion.action?id=<s:property value="id"/>"><s:if test="name != null"><s:property value="name.length()>100 ? name.substring(0,97) + '...' : name"/></s:if><s:else>Question has no text</s:else></a></li>
		</s:iterator>
		</ul>
		
		<a class="add" href="ManageQuestion.action?button=AddNew&parentID=<s:property value="category.id"/>&question.category.id=<s:property value="category.id"/>">Add New Question</a>
		<div id="listQ-info"></div>
	</div>
</s:if>

<div id="copy_audit" class="thinking"></div>

<ul id="allCategories" class="mcdropdown_menu">
	<s:iterator value="category.ancestors.get(0).auditType.categories">
		<li rel="<s:property value="id" />"><s:property value="number" />. <s:property value="name" />
			<s:include value="manage_category_subcategories.jsp" />
		</li>
	</s:iterator>
</ul>

</body>
</html>