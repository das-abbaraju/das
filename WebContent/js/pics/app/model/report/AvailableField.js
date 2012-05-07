Ext.define('PICS.model.report.AvailableField', {
	extend: 'Ext.data.Model',

	fields: [{
	    name: 'category',
	    type: 'string'
    }, {
        name: 'dateFormat',
        type: 'string'
    }, {
        name: 'filterable',
        type: 'boolean',
        defaultValue: true
    }, {
        name: 'filterType',
        type: 'string'
    }, {
        name: 'help',
        type: 'string'
    }, {
        name: 'name',
        type: 'string'
    }, {
        name: 'renderer',
        type: 'string'
    }, {
        name: 'sortable',
        type: 'boolean',
        defaultValue: true
    }, {
        name: 'text',
        type: 'string'
    }, {
        name: 'type',
        type: 'string'
    }, {
        name: 'visible',
        type: 'boolean',
        defaultValue: true
    }, {
        name: 'width',
        type: 'int',
        defaultValue: 0
    }],

    createColumn: function () {
        var column = Ext.create('PICS.model.report.Column', {
        	'name': this.get('name')
        });

        column.set(this.data);

        return column;
    },

    createFilter: function () {
        var filter = Ext.create('PICS.model.report.Filter', {
            'name': this.get('name')
        });

        filter.set(this.data);

        //set default operator
        if (filter.get('operator') === "") {
            filter.set('operator', 'Contains');
        }

        return filter;
    },

    createSort: function () {
        var sort = Ext.create('PICS.model.report.Sort', {
            'name': this.get('name')
        });

        sort.setAvailableField(this);

        return sort;
    },

    //hack to override ajax request and prevent ExtJs Error
    proxy: {
        type: 'memory'
    }
});

