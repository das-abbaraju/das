(function($){ 
  $.fn.blank = function(){ 
    return $.trim($(this).val()).length == 0; 
  }
  $.fn.capitalize = function(){
	var val= $(this).val();
	return val.substr(0,1).toUpperCase()+val.substr(1, val.length);  
  }
  $.fn.equalWidth = function() {
	var max_width = 0;
	$(this).each(function(){
		var width = $(this).width();
		if (width > max_width)
			max_width = width;
	});
	$(this).width(max_width + 5);

	return $(this);
  }
})(jQuery)