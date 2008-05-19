<script type="text/javascript" src="js/prototype.js"></script>
<span id="conHeader" style="min-height: 100px">
<img src="imgages/ajax_process.gif">
...loading header information
</span>
<script type="text/javascript">
	var pars = "";
	
	<% if( request.getParameter( "auditID" ) != null ) { %>
	pars = pars + "auditID=" + <%=request.getParameter("auditID") %> + "&";
	<% } %>
	
	<% if( request.getAttribute( "subHeading" ) != null ) { %>
	pars = pars + "subHeading=" + "<%= (String) request.getAttribute("subHeading") %>" + "&";
	<% } %>
	
	pars = pars + "id=" + <%= conID %>;
	
	var divName = 'conHeader';
	var myAjax = new Ajax.Updater(divName,'ConHeader.action', {method: 'post', parameters: pars});
</script>
