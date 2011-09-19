$(function() {
	$('.datepicker').datepicker();
	
	$('.newCriteria').live('click', function(e) {
		e.preventDefault();
		var insurance = $("#form1_insurance").val();
		
		if ($('#addCriteria').is(':empty')) {
			startThinking({div:'thinking', message:translate('JS.ManageFlagCriteriaOperator.message.LoadingCriteria')});
			$('#addCriteria').load('ManageFlagCriteriaOperator!questions.action?insurance='+insurance, 
					{ operator: $('#form1_id').val() }, function() {
					stopThinking({div:'thinking'});
					$(this).slideDown();
				}
			);
		} else {
			$('#addCriteria').slideToggle();
		}
	});
	
	$('.childCriteria').live('click', function(e) {
		e.preventDefault();
		
		var opID = $(this).data('facid');
		var opName = $(this).data('facname');
		
		if (opID == $('#form1_id').val()) {
			$('#childCriteria').empty();
			$('#emptyChildCriteria').hide();
		} else {
			var insurance = $("#form1_insurance").val();
			
			startThinking({div:'thinking', message:translate('JS.ManageFlagCriteriaOperator.message.LoadingLinked')});
			$('#childCriteria').load('ManageFlagCriteriaOperator!childOperator.action?insurance='+insurance, { childID: opID }, 
				function() {
					stopThinking({div:'thinking'});
					$('#emptyChildCriteria').text(opName).show();
					$('#childCriteria').attr('data-op', opID);
				}
			);
		}
	});
	
	$('#emptyChildCriteria').live('click', function(e) {
		e.preventDefault();
		$('#childCriteria').empty();
		$(this).hide();
	});
	
	$('#criteriaDiv').delegate('.needsRecalc', 'load', function() {
		var fcoID = $(this).attr('id');
		
		updateAffected(fcoID, accountID);
	}).delegate('#recalculateAll', 'click', function(e) {
		e.preventDefault();
		
		$('#criteriaDiv table.report tbody tr').each(function() {
			var fcoID = $(this).attr('id');
			updateAffected(fcoID, accountID);
		});
	}).delegate('a.getImpact', 'click', function(e) {
		e.preventDefault();
		
		var fcoID = $(this).closest('tr').attr('id');
		getImpact(fcoID, accountID);
	}).delegate('.edit', 'click', function(e) {
		e.preventDefault();
		
		var id = $(this).data('id');
		
		$(".editable").hide();
		$(".hurdle").show();
		
		if ($("#"+id).find(".viewable").is(":visible")) {
			$(".viewable").show();
			$("#"+id).find(".viewable").hide();
			$("#"+id).find(".editable").show();
			$("#"+id).find(".hurdle").hide();
		} else {
			$("#"+id).find("input[type!=button]").val($("#"+id).find(".hurdle").text());
			$("#"+id).find(".viewable").show();
		}
		
		$("#"+id).find("span.newImpact").empty();
	}).delegate('.remove', 'click', function(e) {
		e.preventDefault();
		
		if (confirm(translate('JS.ManageFlagCriteriaOperator.message.ConfirmRemoveCriteria'))) {
			var id = $(this).data('id');
			var insurance = $("#form1_insurance").val();
			var data = {
				operator: $('#form1_id').val(),
				flagCriteriaOperator: id
			};
			
			$('#criteriaDiv').load('ManageFlagCriteriaOperator!delete.action?insurance='+insurance, data, function() {
				$('#criteriaDiv .datepicker').datepicker();
			});
		}
	}).delegate('.editHurdle', 'click', function(e) {
		e.preventDefault();
		var id = $(this).data('id');
		submitHurdle(id);
	});
	
	$('#impactDiv').delegate('a.excel', 'click', function(e) {
		e.preventDefault();
		
		var fcoID = $(this).data('fco');
		var opID = $(this).data('op');
		
		newurl = "OperatorFlagsCalculatorCSV.action?button=download&fcoID=" + fcoID + "&opID=" + opID;
		popupWin = window.open(newurl, 'OperatorFlagsCalculator', '');
	});
	
	/* Add New Criteria */
	$('#criteriaDiv').delegate('#addCriteria .add', 'click', function(event) {
		event.preventDefault();
		
		startThinking({
			div: 'thinking', 
			message: translate('JS.ManageFlagCriteriaOperator.message.AddingCriteria')
		});
		
		var fcOptions = $(this).closest('tr').find('input,select').serialize();
		var insurance = $("#form1_insurance").val();
		var data = {
			operator: $('#form1_id').val(),
			flagCriteria: $(this).data('id')
		};
		
		$('#criteriaDiv').load('ManageFlagCriteriaOperator!add.action?insurance='+insurance + (fcOptions.length > 0 ? "&" + fcOptions : ""), data, function() {
				$('#addCriteria').slideUp('slow', function() {
					$(this).empty();
				});
				
				stopThinking({
					div: 'thinking'
				});
				
				$('#criteriaDiv .datepicker').datepicker();
			}
		);
	});
	
	$('#childCriteria').delegate('a.getImpact', 'click', function(e) {
		e.preventDefault();
		
		var opID = $('#childCriteria').data('op');
		var fcoID = $(this).closest('tr').attr('id');
		
		getImpact(fcoID, opID);
	}).delegate('#recalculateAll', 'click', function(e) {
		e.preventDefault();
		
		var opID = $('#childCriteria').data('op');
		
		$('#childCriteria table.report tbody tr').each(function() {
			var fcoID = $(this).attr('id');
			updateAffected(fcoID, opID);
		});
	});
	
	// bump contractors
	$('.bump-contractor').bind('click', function(event) {
		$('#bump_contractors input[type=submit]').click();
	});
});

function getImpact(fcoID, accountID) {
	startThinking({div:'thinking', message:translate('JS.ManageFlagCriteriaOperator.message.ImpactedContractors')});
	$('#impactDiv').load('OperatorFlagsCalculatorAjax.action', { fcoID: fcoID, opID: accountID }, 
		function() {
			$(this).show('slow');
			stopThinking({div:'thinking'});
		}
	);
}

function submitHurdle(id) {
	var insurance = $("#form1_insurance").val();
	var fcoOptions = $("#criteriaDiv tr#" + id).find("input, select").serialize();
	
	startThinking({div:'thinking', message:translate('JS.ManageFlagCriteriaOperator.message.SavingChanges')});
	
	var data = {
		operator: $('#form1_id').val(),
		flagCriteriaOperator: id
	};
	
	$('#criteriaDiv').load('ManageFlagCriteriaOperator!save.action?insurance='+insurance + 
			(fcoOptions.length > 0 ? "&" + fcoOptions : ""), data, function() {
			stopThinking({div:'thinking'});
			$('#criteriaDiv .datepicker').datepicker();
		}
	);
}

function calculateImpact(fcoID, opID, newHurdle) {
	var data = {
		button: 'count',
		newHurdle: newHurdle,
		fcoID: fcoID,
		opID: opID
	};
	
	startThinking({div:'thinking', message:translate('JS.ManageFlagCriteriaOperator.message.LoadingAffected')});
	$('#'+fcoID).find('span.newImpact').load('OperatorFlagsCalculatorAjax.action', data,
		function() {
			stopThinking({div:'thinking'});
		}
	);
}

function updateAffected(fcoID, opID) {
	var result = "";
	var data = {
		button: 'count',
		fcoID: fcoID,
		opID: opID
	};
	
	$('#'+fcoID).find('a.oldImpact').html('<img src="images/ajax_process.gif" alt="Loading image" />');
	
	$.ajax({
		url: "OperatorFlagsCalculatorAjax.action",
		data: data,
		success: function(msg) {
			if (msg.search(/error/i) == -1)
				$('#'+fcoID).find('a.oldImpact').html(jQuery.trim(msg));
			else
				$('#'+fcoID).find('a.oldImpact').replaceWith('<a href="#" class="oldImpact" onclick="window.location.reload();">Reload</a>');
		}
	});
}

var wait = (function() {
	var timer = 0;
	return function(fcoID, opID, newHurdle, ms) {
		clearTimeout(timer);
		timer = setTimeout(function() {
			calculateImpact(fcoID, opID, newHurdle);
		}, ms);
	}
})();