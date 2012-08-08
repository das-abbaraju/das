Ext.define('PICS.view.report.filter.FilterFormula', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportfilterformula'],

    border: 0,
    id: 'report_filter_formula',
    dock: 'top',
    height: 50,
    items: [{
        xtype: 'form',
        border: 0,
        items: [{
            xtype: 'textfield',
            name: 'filter_formula',
            width: 200
        }, {
            xtype: 'button',
            action: 'info',
            cls: 'info',
            height: 16,
            margin: '0 0 0 10',
            text: '<i class="icon-info-sign"></i>',
            tooltip: [
                '<div class="filter-formula-tooltip">',
                    '<h4>Use "()" "AND" "OR"</h4>',
                    '<span>Example:</span>',
                    '<p>(1 OR (2 AND 3)) AND 4</p>',
                '</div>'
            ].join(''),
            width: 16
        }, {
            xtype: 'button',
            action: 'cancel',
            cls: 'cancel',
            height: 16,
            margin: '0 0 0 10',
            text: '<i class="icon-remove-sign"></i>',
            width: 16
        }],
        layout: {
            type: 'hbox',
            align: 'middle'
        },
        width: 251
    }],
    layout: {
        type: 'hbox',
        align: 'middle',
        pack: 'center'
    }
});