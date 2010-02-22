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
	
	var data = {
			button: 'add',
			id: $('#form1_id').val(),
			criteriaID: criteriaID,
			newFlag: $('#'+criteriaID).find("[name='newFlag']").val(),
			newHurdle: hurdle == null ? '' : hurdle
		};
	
	$('#criteriaDiv').load('ManageFlagCriteriaOperatorAjax.action', data, function() { $('#addCriteria').hide('slow'); });
}
function submitHurdle(inputObject) {
	var criteriaID = inputObject.parentNode.parentNode.id;
	var hurdle = $('#'+criteriaID).find("[name='newHurdle']").val();
	startThinking({div:'thinking', message:'Saving changes...'});
	
	var data = {
			button: 'save',
			id: $('#form1_id').val(),
			criteriaID: criteriaID,
			newHurdle: hurdle
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