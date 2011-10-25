// JavaScript Document

var BusinessUnitList= new Array();
BusinessUnitList[0] = "Suncor Energy NAO";
BusinessUnitList[1] = "Suncor Energy Edmonton Refinery";
BusinessUnitList[2] = "Suncor Energy Major Projects";
BusinessUnitList[3]	= "Suncor Energy Oil Sands";

$(document).ready(function() {
	$("input:submit").button();
	
	$("#btnLogin").click(function() {
			if ($("[name=FirstName]").val() == "" || $("[name=LastName]").val() == "" || $("[name=EmailAddress]").val() == "") {
				Surge.Notify.message("Required Information Needed", "Please ensure you have entered information for all fields, including Company, First Name, Last Name, and Email Address");
			}
			else {
				window.location.replace("home.html?Name=" + $("[name=FirstName]").val() + " " + $("[name=LastName]").val() + "&Company=" + $("[name=Company]").val());
			}
	});

});

function getUrlVars()
{
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}