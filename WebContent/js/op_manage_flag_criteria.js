function checkSubmit(criteriaID) {
	var checked = confirm('Are you sure you want to remove this criteria?');
	var insurance = $("#form1_insurance").val();

	if (checked == true) {
		var data = {
				button: 'delete',
				id: $('#form1_id').val(),
				criteriaID: criteriaID
			};
		$('#criteriaDiv').load('ManageFlagCriteriaOperatorAjax.action?insurance='+insurance, data);
	}
}
function addCriteria(criteriaID) {
	var fcOptions = $('#addCriteria tr#' + criteriaID).find('input,select').serialize();
	
	var insurance = $("#form1_insurance").val();
	startThinking({div:'thinking', message:'Adding criteria...'});
	
	var data = {
		button: 'add',
		id: $('#form1_id').val(),
		criteriaID: criteriaID
	};
	
	$('#criteriaDiv').load('ManageFlagCriteriaOperatorAjax.action?insurance='+insurance + 
			(fcOptions.length > 0 ? "&" + fcOptions : ""), data, 
		function() {
			$('#addCriteria').hide('slow');
			stopThinking({div:'thinking'});
		}
	);
}

function submitHurdle(id) {
	var criteriaID = id;
	var insurance = $("#form1_insurance").val();
	var fcoOptions = $("#criteriaDiv tr#" + id).find("input, select").serialize();
	
	startThinking({div:'thinking', message:'Saving changes...'});
	
	var data = {
		button: 'save',
		id: $('#form1_id').val(),
		criteriaID: criteriaID
	};
	
	$('#criteriaDiv').load('ManageFlagCriteriaOperatorAjax.action?insurance='+insurance + 
			(fcoOptions.length > 0 ? "&" + fcoOptions : ""), data,
		function() { stopThinking({div:'thinking'}); }
	);
}
function getAddQuestions() {
	var insurance = $("#form1_insurance").val();
	
	if ($('#addCriteria').is(':hidden')) {
		var data= {
			button: 'questions',
			id: $('#form1_id').val()
		};
		startThinking({div:'thinking', message:'Fetching criteria...'});
		$('#addCriteria').load('ManageFlagCriteriaOperatorAjax.action?insurance='+insurance, data, 
			function() {
				stopThinking({div:'thinking'});
				$(this).slideDown();
			}
		);
	} else {
		$('#addCriteria').slideUp();
	}
}

function editCriteria(id) {
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
}

function getImpact(fcoID, opID) {
	var data = {
		fcoID: fcoID,
		opID: opID
	};
	
	$('#impactDiv').empty();
	startThinking({div:'thinking', message:'Getting impacted contractors...'});
	$('#impactDiv').load('OperatorFlagsCalculatorAjax.action', data, 
		function() {
			$(this).show('slow');
			stopThinking({div:'thinking'});
		}
	);
}

function downloadImpact(fcoID, opID) {
	newurl = "OperatorFlagsCalculatorCSV.action?button=download&fcoID=" + fcoID + "&opID=" + opID;
	popupWin = window.open(newurl, 'OperatorFlagsCalculator', '');
}

function calculateImpact(fcoID, opID, newHurdle) {
	var data = {
		button: 'count',
		newHurdle: newHurdle,
		fcoID: fcoID,
		opID: opID
	};
	
	startThinking({div:'thinking', message:'Loading affected contractors...'});
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

function getChildCriteria(opID, opName) {
	if (opID == $('#form1_id').val())
		$('#childCriteria').empty();
	else {
		var insurance = $("#form1_insurance").val();
		var data = {
			button: 'childOperator',
			id: $('#form1_id').val(),
			childID: opID,
			insurance: insurance
		};
		
		startThinking({div:'thinking', message:'Loading linked facility criteria...'});
		$('#childCriteria').load('ManageFlagCriteriaOperatorAjax.action?insurance='+insurance, data, 
			function() {
				stopThinking({div:'thinking'});
				$('#childCriteria').prepend('<a href="#" onclick="$(\'#childCriteria\').empty(); return false;" class="remove">'
						+ opName + '</a>');
			}
		);
	}
}

var wait = function(){
    var timer = 0;
    return function(fcoID, opID, newHurdle, ms){
        clearTimeout(timer);
        timer = setTimeout('calculateImpact('+fcoID+','+opID+','+newHurdle+')', ms);
    }  
}();