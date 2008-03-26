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
