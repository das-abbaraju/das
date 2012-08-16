Ext.define('PICS.view.report.available-field.AvailableFieldList', {
    extend: 'Ext.grid.Panel',
    alias: ['widget.reportavailablefieldlist'],

    store: 'report.AvailableFieldsByCategory',

    border: 0,
    columns: [{
    	xtype: 'templatecolumn',
        dataIndex: 'text',
        text: 'Column Name',
        tpl: '{text} <span class="help">{help}</span>',
        flex: 1
    }],
    enableColumnHide: false,
    features: Ext.create('Ext.grid.feature.Grouping', {
        groupHeaderTpl: '{name} <span class="number-of-items">({rows.length} item{[values.rows.length != 1 ? "s" : ""]})</span>'
    }),
    hideHeaders: true,
    id: 'available_field_list',
    listeners: {
        render: function (cmp, eOpts) {
            this.mon(cmp.el, 'mouseover', function (event, html, eOpts) {
                var class_names = this.getGroupClassNamesWithoutOver(html);

                class_names.push('x-over');
                html.className = class_names.join(' ');
            }, cmp, {
                delegate: '.x-grid-group-hd'
            });

            this.mon(cmp.el, 'mouseout', function (event, html, eOpts) {
                var class_names = this.getGroupClassNamesWithoutOver(html);

                html.className = class_names.join(' ');
            }, cmp, {
                delegate: '.x-grid-group-hd'
            });
        }
    },
    rowLines: false,
    selModel: Ext.create('Ext.selection.CheckboxModel'),

    getGroupClassNamesWithoutOver: function (html) {
        var class_names = html.className.split(' '),
            class_names_length = class_names.length,
            new_class_names = [];

        while (class_names_length--) {
            var class_name = class_names[class_names_length];

            if (class_name != 'x-over') {
                new_class_names.push(class_name);
            }
        }

        return new_class_names;
    }
});