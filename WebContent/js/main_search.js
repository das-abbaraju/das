var autoSearch;
var SEARCH_DELAY = 200;
$(function(){
	autoSearch = $('#search_box').autocomplete('HeaderSearchAjax.action', {
		width: 325,
		scroll: false,
		max: 11,
		delay: SEARCH_DELAY*2,
		selectFirst: false,
		highlight : false,
		formatItem: function(data,i,count){
			return format(data, i);
		},
		formatResult: function(data,i,count){
			if(data[0]=='FULL')
				return " ";
			if(data[0]=='NULL')
				return " ";
			return data[3];
		}
	}).result(function(event, data){
		getResult(data);
	}).keydown(function(){
		var tSize = $(this).val().length;
		if(tSize<=1)
			sDelay = SEARCH_DELAY * 2;
		else if(tSize<=2)
			sDelay = SEARCH_DELAY * 1.5;
		else if(tSize<=3)
			sDelay = SEARCH_DELAY * 1.25;
		else
			sDelay = SEARCH_DELAY;
		$(this).setOptions({delay: sDelay});
	});
});
function getResult(data){
	if(data[0]=='FULL'){
		location.href='Search.action?button=search&searchTerm='+data[2];
		return;
	}
	if(data[0]=='NULL'){
		return;
	}
	location.href='HeaderSearchAjax.action?button=getResult&searchID='+data[2]+'&searchType='+data[0];
}
function format(row, i){
	if(row[0]=='account'){
		var rStr = "<div style=\"float: left; margin-right: 5px;\"><strong>"+row[1]+":</strong><br/><span style=\"font-size: .9em\">(ID "+row[2]+
			")</span></div>"+row[3];
		if(row[4]!=null)
			rStr+="<br/> at ("+row[4]+")";
		return rStr;
	}
	if(row[0]=='user'){
		var rStr = "<div style=\"float: left; margin-right: 23px;\"><strong>"+row[1]+":</strong><br/><span style=\"font-size: .9em\">(ID "+row[2]+
			")</span></div><div style=\"\">"+row[3]+"<br/> at ("+row[4]+")</div>";
		return rStr;
	}
	if(row[0]=='employee'){
		var rStr = "<div style=\"float: left; margin-right: 10px;\"><strong>"+row[1]+":</strong><br/><span style=\"font-size: .9em\">(ID "+row[2]+
			")</span></div><div style=\"\">"+row[3]+"<br/> at ("+row[4]+")</div>";
		return rStr;
	}
	if(row[0]=='audit'){
		var rStr = "<div style=\"float: left; margin-right: 10px;\"><strong>"+row[1]+":</strong><br/><span style=\"font-size: .9em\">(ID "+row[2]+
			")</span></div><div style=\"\">"+row[3]+"<br/> for ("+row[4]+")</div>";
		return rStr;
	}
	if(row[0]=='FULL')
		return row[1];
	if(row[0]=='NULL')
		return row[1];
	return row[0];
}
function buildAction(type, id){
	if(type=='user'){
		return '<div class="searchAction" onclick="location.href("Login.action?button=login&switchToUser='+id+')">S</div>';
	}
}