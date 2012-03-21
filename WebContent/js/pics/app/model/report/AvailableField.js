Ext.define('PICS.model.report.AvailableField', {
	extend: 'Ext.data.Model',

	fields: [
        { name: 'category', type: 'string' },
        { name: 'dateFormat', type: 'string' },
        { name: 'filterable', type: 'boolean', defaultValue: true },
        { name: 'filterType', type: 'string' },
        { name: 'help', type: 'string' },
        { name: 'name', type: 'string' },
        { name: 'renderer', type: 'string' },
        { name: 'sortable', type: 'boolean', defaultValue: true },
        { name: 'text', type: 'string' },
        { name: 'type', type: 'string' },
        { name: 'visible', type: 'boolean', defaultValue: true },
        { name: 'width', type: 'int', defaultValue: 0 }
    ],
    
    createSimpleColumn: function () {
        // TODO: what the heck is type it does not exist??????
        var column = Ext.create('PICS.model.report.SimpleColumn', {
        	'name': this.get('name'),
        	'type': this.get('type')
        });
        
        column.setAvailableField(this);
        
        return column;
    },
    
    createSimpleFilter: function () {
        // TODO: why the heck was this called name it did not work
        // it is column????
        // what the heck is type it does not exist
        // remove default operator hack
        var filter = Ext.create('PICS.model.report.SimpleFilter', {
        	'column': this.get('name'),
        	'type': this.get('type'),
        	'operator': 'Contains'
        });
        
        filter.setAvailableField(this);
        
        return filter;
    },
    
    createSimpleSort: function () {
        var sort = Ext.create('PICS.model.report.SimpleSort', {
            'column': this.get('name')
        });
        
        sort.setAvailableField(this);
        
        return sort;
    },
    // TODO: not sure if this is used anymore
	// See http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.Field
    toStoreField: function () {
    	// TODO I've only tested a couple of the config options here
    	var field = {};
    	
    	field.name = this.get('name');
    	
    	if (this.get('type')) {
    		field.type = this.get('type');
    		
    		if (field.type == 'date') {
    			field.dateFormat = 'time';
    		}
    	}
    	
    	return field;
    },
    
	// See http://docs.sencha.com/ext-js/4-0/#!/api/Ext.grid.column.Column
    toGridColumn: function () {
    	// TODO I've only tested a couple of the config options here
    	var gridColumn = {};
    	
    	gridColumn.dataIndex = this.get('name');
    	gridColumn.text = this.get('text');
    	
    	if (this.get('type')) {
    		var type = this.get('type');
    		
    		if (type == 'boolean') {
    			gridColumn.align = 'center';
    			gridColumn.width = 50;
    			gridColumn.renderer = function (value) {
    	            if (value) {
    	                return '<img src="images/tick.png" width="16" height="16" />';
    	            }
    	            
    	            return '';
    	        };
    		} else if (type == 'int') {
    			gridColumn.width = 75;
    			gridColumn.align = 'right';
    			gridColumn.xtype = 'numbercolumn';
    			gridColumn.format = '0,000';
    		} else if (type == 'float') {
    			gridColumn.width = 75;
    			gridColumn.align = 'right';
    			gridColumn.xtype = 'numbercolumn';
    		} else if (type == 'date' || type == 'datetime') {
    			gridColumn.xtype = 'datecolumn';
    			gridColumn.format = 'n/j/Y';
    			// gridColumn.align = 'center';
    		}
    	}
    	
    	if (this.get('url')) {
        	gridColumn.xtype = 'linkcolumn';
        	gridColumn.url = this.get('url');
    	}
    	
    	if (this.get('width') > 0) {
    		gridColumn.width = this.get('width');
    	}

    	if (this.get('renderer')) {
    		gridColumn.renderer = this.get('renderer');
    	}
    	
    	return gridColumn;
    }
});