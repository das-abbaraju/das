Ext.define('PICS.view.report.ReportOptionsSorts', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptionssorts'],

    items: [{
        xtype: 'gridpanel',
        store: 'report.ReportsSort',

        bbar: [{
            xtype: 'button',
            itemId: 'apply',
            action: 'apply',
            // disabled: true,
            text: 'Apply',
            cls: 'x-btn-default-small',
        }],

        // custom type to determine panel actions
        _column_type: 'sort',
        
        columns: [{
            xtype: 'rownumberer'
        }, {
            xtype: 'gridcolumn',

            dataIndex: 'column',
            flex: 1,
            hideable: false,
            sortable: false,
            renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                return record.data.field.get('text');
            },
            text: 'Column'            
        }, {
            xtype: 'gridcolumn',
            
            dataIndex: 'ascending',
            flex: 1,
            hideable: false,
            sortable: false,
            renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                if (record.data.direction === 'ASC') {
                    return 'Ascending'
                } else {
                    return 'Descending'
                }
            },
            text: 'Direction',            
        }, {
            xtype: 'actioncolumn',
            
            hideable: false,
            items: [{
                icon: 'images/cross.png',
                iconCls: 'ext-icon grid remove-sort',
                tooltip: 'Remove'
            }],
            sortable: false,
            width: 25
        }],
        enableColumnResize: false,
        tbar: [{
            action: 'add-sort',
            icon: 'js/pics/resources/images/dd/drop-add.gif',
            text: 'Add Sort'
        }],
        viewConfig: {
            plugins: {
                dragText: 'Drag and drop to reorganize',
                ptype: 'gridviewdragdrop'
            }
        }        
    }],
    layout: {
        align: 'stretch',
        type: 'vbox'
    },
    title: 'Sort'
});