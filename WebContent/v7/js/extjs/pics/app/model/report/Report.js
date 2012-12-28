Ext.define('PICS.model.report.Report', {
    extend: 'Ext.data.Model',
    requires: [
        'PICS.model.report.Column',
        'PICS.model.report.Filter',
        'PICS.model.report.Sort'
    ],

    fields: [{
        // report id
        name: 'id',
        type: 'int'
    }, {
        // report base (aka mysql view)
        name: 'modelType',
        type: 'string'
    }, {
        // report name
        name: 'name',
        type: 'string'
    }, {
        // report description
        name: 'description',
        type: 'string'
    }, {
        // query expression used to generate report data aka (1 AND 2) OR 3
        name: 'filterExpression',
        type: 'string'
    }, {
        // report limit
        name: 'rowsPerPage',
        type: 'int',
        defaultValue: 50
    }],
    hasMany: [{
        model: 'PICS.model.report.Column',
        name: 'columns'
    }, {
        model: 'PICS.model.report.Filter',
        name: 'filters'
    }, {
        model: 'PICS.model.report.Sort',
        name: 'sorts'
    }],

    /**
     * Get Report JSON
     *
     * Builds a jsonified version of the report to be sent to the server
     */
    toJson: function () {
        var report = {};

        function convertStoreToDataObject(store) {
            var data = [];

            store.each(function (record) {
                var item = {};
                
                record.fields.each(function (field) {
                    // block to prevent extraneous id from being inject into request parameters
                    // ???
                    if (record.get(field.name)) {
                        item[field.name] = record.get(field.name);
                    }
                });
                
                data.push(item);
            });

            return data;
        }

        report = this.data;
        report.columns = convertStoreToDataObject(this.columns());
        report.filters = convertStoreToDataObject(this.filters());
        report.sorts = convertStoreToDataObject(this.sorts());

        return Ext.encode(report);
    },

    toRequestParams: function () {
        var report = {};

        report.report = this.get('id');
        report['report.description'] = this.get('description');
        report['report.name'] = this.get('name');
        report['report.parameters'] = this.toJson();
        report['report.rowsPerPage'] = this.get('rowsPerPage');

        return report;
    },
    
    
    
    
    addColumn: function (column) {
        if (Ext.getClassName(column) != 'PICS.model.report.Column') {
            Ext.Error.raise('Invalid column');
        }
        
        var column_store = this.columns();
        
        column_store.add(column);
    },
    
    addColumns: function (columns) {
        Ext.Array.forEach(columns, function (column) {
            if (Ext.getClassName(column) != 'PICS.model.report.Column') {
                Ext.Error.raise('Invalid column');
            }
        });
        
        var column_store = this.columns();
        
        column_store.add(columns);
    },
    
    addFilter: function (filter) {
        if (Ext.getClassName(filter) != 'PICS.model.report.Filter') {
            Ext.Error.raise('Invalid filter');
        }
        
        var filter_store = this.filters();
        
        filter_store.add(filter);
    },
    
    addFilters: function (filters) {
        Ext.Array.forEach(filters, function (filter) {
            if (Ext.getClassName(filter) != 'PICS.model.report.Filter') {
                Ext.Error.raise('Invalid filter');
            }
        });
        
        var filter_store = this.filters();
        
        filter_store.add(filters);
    },
    
    addSort: function (column, direction) {
        var sort_store = this.sorts(),
            column_name = column.getAvailableField().get('name');
        
        sort_store.add({
            name: column_name,
            direction: direction
        });
    },
    
    removeColumns: function () {
        this.columns().removeAll();
    },
    
    removeSorts: function () {
        this.sorts().removeAll();
    },
    
    // reorder columns
    moveColumnByIndex: function (from_index, to_index) {
        var column_store = this.columns(),
            columns = [];

        // generate an array of columns from column store
        column_store.each(function (column, index) {
            columns[index] = column;
        });
    
        // splice out the column store - column your moving
        var spliced_column = columns.splice(from_index, 1);
    
        // insert the column store - column to the position you moved it to
        columns.splice(to_index, 0, spliced_column);
    
        // remove all column store records
        column_store.removeAll();
        
        // re-insert column store records in the new position
        Ext.each(columns, function (column, index) {
            column_store.add(column);
        });
    }
});