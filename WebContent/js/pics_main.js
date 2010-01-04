// This file is included in the main decorator
// Make sure everything in this file should be included on EVERY page call
// In other words, make it brief here

/*** Chat icon on top right of every page ***/
function showChat() {
	var elem = getElement('chatIcon');
	elem.style.display = 'block';
}
function hideChat() {
	var elem = getElement('chatIcon');
	elem.style.display = 'none';
}
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
	thefield.defaultValue=thefield.value;
	thefield.value = "";
}

function unclearText(thefield)
{
	if (thefield.value == "")
		thefield.value = thefield.defaultValue;
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
		innerSrc="<img src='images/ajax_process.gif' />";
	}
	if( oOptions.type == 'large' ) {
		innerSrc="<img src='images/ajax_process2.gif' />";
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
