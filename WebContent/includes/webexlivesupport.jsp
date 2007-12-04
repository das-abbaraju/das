<!--<form name="JoinNowForm" method="get" action="https://picsauditing.webex.com/mw0302l/mywebex/joinnow.do">
<input type="submit" name="JoinNow" style="font-family: Verdana Plain; font-size: 11pt" value="JOIN">
</form>
-->
<form name="JoinNowForm" method="get" action="https://picsauditing.webex.com/mw0302l/mywebex/joinnow.do"
onsubmit="return CheckMeetingNum()" >
  <div class="blueMain"><strong>Web Conference Login</strong></div>
  <br />
  <div class="blueMain" style="margin-bottom:5px">
    Meeting Number
    <input type="text" name="trackNum" maxlength="15" size="20" value="" class="forms">
  </div>
  <div style="margin-left: 95px; text-align: left;">
    <input type="image" value=" Log In " name="btnLogon" src="images/button_login.jpg" width="65" height="28" border="0">
  </div>
<input type="hidden" name="siteurl" value="picsauditing">
<input type="hidden" name="inputURL" value="welcome">
</form>
<script language="JavaScript">
function AtTrim(s){
	var r1, r2, s1, s2, s3;
    r1 = new RegExp("^ *");
    r2 = new RegExp(" *$");
    s1 = ""+s+"";
    s2 = s1.replace(r1, "");
    s3 = s2.replace(r2, "");
    r1 = null;
    r2 = null;
    return(s3);
}
function IsSpaceNum(str){
	for (i=0;i< str.length;i++){
		c = str.charAt(i);
		if ((c<='9' && c>='0') || c==' ')
			continue;
		else
			return false;
	}
	return true;
}
  function trimSpace(str){
      var r1 = /[\s]*/g;
      var s1 = ""+str+"";
      var s2 = s1.replace(r1, "");
      return s2;
  }
  function CheckMeetingNum(){
      document.JoinNowForm.trackNum.value=trimSpace(document.JoinNowForm.trackNum.value);
    if (AtTrim(document.JoinNowForm.trackNum.value) == ""){
            alert("Please provide the meeting number.");
            document.JoinNowForm.trackNum.focus();
            return false;
    } else if (document.JoinNowForm.trackNum.value == 0){
            alert("The meeting number cannot be zero (0).");
            document.JoinNowForm.trackNum.focus();
            return false;
    } else if ((document.JoinNowForm.trackNum.value).indexOf(".")!=-1){
            alert("The meeting number cannot contain a period (.).");
            document.JoinNowForm.trackNum.focus();
            return false;
    } else if (!IsSpaceNum(document.JoinNowForm.trackNum.value)){
            alert("The meeting number can only contain numbers.");
            document.JoinNowForm.trackNum.focus();
            return false;
    }
    return true;
  }
  </script>

<!--<form name="LoginActionForm" method="post" action="https://picsauditing.webex.com/mw0302l/mywebex/login/login.do">
  <div class="blueMain" style="text-align: center; padding-bottom: 2px;">
    <strong>Web Conference</strong>
  </div>
  <div style="padding: 3px;">
    <img src="images/login_user.gif" alt="User Name" width="50" height="9">
    <input type="text" name="userName" maxlength="60" size="30" class="forms">
  </div>
  <div style="padding: 3px;">
    <img src="images/login_pass.gif" alt="Password" width="50" height="9">
    <input type="password" name="password" size="30" maxlength="60" class="forms">
  </div>
  <div style="margin-left: 95px; text-align: left;">
    <input type="image" value=" Log In " name="btnLogon" src="images/button_login.jpg" width="65" height="28" border="0">
  </div>
  <input type="hidden" name="oneclicklogin" value="">
  <input type="hidden" name="allowAccountSignUp" value="false">
  <input type="hidden" name="returnUrl" value="">
  <input type="hidden" name="siteurl" value="picsauditing">
</form>-->
