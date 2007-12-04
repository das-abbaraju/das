<%	int numQuestions = 0;
	if (0==catCount)
		catCount = Integer.parseInt(pcBean.number);
	while (pqBean.isNextRecord()) {
		numQuestions++;
%>
					<%=pqBean.getTitleLine("blueMain")%>
<%		if (!"".equals(pqBean.getOriginalAnswerView())) { %>
					<tr <%=pqBean.getGroupBGColor()%> class=blueMain>
                      <td valign="top" width="1%"><%=catCount%>.<%=numSections%>.<%=pqBean.number%></td>
                      <td valign="top"><%=pqBean.question%> <%=pqBean.getLinks()%><br>
                        <%=pqBean.getOriginalAnswerView()%> <%=pqBean.getVerifiedAnswerView()%> <%=pqBean.getCommentView()%>
                      </td>
					  <td></td>
                    </tr>
<%		}//if	
	}//while %>
