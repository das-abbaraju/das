$(function(){
	
	$('ul.vert-toolbar li.head .hidden-button').click(function() {
		$('ul.catlist').toggle('slow');
	});
	
	$('.vert-toolbar li').hover(
		function() {
			$(this).addClass('hover');
		},
		function(){
			$(this).removeClass('hover');
		}
	);
	
	// ajax history
	$('a.hist-category').live('click', function() {
		$.bbq.pushState( $.param.fragment(this.href) );
		var li = $(this).parents('li:first');
		li.siblings('li.current').removeClass('current');
		li.addClass('current');
		return false;
	});
	
	$(window).bind('hashchange', function() {
		if ($.bbq.getState().categoryID === undefined)
			$('a.hist-category:first').click();
		else {
			var data = $.deparam.querystring($.param.querystring(location.href, $.bbq.getState()));
			data.button='';
			$('#auditViewArea').load('AuditAjax.action', data);
		}
	});
	
	$(window).trigger('hashchange');
	
	//$('.caos').bind('click', function(){
	//	
	//	$.facebox({div: '#match_'+fType});
	//});
	$('.singleButton').bind('click', function(){
		var buttonAction = $(this).children('.bAction').val();
		var data = {
				auditID: $('#auditID').val(), button: 'statusLoad',
				'buttonAction': buttonAction, caoID: $(this).children('.bCaoID').val()
			};
		$('#statusMessage').load('CaoSaveAjax.action', data, function(response, status, xhr){
			if(status=='success'){
				$('#statusMessage').append($('<form>').append($('<textarea>').attr({
						'cols': '30',
						'rows': '3',
						'name': 'addToNotes'
					})).attr({
						'id': 'changeStatusForm',
						'method': 'post',
						'action': 'RequestNewContractor.action'
					}).css('width','400px')); 
				$.facebox({div: '#ajaxBox'});
				if(!$('#actionButtonFooter').length>0)
					$('#facebox .faceFooter').append($('<div>').attr('id','actionButtonFooter'));
				var footButton = $('<input>').attr({
					'type': 'submit',
					'name': 'button',
					'value': buttonAction,
					'class': 'picsbutton negative'
				}).css('float','left');
				$('#actionButtonFooter').html(footButton);
				$('#facebox .content').css('height','15em');
			} else {
			}
		});
		
	}); //add class
	
	//$('a[rel*=facebox]').facebox({
		//loading_image : 'loading.gif',
		//close_image : 'closelabel.gif'
	//});
	
	
	
});