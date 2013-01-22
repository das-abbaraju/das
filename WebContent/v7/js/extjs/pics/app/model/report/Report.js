Ext.define('PICS.model.report.Report', {
    extend: 'Ext.data.Model',
    requires: [
        'PICS.model.report.Column',
        'PICS.model.report.Filter',
        'PICS.model.report.Sort'
    ],

    fields: [{
        name: 'type',
        type: 'string',
        persist: false
    }, {
        name: 'name',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'filter_expression',
        type: 'string',
        useNull: true
    }, {
        name: 'is_editable',
        type: 'boolean',
        persist: false
    }, {
        name: 'is_favorite',
        type: 'boolean'
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

    getFilterExpression: function () {
        var filter_expression = this.get('filter_expression');
        
        return filter_expression.replace(/\{([\d]+)\}/g, function (match, p1) {
            return parseInt(p1);
        });
    },
    
    // TODO: probably fix this because nichols wrote it
    setFilterExpression: function (filter_expression) {
        // Hack: because this is broken
        if (filter_expression == '') {
            this.set('filter_expression', filter_expression);

            return false;
        }

        // TODO write a real grammar and parser for our filter formula DSL

        // Split into tokens
        var validTokenRegex = /[0-9]+|\(|\)|and|or/gi;
        filter_expression = filter_expression.replace(validTokenRegex, ' $& ');

        var tokens = filter_expression.trim().split(/ +/);
        filter_expression = '';

        // Check for invalid tokens and make sure parens are balanced
        var parenCount = 0;
        for (var i = 0; i < tokens.length; i += 1) {
            var token = tokens[i];

            if (token.search(validTokenRegex) === -1) {
                return false;
            }

            if (token === '(') {
                parenCount += 1;
                filter_expression += token;
            } else if (token === ')') {
                parenCount -= 1;
                filter_expression += token;
            } else if (token.toUpperCase() === 'AND') {
                filter_expression += ' AND ';
            } else if (token.toUpperCase() === 'OR') {
                filter_expression += ' OR ';
            } else if (token.search(/[0-9]+/) !== -1) {
                if (token === '0') {
                    return false;
                }

                // Convert from counting number to index
                var indexNum = new Number(token);
                filter_expression += '{' + indexNum + '}';
            } else {
                return false;
            }

            if (parenCount < 0) {
                return false;
            }
        }

        if (parenCount !== 0) {
            return false;
        }

        this.set('filter_expression', filter_expression);
    },

    /**
     * Get Report JSON
     *
     * Builds a jsonified version of the report to be sent to the server
     */
    toJson: function () {
        var report = {};

        function getStoreType(store) {
            var className = store.getName(), // store.self.getName()
                nameSpaces = className.split("."),
                storeType = nameSpaces[nameSpaces.length-1];

            return storeType;
        }

        function convertStoreToDataObject(store) {
            var data = [],
                mutableFields = store.mutableFields;

            store.each(function (record) {
                var item = {};
                
                record.fields.each(function (field) {

                    var fieldName = record.get(field.name),
                        storeType = getStoreType(store);
                    
                    //if (fieldname && this.mutableFields[store].indexOf(fieldName) != 1) {
                    if (fieldName && store.mutableFields.indexOf(fieldName) != -1) {
                        item[fieldName] = fieldName;
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
        
        var column_store = this.columns(),
            new_column = column.getData();
        
        column_store.add(new_column);
    },
    
    addColumns: function (columns) {
        var new_columns = [];
        
        Ext.Array.forEach(columns, function (column) {
            if (Ext.getClassName(column) != 'PICS.model.report.Column') {
                Ext.Error.raise('Invalid column');
            }
            
            new_columns.push(column.getData());
        });
        
        var column_store = this.columns();
        
        column_store.add(new_columns);
    },
    
    addFilter: function (filter) {
        if (Ext.getClassName(filter) != 'PICS.model.report.Filter') {
            Ext.Error.raise('Invalid filter');
        }
        
        var filter_store = this.filters(),
            new_filter = filter.getData();
        
        filter_store.add(new_filter);
    },
    
    addFilters: function (filters) {
        var new_filters = [];
        
        Ext.Array.forEach(filters, function (filter) {
            if (Ext.getClassName(filter) != 'PICS.model.report.Filter') {
                Ext.Error.raise('Invalid filter');
            }
            
            new_filters.push(filter.getData());
        });
        
        var filter_store = this.filters();
        
        filter_store.add(new_filters);
    },
    
    addSort: function (column, direction) {
        if (Ext.getClassName(column) != 'PICS.model.report.Column') {
            Ext.Error.raise('Invalid column');
        }
        
        var sort_store = this.sorts(),
            field_id = column.get('field_id');
        
        sort_store.add({
            field_id: field_id,
            direction: direction
        });
    },
    
    convertColumnsToModelFields: function () {
        var column_store = this.columns(),
            model_fields = [];
        
        column_store.each(function (column) {
            var model_field = column.toModelField();
            
            model_fields.push(model_field);
        });
        
        return model_fields;
    },
    
    convertColumnsToGridColumns: function () {
        var column_store = this.columns(),
            grid_columns = [];
        
        column_store.each(function (column) {
            var grid_column = column.toGridColumn();
            
            grid_columns.push(grid_column);
        });
        
        return grid_columns;
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