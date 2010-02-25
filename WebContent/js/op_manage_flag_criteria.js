function checkSubmit(criteriaID) {
	var checked = confirm('Are you sure you want to remove this criteria?');

	if (checked == true) {
		var data = {
				button: 'delete',
				id: $('#form1_id').val(),
				criteriaID: criteriaID
			};
		$('#criteriaDiv').load('ManageFlagCriteriaOperatorAjax.action', data);
	}
}
function addCriteria(criteriaID) {
	var hurdle = $('#'+criteriaID).find("[name='newHurdle']").val();
	startThinking({div:'thinking', message:'Adding criteria...'});
	
	var data = {
			button: 'add',
			id: $('#form1_id').val(),
			criteriaID: criteriaID,
			newFlag: $('#'+criteriaID).find("[name='newFlag']").val(),
			newHurdle: hurdle == null ? '' : hurdle
		};
	
	$('#criteriaDiv').load('ManageFlagCriteriaOperatorAjax.action', data, 
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
	startThinking({div:'thinking', message:'Saving changes...'});
	
	var data = {
			button: 'save',
			id: $('#form1_id').val(),
			criteriaID: criteriaID,
			newHurdle: hurdle,
			newFlag: flag
	};
	
	$('#criteriaDiv').load('ManageFlagCriteriaOperatorAjax.action', data,
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
	var layer = '#addCriteria';
	if ($(layer).is(':hidden')) {
		var data= {
			button: 'questions',
			id: $('#form1_id').val()
		};
		startThinking({div:'thinking', message:'Fetching criteria...'});
		$(layer).load('ManageFlagCriteriaOperatorAjax.action', data, 
			function() {
				stopThinking({div:'thinking'});
				$(this).show('slow');
			}
		);
	} else {
		$(layer).hide('slow');
	}
}

function editCriteria(tdCell) {
	var id = tdCell.parentNode.id;
	
	$("#"+id).find("span.newImpact").html("");
	$(tdCell).find(".hide").toggle();
	$("#"+id+" .hideOld").toggle();
	
	if ($("#"+id+" .hideOld").is(":hidden")) {
		$(tdCell).find(".hurdle").toggle();
		$(tdCell).find(".hover").text("[cancel]");
		$(tdCell).find("input").val($(tdCell).find(".hurdle").text());
	}
	else {
		$(tdCell).find(".hurdle").toggle();
		$(tdCell).find(".hover").text("[edit]");
	}
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

var wait = function(){
    var timer = 0;
    return function(criteriaID, newHurdle, ms){
        clearTimeout(timer);
        timer = setTimeout('calculateImpact('+criteriaID+','+newHurdle+')', ms);
    }  
}();