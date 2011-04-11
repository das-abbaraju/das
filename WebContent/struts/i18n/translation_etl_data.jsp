<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<div class="right"><s:property value="foundRows" /> entries found</div>
<a href="#" id="copyToClipboard" onclick="return false;">Copy to Clipboard</a><br />
<s:textarea name="translations" id="translationsArea" rows="20" cssStyle="width: 100%;" />
<script type="text/javascript">
$(function() {
	$('#copyToClipboard').zclip({ 
		path:"js/jquery/zClip/ZeroClipboard.swf",
		copy:$("#translationsArea").text(),
		beforeCopy:function(){alert("COPY?");},
		afterCopy:function(){alert("COPIED?");}
	});
});
</script>