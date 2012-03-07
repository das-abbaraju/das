Ext.define('PICS.store.report.AvailableFields', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.AvailableField',
	
	autoLoad: false,
	data: availableFields,
	proxy: {
	    reader: {
            root: 'fields',
            type: 'json'
        },
        type: 'ajax',
        url: 'ReportDynamic!availableFields.action?report=7'
    },
    findField: function (name) {
    	var i, ln = this.data.length;
    	for(var i = 0; i < ln; i++) {
    		if (name == this.data.items[i].get("name")) {
    			return this.data.items[i];
    		}
    	}
    	console.log("Failed to find '" + name + "' from " + ln + " availableField(s)");
    	return null;
    }
});