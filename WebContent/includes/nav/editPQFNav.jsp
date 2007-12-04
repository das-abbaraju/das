<%	String thisPage = request.getServletPath();
	String thisQuery = request.getQueryString();
	String temp = "";
%>
	<center>	
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editQuestions.jsp",thisPage,temp,"",thisQuery,"Edit Audit Questions")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editQuestion.jsp",thisPage,temp,"",thisQuery,"Add Audit Question")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editSubCategories.jsp",thisPage,temp,"",thisQuery,"Edit Audit Sub Categories")+"<br>"+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editSubCategory.jsp",thisPage,temp,"",thisQuery,"Add Audit Sub Category")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editCategories.jsp",thisPage,temp,"",thisQuery,"Edit Audit Categories")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editCategory.jsp",thisPage,temp,"",thisQuery,"Add Audit Category")+"<br>"+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editPreview.jsp",thisPage,temp,"",thisQuery,"Preview Edit Audit")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_viewAll.jsp",thisPage,temp,"",thisQuery,"Preview Entire Audit")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_selectType.jsp",thisPage,temp,"",thisQuery,"Select Audit Type")+"<br>"+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_desktopMatrix.jsp",thisPage,temp,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"Edit PQF Matrix")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_desktopMatrix.jsp",thisPage,temp,"auditType="+com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE,thisQuery,"Edit Desktop Matrix")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_regeneratePQFCategories.jsp",thisPage,temp,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"Re-gen PQF Categories")
%>
	</center>