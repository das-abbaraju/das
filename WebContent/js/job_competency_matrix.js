function showEdit(jobRoleID) {
	$("#role" + jobRoleID + "_view").hide();
	$("#role" + jobRoleID + "_edit").show();
}

function showView(jobRoleID) {
	$("#role" + jobRoleID + "_edit").hide();
	$("#role" + jobRoleID + "_view").show();
}

function competency(competencyID, label, percentUsed, selected) {
	this.competencyID = competencyID;
	this.label = label;
	this.percentUsed = percentUsed;
	this.selected = selected;
}

function fillTables(compList, jobRoleID) {
	$("#requiredComp" + jobRoleID).empty();
	$("#notRequiredComp" + jobRoleID).empty();
	
	for(i=0; i < compList.length; i++) {
		if (compList[i].selected) {
			$("#requiredComp" + jobRoleID).append("<tr class='clickable' onclick='move(compList"+jobRoleID+", "+jobRoleID+", "+i+", false);'><td>"+compList[i].label+"</td></tr>");
		} else {
			$("#notRequiredComp" + jobRoleID).append("<tr class='clickable' onclick='move(compList"+jobRoleID+", "+jobRoleID+", "+i+", true);'><td>"+compList[i].label+"</td><td class='right'>"+compList[i].percentUsed+"</td></tr>");
		}
	}
}

function move(compList, jobRoleID, i, selected) {
	compList[i].selected = selected;
	fillTables(compList, jobRoleID);
}
