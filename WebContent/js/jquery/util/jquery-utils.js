(function($){ 
  $.fn.blank = function(){ 
    return $.trim(this[0]).length == 0; 
  };
  $.fn.capitalize = function(){
	return this[0].substr(1).toUpperCase()+this[0].substr(1, this[0].length);  
  };
  $.fn.toObj = function(){
	var data = {};
	$.each($(this).find(':input[name]').serializeArray(), function(i, e) {
		data[e.name] = e.value;
	});
	return data;
  }
})(jQuery)