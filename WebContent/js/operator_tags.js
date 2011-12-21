$(function() {
	$('a[rel*="facebox"]').facebox({
 		loading_image : 'loading.gif',
 		close_image : 'closelabel.gif'
 	});
	
	$('#operatorTagForm').delegate('.checkRemove', 'click', function(e) {
		var id = $(this).attr('rel');
		
		$.get('ContractorTagsAjax.action', {
			tagID: id, button: 'removeNum'
		} , function(data) {
			$.facebox(data);
		});
	});

	$('#tagListDiv').delegate('a.editAuditTypeRules', 'click', function(e) {
		e.preventDefault();
		
		var tagID = $(this).closest('td').attr('id');
		
		showAuditTypeRules(tagID);
	}).delegate('a.editCategoryTypeRules', 'click', function(e) {
		e.preventDefault();
		
		var tagID = $(this).closest('td').attr('id');
		
		showAuditCategoryRules(tagID);
	})
});

function showAuditTypeRules(tagID) {
	var data = {
		'comparisonRule.tag.id': tagID,
		button: 'tags'
	};
	
	$('#rules').think({
		message: translate('JS.OperatorTags.message.LoadingRelatedRules', ['<s:text name="AuditType" />'])
	}).load('AuditTypeRuleTableAjax.action', data);
}

function showCategoryRules(tagID) {
	var data = {
		'comparisonRule.tag.id': tagID,
		button: 'tags'
	};
	
	$('#rules').think({
		message: translate('JS.OperatorTags.message.LoadingRelatedRules', ['<s:text name="AuditCategory" />'])
	}).load('CategoryRuleTableAjax.action', data);
}