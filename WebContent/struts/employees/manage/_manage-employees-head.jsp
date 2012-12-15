<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<head>

	<title>
		<s:text name="ManageEmployees.title" />
	</title>

	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=${version}" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=${version}" />
	<link rel="stylesheet" type="text/css" href="js/jquery/dataTables/css/dataTables.css?v=${version}"/>

	<style>
		.sites-label {
			display: inline !important;
			font-weight: normal !important;
		}

		.layout td {
			vertical-align: top;
		}

		#new_project_div,
		.qualified-tasks {
			display: none;
		}
	</style>

	<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js?v=${version}"></script>
	<script type="text/javascript" src="js/jquery/cluetip/jquery.cluetip.min.js?v=${version}"></script>
	<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js?v=${version}"></script>

</head>
