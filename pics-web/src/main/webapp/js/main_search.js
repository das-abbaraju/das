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
	if(row[0] == 'account') {
	    return getSearchResultHtml(row, '5px');
	}
	
    if (row[0] == 'user') {
        return getSearchResultHtml(row, '23px');
    }

    if (row[0] == 'employee') {
        return getSearchResultHtml(row, '10px');
    }

    if (row[0] == 'audit') {
        return getSearchResultHtml(row, '10px', 'for');
    }

    if (row[0] == 'FULL') {
        return row[1];
    }

    if (row[0] == 'NULL') {
        return row[1];
    }

    return row[0];
}

function buildAction(type, id){
	if(type=='user'){
		return '<div class="searchAction" onclick="location.href("Login.action?button=login&switchToUser='+id+')">S</div>';
	}
}

function getSearchResultHtml(row, marginRight, location, status) {
    if (!location) {
        location = 'at';
    }
    
    if (!status && row[5]) {
        status = row[5];
    }
    
    var breakElement = $(document.createElement('br'));
    
    var wrapper = $(document.createElement('div')),
        container = $(document.createElement('div')).css('float', 'left'),
        strong = $(document.createElement('strong')),
        info = $(document.createElement('span')).css('font-size', '0.9em'),
        name = $(document.createElement('span')).append($(document.createTextNode(row[3])));
    
    if (status) {
        name.addClass('search-result-status');
        name.addClass(row[0] + '-' + status.toLowerCase());
    }
    
    container.css('margin-right', marginRight);
    strong.text(row[1]);
    info.text('(ID ' + row[2] + ')');
    
    container.append(strong)
        .append(breakElement.clone())
        .append(info);
    
    wrapper.append(container)
        .append(name);
    
    if (row[4] != null) {
        wrapper.append(breakElement.clone())
            .append(document.createTextNode(location + ' (' + row[4] + ')'));
    }

    return wrapper.html();
}