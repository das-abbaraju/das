var RecaptchaOptions = {
   theme : 'white',
   lang	 : '<s:property value="locale.language" />'
};

$(function() {
	$('a.showUser').click(function(){
		$('.showEmail').toggle();
		$('.showUser').toggle();
	});
	
	$('a.showEmail').click(function(){
		$('.showEmail').toggle();
		$('.showUser').toggle();
	});
});