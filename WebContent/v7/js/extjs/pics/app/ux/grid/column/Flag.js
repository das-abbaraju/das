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
	}
});