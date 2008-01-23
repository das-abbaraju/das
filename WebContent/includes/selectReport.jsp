          <table border="0" cellpadding="0" cellspacing="0">
            <tr>             
  		    <td colspan="2" align="center" class="redMain"> 
  		      <form name="form1" method="post" action="reports.jsp">
<%	if (pBean.isAdmin()){%>
                      <select id="report" name="report" class="forms" onChange="this.form.submit()">
			            <option>-- Select a report --</option>
      		            <option value="report_activation.jsp?changed=1">Activation</option>
      		            <option value="report_annualUpdate.jsp?changed=1">Annual Update</option>
      		            <option value="report_audits.jsp?changed=1">Audit Dates</option>
			            <option value="report_operatorContractor.jsp?changed=1">Contractor Assignments</option>
			            <option value="report_contactInfo.jsp?changed=1">Contractor Contact Info</option>
      		            <option value="report_EMRRates.jsp?changed=1">EMR Rates</option>
			            <option value="report_expiredAudits.jsp">Expired Audits</option>
			            <option value="report_expiredCertificates.jsp">Expired Insurance Certificates</option>
      		            <option value="report_fatalities.jsp?changed=1">Fatalities</option>
      		            <option value="report_incidenceRates.jsp?changed=1">Incidence Rates</option>
						<option value="report_incompleteAudits.jsp?changed=1&incompleteAfter=3">Incomplete Audits</option>
			            <option value="report_certificates.jsp?">Insurance Certificates</option>
						<option value="report_ncms.jsp?changed=1">NCMS Data</option>
						<option value="audit_calendar.jsp?changed=1">Office Audit Calendar</option>
						<option value="report_payment.jsp?changed=1">Payment</option>
      		            <option value="op_editFlagCriteria.jsp?changed=1">Red Flag Criteria</option>
			            <option value="report_scheduleAudits.jsp?changed=1&which=<%=com.picsauditing.PICS.SearchBean.RESCHEDULE_AUDITS%>">Reschedule Audits</option>
			            <option value="report_daAudit.jsp?changed=1">Schedule Drug &amp; Alcohol Audit</option>
			            <option value="report_desktop.jsp?changed=1">Schedule Desktop Audit</option>
			            <option value="report_scheduleAudits.jsp?changed=1">Schedule Office Audits</option>
      		            <option value="report_upgradePayment.jsp?changed=1">Upgrade Payment</option>
<!-- 				        <option value="http://localhost:8480/PICSReportsProject">Osha Logs Verification</option>-->
<%/*			            <option value="report_RFR.jsp">PQF Snapshot Report</option>
						<option value="pqf_viewQuestions.jsp?id=<%=pBean.userID%">Create PQF Snapshot</option>
 */%>		  </select>
<%	} else {%>
			          <select id="report" name="report" class="forms" onChange="this.form.submit()">
			            <option>-- Select a report --</option>
			            <option value="report_operatorContractor.jsp?changed=1&searchCorporate=Y">Corporate Contractors</option>
			            <option value="report_contactInfo.jsp?changed=1">Contractor Contact Info</option>
<%	if(pBean.oBean.seesAllContractors() &&  pBean.userAccess.hasAccess(com.picsauditing.access.OpPerms.SearchContractors)){%>

			            <option value="contractorsSearch.jsp?changed=1">Contractor Search</option>
<%	}//if%>
      		            <option value="report_EMRRates.jsp?changed=1">EMR Rates</option>
			            <option value="report_expiredAudits.jsp">Expired Audits</option>
<%	if(pBean.userAccess.hasAccess(com.picsauditing.access.OpPerms.InsuranceCerts)){%>
			            <option value="report_expiredCertificates.jsp">Expired Insurance Certificates</option>
<%	}//if%>
      		            <option value="report_fatalities.jsp?changed=1">Fatalities</option>
      		            <option value="report_incidenceRates.jsp?changed=1">Incidence Rates</option>
						<option value="report_incompleteAudits.jsp?changed=1&incompleteAfter=3">Incomplete Audits</option>
<%	if(pBean.userAccess.hasAccess(com.picsauditing.access.OpPerms.InsuranceCerts)){%>
			            <option value="report_certificates.jsp?">Insurance Certificates</option>
<%	}//if%>
<%	if(pBean.userAccess.hasAccess(com.picsauditing.access.OpPerms.OfficeAuditCalendar)){%>
			            <option value="audit_calendar.jsp">Office Audit Calendar</option>
<%	}//if%>
<!--       		            <option value="report_hurdleRates.jsp?changed=1">Red Flag Hurdle Rates</option>
 -->
<%	if(pBean.userAccess.hasAccess(com.picsauditing.access.OpPerms.EditFlagCriteria)){%>
       		            <option value="op_editFlagCriteria.jsp?changed=1">Red Flag Criteria</option>
<% }//if%>
<!--			            <option value="report_RFR.jsp">PQF Snapshot Report</option>
						<option value="pqf_viewQuestions.jsp?id=<%=pBean.userID%>">Create PQF Snapshot</option>
-->          </select>
<%	}//else%>
        </form>
              </td>
            </tr>
          </table>