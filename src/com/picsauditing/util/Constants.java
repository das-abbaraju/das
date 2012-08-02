package com.picsauditing.util;

import java.util.*;

public class Constants {
	
	private static final String[] COUNTRY_SUBDIVISION_ARRAY = {
			"Alabama","Yes","http://www.genconbd.state.al.us/DATABASE-LIVE/RosterResult.asp","txtlicenseno","action","Search",";",
			"Alaska","Yes","http://www.dced.state.ak.us/occ/OccSearch/main.cfm?CFID=1607376&CFTOKEN=46ef7aacf0153993-C1C5CF3D-C169-2801-9511A03E87A050E8","LicNum",";",
			"Arkansas","No","http://www.state.ar.us/clb/search.html?","LicNum","action","search",";",
			"Arizona","Yes","http://www.rc.state.az.us/clsc/AZROCLicenseQuery?pagerequest=license","licensenumber","pagename","license",";",
			"California","Yes","https://www2.cslb.ca.gov/General-Information/interactive-tools/check-a-license/License+Detail.asp","LicNum","EditForm","Yes",";",
			"Florida,Yes","https://www.myfloridalicense.com/licensing/wl12.jsp;jsessionid=EMDMODPOLLJAkKj9f-zKC?fhash=93eff21eg0","search_key_licensenum",";",
			"Georgia","No","https://secure.sos.state.ga.us/myverification/?","t_web_lookup__license_no","sch_button","Search","__VIEWSTATE","dDwyMTQxMjc4NDIxO3Q8O2w8aTwxPjs+O2w8dDw7bDxpPDA+Oz47bDx0PDtsPGk8MT47PjtsPHQ8O2w8aTwxPjs+O2w8dDw7bDxpPDE+Oz47bDx0PDtsPGk8Mj47aTwzPjtpPDk+O2k8MTI+Oz47bDx0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjs+O2w8dDx0PDs7bDxpPDA+Oz4+Ozs+Oz4+Oz4+O3Q8O2w8aTwxPjs+O2w8dDw7bDxpPDA+Oz47bDx0PHQ8OztsPGk8MD47Pj47Oz47Pj47Pj47dDw7bDxpPDE+Oz47bDx0PDtsPGk8MD47PjtsPHQ8dDw7O2w8aTwwPjs+Pjs7Pjs+Pjs+Pjt0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjs+O2w8dDx0PDs7bDxpPDA+Oz4+Ozs+Oz4+Oz4+Oz4+Oz4+Oz4+Oz4+Oz4+Oz4+Oz6UuXSbsuidoQ0BFQiKa4LHqFr3aw==",";",
			"Hawaii","Yes","http://pahoehoe.ehawaii.gov/pvl/app","licno","lictp","CT","_a","Submit Query","_f","lic",";",
			"Iowa","No","http://www2.iwd.state.ia.us/contractor.nsf/WebVVLCR00ByRegNo/?SearchView&Query=Field%20flcrRegNum_IA%20=%",";",
			"Louisiana","Yes","http://www.lslbc.state.la.us/search/cresults.asp","licenseno","count","50",";",
			"Massachusetts","Yes","http://db.state.ma.us/bbrs/hic.pl","keys",";",
			"Minnesota","No","https://www.egov.state.mn.us/Commerce/license_lookup.do?action=results&lookupType=COMP&compName=&compLicType=&compSubmit.x=14&compSubmit.y=13&compLicNumber=","compLicNumber",";",
			"Mississippi","Yes","http://www.msboc.state.ms.us/Results.CFM","Lic","ContractorType","Commercial","VarDatasource","Commercial","vozip__county" ,"","Clas" ,"","Co_Name" ,"","City" ,"","State" ,"","Zip" ,"","Dba_name" ,"","Minority" ,"","OrderBy" ,"Co_Name","maxrecords" ,"25", ";",
			"Montana","No","http://dlihlnerd010.dli.state.mt.us:8911/CRxTier/CntrLstRegID.jsp?cRegId=","cRegId",";",
			"Nebraska","No","http://www.dol.state.ne.us/nwd/center.cfm?PRICAT=4&SUBCAT=4F&ACTION=regcontract&",";",
			"Nevada","Yes","http://nscb.sierracat.com/index.cfm?action=search_results","license",";",
			"New Mexico","Yes","http://www.contractorsnm.com/search/contractors/index.do","licenseNo",";",
			"North Carolina","Yes","http://www.nclbgc.org/lbgcWeb/servlet/LicenseeSearch","LicNum","src","search",";",
			"North Dakota","Yes","https://secure.apps.state.nd.us/sc/busnsrch/busnSearch.htm#Search_Results","srchLicenseNo","command","Search",";",
			"Oregon","Yes","http://ccbed.ccb.state.or.us/New_Web/asp/new_search_results.asp","regno",";",
			"South Carolina","Yes","http://verify.llronline.com/LicLookup/Contractors/Contractor.aspx?div=69","UserInputGen:txt_licNum","__VIEWSTATE","dDw1MDIyODc0ODY7dDw7bDxpPDE+Oz47bDx0PDtsPGk8Mj47aTw2PjtpPDEyPjtpPDEzPjtpPDE0Pjs+O2w8dDxwPHA8bDxUZXh0Oz47bDxDb250cmFjdG9yczs+Pjs+Ozs+O3Q8O2w8aTwxNz47aTwyMz47aTwyNT47aTwyNz47PjtsPHQ8cDxwPGw8VGV4dDtWaXNpYmxlOz47bDxDb21wYW55IG5hbWU6O288dD47Pj47Pjs7Pjt0PHA8cDxsPFZpc2libGU7PjtsPG88dD47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPENsYXNzaWZpY2F0aW9uOjs+Pjs+Ozs+O3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDxMaWNOYW1lO0xpY0lkbnQ7Pj47Pjt0PGk8MzU+O0A8QWxsO0FpciBDb25kaXRpb25pbmcgLSBBQztBc3BoYWx0IFBhdmluZyAtIEFQO0FzcGhhbHQgUGF2aW5nIEFQIEhlYXRpbmcgLSBIVDtCb2lsZXIgSW5zdGFsbGF0aW9uIC0gQkw7Qm9yaW5nIEFuZCBUdW5uZWxpbmcgLSBCVDtCcmlkZ2VzIC0gQlI7QnVpbGRpbmcgLSBCRDtDb25jcmV0ZSAtIENUO0NvbmNyZXRlIFBhdmluZyAtIENQO0VsZWN0cmljYWwgLSBFTDtHZW5lcmFsIFJvb2ZpbmcgLSBHUjtHbGFzcyBBbmQgR2xhemluZyAtIEdHO0dyYWRpbmcgLSBHRDtIaWdod2F5IC0gSFkgKEFQLCBDUCwgQlIsIEdELCBISSk7SGlnaHdheSBJbmNpZGVudGFsIC0gSEk7SW50ZXJpb3IgUmVub3ZhdGlvbiAtIElSO0xpZ2h0ZW5pbmcgUHJvdGVjdGlvbiAtIExQO01hcmluZSAtIE1SO01hc29ucnkgLSBNUztQYWNrYWdlZCBFcXVpcG1lbnQgLSBQSztQaXBlbGluZXMgLSBQTDtQbHVtYmluZyAtIFBCO1ByZS1lbmdpbmVlcmVkIE1ldGFsIEJ1aWxkaW5ncyAtIE1CO1Byb2Nlc3MgUGlwaW5nIC0gMVAgb3IgMlA7UHVibGljIFV0aWxpdHkgRWxlY3RyaWNhbCAtIDFVIG9yIDJVO1JhaWxyb2FkIC0gUlI7UmVmcmlnZXJhdGlvbiAtIFJHO1NwZWNpYWx0eSBSb29maW5nIC0gU1I7U3RydWN0dXJhbCBGcmFtaW5nIC0gU0Y7U3RydWN0dXJhbCBTaGFwZXMgLSBTUztTd2ltbWluZyBQb29scyAtIFNQO1dhdGVyIEFuZCBTZXdlciBMaW5lcyAtIFdMO1dhdGVyIEFuZCBTZXdlciBQbGFudHMgLSBXUDtXb29kIEZyYW1lIFN0cnVjdHVyZXMgLSBXRjs+O0A8QWxsO0FDO0FQO0hUO0JMO0JUO0JSO0JEO0NUO0NQO0VMO0dSO0dHO0dEO0hZO0hJO0lSO0xQO01SO01TO1BLO1BMO1BCO01COzFQOzFVO1JSO1JHO1NSO1NGO1NTO1NQO1dMO1dQO1dGOz4+Oz47Oz47Pj47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPFlvdXIgc2VhcmNoIHJldHVybmVkOiAwIHJlY29yZChzKS5cPEJSXD5cPEJSXD47bzx0Pjs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w8XGU7Pj47Pjs7Pjt0PEAwPHA8cDxsPF8hRGF0YVNvdXJjZUl0ZW1Db3VudDtfIUl0ZW1Db3VudDtQYWdlQ291bnQ7RGF0YUtleXM7PjtsPGk8LTE+O2k8LTE+O2k8MD47bDw+Oz4+Oz47Ozs7Ozs7Ozs7Pjs7Pjs+Pjs+Pjs+OxauStQquGVOZHIzSmdaktYVD1E=",";",
			"Tennessee","Yes","http://www.tennessee.gov/cgi-bin/commerce/rostero.pl","indata","board","Contractors","wordsearch","Exact Word Search","search","Search by License #",";",
			"Utah","Yes","https://secure.utah.gov/llv/llv?sub_no=&action=search&type=by_license_no&core_no=<LIC>","core_num","action","search","type","by_license_no","sub_no",";",
			"Virginia","Yes","http://www.dpor.state.va.us/regulantlookup/searchcollect.cfm?CFID=7928520&CFTOKEN=60644143","qryLicenseNo","qry_NAME","","zipcode","",";",
			"Washington","Yes","https://fortress.wa.gov/lni/bbip/search.aspx","txtSearch","rdoItem","3","btnSearch","Look Up","__VIEWSTATE","dDwtMTE0MDQwMDI0Njt0PDtsPGk8Mj47PjtsPHQ8O2w8aTw2PjtpPDg+O2k8MTA+O2k8MTQ+O2k8MTY+O2k8MTg+O2k8MjA+O2k8MjI+O2k8MjQ+Oz47bDx0PHQ8OztsPGk8Mz47Pj47Oz47dDxwPHA8bDxGb3JlQ29sb3I7XyFTQjs+O2w8MjxCbGFjaz47aTw0Pjs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w8TGljZW5zZSAjIChwYXJ0IG9yIGFsbCk6IDs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w8VHlwZSBvZiBMaWNlbnNlOjs+PjtwPGw8b25jbGljazs+O2w8d2luZG93Lm9wZW4oJ1xcbGljZW5zZXMuaHRtJywgJ2xpY2Vuc2VzJywgJ3dpZHRoPTc2MCwgaGVpZ2h0PTQ4MCwgc2Nyb2xsYmFycz15ZXMnKTs+Pj47Oz47dDx0PDt0PGk8MTY+O0A8XGU7RWxlY3RyaWNhbCBBZG1pbmlzdHJhdG9yO0NvbnN0cnVjdGlvbiBDb250cmFjdG9yO0VsZWN0cmljYWwgQ29udHJhY3RvcjtFbGVjdHJpY2lhbjtFbGVjdHJpY2FsIFRyYWluZWU7TWFzdGVyIEVsZWN0cmljaWFuO01lZGljYWwgR2FzIFBsdW1iZXI7UGx1bWJlcjtQbHVtYmVyIFRyYWluZWU7RWxldmF0b3IgUHJpbWFyeSBDb250YWN0O0VsZXZhdG9yIENFVSBQcm92aWRlcjtFbGV2YXRvciBDb250cmFjdG9yO0VsZXZhdG9yIE1lY2hhbmljO0VsZXZhdG9yIFRyYWluZWU7RWxldmF0b3IgTWVjaGFuaWMgVGVtcDs+O0A8XGU7QUQ7Q0M7RUM7RUw7RVQ7TUU7TUc7UEw7UFQ7TEE7TEk7TEM7TE07TFQ7TFg7Pj47Pjs7Pjt0PHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs+O2w8XGU7bzxmPjs+Pjs+Ozs+O3Q8cDxwPGw8VmlzaWJsZTs+O2w8bzxmPjs+Pjs+Ozs+O3Q8dDxwPHA8bDxWaXNpYmxlOz47bDxvPGY+Oz4+Oz47O2w8aTwwPjs+Pjs7Pjs+Pjs+Pjs+eOah2KcYCy40uqvNTXxn/0MKgRM=",";",
			"Wisconsin","No","http://apps.commerce.state.wi.us/SB_Credential/SB_CredentialApp?cmd=Search&form=SearchByIdForm&cred_id=",";"
	};
	
	public static String displayCountrySubdivisionLink(String selectedCountrySubdivision, String licNum) {
		ArrayList<String> tempAL = new ArrayList<String>();
	
		tempAL.addAll(Arrays.asList(COUNTRY_SUBDIVISION_ARRAY));
		StringBuffer temp = new StringBuffer();
		ListIterator li = tempAL.listIterator();
		while (li.hasNext()) {
			if (((String)li.next()).equals(selectedCountrySubdivision)) {
				String post = (String)li.next();
				String link = (String)li.next();
				String licparam = (String)li.next();
				if ("Yes".equals(post)) {
					temp.append("<form method=\"get\" target=\"_blank\" id=\"form1\" action=\"").append(link).append("\" name=frmInput>");
					temp.append("<input type=\"hidden\" name=\"").append(licparam).append("\" value=\"").append(licNum).append("\">");
					String nextParam = (String)li.next();
					while  (!nextParam.equals(";")) {
						temp.append("<input type=\"hidden\" name=\"").append(nextParam).append("\" value=\"").append((String)li.next()).append("\">");
						nextParam = (String)li.next();
					} //while
					temp.append("<input type=\"image\" name=\"CheckLicense\" src=\"images/checklicense.gif\" alt='Check License'>");
				temp.append("</form>");
				} else {
				temp.append("<a target=\"_blank\" href=\"").append(link).append(licNum).append("\"><img src=\"images/checklicense.gif\" alt='Check License' border=\"0\"></a>");
				}
			}
		}
		
		String junk = temp.toString();
		return temp.toString();
	}

}