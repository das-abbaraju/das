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
	var hurdle = $('#'+criteriaID).find("[name='newHurdle']").val();
	var insurance = $("#form1_insurance").val();
	startThinking({div:'thinking', message:'Adding criteria...'});
	
	var data = {
			button: 'add',
			id: $('#form1_id').val(),
			criteriaID: criteriaID,
			newFlag: $('#'+criteriaID).find("[name='newFlag']").val(),
			newHurdle: hurdle == undefined ? '' : hurdle
		};
	
	$('#criteriaDiv').load('ManageFlagCriteriaOperatorAjax.action?insurance='+insurance, data, 
			function() {
				$('#addCriteria').hide('slow');
				stopThinking({div:'thinking'});
			}
		);
}

function submitHurdle(tdCell) {
	var criteriaID = tdCell.parentNode.id;
	var hurdle = $('#'+criteriaID).find("[name='newHurdle']").val();
	var flag = $('#'+criteriaID).find("[name='newFlag']").val();
	var insurance = $("#form1_insurance").val();
	
	startThinking({div:'thinking', message:'Saving changes...'});
	
	var data = {
			button: 'save',
			id: $('#form1_id').val(),
			criteriaID: criteriaID,
			newHurdle: hurdle == undefined ? '' : hurdle,
			newFlag: flag
	};
	
	$('#criteriaDiv').load('ManageFlagCriteriaOperatorAjax.action?insurance='+insurance, data,
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
				$(this).show('slow');
			}
		);
	} else {
		$('#addCriteria').hide('slow');
	}
}

function editCriteria(id) {
	$(".hide").hide();
	$(".hurdle").show();
	
	if ($("#"+id).find(".hideOld").is(":visible")) {
		$(".hideOld").show();
		$("#"+id).find(".hideOld").hide();
		$("#"+id).find(".hide").show();
		$("#"+id).find(".hurdle").hide();
	} else {
		$("#"+id).find("input").val($("#"+id).find(".hurdle").text());
		$("#"+id).find(".hideOld").show();
	}
	
	$("#"+id).find("span.newImpact").html("");
}

function getImpact(fcoID) {
	var data = {
		fcoID: fcoID
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

function downloadImpact(fcoID) {
	var data = {
		fcoID: fcoID,
		button: 'download'
	};
	
	newurl = "OperatorFlagsCalculatorCSV.action?fcoID=" + fcoID + "&button=download";
	popupWin = window.open(newurl, 'OperatorFlagsCalculator', '');
}

function calculateImpact(fcoID, newHurdle) {
	var data = {
		button: 'count',
		newHurdle: newHurdle,
		fcoID: fcoID
	};
	
	startThinking({div:'thinking', message:'Calculating impact...'});
	$('#'+fcoID).find('span.newImpact').load('OperatorFlagsCalculatorAjax.action', data,
		function() {
			stopThinking({div:'thinking'});
		}
	);
}

function updateAffected(fcoID) {
	var result = "";
	var data = {
		button: 'count',
		fcoID: fcoID
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
		
		startThinking({div:'thinking', message:'Fetching linked facility criteria...'});
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
    return function(criteriaID, newHurdle, ms){
        clearTimeout(timer);
        timer = setTimeout('calculateImpact('+criteriaID+','+newHurdle+')', ms);
    }  
}();