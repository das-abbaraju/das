Ext.define('PICS.model.report.AvailableField', {
	extend: 'Ext.data.Model',

	fields: [{
	    // field category (categorizes fields in available field modal - column, filter picker)
	    name: 'category',
	    type: 'string'
    }, {
        // filter type used to display filter configuration aka drop down, autocomplete, string search, etc.
        name: 'filterType',
        type: 'string'
    }, {
        // field help
        name: 'help',
        type: 'string'
    }, {
        // field name
        name: 'name',
        type: 'string'
    }, {
        // field translation
        name: 'text',
        type: 'string'
    }, {
        // type used to generate grid model / column
        name: 'type',
        type: 'string'
    }, {
        // url used to generate a url as the value
        name: 'url',
        type: 'string'
    }, {
        name: 'width',
        type: 'int',
        defaultValue: 0
    }],

    // Must have a specified proxy when interacting with the Available Store
    // hack to override ajax request and prevent ExtJs Error
    proxy: {
        type: 'memory'
    },

    toColumn: function () {
        var column = Ext.create('PICS.model.report.Column', {
        	'name': this.get('name')
        });

        column.getAvailableField().set(this.data);

        return column;
    },

    toFilter: function () {
        var filter = Ext.create('PICS.model.report.Filter', {
            name: this.get('name')
        });

        filter.getAvailableField().set(this.data);

        return filter;
    }
});