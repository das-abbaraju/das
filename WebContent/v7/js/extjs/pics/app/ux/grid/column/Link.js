Ext.define('PICS.ux.grid.column.Link', {
    extend: 'Ext.grid.column.Column',

    constructor: function () {
		this.callParent(arguments);

        if (!this.record) {
            Ext.Error.raise('Invalid column record');
        }
        
        var field = this.record.getAvailableField();

        if (!field) {
            Ext.Error.raise('Invalid available field');
        }
        
        var name = field.get('name'),
	    	text = field.get('text'),
	    	url = field.get('url'),
	    	width = field.get('width');
        
        this.dataIndex = name;
        this.text = text;
        this.width = width;
        
        this.configureRenderer(url);
	},
	
	// example: ?id={accountID}&report.name={reportName}
	configureRenderer: function (url) {
		var params = url.match(/{(\w+)}/);
		
    	this.renderer = function(value, metaData, record) {
            Ext.Array.forEach(params, function(field_name) {
            	var field = record.get(field_name);
            	
            	if (field) {
            		url = url.replace("{" + field_name + "}", field);
            	}
            });
            
            return "<a href='" + url + "'>" + value + "</a>";
        };
    }
});