$(function(){
	$('input.tokenAuto').each(function() {
		var that = $(this);
		var field_type = that.attr('rel').split('/')[0];
		var extraArgs = that.attr('rel').split('/')[1];
	    var name = that.attr('name');
	    var opt = $('<option>').attr('selected', 'selected');
	    var value = that.val();
	    var r_ids;
	    if(value != 'undefined' && value.length > 0){
		    r_ids = value.split(',').map(Number);
	    }
	    console.log(field_type+": "+r_ids);
	    that.removeAttr('name');
	    //that.removeAttr('value');
		
		$.getJSON(field_type+'Autocomplete!tokenJson.action', {'itemKeys': r_ids, 'extraArgs': extraArgs}, function(json) {
			var results;
			if(json.result) {
				results = json.result;
			}
			var url = field_type+'Autocomplete!tokenJson.action?' + (extraArgs == undefined? '' : 'extraArgs=' + extraArgs + '&') + 'limit=10';
			that.tokenInput(url, {
				jsonContainer: 'result',
				prePopulate: results,
		        onAdd: function(item) {
					that.closest('.q_box').find('select[name="' + name + '"]').append(opt.clone().attr('value', item.id).append(item.name));
	        	},
		       	onDelete: function(item) {
	        		that.closest('.q_box').find('select[name="' + name + '"]').find('option[value="'+item.id+'"]').remove();
		        }
	    	});
		});

	    that.closest('.q_box').append($('<select>').attr({
	        'class': 'hidden tokenSelect',
	        'name': name,
	        'multiple': 'multiple'
	    }));
	});

	$('a.filterBox').click(function(e) {
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
	
	$('a.clearLink').click(function(e) {
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

	$('div.filterOption').delegate('span.select', 'updateQuery', function() {
		var status_text = '';
		$(this).find('select option:selected').each(function() {
			if(status_text!='')
				status_text += ', ';
			status_text += $(this).text();		
		});
		if(status_text=='')
			status_text = 'ALL';

		$(this).closest('.filterOption').find('.q_status').text(status_text);			
	});
	
	$('dev.filterOption').delegate('span.textfield', 'updateQuery', function() {
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
			status_text = 'ALL';

		$(this).closest('.filterOption').find('.q_status').text(status_text);			
	});

	$(':input[type="text"].forms:not(.datepicker)').focus(function() {
		clearText($(this).get(0));
	});

	$('#write_email_button').click(function() {
		clickSearchSubmit('form1');
	});
	
	$('#find_recipients').click(function() {
		clickSearch('form1');
	});

	// -Auto Opens Fields that Have values.  Not sure if we want this.
	//$(':input:not([value]):not(hidden)').each(function() {
	//	$(this).closest('.filterOption').find('.filterBox').click();
	//});
	
	$('span.q_box').trigger('updateQuery');
});