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

    setAvailableFieldHandle: function () {
        var handle = '';

        if (this.getAvailableField().get('name').length > 0) {
            handle = this.getAvailableField();
        } else {
            handle = this;
        }

        return handle;
    },

    toDataSetModelField: function () {
        var available_field = this.setAvailableFieldHandle(),
            name = available_field.get('name'),
            type = available_field.get('type');

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
        var available_field = this.setAvailableFieldHandle(),
            data_set_column = {},
            name = available_field.get('name')
            renderer = available_field.get('renderer'),
            text = available_field.get('text'),
            type = available_field.get('type'),
            url = available_field.get('url'),
            width = available_field.get('width')

        //prevents header click sort
        data_set_column.sortable = false;

        data_set_column.dataIndex = name;
        data_set_column.text = text;

        data_set_column.width = width;

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
                data_set_column.format = '0000';
                data_set_column.width = 75;

                break;
            default:
                break;
        }

        if (url) {
            data_set_column.xtype = 'linkcolumn';
            data_set_column.url = url;
        }

        if (renderer) {
            data_set_column.renderer = renderer;
        }

        return data_set_column;
    }
});