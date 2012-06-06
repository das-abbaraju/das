Ext.define('PICS.view.report.LinkColumn', {
    extend: 'Ext.grid.column.Column',
    alias: ['widget.linkcolumn'],

    /**
     * Example: Page.action?id={accountID}&report.name={reportName}
     */
    url: '',

    constructor: function(cfg) {
        this.callParent(arguments);
        var defaultURL = this.url,
        	params = this.urlParams;
        
        params = defaultURL.match(/{(\w+)}/);
        
        this.renderer = function(value, metaData, record) {
        	var url = defaultURL;
        	
            Ext.Array.forEach(params, function(fieldName) {
            	var field = record.raw[fieldName];
            	
            	if (field) {
            		url = url.replace("{" + fieldName + "}", field);
            	}
            });
            
            return "<a href='" + url + "'>" + value + "</a>";
        };
    }
});