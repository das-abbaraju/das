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
        
        var field = this.column.getAvailableField(),
            name = field.get('name'),
            text = field.get('text'),
            width = field.get('width');
        
        this.dataIndex = name;
        
        this.setText(text);
        this.setWidth(width);
        
        this.callParent(arguments);
    },
    
    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        var grid = view.ownerCt,
            grid_column = grid.columns[colIndex],
            column = grid_column.column,
            field = column.getAvailableField(),
            url = field.get('url');
        
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
            field = this.column.getAvailableField(),
            text = field.get('text'),
            help = field.get('help');
        
        var tooltip = Ext.create('PICS.view.report.report.ColumnTooltip', {
            target: target
        });
        
        tooltip.update({
            text: text,
            help: help
        });
    }
});