Ext.define('PICS.ux.grid.column.Flag', {
	extend: 'Ext.grid.column.Action',
	
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
	    	width = field.get('width');
        
        this.dataIndex = name;
        this.text = text;
        this.width = width;
	},
	
	renderer: function (value) {
		var icon; 
			
		switch (value) {
			case 'green':
				icon = '<i class="icon-flag" class="green"></i>';
				
				break;
			case 'red':
				icon = '<i class="icon-flag" class="red"></i>';
				
				break;
			case 'yellow':
				icon = '<i class="icon-flag" class="yellow"></i>';
				
				break;
			default:
				break;
		}
		
		return icon;
	},
	
	renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        var grid = view.ownerCt,
            column = grid.columns[colIndex],
            col_record = column.record,
            field = col_record.getAvailableField(),
            url = field.get('url'),
            icon;
        
        switch (value) {
            case 'green':
                icon = '<i class="icon-flag" class="green"></i>';
                
                break;
            case 'red':
                icon = '<i class="icon-flag" class="red"></i>';
                
                break;
            case 'yellow':
                icon = '<i class="icon-flag" class="yellow"></i>';
                
                break;
            default:
                break;
        }
        
        if (url) {
            var href = column.getHref(url, record);
            
            return '<a href="' + href + '" target="_blank">' + icon + '</a>';
        }
        
        return icon;
    },
    
    getHref: function (url, record) {
        return url.replace(/\{(.*?)\}/g, function (match, p1) {
            return record.raw[p1];
        });
    }
});