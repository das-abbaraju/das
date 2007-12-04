<jsp:useBean id="oBean" class="com.picsauditing.PICS.OSHABean" scope ="page"/>


<table>
  <tr class="blueMain"> 
    <%String HR=request.getParameter("select_HR"); if (!(HR!=null)) HR="Choose One";
	  String EMR=request.getParameter("select_EMR"); if (!(EMR!=null)) EMR="Choose One";%>
    <td> 
      <form name="form1" method="post" action="pqf_viewQuestions.jsp">
        Choose the Hurdle Rates 
        <select name="select_HR" class="blueMain">
          <option value="<%=HR%>"><%=HR%></option>
          <option value="Year">Year By Year</option>
          <option value="Average">3 Years</option>
        </select>
        &amp; EMR 
        <select name="select_EMR" class="blueMain">
		  <option value="<%=EMR%>"><%=EMR%></option>
          <option value="Choose One">Choose One</option>
          <option value="Year">EMR Year by Year</option>
          <option value="Average">EMR 3 years Average</option>
        </select>
        <input type="submit" name="Submit" value="Submit">
      </form> </td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <% if (!(HR.equals("Choose One"))){ %>
  <tr class="blueMain"> 
    <td bgcolor="#003366" colspan="4" align="center"> <font color="#FFFFFF"><strong> 
      Category 7 - Set Hurdle Rates <%=oBean.getLocationDescription()%></strong></font> </td>
  </tr>
  <tr class="blueMain"> 
    <td height="19" colspan="4" align="center">&nbsp;</td>
  </tr>
  <tr class="blueMain"> 
    <td bgcolor="#003366" colspan="4" align="center"> <font color="#FFFFFF"><strong> 
      </strong></font></td>
  </tr>
  <tr align="center" class="blueMain"> 
    <td colspan="4"> <table border="1" cellpadding="5" cellspacing="0" bordercolor="#FFFFFF" class="blueMain">
        <tr> 
          <td width="464" class="redMain">&nbsp;</td>
          <td width="59">No.</td>
          <td width="55">Rate</td>
        </tr>
        <tr> 
          <td class="redMain">Number of Fatalities........................ <% if (HR.equals("Year")){%>
            2003 
            <input name="Fat1" type="text" size="5">
            2004 
            <input name="Fat2" type="text" size="5">
            2005 
            <input name="Fat2" type="text" size="5"> <%}%></td>
          <td> <% if (HR.equals("Average")){%> <input name="fatal_number" type="text" size="5"> 
            <%}%> </td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td colspan="2" class="redMain">Total OSHA Recordable Injuries and Illnesses 
            <% if (HR.equals("Year")){%>
            2003 
            <input name="TO1" type="text" size="5">
            2004 
            <input name="TO2" type="text" size="5">
            2005 
            <input name="TO3" type="text" size="5"> <%}%> </td>
          <td>
            <% if (HR.equals("Average")){%>
            <input name="restricted3" type="text" size="5">
            <%}%>
          </td>
        </tr>
        <%}//choose one%>
      </table>
      <p>&nbsp;</p></td>
  </tr>
</table>
<table>
  <% if (!(HR.equals("Choose One"))){ %>
  <tr class="blueMain"> 
    <td width="353" height="8" colspan="4" align="center" bgcolor="#003366">
        <font color="#FFFFFF"><strong> EMR</strong></font> 
      </td>
  </tr>
  <tr class="blueMain">
    <td height="9" colspan="4" align="center">EMR 
      <% if (EMR.equals("Year")){%>
      2003 
      <input name="EMR1" type="text" size="5">
      2004 
      <input name="EMR2" type="text" size="5">
      2005 
      <input name="EMR3" type="text" size="5">
      <%}%>
      <% if (EMR.equals("Average")){%>
      <input name="EMR_average" type="text" size="5"> 
      <%}%>
    </td>
  </tr>
  <tr class="blueMain"> 
    <td bgcolor="#003366" colspan="4" align="center"> <font color="#FFFFFF"><strong> 
      </strong></font></td>
  </tr><%}%>
</table>
<p>&nbsp;</p>
