Ext.define('PICS.model.report.Column', {
    extend: 'Ext.data.Model',
    
    requires: [
        'PICS.ux.grid.column.Column',
        'PICS.ux.grid.column.Boolean',
        'PICS.ux.grid.column.Flag',
        'PICS.ux.grid.column.Number',
        'PICS.ux.grid.column.String'
    ],

    fields: [{
        name: 'field_id',
        type: 'string'
    }, {
        name: 'type',
        type: 'string',
        persist: false
    }, {
        name: 'category',
        type: 'string',
        persist: false
    }, {
        name: 'name',
        type: 'string',
        persist: false
    }, {
        name: 'description',
        type: 'string',
        persist: false
    }, {
        name: 'url',
        type: 'string',
        persist: false,
        useNull: true
    }, {
        name: 'sql_function',
        type: 'string',
        useNull: true
    }, {
        name: 'width',
        type: 'int'
    }, {
        name: 'is_sortable',
        type: 'boolean',
        persist: false
    }],

    // ALERT: Ext.data.Field.type (auto, string, int, float, boolean, date)
    // ALERT: Ext.data.Field.type (auto, string, int, float, boolean, date)
    // ALERT: Ext.data.Field.type (auto, string, int, float, boolean, date)
    toModelField: function () {
        var field_id = this.get('field_id'),
            type = this.get('type'),
            data_type;
        
        switch (type.toLowerCase()) {
            case 'boolean':
                data_type = 'boolean';
    
                break;
            default:
                data_type = 'auto';
                
                break;
        }
        
        var model_field = {
            name: field_id,
            type: data_type
        };

        return model_field;
    },

    // ALERT: Ext.grid.column.Column is DEFAULT
    // ALERT: Ext.grid.column.* (Action, Boolean, Column, Date, Number, Template)
    // ALERT: Ext.grid.column.* (Action, Boolean, Column, Date, Number, Template)
    // ALERT: Ext.grid.column.* (Action, Boolean, Column, Date, Number, Template)
    toGridColumn: function () {
        var type = this.get('type'),
            url = this.get('url'),
            grid_column;
        
        var config = {
            column: this
        };

        switch (type.toLowerCase()) {
            case 'boolean':
                grid_column = Ext.create('PICS.ux.grid.column.Boolean', config);

                break;
            case 'flag':
                grid_column = Ext.create('PICS.ux.grid.column.Flag', config);

                break;
            case 'number':
                grid_column = Ext.create('PICS.ux.grid.column.Number', config);

                break;
            case 'date':
            case 'string':
            default:
                grid_column = Ext.create('PICS.ux.grid.column.String', config);
                
                break;
        }
        
        return grid_column;
    }
});