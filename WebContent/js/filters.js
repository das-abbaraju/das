function loadFiltersCallback() {
	$('input.tokenAuto').each(function() {
		var that = $(this);
		var field_type = that.attr('rel').split('/')[0];
		var extraArgs = that.attr('rel').split('/')[1];
	    var name = that.attr('name');
	    var value = that.val();
	    var r_ids;
	    if(value != 'undefined' && value.length > 0){
		    r_ids = value.split(',').map(Number);
	    }
	    that.removeAttr('name');

		$.getJSON(field_type+'Autocomplete!tokenJson.action', {'itemKeys': r_ids, 'extraArgs': extraArgs}, function(json) {
			var results;
			if(json.result) {
				results = json.result;
			}
			var url = field_type+'Autocomplete!tokenJson.action?' + (extraArgs == undefined? '' : 'extraArgs=' + extraArgs + '&') + 'limit=25';
			that.tokenInput(url, {
				jsonContainer: 'result',
				prePopulate: results,
		        onAdd: function(item) {
					var ele = $('<option>', {'value': item.id, 'selected': 'selected'}).append(item.name);
					that.closest('.q_box').find('select[name="' + name + '"]').append(ele);
	        	},
		       	onDelete: function(item) {
	        		that.closest('.q_box').find('select[name="' + name + '"]').find('option[value="'+item.id+'"]').remove();
		        }
	    	});
			
			that.trigger('updateQuery');
		});

	    that.closest('.q_box').append($('<select>').attr({
	        'class': 'hidden tokenSelect',
	        'name': name,
	        'multiple': 'multiple'
	    }));
	});
	
	$('span.q_box:not(.tokenAuto)').trigger('updateQuery');
}

$(function() {
	$('#search').delegate('#showSearch', 'click', function(e) {
		showSearch();
	})
	
	loadFiltersCallback();
	
	$('body').delegate('.filterOption a.filterBox', 'click', function(e) {
		e.preventDefault();
		var box = $(this).closest('.filterOption').find('.q_box');
		var query = $(this).closest('.filterOption').find('.q_status');
		if(box.hasClass('open')){ //closing box
			box.trigger('updateQuery');
			box.removeClass('open');
			query.addClass('open');
		} else { // opening box
			query.removeClass('open');
			box.addClass('open');
		}
	});

	$('body').delegate('.filterOption a.clearLink', 'click', function(e) {
		e.preventDefault();
		var ele = $(this).closest('span.clearLink');
		var tokenInput = ele.find('ul.token-input-list');
		ele.find(':input:not(:hidden)').each(function() {
			switch(this.type) {
				case 'select-multiple':
				case 'select-one':
				case 'text':
					$(this).val('');
					break;
			}
		});
		if(tokenInput.length){
			tokenInput.find('li.token-input-token').remove();
			ele.find('select.tokenSelect option').remove();
		}
		$(this).closest('.filterOption').find('a.filterBox').click();
	});

	$('body').delegate('.filterOption span.select', 'updateQuery', function() {
		var status_text = '';
		$(this).find('select option:selected').each(function() {
			if(status_text!='')
				status_text += ', ';
			status_text += $(this).text();
		});
		if(status_text=='')
			status_text = translate('JS.Filters.status.All');

		$(this).closest('.filterOption').find('.q_status').text(status_text);
	});

	$('body').delegate('.filterOption span.textfield', 'updateQuery', function() {
		var status_text = '';
		var text1 = $(this).find(':input[type="text"]').eq(0);
		var text2 = $(this).find(':input[type="text"]').eq(1);

		if (text1.val() != undefined) {
			if(text1.val() != '' && text2.val() != '')
				status_text = 'between '+text1.val()+' and '+ text2.val();
			if(text1.val() != '' && text2.val() == '')
				status_text = 'after '+ text1.val();
			if(text1.val() == '' && text2.val() != '')
				status_text = 'before '+ text2.val();
		} else if (text2.val() != '') {
			queryText = 'before '+ text2.val();
		}
		if(status_text=='')
			status_text = translate('JS.Filters.status.All');

		$(this).closest('.filterOption').find('.q_status').text(status_text);
	});
	
	$('body').delegate('.filterOption :input[type="text"]:not(.datepicker)','focus', function() {
		clearText($(this)[0]);
	});
	
	$('body').delegate('#write_email_button','click', function() {
		clickSearchSubmit('form1');
	});

	$('body').delegate('#find_recipients','click',function() {
		clickSearch('form1');
	});

	// -Auto Opens Fields that Have values.  Not sure if we want this.
	//$(':input:not([value]):not(hidden)').each(function() {
	//	$(this).closest('.filterOption').find('.filterBox').click();
	//});

});