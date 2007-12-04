	var min =30;
	var sec =00;
//	var min =5;
//	var sec =5;
	function Display(min, sec) {
		var disp;
		if (min <= 9) disp = " 0";
		else disp = " ";
		disp += min + ":";
		if (sec <= 9) disp += "0" + sec;
		else disp += sec; 
		return (disp);
	}//Display
	function window_onload() {
		Down();	
	}//window_onload
	function Down() { 
		sec--;      
		if (sec == -1) { sec = 59; min--; }
		window.status = "This Session will time out in: " + Display(min, sec);
		if (min == 5 && sec == 0) {
			window.status = "Session about to time out.  Please save your data";
			if (window.opener == null)
				alert('Your session will time out in 5 minutes due to inactivity.  Please save your data or it will be lost.');
		} else {
			down = setTimeout("Down()", 1000);
		}//else			
	}//Down