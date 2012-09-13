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

    fields: [{
        // column name
        name: 'name',
        type: 'string'
    }, {
        // column aggragate function aka Count, Min, Max, Year, etc.
        name: 'method',
        type: 'string'
    }],

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

    toGridColumn: function () {
        var field = this.getAvailableField();

        if (!field) {
            Ext.Error.raise('Invalid available field');
        }
        
        var type = field.get('type');
        
        var config = {
    		record: this
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
            case 'flag':
            	grid_column = Ext.create('PICS.ux.grid.column.Flag', config);

                break;
            // 1,234.00
            case 'float':
            	grid_column = Ext.create('PICS.ux.grid.column.Float', config);

                break;
            // 1234
            case 'int':
            	grid_column = Ext.create('PICS.ux.grid.column.Int', config);

                break;
             // link
            case 'link':
            	grid_column = Ext.create('PICS.ux.grid.column.Link', config);
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