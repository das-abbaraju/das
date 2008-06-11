<script type="text/javascript" src="js/prototype.js"></script>
<span id="conHeader">
<h1>Loading Contractor Information</h1>
<img src="images/ajax_process.gif">
<br /><br /><br /><br /><br /><br /><br /><br /><br />
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
