(function($){ 
  $.fn.blank = function(){ 
    return $.trim($(this).val()).length == 0; 
  }
  $.fn.capitalize = function(){
	var val= $(this).val();
	return val.substr(1).toUpperCase()+val.substr(1, val.length);  
  }
  $.fn.toObj = function(){
	var data = {};
	$.each($(this).find(':input[name]').serializeArray(), function(i, e) {
		data[e.name] = e.value;
	});
	return data;
  }
})(jQuery)