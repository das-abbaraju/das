Ext.define('PICS.ux.grid.column.Column', {
    extend: 'Ext.grid.column.Column',
    
    requires: [
        'PICS.view.report.report.ColumnTooltip'
    ],
    
    menuDisabled: true,
    sortable: false,
    
    constructor: function (args) {
        this.column = args.column;
        
        if (Ext.getClassName(this.column) != 'PICS.model.report.Column') {
            Ext.Error.raise('Invalid column record');
        }
        
        var column = this.column,
            id = column.get('id'),
            name = column.get('name'),
            width = column.get('width');
        
        this.dataIndex = id;
        
        this.setText(name);
        this.setWidth(width);
        
        this.callParent(arguments);
    },
    
    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        var grid = view.ownerCt,
            grid_column = grid.columns[colIndex],
            column = grid_column.column,
            url = column.get('url');
        
        if (url) {
            var href = grid_column.getHref(url, record);
            
            return '<a href="' + href + '" target="_blank">' + value + '</a>';
        }
        
        return value;
    },
    
    getHref: function (url, record) {
        return url.replace(/\{(.*?)\}/g, function (match, p1) {
            // raw attribute is to get data from the record that is not observed in the model
            return record.raw[p1];
        });
    },
    
    createTooltip: function () {
        if (Ext.getClassName(this.column) != 'PICS.model.report.Column') {
            Ext.Error.raise('Invalid column record');
        }
        
        var target = this.el,
            column = this.column,
            name = column.get('name'),
            description = column.get('description');
        
        var tooltip = Ext.create('PICS.view.report.report.ColumnTooltip', {
            target: target
        });
        
        tooltip.update({
            name: name,
            description: description
        });
    }
});