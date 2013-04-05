Ext.define('PICS.model.report.Report', {
    extend: 'Ext.data.Model',
    requires: [
        'PICS.model.report.Column',
        'PICS.model.report.Filter',
        'PICS.model.report.Sort'
    ],

    fields: [{
        name: 'type',
        type: 'string'
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
    
    isNewFilterExpression: function (filter_expression) {
        var current_expression = this.get('filter_expression'),
            sanitized_expression = this.sanitizeFilterExpression(filter_expression);

        if (sanitized_expression == current_expression) {
            return false;
        } else {
            return true;
        }
    },

    // TODO: probably fix this because nichols wrote it
    setFilterExpression: function (filter_expression) {
        var sanitized_expression = this.sanitizeFilterExpression(filter_expression);

        this.set('filter_expression', sanitized_expression);
    },

    // TODO write a real grammar and parser for our filter formula DSL
    sanitizeFilterExpression: function (filter_expression) {
        var validTokenRegex = /[0-9]+|\(|\)|and|or/gi,
            tokens = [],
            token = '',
            paren_count = 0,
            token_count = 0,
            index_num = 0,
            sanitized_expression = '',
            max_token_value = this.filters().count(),
            token_value;
        
        if (typeof filter_expression != 'string') {
            return '';
        }

        // Split into tokens
        filter_expression = filter_expression.replace(validTokenRegex, ' $& ');
        tokens = filter_expression.trim().split(/ +/);

        // Check for invalid tokens and make sure parens are balanced
        for (token_count = 0; token_count < tokens.length; token_count += 1) {
            token = tokens[token_count];

            if (token.search(validTokenRegex) === -1) {
                return '';
            }

            if (token === '(') {
                paren_count += 1;
                sanitized_expression += token;
            } else if (token === ')') {
                paren_count -= 1;
                sanitized_expression += token;
            } else if (token.toUpperCase() === 'AND') {
                sanitized_expression += ' AND ';
            } else if (token.toUpperCase() === 'OR') {
                sanitized_expression += ' OR ';
            } else if (token.search(/[0-9]+/) !== -1) {
                token_value = parseInt(token);

                if (token_value == 0 || token_value > max_token_value) {
                    return '';
                }

                sanitized_expression += '{' + token_value + '}';
            } else {
                return '';
            }

            if (paren_count < 0) {
                return '';
            }
        }

        if (paren_count !== 0) {
            return '';
        }

        return sanitized_expression;
    },

    addColumn: function (column) {
        if (Ext.getClassName(column) != 'PICS.model.report.Column') {
            Ext.Error.raise('Invalid column');
        }
        
        var column_store = this.columns(),
            new_column = column.getData();
        
        column_store.add(new_column);
        
        this.resortColumns();
    },
    
    addColumns: function (columns) {
        var new_columns = [];
        
        Ext.each(columns, function (column) {
            if (Ext.getClassName(column) != 'PICS.model.report.Column') {
                Ext.Error.raise('Invalid column');
            }
            
            new_columns.push(column.getData());
        });
        
        var column_store = this.columns();
        
        column_store.add(new_columns);
        
        this.resortColumns();
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
    
    // reorder columns
    moveColumnByIndex: function (from_index, to_index) {
        var column_store = this.columns(),
            columns = [];

        // generate an array of columns from column store
        column_store.each(function (column, index) {
            columns[index] = column;
        });
    
        // splice out the column store - column your moving
        var spliced_column = columns.splice(from_index, 1)[0];
    
        // insert the column store - column to the position you moved it to
        columns.splice(to_index, 0, spliced_column);
    
        // remove all column store records
        this.removeColumns();
        
        this.addColumns(columns);
        
        this.resortColumns();
    },
    
    removeColumns: function () {
        this.columns().removeAll();
    },
    
    removeSorts: function () {
        this.sorts().removeAll();
    },
    
    resortColumns: function () {
        var column_store = this.columns();
        
        column_store.each(function (column, index) {
            index += 1;
            
            column.set('sort', index);
        });
    }
});