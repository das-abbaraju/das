	    <table border="0" cellspacing="1" cellpadding="0" BGCOLOR="#EEEEEE">
              <tr align="center" valign="top">
  					<%firstSquare = true;
					for (int i=1;i<6;i+=1) {
     					if (i==whichPage) {%>
						<td width="72"><img src="/images/squarePhoto<%=i%>.gif" height="72" border="0"></td>
						<%} else if (i == whichPage - 1) { %>	
							<td bgcolor="#6699cc" width="72"></td>
						<% } else { %>
						<td  width="72"></td>
						<%}//if-else
					firstSquare = false;
					out.println("</td>");			
				 } //for %>	
		 </tr></table>