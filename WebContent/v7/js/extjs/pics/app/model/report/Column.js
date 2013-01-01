Ext.define('PICS.model.report.Column', {
    extend: 'Ext.data.Model',
    
    requires: [
        'PICS.ux.grid.column.Boolean',
        'PICS.ux.grid.column.Date',
        'PICS.ux.grid.column.Flag',
        'PICS.ux.grid.column.Float',
        'PICS.ux.grid.column.Int',
        'PICS.ux.grid.column.Number',
        'PICS.ux.grid.column.Column'
    ],

    // http://www.sencha.com/forum/showthread.php?180111-4.1-B2-HasOne-constructor-does-not-work
    associations: [{
        type: 'hasOne',
        model: 'PICS.model.report.AvailableField',
        associationKey: 'field',
        getterName: 'getAvailableField',
        setterName: 'setAvailableField'
    }],

    fields: [{
        // column name
        name: 'name',
        type: 'string'
    }, {
        // column aggragate function aka Count, Min, Max, Year, etc.
        name: 'method',
        type: 'string'
    }],

    // ALERT: Ext.data.Field.type (auto, string, int, float, boolean, date)
    // ALERT: Ext.data.Field.type (auto, string, int, float, boolean, date)
    // ALERT: Ext.data.Field.type (auto, string, int, float, boolean, date)
    toModelField: function () {
        var field = this.getAvailableField();

        if (!field) {
            Ext.Error.raise('Invalid available field');
        }

        var model_field = {
            name: field.get('name'),
            type: field.get('type')
        };

        if (field.get('type') == 'date') {
            model_field.dateFormat = 'time';
        }

        return model_field;
    },

    // ALERT: Ext.grid.column.Column is DEFAULT
    // ALERT: Ext.grid.column.* (Action, Boolean, Column, Date, Number, Template)
    // ALERT: Ext.grid.column.* (Action, Boolean, Column, Date, Number, Template)
    // ALERT: Ext.grid.column.* (Action, Boolean, Column, Date, Number, Template)
    toGridColumn: function () {
        var field = this.getAvailableField(),
            url = field.get('url'),
            grid_column;

        if (!field) {
            Ext.Error.raise('Invalid available field');
        }
        
        var type = field.get('type');
        
        var config = {
            menuDisabled: true,
            record: this,
            sortable: false
        };

        switch (type) {
            // <i class="icon-ok"></i>
            case 'boolean':
                grid_column = Ext.create('PICS.ux.grid.column.Boolean', config);

                break;
            // Y-m-d
            case 'date':
                grid_column = Ext.create('PICS.ux.grid.column.Date', config);

                break;
            // <i class="icon-flag"></i>
            case 'flagcolor':
            	grid_column = Ext.create('PICS.ux.grid.column.Flag', config);

                break;
            // 1,234.00
            case 'float':
                grid_column = Ext.create('PICS.ux.grid.column.Float', config);

                break;
            // 1234
            case 'integer':
                grid_column = Ext.create('PICS.ux.grid.column.Int', config);

                break;
            // 1,234
            case 'number':
                grid_column = Ext.create('PICS.ux.grid.column.Number', config);

                break;
            // text
            case 'string':
            default:
                grid_column = Ext.create('PICS.ux.grid.column.Column', config);
                
                break;
        }
        
        return grid_column;
    }
});