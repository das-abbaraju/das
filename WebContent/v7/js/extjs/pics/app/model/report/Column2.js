Ext.define('PICS.model.report.Column2', {
    extend: 'Ext.data.Model',
    
    requires: [
        'PICS.ux.grid.column.Column',
        'PICS.ux.grid.column.Boolean',
        'PICS.ux.grid.column.Flag',
        'PICS.ux.grid.column.Number',
        'PICS.ux.grid.column.String'
    ],

    fields: [{
        name: 'id',
        type: 'string'
    }, {
        name: 'type',
        type: 'string'
    }, {
        name: 'category',
        type: 'string'
    }, {
        name: 'name',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'url',
        type: 'string'
    }, {
        name: 'sql_function',
        type: 'string'
    }, {
        name: 'width',
        type: 'int'
    }, {
        name: 'is_sortable',
        type: 'boolean'
    }],

    mutableFields: ['id','sql_function','width'],
    
    // ALERT: Ext.data.Field.type (auto, string, int, float, boolean, date)
    // ALERT: Ext.data.Field.type (auto, string, int, float, boolean, date)
    // ALERT: Ext.data.Field.type (auto, string, int, float, boolean, date)
    toModelField: function () {
        var id = this.get('id'),
            type = this.get('type'),
            data_type;
        
        switch (type) {
            case 'boolean':
                data_type = 'boolean';
    
                break;
            default:
                data_type = 'auto';
                
                break;
        }
        
        var model_field = {
            name: id,
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

        switch (type) {
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