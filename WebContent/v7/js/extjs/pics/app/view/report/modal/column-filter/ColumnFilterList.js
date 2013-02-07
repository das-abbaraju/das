Ext.define('PICS.view.report.modal.column-filter.ColumnFilterList', {
    extend: 'Ext.grid.Panel',
    alias: 'reportcolumnfilterlist',
    
    requires: [
        'Ext.grid.feature.Feature',
        'Ext.grid.feature.Grouping',
        'Ext.grid.column.Template'
    ],
    
    border: 0,
    columns: [{
        xtype: 'templatecolumn',
        dataIndex: 'name',
        tpl: '{name} <span class="description">{description}</span>',
        flex: 1
    }],
    enableColumnHide: false,
    features: [{
        ftype: 'grouping',
        groupHeaderTpl: '{name} <span class="number-of-items">({rows.length} item{[values.rows.length != 1 ? "s" : ""]})</span>'
    }],
    hideHeaders: true,
    listeners: {
        render: function (cmp, eOpts) {

            // Adds '.x-over' to a 'x-grid-group-hd' (group header) on mouseover.
            this.mon(cmp.el, 'mouseover', function (event, html, eOpts) {
                var class_names = this.getGroupClassNamesWithoutOver(html);

                class_names.push('x-over');
                html.className = class_names.join(' ');
            }, cmp, {
                delegate: '.x-grid-group-hd'
            });

            // Removes '.x-over' from a 'x-grid-group-hd's (group headers) on mouseout.
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
    },

    reset: function () {
        var store = this.getStore(),
            selection_model = this.getSelectionModel();

        store.clearFilter();

        selection_model.deselectAll();
    }
});