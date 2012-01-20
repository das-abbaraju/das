// This file is included in the main decorator
// Make sure everything in this file should be included on EVERY page call
// In other words, make it brief here

function getElement(whichLayer) {
	var elem;
	if( document.getElementById )
		// this is the way the standards work
		elem = document.getElementById( whichLayer );
	else if( document.all )
		// this is the way old msie versions work
		elem = document.all[whichLayer];
	else if( document.layers )
		// this is the way nn4 works
		elem = document.layers[whichLayer];
	return elem;
}

function openWindow(url, wndName)
{
	var wnd = window.open(url, wndName, 'toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width=700,height=500');
	wnd.focus();
	return false;
}

function clearText(thefield)
{
	// if field is in the form - string -
	// then remove text, else select it
	var patt1 = /^-[^-]*-$/;
	var str = thefield.value;
	str = $.trim(str);
	if(str.match(patt1)!=null)
		thefield.value = "";
	else
		thefield.select();
}

function startThinking( args ) {
	 var oOptions = augment({
      div: "mainThinkingDiv",
      message: "Communicating with PICS",
      type: "small"
    }, args);

	var targetDiv = getElement(oOptions.div);
	var innerSrc;
	if( oOptions.type == 'small' ) {
		innerSrc="<img src='images/ajax_process.gif' /> ";
	}
	if( oOptions.type == 'large' ) {
		innerSrc="<img src='images/ajax_process2.gif' /> ";
	}

	innerSrc=innerSrc+oOptions.message;
	if(typeof(targetDiv) != 'undefined' && targetDiv != null) {
		targetDiv.innerHTML=innerSrc;
	}
	return true;
}

function stopThinking( args ) {
	 var oOptions = augment({
      div: "mainThinkingDiv"
    }, args);
	var targetDiv = getElement(oOptions.div);
	if(typeof(targetDiv) != 'undefined' && targetDiv != null) {
		targetDiv.innerHTML='';
	}
	return true;
}

function augment (oSelf, oOther) {
    if (oSelf == null) {
        oSelf = {};
    }
    for (var i = 1; i < arguments.length; i++) {
        var o = arguments[i];
        if (typeof(o) != 'undefined' && o != null) {
            for (var j in o) {
                oSelf[j] = o[j];
            }
        }
    }
    return oSelf;
}

/**
 * Chat - solutions.liveperson.com
 */

$(function() {
	$('.liveperson-chat-toggle').bind('mouseover', function() {
		var element = $('.liveperson-chat');
		
		if (!element.is(':visible')) {
			element.css({
				display: 'block'
			});
		}
	});
	
	$('#helpbox').bind('mouseleave', function(event) {
		var element = $('.liveperson-chat');
		
		if (element.is(':visible')) {
			element.css({
				display: 'none'
			});
		}
	});
	
	
	$('#tracing-open').live('click', function(event) {
		event.preventDefault();
		
		
		$.ajax({
			url: "ManageTranslations.action?button=tracingOnClearAjax",
			success: function () {
				$('body').append('<iframe src="' + window.location.href 
						+ '" style="display: none;" id="translationTracingFrame"></iframe>');
				$('body').remove("#translationTracingFrame");
				window.open("ManageTranslations.action?showDoneButton=true", "tracing_window");
			}
		});
	});
});