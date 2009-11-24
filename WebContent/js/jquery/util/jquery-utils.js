(function($){ 
  $.fn.blank = function(){ 
    return $.trim($(this).val()).length == 0; 
  }
  $.fn.capitalize = function(){
	var val= $(this).val();
	return val.substr(0,1).toUpperCase()+val.substr(1, val.length);  
  }
})(jQuery)