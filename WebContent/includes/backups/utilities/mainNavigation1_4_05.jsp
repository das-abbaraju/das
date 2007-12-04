<% if ("admin".equals((String)session.getAttribute("usertype"))) {
%>
<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
              <param name="movie" value="../flash/nav_admin1.swf">
            <param name="quality" value="high">
<embed src="../flash/nav_admin1.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed></object>

<% } else if ("Operator".equalsIgnoreCase((String)session.getAttribute("usertype")) 
		|| "General".equalsIgnoreCase((String)session.getAttribute("usertype"))) {
%>
<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
              <param name="movie" value="../flash/nav_operator.swf">
            <param name="quality" value="high">
<embed src="../flash/nav_operator.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed></object>
<% } else if ("Contractor".equalsIgnoreCase((String)session.getAttribute("usertype"))) {
%>
<%/*<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
              <param name="movie" value="nav_contractor1.swf">
            <param name="quality" value="high">
            <embed src="nav_contractor1.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed></object>
*/%>
<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
              <param name="movie" value="../flash/NAV_CONTRACTORS1.swf">
            <param name="quality" value="high">
<embed src="../flash/NAV_CONTRACTORS1.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed></object>
<% } else  if ("Auditor".equalsIgnoreCase((String)session.getAttribute("usertype"))) {
%>
<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
              <param name="movie" value="../flash/nav_auditor.swf">
            <param name="quality" value="high">
<embed src="../flash/nav_auditor.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed></object>
<%	} else {
%>
<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
              <param name="movie" value="../flash/NAV_CONTRACTORS1.swf">
            <param name="quality" value="high">
<embed src="../flash/NAV_CONTRACTORS1.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed></object>

<% }//if %>

			<%/*
			      <td width="147"><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="147" height="72">
        <param name="movie" value="NAV_ADMIN.swf">
        <param name="quality" value="high">
        <embed src="NAV_ADMIN.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="147" height="72"></embed></object>
</td>
*/%>