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

function clearText(thefield)
{
	if (thefield.defaultValue==thefield.value)
		thefield.value = ""
}

function unclearText(thefield)
{
	if (thefield.value == "")
		thefield.value = thefield.defaultValue;
}



/********   DOC 2 HELP Start ********/
var CTXT_DISPLAY_FULLHELP = 1;
var CTXT_DISPLAY_TOPICONLY = 2;

function D2H_ShowHelp(contextID, mainURL, wndName, uCommand)
{
	var indx = mainURL.lastIndexOf("\\");
	var indx1 = mainURL.lastIndexOf("/");
	if (indx1 > indx)
		indx = indx1;
	var url = "";
	if (indx > 0)
		url = mainURL.substring(0, indx+1);
	url += "_d2h_ctxt_help.htm?contextID=" + contextID + "&mode=" + ((uCommand == CTXT_DISPLAY_TOPICONLY) ? "0" : "1");
	var wnd = window.open(url, wndName);
	wnd.focus();
}
helpURL = "help/c/default.htm";
/********   DOC 2 HELP End ********/
