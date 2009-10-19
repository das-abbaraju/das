(function($){ 
  $.fn.blank = function(){ 
    return $.trim(this[0]).length == 0; 
  };
  $.fn.capitalize = function(){
	return this[0].substr(1).toUpperCase()+this[0].substr(1, this[0].length);  
  };
})(jQuery)