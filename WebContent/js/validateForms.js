function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function validateNumber() {
  var i,val,qName,errors='',args=validateNumber.arguments;
  qName=args[1];
  val=MM_findObj(args[0]);
  if (val)
  	val = val.value;
  if (val && (""!=val.value)) {
    var strValidChars = "0123456789.-,";
    var strChar;
    var blnResult = true;
    //  test strString consists of valid characters listed above
    for (i = 0; i < val.length && blnResult == true; i++) {
      strChar = val.charAt(i);
      if (strValidChars.indexOf(strChar) == -1)
        blnResult = false;
    }
	if (!blnResult) {
      errors+='- '+qName+' must contain a number.\n';
      alert('The following error(s) occurred:\n'+errors);
    }
  }
  document.MM_returnValue = (errors == '');
}

function validateDecimal() {
  var i,val,qName,errors='',args=validateDecimal.arguments;
  qName=args[1];
  val=MM_findObj(args[0]);
  if (val)
  	val = val.value;
  if (val && (""!=val.value)) {
    var strValidChars = "0123456789.-,";
    var strChar;
    var blnResult = true;
    //  test strString consists of valid characters listed above
    for (i = 0; i < val.length && blnResult == true; i++) {
      strChar = val.charAt(i);
      if (strValidChars.indexOf(strChar) == -1)
        blnResult = false;
    }
    
	if (!blnResult) {
      errors+='- '+qName+' must contain a decimal number.\n';
      alert('The following error(s) occurred:\n'+errors);
    }//if
  }//if
  document.MM_returnValue = (errors == '');
}
