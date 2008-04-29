function changePage( formid, pageNum )
{
	document.getElementById( formid )['showPage'].value = pageNum;
	document.getElementById( formid ).submit();
	return false;
}

function changeOrderBy( formid, orderBy )
{
	document.getElementById( formid )['showPage'].value = "1";
	document.getElementById( formid )['orderBy'].value = orderBy;
	document.getElementById( formid ).submit();
	return false;
}

function changeStartsWith( formid, v )
{
	document.getElementById( formid )['startsWith'].value = v;
	document.getElementById( formid )['showPage'].value = "1";
	document.getElementById( formid ).submit();
	return false;
}

function runSearch( formid )
{
	//document.getElementById( formid )['orderBy'].value = "";
	document.getElementById( formid )['showPage'].value = "1";
	document.getElementById( formid )['startsWith'].value = "";
	return true;
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

function showSearch()
{
	$('showSearch').hide();
	$('hideSearch').show();
	Effect.SlideDown('form1',{duration:.3});
	return false;
}

function hideSearch()
{
	$('hideSearch').hide();
	$('showSearch').show();
	Effect.SlideUp('form1',{duration:.5});
	return false;
}
