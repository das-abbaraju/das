(function($) {
	$.fn.extend( {
		picsReport : function() {
			return this.each(function() {
			    var obj = $(this);
			    
			    obj.tabSlideOut({
			        tabHandle: '.handle',
			        //onLoadSlideOut: true,
			        pathToTabImage: 'images/button_continue.gif',
			        imageHeight: '23px',
			        imageWidth: '90px',
			        tabLocation: 'left',
			        speed: 300,
			        action: 'click',
			        //fixedPosition: true,
			        topPos: '115px'
			    });

			    $("#filterAccordion").accordion({autoHeight: false});
			    
		    	var search = obj.find("form");
		    	var showPage = 1;
		    	var startsWith = "";
		    	var ajax = true;
				var destinationAction = "ReportBilling";
			    

				
			    var searchButton = $("#searchfilter");
			    searchButton.click(function() {
			    	showPage = 1;
			    	startsWith = "";
			    	runSearch();
			    	if (search['filter.allowMailMerge'].value == "true")
			    		$('write_email_button').show();
			    	return false;
			    });
			    
				obj.find("input").focus(function() {
					var value2 = this.value.trim();
					if (value2.charAt(0) == '-' && value2.charAt(value2.length - 1) == '-') {
						this.value = "";
					} else {
						this.select();
					}
				});
				
				$("div.filterOption > .editor").hide();
				
				obj.find("div.filterOption a.toggle").click(function() {
					var editor = $(this).siblings(".editor");
					var summary = $(this).siblings(".summary");
					
					if (editor.css("display") == "none") {
						summary.hide();
						editor.show();
					} else {
						editor.hide();
						updateQuery($(this).parent(".filterOption"));
						summary.show();
					}
					return false;
				});
				
				obj.find("div.filterOption a.clearLink").click(function() {
					$(this).siblings("select").each(function() {
						for(i=0; i < this.length; i++)
							this.options[i].selected = false
					});
					
					return false;
				});
				
				function updateQuery(filterOption) {
					var box = $(this).siblings(".editor select");
					var summary = $(this).siblings(".summary");
					
					var queryText = '';
					var values = box.val();
					for(i=0; i < box.length; i++) {
						if (box.options[i].selected) {
							if (queryText != '') queryText = queryText + ", ";
							queryText = queryText + box.options[i].text;
						}
					}
					
					if (queryText == '') {
						queryText = 'ALL';
					}
					summary.val(queryText);
				}
				
			    // private function for debugging
			    function runSearch() {
			    	if (!ajax) {
			    		search.submit();
			    	} else {
						// if this is an ajax call, then get the form elements and then post them through ajax and return the results to a div
						//$('report_data').innerHTML = "<img src='images/ajax_process2.gif' width='48' height='48' /> finding search results";
						//Effect.Opacity('report_data', { from: 1.0, to: 0.7, duration: 0.5 });
						
						var pars = search.serialize();
						var reportData = $("#report_data");
						reportData.fadeTo("normal", 0.5);
						reportData.load(destinationAction+'Ajax.action', pars, function(){
							reportData.fadeTo("fast", 1);
							obj.find(".handle").click();
							$("a.contractorQuick").cluetip();
						});
			    	}

		    	};
			});
		}
	});
})(jQuery);
