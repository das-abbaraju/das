var cal2;
function showCriteria(questionID, auditName, requiresCal) {
	if(requiresCal) {
		if (typeof(cal2) == 'undefined') {
			cal2 = new CalendarPopup('caldiv2');
			cal2.setCssPrefix("PICS");
			cal2.setReturnFunction("calendarReturn");
			cal2.offsetX = -350;
			cal2.offsetY = -250;
		}
	}
	var pars = {
		'id' :opID,
		'question.id' :questionID
	};
	
	Modalbox.show('FlagCriteriaActionAjax.action', {
		method : 'post', 
		params: pars, 
		title: 'Edit Criteria - '+auditName,
		slideDownDuration: .5,
		slideUpDuration: 0,
		resizeDuration: .2,
		overlayClose: false,
		height: 250
		});
}

function showOshaCriteria(type) {
	var pars = {
		'id' :opID,
		'type': type 
	};
	
	Modalbox.show('FlagOshaCriteriaActionAjax.action', {
		method : 'post', 
		params: pars, 
		title: 'Edit Criteria - ' + shaType,
		slideDownDuration: .5,
		slideUpDuration: 0,
		resizeDuration: .2,
		overlayClose: false,
		height: 400
	});
}

function calendarReturn(y, m, d) {
	if (window.CP_targetInput != null) {
		var dt = new Date(y, m - 1, d, 0, 0, 0);
		if (window.CP_calendarObject != null) {
			window.CP_calendarObject.copyMonthNamesToWindow();
		} 
		var now = new Date();
		if (typeof(y) == "string" && now.getFullYear() == dt.getFullYear() && now.getMonth() == dt.getMonth() && now.getDay() == dt.getDay())
			window.CP_targetInput.value = "Today";
		else
			window.CP_targetInput.value = formatDate(dt, window.CP_dateFormat);
		
		window.CP_targetInput.onchange();
	}
}

function clearRow(row) {
	var comparision = $(row+'_comparison');
	if(comparision != null)
		comparision.value = '';
	var amBest = $(row+'_amRatings');
	if(amBest != null)
		amBest.value = 0;
	var amBestClass = $(row+'_amClass');
	if(amBestClass != null)
		amBestClass.value = 0;
	var value = $(row+'_value');
	if(value != null)
	  value.value = '';
	$(row+'_clear').hide();
}

function testCriteria(criteria) {
	if (!$('test').value.blank()) {
		startThinking({div:'test_output', message:''});
		var pars = $('criteriaEditForm').serialize(true);
		pars.button = 'test';
		pars.testValue = $('test').value.capitalize();
		var myAjax = new Ajax.Updater('test_output','FlagCriteriaActionAjax.action', {
				method: 'post',
				parameters: pars
			});
	}
}

function closeCriteriaEdit() {
	Modalbox.hide();
}

function deactivateModal() {
	Modalbox.show("<div style='text-align:center'><img src='images/ajax_process2.gif' /></div>");
	Modalbox.deactivate();
}

function saveCriteria() {
	startThinking( {
		message :"saving criteria..."
	});
	var pars = $('criteriaEditForm').serialize(true);
	pars.button = 'save';
	deactivateModal();
	var myAjax = new Ajax.Updater('growlBox','FlagCriteriaActionAjax.action', {
			method: 'post',
			parameters: pars,
			onComplete: function() {
					location.reload();
				}
		});
}

function saveOshaCriteria() {
	startThinking( {
		message :"saving criteria..."
	});
	var pars = $('criteriaEditForm').serialize(true);
	pars.button = 'save';
	deactivateModal();
	var myAjax = new Ajax.Updater('growlBox','FlagOshaCriteriaActionAjax.action', {
			method: 'post',
			parameters: pars,
			onComplete: function() {
				location.reload();	
			}
		});
}

function toggleQuestionList() {
	$('questionList').toggle();
	$('addQuestionButton').toggle();
	$('hideQuestionButton').toggle();
}

function showOtherAccounts() {
	if ($('otherAccounts').visible())
		Effect.SlideUp('otherAccounts', { duration: .2 });
	else
		Effect.SlideDown('otherAccounts', { duration: .3 });
}

function showHudleType(elm, criteria) {
	var option =  $F(elm);
	if(option == 'None') {
		$('show_'+criteria+'hurdle').hide();
		$('show_'+criteria+'hurdlepercent').hide();
	}
	if(option == 'Absolute') {
		$('show_'+criteria+'hurdle').show();
		$('show_'+criteria+'hurdlepercent').hide();
	}
	if(option == 'NAICS') {
		$('show_'+criteria+'hurdle').show();
		$('show_'+criteria+'hurdlepercent').show();
	}	
	return false;
}
