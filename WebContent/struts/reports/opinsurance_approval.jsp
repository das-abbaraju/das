<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
    <title><s:text name="ReportInsuranceApproval.title" /></title>
    
    <s:include value="reportHeader.jsp" />
    
    <script type="text/javascript">
        $(function () {
        	$('.buttons').delegate('.searchByFlag', 'click', function () {
        		var flag = $(this).data('color');
        		
        		$('[name="filter.recommendedFlag"]').val(flag);
        		
        		return clickSearch('form1');
        	}); 	
        });
    </script>
    
    <script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js?v=${version}"></script>
</head>
<body>
    <h1><s:text name="ReportInsuranceApproval.title" /></h1>
    
    <div class="buttons">
    	<button class="picsbutton searchByFlag" data-color="Green">
    		<s:property	value="@com.picsauditing.jpa.entities.FlagColor@Green.bigIcon" escape="false"/>
    		<s:text name="ReportInsuranceApproval.ShowPoliciesToApprove" />
    	</button>
        
    	<button class="picsbutton searchByFlag" data-color="Red">
    		<s:property	value="@com.picsauditing.jpa.entities.FlagColor@Red.bigIcon" escape="false"/>
    		<s:text name="ReportInsuranceApproval.ShowPoliciesToReject" />
    	</button>
    </div>
    
    <div class="clear"></div>
    
    <s:include value="filters.jsp" />
    
    <div id="messages"></div>
    <div id="noteAjax" style="display: none;"></div>
    
    <div id="${actionName}-page">
        <s:form id="approveInsuranceForm" method="post">
            <div id="report_data">
            	<s:include value="opinsurance_approval_data.jsp" />
            </div>
        </s:form>
    </div>
</body>