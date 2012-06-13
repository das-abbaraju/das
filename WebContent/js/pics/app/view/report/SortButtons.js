Ext.define('PICS.view.report.SortButtons', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.sortbuttons'],

    border: false,
    defaults: {
        reorderable: true
    },
    plugins: Ext.create('Ext.ux.BoxReorderer', {
        listeners: {
            drop: function (reorderer, container, dragComponent, startIndex, index, options) {
                var sortStore = Ext.StoreMgr.get('report.Reports').first().sorts(),
                    draggedItem = dragComponent.child('button[action=sort-report]'),
                    record = draggedItem.record;

                sortStore.remove(record);

                sortStore.insert(index, record);

                PICS.app.fireEvent('refreshreport');
            }
        }
    })
});