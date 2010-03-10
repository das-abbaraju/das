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
			newHurdle: hurdle == null ? '' : hurdle
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
			newHurdle: hurdle,
			newFlag: flag
	};
	
	$('#criteriaDiv').load('ManageFlagCriteriaOperatorAjax.action?insurance='+insurance, data,
		function() { stopThinking({div:'thinking'}); }
	);
}
function getImpact(criteriaID) {
	var data = {
			button: 'impact',
			id: $('#form1_id').val(),
			criteriaID: criteriaID
		};
	startThinking({div:'thinking', message:'Fetching impact...'});
	$('#impactDiv').load('ManageFlagCriteriaOperatorAjax.action', data,
		function() {
			stopThinking({div:'thinking'});
			$(this).show('slow');
		}
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

function calculateImpact(criteriaID, newHurdle) {
	var data = {
		button: 'calculateSingle',
		id: $('#form1_id').val(),
		criteriaID: criteriaID,
		newHurdle: newHurdle
	};
	
	startThinking({div:'thinking', message:'Calculating impact...'});
	$('#'+criteriaID).find('span.newImpact').load('ManageFlagCriteriaOperatorAjax.action', data,
		function() {
			stopThinking({div:'thinking'});
		}
	);
}

function updatePercentAffected(criteriaID) {
	var data = {
			button: 'calculateSingle',
			id: $('#form1_id').val(),
			criteriaID: criteriaID
		};
		
		//startThinking({div:'thinking', message:'Calculating impact...'});
		$('#'+criteriaID).find('a.oldImpact').load('ManageFlagCriteriaOperatorAjax.action', data,
			function() {
				//stopThinking({div:'thinking'});
			}
		);
}

var wait = function(){
    var timer = 0;
    return function(criteriaID, newHurdle, ms){
        clearTimeout(timer);
        timer = setTimeout('calculateImpact('+criteriaID+','+newHurdle+')', ms);
    }  
}();