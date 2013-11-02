<!-- Grab Google CDN's jQuery, with a protocol relative URL; fall back to local if necessary -->
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script>window.jQuery || document.write('<script src="js/jquery/jquery-1.7.1.min.js">\x3C/script>')</script>

<%--
Note: 1.7.2 version affects the website in the followings ways, upgrading jquery.ui may effect the following

- Including multiple jquery.ui will break plugins
- UI calendar z-index
- MySchedule calendar plugin

--%>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>

<%-- DO NOT MODIFY --%>
<script type="text/javascript" src="v7/js/pics/core/core.js?v=${version}"></script>
<script type="text/javascript" src="v7/js/pics/widget/modal.js?v=${version}"></script>
<script type="text/javascript" src="v7/js/pics/widget/mibew.js?v=${version}"></script>

<%-- v7 Chart Classes --%>
<script type="text/javascript" src="v7/js/pics/widget/chart/Chart.js?v=${version}"></script>
<script type="text/javascript" src="v7/js/pics/widget/chart/_child/BasicChart.js?v=${version}"></script>
<script type="text/javascript" src="v7/js/pics/widget/chart/_child/StackedColumnChart.js?v=${version}"></script>
<script type="text/javascript" src="v7/js/pics/widget/chart/_child/custom-color-chart/CustomColorChart.js?v=${version}"></script>
<script type="text/javascript" src="v7/js/pics/widget/chart/_child/custom-color-chart/_child/flags-chart/FlagsChart.js?v=${version}"></script>
<script type="text/javascript" src="v7/js/pics/widget/chart/_child/custom-color-chart/_child/flags-chart/_child/StackedFlagsChart.js?v=${version}"></script>
<%-- END v7 Chart Classes --%>

<script type="text/javascript" src="v7/js/vendor/select2.js?v=${version}"></script>
<script type="text/javascript" src="v7/js/pics/widget/session-timer.js?v=${version}"></script>
<script type="text/javascript" src="v7/js/pics/country/country.js?v=${version}"></script>
<script type="text/javascript" src="v7/js/pics//select2/select2.js?v=${version}"></script>

<%-- END DO NOT MODIFY --%>

<script type="text/javascript" src="js/main.js?v=${version}"></script>
<script type="text/javascript" src="js/ajax.js?v=${version}"></script>
<script type="text/javascript" src="js/bootstrap/bootstrap-modal.js?v=${version}"></script>
<script type="text/javascript" src="js/bootstrap/bootstrap-tooltip.js?v=${version}"></script>
<script type="text/javascript" src="js/bootstrap/bootstrap-popover.js?v=${version}"></script>
<script type="text/javascript" src="js/jquery/tagit/jquery.tagit.js?v=${version}"></script>
<script type="text/javascript" src="js/audit_category_matrix.js?v=${version}"></script>
<script type="text/javascript" src="js/con_audit_new.js?v=${version}"></script>
<script type="text/javascript" src="js/contractor_dashboard.js?v=${version}"></script>
<script type="text/javascript" src="js/contractor_flag.js?v=${version}"></script>
<script type="text/javascript" src="js/contractor_safety_statistics.js?v=${version}"></script>
<script type="text/javascript" src="js/header_search.js?v=${version}"></script>
<script type="text/javascript" src="js/main_system_message.js?v=${version}"></script>
<script type="text/javascript" src="js/operator_edit.js?v=${version}"></script>
<script type="text/javascript" src="js/registration.js?v=${version}"></script>
<script type="text/javascript" src="js/registration/registration-validation.js?v=${version}"></script>
<script type="text/javascript" src="js/registration/registration_add_client_site.js?v=${version}"></script>
<script type="text/javascript" src="js/registration/registration_make_payment.js?v=${version}"></script>
<script type="text/javascript" src="js/con_payment_options.js?v=${version}"></script>
<script type="text/javascript" src="js/contractor_facilities.js?v=${version}"></script>
<script type="text/javascript" src="js/registration_request_report.js?v=${version}"></script>
<script type="text/javascript" src="js/subcontractors_report.js?v=${version}"></script>
<script type="text/javascript" src="js/trade.js?v=${version}"></script>
<script type="text/javascript" src="js/translation_trace.js?v=${version}"></script>
<script type="text/javascript" src="js/translation_unsynced.js?v=${version}"></script>
<script type="text/javascript" src="js/utility.js?v=${version}"></script>
<script type="text/javascript" src="js/workflow_manage.js?v=${version}"></script>
<script type="text/javascript" src="js/audit/audit.js?v=${version}"></script>
<script type="text/javascript" src="js/audit/audit-controller.js?v=${version}"></script>
<script type="text/javascript" src="js/audit/cao-table-controller.js?v=${version}"></script>
<script src="js/audit/ManageAuditTypeController.js?v=${version}"></script>
<script src="js/audit/ManageQuestionController.js?v=${version}"></script>
<script type="text/javascript" src="js/employee/employee_competencies.js?v=${version}"></script>
<script type="text/javascript" src="js/employee/employee_detail.js?v=${version}"></script>
<script type="text/javascript" src="js/employee/manage_employees.js?v=${version}"></script>
<script type="text/javascript" src="js/employee/manage_job_roles.js?v=${version}"></script>
<script type="text/javascript" src="js/employee/skills_and_training.js?v=${version}"></script>
<script type="text/javascript" src="js/insureguard/report_insurance_approval.js?v=${version}"></script>
<script type="text/javascript" src="js/insureguard/report_insurance_approval_controller.js?v=${version}"></script>
<script type="text/javascript" src="js/contractor/dashboard/dashboard-controller.js?v=${version}"></script>
<script type="text/javascript" src="js/contractor/edit-account/edit-controller.js?v=${version}"></script>
<script type="text/javascript" src="js/contractor/lc-cor/lc-cor-controller.js?v=${version}"></script>
<script type="text/javascript"
        src="js/contractor/third-party-identifier/identifier-controller.js?v=${version}"></script>
<script type="text/javascript" src="js/operator/define_competencies.js?v=${version}"></script>
<script type="text/javascript" src="js/request/registration_gap_analysis.js?v=${version}"></script>
<script type="text/javascript" src="js/request/request_new_contractor.js?v=${version}"></script>
<script src="js/billing/InvoiceDetailController.js?v=${version}"></script>
<script type="text/javascript" src="js/widget/Slugifier.js?v=${version}"></script>

<%-- struts utils --%>
<script type="text/javascript" src="js/utils.js?v=${version}"></script>
<script type="text/javascript" src="TranslateJS.action"></script>

<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script>google.load('visualization', '1.0', {'packages':['corechart']});</script>
<!--[if IE 6]><script src="js/jquery/supersleight/supersleight.plugin.js?v=${version}"></script><![endif]-->
<!--[if IE 6]><script>$('body').supersleight({shim: 'images/x.gif'});</script><![endif]-->
