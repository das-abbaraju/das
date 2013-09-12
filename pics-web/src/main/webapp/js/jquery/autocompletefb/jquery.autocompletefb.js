/*
 * jQuery plugin: autoCompletefb(AutoComplete Facebook)
 * @requires jQuery v1.2.2 or later
 * using plugin:jquery.autocomplete.js
 *
 * Credits:
 * - Idea: Facebook
 * - Guillermo Rauch: Original MooTools script
 * - InteRiders <http://interiders.com/> 
 *
 * Copyright (c) 2008 Widi Harsojo <wharsojo@gmail.com>, http://wharsojo.wordpress.com/
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 */
 
jQuery.fn.autoCompletefb = function(options) 
{
	var tmp = this;
	var settings = 
	{
		ul         : tmp,
		urlLookup  : [""],
		acOptions  : {formatItem: function(d){return d;}, formatResult: function(d){return d;}},
		foundClass : ".acfb-data",
		inputClass : ".acfb-input",
		delimeter  : ",",
		onfind     : function(d,count) {},
		onremove   : function(d,count) {}
	}
	if(options) jQuery.extend(settings, options);
	var count=0;
	var acfb = 
	{
		params  : settings,
		getData : function()
		{	
			var result = '';
		    $(settings.foundClass,tmp).each(function(i)
			{
				if (i>0)result+=settings.delimeter;
			    result += $(this).attr('id');
		    });
			return result;
		},
		clearData : function()
		{	
		    $(settings.foundClass,tmp).remove();
			$(settings.inputClass,tmp).focus();
			return tmp.acfb;
		},
		removeFind : function(o){
			var d = {id:$(o).parent().attr('id'), name:$(o).parent().find('span').text()};
			$(o).unbind('click').parent().remove();
			$(settings.inputClass,tmp).focus();
			count--;
			settings.onremove(d,count);
			return tmp.acfb;
		},
		addFind : function(d){
			if ($('#'+d.id+'.'+settings.foundClass).size() != 0)
				return;
			var f = settings.foundClass.replace(/\./,'');
			var v = '<li class="'+f+'" id="'+settings.acOptions.formatResult(d)+'"><span>'+settings.acOptions.formatItem(d)+'</span> <img class="p" src="images/delete.gif"/></li>';
			var x = $(settings.inputClass,tmp).before(v);
			$('.p',x[0].previousSibling).click(function(){
				acfb.removeFind(this);
			});
			count++;
			settings.onfind(d,count);
		},
		init : function(a) {
			$.each(a, function (k,v){
				if (v != null && v !== '')
					acfb.addFind(v);
			});
		}
	}
	
	$(settings.foundClass+" img.p").click(function(){
		acfb.removeFind(this);
	});
	
	$(settings.inputClass,tmp).autocomplete(settings.urlLookup,settings.acOptions);
	$(settings.inputClass,tmp).result(function(e,d,f){
		$(settings.inputClass,tmp).val('').focus();
		acfb.addFind(d);
	});

	return acfb;
}
