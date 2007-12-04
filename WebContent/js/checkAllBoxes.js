function checkAll(thisForm){
	for (var i=0;i<thisForm.elements.length;i++){
		var e=thisForm.elements[i];
		if ((e.name!='checkAllBox')&&(e.type=='checkbox'))
			e.checked=document.getElementById('checkAllBox').checked;
	}//for
}//checkAll
