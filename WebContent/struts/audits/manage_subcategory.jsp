<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage SubCategory</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091105" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<script type="text/javascript" src="js/jquery/autocompletefb/jquery.autocompletefb.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocompletefb/jquery.autocompletefb.css" />
<script type="text/javascript">
$(function(){
	var sortList = $('#list').sortable({
		update: function() {
			$('#list-info').load('OrderAuditChildrenAjax.action?id=<s:property value="subCategory.id"></s:property>&type=AuditSubCategory', 
				sortList.sortable('serialize'), 
				function() {sortList.effect('highlight', {color: '#FFFF11'}, 1000);}
			);
		}
	});
});

var data = <s:property value="data" escape="false"/>;
var acfb;
$(function(){
	function acfbuild(cls,url){
		var ix = $("input"+cls);
		ix.addClass('acfb-input').wrap('<ul class="'+cls.replace(/\./,'')+' acfb-holder"></ul>');
		
		return $("ul"+cls).autoCompletefb({urlLookup:url, delimeter: '|'});
	}
	acfb = acfbuild('.countries', data);
	acfb.init($('form [name=countries]').val().split('|'));

	$('form#save').submit(function() {
			$(this).find('[name=countries]').val(acfb.getData());
		}
	);
});
</script>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />

<s:form id="save" cssStyle="width: 800px;">
	<s:hidden name="id" />
	<s:hidden name="parentID" value="%{subCategory.category.id}" />
	<s:hidden name="subCategory.category.id" />
		<fieldset class="form">
		<legend><span>Sub Category</span></legend>
		<ol>
			<li><label>ID:</label>
				<s:if test="category.id > 0">
					<s:property value="category.id" />
				</s:if>
				<s:else>
					NEW
				</s:else>
			</li>
			<li><label>Sub Category Name:</label>
				<s:textfield name="subCategory.subCategory" size="50" />
			</li>				
			<li><label>Countries:</label>
				<s:hidden name="countries" value="%{subCategory.countries}"/>
				<s:textfield size="50" cssClass="countries"/>
			</li>				
			<li><label>Help Text:</label>
				<s:textarea name="subCategory.helpText" rows="3" cols="50"/>
			</li>				
		</ol>
		</fieldset>
		<fieldset class="form submit">
			<div>
				<button class="picsbutton positive" name="button" type="submit" value="save">Save</button>
			<s:if test="subCategory.questions.size == 0">
				<button name="button" class="picsbutton negative" type="submit" value="delete">Delete</button>
			</s:if>
			</div>
		</fieldset>
</s:form>

<s:if test="id != 0">
	<div>
		<ul id="list" title="Drag and drop to change order">
		<s:iterator value="subCategory.questions">
		    <li id="item_<s:property value="id"/>"><s:property value="number"/>.
		    <a href="ManageQuestion.action?id=<s:property value="id"/>"><s:property value="question.length()>100 ? question.substring(0,97) + '...' : question"/></a></li>
		</s:iterator>
		</ul>
		
		<a href="ManageQuestion.action?button=AddNew&parentID=<s:property value="subCategory.id"/>&question.subCategory.id=<s:property value="subCategory.id"/>">Add New Question</a>
		<div id="list-info"></div>
	</div>
</s:if>
</body>
</html>