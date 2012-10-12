$(function() {
	if ($('input.show-address').val().length == 0) {
		$(".address-zip").hide();
	}
	
	if ($('#requestedUser').val() != 0) {
		$("#requestedOther").hide();
	}
	
	$('#phoneContact').click(function() {
        $.blockUI({
        	message: $('#phoneSubmit')
        });
 
        $('.blockOverlay').attr('title', translate('JS.RequestNewContractor.ClickToUnblock')).click($.unblockUI);
    });
	
	$('#emailContact').click(function() {
		$.blockUI({
			message: $('#emailSubmit')
		});
		
		$('.blockOverlay').attr('title', translate('JS.RequestNewContractor.ClickToUnblock')).click($.unblockUI);              
    });
	
	$('.cluetip').cluetip({
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		arrows: true,
		cluetipClass: 'jtip',
		local: true,
		clickThrough: false
	});
	
	$('.checkReq').change(function() {
		var ele = $(this);
		var term = ele.val();
		var fType = ele.attr('name').substr(ele.attr('name').indexOf('.')+1, ele.attr('name').length);
		$('#_'+fType).hide();
		startThinking( {div: 'think_'+fType, message: translate('JS.RequestNewContractor.message.CheckingForMatches'), type: 'small' } );
		if(fType=='name' || fType=='phone' || fType=='taxID') var type = 'C';
		else if(fType=='contact' || fType=='email') var type = 'U';
		$.getJSON(
			'RequestNewContractorAjaxCheck.action',
			{term: term, type: type},
			function(json){
				if(json==null)
					return;
				var result = json.result;
				if(result!=null) {
					var used = result[2];
					var usedList = $('<div>');
					var usedStr = '';
					for(var i=0; i<used.length; i++){
						usedStr += used[i].used+' ' ;
					}
					usedList.append(translate('JS.RequestNewContractor.message.MatchingOnWords')).append('<br/>');
					usedList.append($('<div>').append(usedStr).css('font','italic').css('color','#A84D10'));
					var unused = result[1];
					var matchList = $('<div>');
					if(unused.length>0){
						var unusedList =$('<div>');
						var uStr = '';
						for(var i=0; i<unused.length; i++){
							uStr += unused[i].unused+', ' ;
						}
						uStr = uStr.substr(0, uStr.length-2);
						unusedList.append(translate('JS.RequestNewContractor.message.NoMatches')).append('<br/>');
						unusedList.append(uStr).append('<br/>'); 
						matchList.append(unusedList);
					}
					matchList.append(usedList);
					matchList.append(translate('JS.RequestNewContractor.message.CompanyInSystem'))
					.append('<br/>');
					var ul = $('<ul>');
					for(var i=3; i<result.length; i++){
						var id=result[i].id;
						var name=result[i].name;
						if(result[i].add)
							ul.append($('<li>').append($('<a>').attr('href','ContractorFacilities.action?id='+id).append(name)));
						else
							ul.append($('<li>').append($('<a>').attr('href','ContractorView.action?id='+id).append(name)));
					}
					matchList.append(ul);
					var hasResults = $('#match_'+fType).attr('matched');
					if(hasResults!=null)
						$('#match_'+fType).html(' ');
					$('#match_'+fType).attr('matched', 'true').css('width','600px').append($('<h2>').text(translate('JS.RequestNewContractor.message.PotentialMatches')))
						.append($('<div>').attr('id','inner_'+fType).append(matchList)).hide();
					var link = $('#_'+fType);
					if(!link.length>0){
						link = $('<div>').attr('id','_'+fType).append($('<a>').attr('href','#').css('float', 'left').text(translate('JS.RequestNewContractor.message.PossibleMatches')).click(function(e){
							e.preventDefault();
							$.facebox({div: '#match_'+fType});
						}));
					}
					ele.parent().append(link);
					link.show();
				}
				
			}
		);
		stopThinking( {div: 'think_'+fType} );
	});
	changeCountrySubdivision($("#newContractorCountry").val());
	$('.datepicker').datepicker({
		showOn: 'button',
		buttonImage: 'images/icon_calendar.gif',
		buttonImageOnly: true,
		buttonText: chooseADate,
		showAnim: 'fadeIn',
		minDate: new Date()
	});
	
	$('.show-address').keyup(function() {
		if (!$(this).blank())
			$('.address-zip').show();
		else
			$('.address-zip').hide();
	});
	
	$('#saveContractorForm').delegate('#operatorForms', 'click', function(e) {
		e.preventDefault();
	}).delegate('#toggleEmailPreview', 'click', function(e) {
		e.preventDefault();
		$('#email_preview').toggle();
	}).delegate('#addToNotes', 'keyup', function() {
		var d = new Date();
		var dateString = (d.getMonth() + 1 < 10 ? "0" : "") + (d.getMonth() + 1) + "/" + (d.getDate() < 10 ? "0" : "") + (d.getDate()) + "/" + d.getFullYear();
		$('#addHere').html(dateString + " - " + name + " - " + $(this).val() + "\n\n");

		if ($('#addToNotes').val() == '')
			$('#addHere').text('');
	}).delegate('#newContractorCountry', 'change', function() {
		countryChanged($(this).val());
	}).delegate('#getMatches', 'click', function() {
		var data = {
			button: 'MatchingList',
			newContractor: $('#saveContractorForm input[name=newContractor]').val()
		};
	
		$('#potentialMatches').show();
		$('#potentialMatches').append('<img src="images/ajax_process.gif" style="border: none;" />');
		$('#potentialMatches').load('RequestNewContractorAjax.action', data);
	}).delegate("#requestedUser", "change", function(e) {
		checkUserOther($(this).val());
	}).delegate("#operatorsList", "change", function() {
		$('#loadUsersList').load('OperatorUserListAjax.action',
				{opID: $(this).val(), newContractor: newContractor}, function() {
			checkUserOther(opID);
		});
	});
	
	$("#operatorForms").delegate(".addForm", "click", function(e) {
		e.preventDefault();
		var formName = $(this).data("formname");
		var filename = $(this).data("file");
		
		$.fancybox.close();
		var id = filename.substring(0, filename.indexOf('.'));
		
		var attachment = '<span id="' + id + '"><a href="#" class="remove" onclick="removeAttachment(\'' + id
			+ '\'); return false;">' + formName + '</a><input type="hidden" id="' + id + '_input" name="filenames" value="'
			+ filename + '" /><br /></span>';
		
		$('#attachment').append(attachment);
		$('#'+id+'_input').val(filename);
	});
	
	$('#preview_email').live('click', function() {
	    PICS.ajax({
	        url: 'RequestNewContractor!emailPreview.action',
	        data: $('#saveContractorForm').serialize(),
	        success: function(data, textStatus, XMLHttpRequest) {
	            var modal = PICS.modal({
	                content: data,
	                title: translate('JS.button.Preview'),
	                width: 900
	            });
	            
	            modal.show();
	        }
	    });
	});
});

function checkUserOther(userID) {
	if (!userID || userID == 0)
		$("#requestedOther").show();
	else
		$("#requestedOther").hide();
}

function countryChanged(country) {
	changeCountrySubdivision(country);
}

function checkDate(input){
	var date = $(input).val();
	date = new Date(date);
	if(date==null){
		var newDate = $.datepicker.formatDate("mm/dd/yy", new Date());
		$(input).val(newDate);
	}
	if(date < new Date()){
		var newDate = $.datepicker.formatDate("mm/dd/yy", new Date());
		$(input).val(newDate);
	}
}

function removeAttachment(id) {
	$('span#'+id).remove();
}