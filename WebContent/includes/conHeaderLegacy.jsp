<script type="text/javascript" src="js/prototype.js"></script>
<div id="conHeader"> </div>
<script type="text/javascript">
	var pars = "auditID=" + <%=request.getParameter("auditID") %>;
	pars = pars + "&id=" + <%= conID %>;
	
	var divName = 'conHeader';
	var myAjax = new Ajax.Updater(divName,'ConHeader.action', {method: 'post', parameters: pars});
</script>
