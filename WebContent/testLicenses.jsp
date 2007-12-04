<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body>
Used for testing the state contractor license pages at:
<a href="http://www.contractors-license.org/">http://www.contractors-license.org/</a><br><br>
-------------------------<br>
CA:<br>
<a href="http://www2.cslb.ca.gov/CSLB_LIBRARY/license+request.asp?LicNum=750580&EditForm=Y">License link</a>
<form method=post action="http://www2.cslb.ca.gov/CSLB_LIBRARY/license+request.asp" name=frmInput>
	<b>Contractor's License Number: </b>
	<input type="text" name="LicNum" size="8" maxlength="8" value="750580">
	<input type="hidden" name="EditForm" value="Yes">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>			
-------------------------<br>
UT:<br>
<a href="https://secure.utah.gov/llv/llv?sub_no=&action=search&type=by_license_no&core_no=123456">License link</a>

<form method=post action="https://secure.utah.gov/llv/llv" name=frmInput>
	<b>Contractor's License Number: </b>
	<input type="text" name="core_no" size="8" maxlength="8" value="123456">
	<input type="text" name="sub_no" size="8" maxlength="8" value="">
    <input type="hidden" name="action" value="search">
    <input type="hidden" name="type" value="by_license_no">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>	
-------------------------<br>
AL<br>
<a href="http://www.genconbd.state.al.us/DATABASE-LIVE/RosterResult.asp">License link</a>

<form method=post action="http://www.genconbd.state.al.us/DATABASE-LIVE/RosterResult.asp" name=frmInput>
	<b>Contractor's License Number: </b>
	<input type="text" name="txtlicenseno" size="8" maxlength="8" value="14296">
    <input type="hidden" name="action" value="search">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>
-------------------------<br>
AK<br>
<a href="http://www.dced.state.ak.us/occ/OccSearch/main.cfm?CFID=1607375&CFTOKEN=46ef7aacf0153993-C1C5CF3D-C169-2801-9511A03E87A050E8">License link</a>

<form method=post action="http://www.dced.state.ak.us/occ/OccSearch/main.cfm?CFID=1607376&CFTOKEN=46ef7aacf0153993-C1C5CF3D-C169-2801-9511A03E87A050E8" name=frmInput>
	<b>Contractor's License Number: </b>
	<input type="text" name="LicNum" size="8" maxlength="8" value="23424">

	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>
-------------------------<br>
AZ<br>
<a href="http://www.rc.state.az.us/clsc/AZROCLicenseQuery?pagerequest=license">License link</a>

<form method=post action="http://www.rc.state.az.us/clsc/AZROCLicenseQuery?pagerequest=license" name=frmInput>
	<b>Contractor's License Number: </b>  
	<INPUT type="hidden" name="pagename" value="license"> 
	<input type="text" name="licensenumber" size="8" maxlength="8" value="123456">
    <br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>
-------------------------<br>
FL<br>
**Need randomly generated session key. Not sure how to do this.
<br>
<a href="https://www.myfloridalicense.com/licensing/wl12.jsp;jsessionid=EMDMODPOLLJAkKj9f-zKC?fhash=93eff21eg0">License link</a>
<form method=post action="https://www.myfloridalicense.com/licensing/wl12.jsp;jsessionid=EMDMODPOLLJAkKj9f-zKC?fhash=93eff21eg0" name=frmInput>
	<b>Contractor's License Number: </b>
	<input type="text" name="search_key_licensenum" size="8" maxlength="8" value="14296">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>	
-------------------------<br>
GA<br>
<a href="https://secure.sos.state.ga.us/myverification/SearchResults.aspx?t_web_lookup__license_no=">License link</a>

<form method=post action="https://secure.sos.state.ga.us/myverification/Search.aspx" name=formSearch>
	<b>Contractor's License Number: </b>
<input type="hidden" name="__VIEWSTATE" value="dDwyMTQxMjc4NDIxO3Q8O2w8aTwxPjs+O2w8dDw7bDxpPDA+Oz47bDx0PDtsPGk8MT47PjtsPHQ8O2w8aTwxPjs+O2w8dDw7bDxpPDE+Oz47bDx0PDtsPGk8Mj47aTwzPjtpPDk+O2k8MTI+Oz47bDx0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjs+O2w8dDx0PDs7bDxpPDA+Oz4+Ozs+Oz4+Oz4+O3Q8O2w8aTwxPjs+O2w8dDw7bDxpPDA+Oz47bDx0PHQ8OztsPGk8MD47Pj47Oz47Pj47Pj47dDw7bDxpPDE+Oz47bDx0PDtsPGk8MD47PjtsPHQ8dDw7O2w8aTwwPjs+Pjs7Pjs+Pjs+Pjt0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjs+O2w8dDx0PDs7bDxpPDA+Oz4+Ozs+Oz4+Oz4+Oz4+Oz4+Oz4+Oz4+Oz4+Oz4+Oz6UuXSbsuidoQ0BFQiKa4LHqFr3aw==">

	<input type="text" name="t_web_lookup__license_no" size="8" maxlength="8" value="14296">
<input type="hidden" name="sch_button" value="Search" id="sch_button">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>		
-------------------------<br>
HI<br>
<a href="http://pahoehoe.ehawaii.gov/pvl/app">License link</a>

<form method=post action="http://pahoehoe.ehawaii.gov/pvl/app" >
	<b>Contractor's License Number: </b>
  <INPUT type=hidden name="_f" value="lic">
	<input type="text" name="licno" size="8" maxlength="8" value="14296">
<input type="hidden" name="lictp" value="CT">
  <INPUT type="hidden"  name="_a"  value="Submit Query" >
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>		


-------------------------<br>
IA<br>
<a href="http://www2.iwd.state.ia.us/contractor.nsf/WebVVLCR00ByRegNo/?SearchView&Query=Field%20flcrRegNum_IA%20=%12345-34">License link</a>
<p>
**five-digit number followed by a dash and a two digit number. (Example 12345-02)<br>
-------------------------<br>
LA<br>
<a href="http://www.lslbc.state.la.us/search/cresults.asp">License link</a>

<form method=post action="http://www.lslbc.state.la.us/search/cresults.asp" name=frmInput>
	<b>Contractor's License Number: </b>
	      <input type="hidden" name="count" value="50">
	<input type="text" name="licenseno" size="8" maxlength="8" value="14296">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>	
-------------------------<br>
MA<br>
<a href="http://db.state.ma.us/bbrs/hic.pl">License link</a>

<form method=post action="http://db.state.ma.us/bbrs/hic.pl" name=frmInput>
	<b>Contractor's License Number: </b>
	<input type="text" name="keys" size="8" maxlength="8" value="14296">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>		
-------------------------<br>
MS<br>
<a href="http://www.msboc.state.ms.us/Results.CFM">License link</a>
<form method=post action="http://www.msboc.state.ms.us/Results.CFM" name=frmInput>
	<b>Contractor's License Number: </b>
	<input type="text" name="Lic" size="8" maxlength="8" value="14296">
	<input type="hidden" name="ContractorType" value="Commercial">
    <input type="hidden" name="VarDatasource" value="Commercial">
	<INPUT TYPE="hidden" NAME="vozip__county" value="">
	<input type="hidden" NAME="Clas" value="">
	<input type="hidden" NAME="Co_Name" value="">
	<input type="hidden" NAME="City" value="">
	<input type="hidden" NAME="State" value="">
	<input type="hidden" NAME="Zip" value="">
	<input type="hidden" NAME="Dba_name" value="">
	<input type="hidden" NAME="Minority" value="">	
	<input type="hidden" NAME="OrderBy" value="Co_Name"> 
	<input type="hidden" NAME="maxrecords" value="25"> 
		
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>		
	-------------------------<br>
NM<br>
<a href="http://www.contractorsnm.com/search/contractors/index.do?func=newsearch">License link</a>

<form method=post action="http://www.contractorsnm.com/search/contractors/index.do" name=frmInput>
	<b>Contractor's License Number: </b>
	    <input type="hidden" name="func" value="">
    <input type="hidden" name="currow" value="0">
    <input type="hidden" name="anchor" value="">
	<input type="text" name="licenseNo" size="8" maxlength="8" value="87415">
     <input type="hidden" name="licenseNoSelect" value="startsWith"> 
	 
	<input type="hidden" name="qpSearch" value="false">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>	
	-------------------------<br>
NC<br>
<a href="http://www.nclbgc.org/lbgcWeb/servlet/LicenseeSearch">License link</a>

<form method=post action="http://www.nclbgc.org/lbgcWeb/servlet/LicenseeSearch" name=FormName>
	<b>Contractor's License Number: </b>
		<input type="hidden" name="src" value="search">
	<input type="text" name="LicNum"  id="FormsEditField2" size="8" maxlength="8" value="87415">
	<br>
	<br>

						
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>	
	-------------------------<br>
ND<br>
<a href="https://secure.apps.state.nd.us/sc/busnsrch/busnSearch.htm">License link</a>

<form method=post action="https://secure.apps.state.nd.us/sc/busnsrch/busnSearch.htm">
	<b>Contractor's License Number: </b>
	<input type="text" name="srchLicenseNo" size="8" maxlength="8" value="87415">
	<input type="hidden" name="command" value="Search">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>
	-------------------------<br>
NV<br>
<a href="http://nscb.sierracat.com/index.cfm?action=search_resultsm">License link</a>
<form method=post action="http://nscb.sierracat.com/index.cfm?action=search_results">
	<b>Contractor's License Number: </b>
	<input type="text" name="license" size="8" maxlength="8" value="14874">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>

	-------------------------<br>
OR<br>
<a href="http://ccbed.ccb.state.or.us/New_Web/asp/new_search_results.asp">License link</a>

<form method=post action="http://ccbed.ccb.state.or.us/New_Web/asp/new_search_results.asp">
	<b>Contractor's License Number: </b>
	<input type="text" name="regno" size="8" maxlength="8" value="87415">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>
	-------------------------<br>
SC<br>
<a href="http://verify.llronline.com/LicLookup/Contractors/Contractor.aspx?div=69">License link</a>
<form method=post target="_blank" id="form1" action="http://verify.llronline.com/LicLookup/Contractors/Contractor.aspx?div=69" name=frmInput>
<input type="hidden" name="UserInputGen:txt_licNum" value="234">
<input type="hidden" name="__VIEWSTATE" value="dDw1MDIyODc0ODY7dDw7bDxpPDE+Oz47bDx0PDtsPGk8Mj47aTw2PjtpPDEyPjtpPDEzPjtpPDE0Pjs+O2w8dDxwPHA8bDxUZXh0Oz47bDxDb250cmFjdG9yczs+Pjs+Ozs+O3Q8O2w8aTwxNz47aTwyMz47aTwyNT47aTwyNz47PjtsPHQ8cDxwPGw8VGV4dDtWaXNpYmxlOz47bDxDb21wYW55IG5hbWU6O288dD47Pj47Pjs7Pjt0PHA8cDxsPFZpc2libGU7PjtsPG88dD47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPENsYXNzaWZpY2F0aW9uOjs+Pjs+Ozs+O3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDxMaWNOYW1lO0xpY0lkbnQ7Pj47Pjt0PGk8MzU+O0A8QWxsO0FpciBDb25kaXRpb25pbmcgLSBBQztBc3BoYWx0IFBhdmluZyAtIEFQO0FzcGhhbHQgUGF2aW5nIEFQIEhlYXRpbmcgLSBIVDtCb2lsZXIgSW5zdGFsbGF0aW9uIC0gQkw7Qm9yaW5nIEFuZCBUdW5uZWxpbmcgLSBCVDtCcmlkZ2VzIC0gQlI7QnVpbGRpbmcgLSBCRDtDb25jcmV0ZSAtIENUO0NvbmNyZXRlIFBhdmluZyAtIENQO0VsZWN0cmljYWwgLSBFTDtHZW5lcmFsIFJvb2ZpbmcgLSBHUjtHbGFzcyBBbmQgR2xhemluZyAtIEdHO0dyYWRpbmcgLSBHRDtIaWdod2F5IC0gSFkgKEFQLCBDUCwgQlIsIEdELCBISSk7SGlnaHdheSBJbmNpZGVudGFsIC0gSEk7SW50ZXJpb3IgUmVub3ZhdGlvbiAtIElSO0xpZ2h0ZW5pbmcgUHJvdGVjdGlvbiAtIExQO01hcmluZSAtIE1SO01hc29ucnkgLSBNUztQYWNrYWdlZCBFcXVpcG1lbnQgLSBQSztQaXBlbGluZXMgLSBQTDtQbHVtYmluZyAtIFBCO1ByZS1lbmdpbmVlcmVkIE1ldGFsIEJ1aWxkaW5ncyAtIE1CO1Byb2Nlc3MgUGlwaW5nIC0gMVAgb3IgMlA7UHVibGljIFV0aWxpdHkgRWxlY3RyaWNhbCAtIDFVIG9yIDJVO1JhaWxyb2FkIC0gUlI7UmVmcmlnZXJhdGlvbiAtIFJHO1NwZWNpYWx0eSBSb29maW5nIC0gU1I7U3RydWN0dXJhbCBGcmFtaW5nIC0gU0Y7U3RydWN0dXJhbCBTaGFwZXMgLSBTUztTd2ltbWluZyBQb29scyAtIFNQO1dhdGVyIEFuZCBTZXdlciBMaW5lcyAtIFdMO1dhdGVyIEFuZCBTZXdlciBQbGFudHMgLSBXUDtXb29kIEZyYW1lIFN0cnVjdHVyZXMgLSBXRjs+O0A8QWxsO0FDO0FQO0hUO0JMO0JUO0JSO0JEO0NUO0NQO0VMO0dSO0dHO0dEO0hZO0hJO0lSO0xQO01SO01TO1BLO1BMO1BCO01COzFQOzFVO1JSO1JHO1NSO1NGO1NTO1NQO1dMO1dQO1dGOz4+Oz47Oz47Pj47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPFlvdXIgc2VhcmNoIHJldHVybmVkOiAwIHJlY29yZChzKS5cPEJSXD5cPEJSXD47bzx0Pjs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w8XGU7Pj47Pjs7Pjt0PEAwPHA8cDxsPF8hRGF0YVNvdXJjZUl0ZW1Db3VudDtfIUl0ZW1Db3VudDtQYWdlQ291bnQ7RGF0YUtleXM7PjtsPGk8LTE+O2k8LTE+O2k8MD47bDw+Oz4+Oz47Ozs7Ozs7Ozs7Pjs7Pjs+Pjs+Pjs+OxauStQquGVOZHIzSmdaktYVD1E=">
<input type="image" name="CheckLicense" SRC="images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20"></form></td>
						  
<form method=post action="http://verify.llronline.com/LicLookup/Contractors/Contractor.aspx?div=69" id="form1">
	<b>Contractor's License Number: </b>
	<input type="hidden" name="__VIEWSTATE" value="dDw1MDIyODc0ODY7dDw7bDxpPDE+Oz47bDx0PDtsPGk8Mj47aTw2PjtpPDEyPjtpPDEzPjtpPDE0Pjs+O2w8dDxwPHA8bDxUZXh0Oz47bDxDb250cmFjdG9yczs+Pjs+Ozs+O3Q8O2w8aTwxNz47aTwyMz47aTwyNT47aTwyNz47PjtsPHQ8cDxwPGw8VGV4dDtWaXNpYmxlOz47bDxDb21wYW55IG5hbWU6O288dD47Pj47Pjs7Pjt0PHA8cDxsPFZpc2libGU7PjtsPG88dD47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPENsYXNzaWZpY2F0aW9uOjs+Pjs+Ozs+O3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDxMaWNOYW1lO0xpY0lkbnQ7Pj47Pjt0PGk8MzU+O0A8QWxsO0FpciBDb25kaXRpb25pbmcgLSBBQztBc3BoYWx0IFBhdmluZyAtIEFQO0FzcGhhbHQgUGF2aW5nIEFQIEhlYXRpbmcgLSBIVDtCb2lsZXIgSW5zdGFsbGF0aW9uIC0gQkw7Qm9yaW5nIEFuZCBUdW5uZWxpbmcgLSBCVDtCcmlkZ2VzIC0gQlI7QnVpbGRpbmcgLSBCRDtDb25jcmV0ZSAtIENUO0NvbmNyZXRlIFBhdmluZyAtIENQO0VsZWN0cmljYWwgLSBFTDtHZW5lcmFsIFJvb2ZpbmcgLSBHUjtHbGFzcyBBbmQgR2xhemluZyAtIEdHO0dyYWRpbmcgLSBHRDtIaWdod2F5IC0gSFkgKEFQLCBDUCwgQlIsIEdELCBISSk7SGlnaHdheSBJbmNpZGVudGFsIC0gSEk7SW50ZXJpb3IgUmVub3ZhdGlvbiAtIElSO0xpZ2h0ZW5pbmcgUHJvdGVjdGlvbiAtIExQO01hcmluZSAtIE1SO01hc29ucnkgLSBNUztQYWNrYWdlZCBFcXVpcG1lbnQgLSBQSztQaXBlbGluZXMgLSBQTDtQbHVtYmluZyAtIFBCO1ByZS1lbmdpbmVlcmVkIE1ldGFsIEJ1aWxkaW5ncyAtIE1CO1Byb2Nlc3MgUGlwaW5nIC0gMVAgb3IgMlA7UHVibGljIFV0aWxpdHkgRWxlY3RyaWNhbCAtIDFVIG9yIDJVO1JhaWxyb2FkIC0gUlI7UmVmcmlnZXJhdGlvbiAtIFJHO1NwZWNpYWx0eSBSb29maW5nIC0gU1I7U3RydWN0dXJhbCBGcmFtaW5nIC0gU0Y7U3RydWN0dXJhbCBTaGFwZXMgLSBTUztTd2ltbWluZyBQb29scyAtIFNQO1dhdGVyIEFuZCBTZXdlciBMaW5lcyAtIFdMO1dhdGVyIEFuZCBTZXdlciBQbGFudHMgLSBXUDtXb29kIEZyYW1lIFN0cnVjdHVyZXMgLSBXRjs+O0A8QWxsO0FDO0FQO0hUO0JMO0JUO0JSO0JEO0NUO0NQO0VMO0dSO0dHO0dEO0hZO0hJO0lSO0xQO01SO01TO1BLO1BMO1BCO01COzFQOzFVO1JSO1JHO1NSO1NGO1NTO1NQO1dMO1dQO1dGOz4+Oz47Oz47Pj47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPFlvdXIgc2VhcmNoIHJldHVybmVkOiAwIHJlY29yZChzKS5cPEJSXD5cPEJSXD47bzx0Pjs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w8XGU7Pj47Pjs7Pjt0PEAwPHA8cDxsPF8hRGF0YVNvdXJjZUl0ZW1Db3VudDtfIUl0ZW1Db3VudDtQYWdlQ291bnQ7RGF0YUtleXM7PjtsPGk8LTE+O2k8LTE+O2k8MD47bDw+Oz4+Oz47Ozs7Ozs7Ozs7Pjs7Pjs+Pjs+Pjs+OxauStQquGVOZHIzSmdaktYVD1E=">

<input name="UserInputGen:txt_licNum" type="text" value="345" id="UserInputGen_txt_licNum" >
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>
	-------------------------<br>
TN<br>
<a href="http://www.state.tn.us/cgi-bin/commerce/roster3.pl">License link</a>
<form method=post action="http://www.state.tn.us/cgi-bin/commerce/roster3.pl">
	<b>Contractor's License Number: </b>
	<input type="hidden" name="board" Value="Contractors">
	<input type="hidden" name="wordsearch" Value="Exact Word Search">
	<input type="hidden" name="search" Value="Search by License #">
	
	<input type="text" name="indata" size="8" maxlength="8" value="53457 ">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>
	-------------------------<br>
TX<br>
<a href="http://www.license.state.tx.us/LicenseSearch/SearchResultsListBrowse.asp">License link</a>

<form method=post action="http://www.license.state.tx.us/LicenseSearch/SearchResultsListBrowse.asp">
	<b>Contractor's License Number: </b>
	<input type="hidden" name="tdlr_status" value="SERVCP">
	<input type="text" name="pht_lic" size="8" maxlength="8" value="87415">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>
	-------------------------<br>
VA<br>
<a href="http://www.dpor.state.va.us/regulantlookup/searchcollect.cfm?CFID=7928520&CFTOKEN=60644143">License link</a>

<form method=post action="http://www.dpor.state.va.us/regulantlookup/searchcollect.cfm?CFID=7928520&CFTOKEN=60644143">
	<b>Contractor's License Number: </b>
	<input type="hidden" name="qry_NAME" value="">
	<input type="hidden" name="zipcode" value="">
	<input type="text" name="qryLicenseNo" size="10" maxlength="10" value="7415234532">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>

	-------------------------<br>
WA<br>
<a href="https://fortress.wa.gov/lni/bbip/search.aspx">License link</a>
<form method=post action="https://fortress.wa.gov/lni/bbip/search.aspx">
<input type="hidden" name="__VIEWSTATE" value="dDwtMTE0MDQwMDI0Njt0PDtsPGk8Mj47PjtsPHQ8O2w8aTw2PjtpPDg+O2k8MTA+O2k8MTQ+O2k8MTY+O2k8MTg+O2k8MjA+O2k8MjI+O2k8MjQ+Oz47bDx0PHQ8OztsPGk8Mz47Pj47Oz47dDxwPHA8bDxGb3JlQ29sb3I7XyFTQjs+O2w8MjxCbGFjaz47aTw0Pjs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w8TGljZW5zZSAjIChwYXJ0IG9yIGFsbCk6IDs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w8VHlwZSBvZiBMaWNlbnNlOjs+PjtwPGw8b25jbGljazs+O2w8d2luZG93Lm9wZW4oJ1xcbGljZW5zZXMuaHRtJywgJ2xpY2Vuc2VzJywgJ3dpZHRoPTc2MCwgaGVpZ2h0PTQ4MCwgc2Nyb2xsYmFycz15ZXMnKTs+Pj47Oz47dDx0PDt0PGk8MTY+O0A8XGU7RWxlY3RyaWNhbCBBZG1pbmlzdHJhdG9yO0NvbnN0cnVjdGlvbiBDb250cmFjdG9yO0VsZWN0cmljYWwgQ29udHJhY3RvcjtFbGVjdHJpY2lhbjtFbGVjdHJpY2FsIFRyYWluZWU7TWFzdGVyIEVsZWN0cmljaWFuO01lZGljYWwgR2FzIFBsdW1iZXI7UGx1bWJlcjtQbHVtYmVyIFRyYWluZWU7RWxldmF0b3IgUHJpbWFyeSBDb250YWN0O0VsZXZhdG9yIENFVSBQcm92aWRlcjtFbGV2YXRvciBDb250cmFjdG9yO0VsZXZhdG9yIE1lY2hhbmljO0VsZXZhdG9yIFRyYWluZWU7RWxldmF0b3IgTWVjaGFuaWMgVGVtcDs+O0A8XGU7QUQ7Q0M7RUM7RUw7RVQ7TUU7TUc7UEw7UFQ7TEE7TEk7TEM7TE07TFQ7TFg7Pj47Pjs7Pjt0PHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs+O2w8XGU7bzxmPjs+Pjs+Ozs+O3Q8cDxwPGw8VmlzaWJsZTs+O2w8bzxmPjs+Pjs+Ozs+O3Q8dDxwPHA8bDxWaXNpYmxlOz47bDxvPGY+Oz4+Oz47O2w8aTwwPjs+Pjs7Pjs+Pjs+Pjs+eOah2KcYCy40uqvNTXxn/0MKgRM=">
	<input type="text" name="txtSearch" size="10" maxlength="10" value="234">
<input type="hidden" name="btnSearch" value="Look Up">
<input type="hidden" name="rdoItem" value="3">
<input type="image" name="CheckLicense" SRC="images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20"></form></td>
						  
<P>			  
<form method=post action="https://fortress.wa.gov/lni/bbip/search.aspx">
	<b>Contractor's License Number: </b>
<input type="hidden" name="__VIEWSTATE" value="dDwtMTE0MDQwMDI0Njt0PDtsPGk8Mj47PjtsPHQ8O2w8aTw2PjtpPDg+O2k8MTA+O2k8MTQ+O2k8MTY+O2k8MTg+O2k8MjA+O2k8MjI+O2k8MjQ+Oz47bDx0PHQ8OztsPGk8Mz47Pj47Oz47dDxwPHA8bDxGb3JlQ29sb3I7XyFTQjs+O2w8MjxCbGFjaz47aTw0Pjs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w8TGljZW5zZSAjIChwYXJ0IG9yIGFsbCk6IDs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w8VHlwZSBvZiBMaWNlbnNlOjs+PjtwPGw8b25jbGljazs+O2w8d2luZG93Lm9wZW4oJ1xcbGljZW5zZXMuaHRtJywgJ2xpY2Vuc2VzJywgJ3dpZHRoPTc2MCwgaGVpZ2h0PTQ4MCwgc2Nyb2xsYmFycz15ZXMnKTs+Pj47Oz47dDx0PDt0PGk8MTY+O0A8XGU7RWxlY3RyaWNhbCBBZG1pbmlzdHJhdG9yO0NvbnN0cnVjdGlvbiBDb250cmFjdG9yO0VsZWN0cmljYWwgQ29udHJhY3RvcjtFbGVjdHJpY2lhbjtFbGVjdHJpY2FsIFRyYWluZWU7TWFzdGVyIEVsZWN0cmljaWFuO01lZGljYWwgR2FzIFBsdW1iZXI7UGx1bWJlcjtQbHVtYmVyIFRyYWluZWU7RWxldmF0b3IgUHJpbWFyeSBDb250YWN0O0VsZXZhdG9yIENFVSBQcm92aWRlcjtFbGV2YXRvciBDb250cmFjdG9yO0VsZXZhdG9yIE1lY2hhbmljO0VsZXZhdG9yIFRyYWluZWU7RWxldmF0b3IgTWVjaGFuaWMgVGVtcDs+O0A8XGU7QUQ7Q0M7RUM7RUw7RVQ7TUU7TUc7UEw7UFQ7TEE7TEk7TEM7TE07TFQ7TFg7Pj47Pjs7Pjt0PHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs+O2w8XGU7bzxmPjs+Pjs+Ozs+O3Q8cDxwPGw8VmlzaWJsZTs+O2w8bzxmPjs+Pjs+Ozs+O3Q8dDxwPHA8bDxWaXNpYmxlOz47bDxvPGY+Oz4+Oz47O2w8aTwwPjs+Pjs7Pjs+Pjs+Pjs+eOah2KcYCy40uqvNTXxn/0MKgRM=">

	<input type="hidden" name="btnSearch" value="Look Up">
	<input type="hidden" name="rdoItem" value="3">
	<input type="text" name="txtSearch" size="10" maxlength="10" value="234">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>
	-------------------------<br>
WI<br>
<a href="http://apps.commerce.state.wi.us/SB_Credential/SB_CredentialApp?cred_id=23&cmd=Search&form=SearchByIdForm">License link</a>

</body>
</html>
