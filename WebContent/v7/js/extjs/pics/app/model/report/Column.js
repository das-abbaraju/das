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

    toModelField: function () {
        var field = this.getAvailableField();

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

        var grid_column = {
            dataIndex: field.get('name'),
            menuDisabled: true,
            sortable: false,
            text: field.get('text'),
            width: field.get('width') || 150
        };

        switch (field.get('type')) {
            case 'boolean':
                grid_column.align = 'center';
                grid_column.renderer = function (value) {
                    if (value) {
                        return '<img src="images/tick.png" width="16" height="16" />';
                    }

                    return '';
                };
                grid_column.width = 50;

                break;
            case 'date':
            case 'datetime':
                grid_column.xtype = 'datecolumn';
                grid_column.format = 'n/j/Y';

                break;
            case 'float':
                grid_column.xtype = 'numbercolumn';
                grid_column.align = 'right';
                grid_column.width = 75;

                break;
            case 'int':
                grid_column.xtype = 'numbercolumn';
                grid_column.align = 'right';
                grid_column.format = '0000';
                grid_column.width = 75;

                break;
            default:
                break;
        }

        if (field.get('url')) {
            grid_column.xtype = 'linkcolumn';
            grid_column.url = field.get('url');
        }

        if (field.get('renderer')) {
            grid_column.renderer = field.get('renderer');
        }

        return grid_column;
    }
});