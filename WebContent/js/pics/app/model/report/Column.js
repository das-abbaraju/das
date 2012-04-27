Ext.define('PICS.model.report.Column', {
	extend: 'Ext.data.Model',

	// http://www.sencha.com/forum/showthread.php?180111-4.1-B2-HasOne-constructor-does-not-work
	associations: [{
        type: 'hasOne',
        model: 'PICS.model.report.AvailableField',
        associationKey: 'field',
        getterName: 'getAvailableField',
        setterName: 'setAvailableField'
    }],
    
    // TODO: figure out what these are for
	fields: [{
	    name: 'name',
	    type: 'string'
    }, {
        name: 'method',
        type: 'string'
    }, {
        name: 'option',
        type: 'string'
    }, {
        name: 'renderer'
    }],
    
    toDataSetModelField: function () {
        var available_field = this.getAvailableField();
        var name = available_field.get('name');
        var type = available_field.get('type');
        
        var model_field = {
            //convert: null,
            //dateFormat: null,
            //defaultValue: '',
            //mapping: null,
            //persist: true,
            //sortDir: 'ASC',
            //sortType: null,
            //useNull: false,
            name: name,
            type: type
        };
        
        if (type == 'date') {
            model_field.dateFormat = 'time';
        }
        
        return model_field;
    },
    
    toDataSetGridColumn: function () {
        var available_field = this.getAvailableField();
        var data_set_column = {};
        
        data_set_column.dataIndex = available_field.get('name');
        data_set_column.text = available_field.get('text');
        
        var type = available_field.get('type');
        
        switch(type) {
            case 'boolean':
                data_set_column.align = 'center';
                data_set_column.width = 50;
                data_set_column.renderer = function (value) {
                    if (value) {
                        return '<img src="images/tick.png" width="16" height="16" />';
                    }
                    
                    return '';
                };
                
                break;
            case 'date':
            case 'datetime':
                data_set_column.xtype = 'datecolumn';
                data_set_column.format = 'n/j/Y';
                
                break;
            case 'float':
                data_set_column.xtype = 'numbercolumn';
                data_set_column.align = 'right';
                data_set_column.width = 75;
                
                break;
            case 'int':
                data_set_column.xtype = 'numbercolumn';
                data_set_column.align = 'right';
                data_set_column.format = '0,000';
                data_set_column.width = 75;
                
                break;
            default:
                break;
        }
        
        var url = available_field.get('url');
        
        if (url) {
            data_set_column.xtype = 'linkcolumn';
            data_set_column.url = url;
        }
        
        var renderer = available_field.get('renderer');
        
        if (renderer) {
            data_set_column.renderer = renderer;
        }
        
        return data_set_column;
    }
});