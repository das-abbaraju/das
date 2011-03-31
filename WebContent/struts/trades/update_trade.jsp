<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css?v=<s:property value="version"/>" />
<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
<script type="text/javascript">
$(function() {
	$('#editSave').click(function() {
    	$.unblockUI();
    	$('#editButton').toggle();
    	$('#editBox').empty();
    	$('#editBox').empty();
    	return false;
        // update the block message 
        //$.blockUI({ message: "<h1>Remote call in progress...</h1>" }); 

        //$.ajax({url: 'wait.php',cache: false,complete: function() { $.unblockUI();}}); 
    }); 

    $('#editCancel').click(function() { 
        $.unblockUI(); 
        $('#editButton').toggle();
    	$('#editBox').empty();
        return false; 
    });
    
    $('.psAutocomplete').autocomplete('TradeAutocomplete.action', {
    	minChars: 3,
    	formatResult: function(data,i,count) { return data[1]; }
    });
});
</script>

<s:form>
	<s:hidden name="id" value="%{id}" />
	<fieldset class="form">
		<h2 class="formLegend">Edit Product/Service</h2>
		<ol>
			<li><label><s:text name="%{scope}.label.ClassificationType" /></label>
				<s:select list="@com.picsauditing.jpa.entities.ClassificationType@values()" name="trade.classificationType" />
			</li>
			<li><label><s:text name="%{scope}.label.ClassificationCode" /></label>
				<s:textfield cssClass="psAutocomplete" name="trade.classificationCode" />
			</li>
			<li><label><s:text name="%{scope}.label.Description" /></label>
				<s:textarea name="trade.description" />
			</li>
			<li><label><s:text name="%{scope}.label.Parent" /></label>
				<s:textfield cssClass="psAutocomplete" name="trade.parent" />
			</li>
			<li><label><s:text name="%{scope}.label.BestMatch" /></label>
				<s:textfield cssClass="psAutocomplete" name="trade.bestMatch" />
			</li>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<s:submit action="UpdateTradeAjax!save" value="%{getText('button.Save')}" cssClass="picsbutton positive" id="editSave" />
		<input type="button" class="picsbutton negative" value="<s:text name="button.Cancel" />" id="editCancel" />
	</fieldset>
</s:form>