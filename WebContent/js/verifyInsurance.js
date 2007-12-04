window.onload = function () {
	setDisplay();
	}

function setDisplay() {
 	
 	var inputs = getElements(null, "input", document);
 	var elems= getElementsByName(inputs, "canSeeInsurance");
 	var span = document.getElementById("auditorID");
 	
 	if(elems[0].checked){
 		span.className = "display_on"; 		
 	}else {
 		span.className = "display_off";
 	}
 }
 
 