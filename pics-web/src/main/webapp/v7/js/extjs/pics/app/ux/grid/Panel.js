Ext.define('PICS.ux.grid.Panel', {
    extend: 'Ext.grid.Panel',

    // update or reset no results message
    updateNoResultsMessage: function () {
        var store = this.getStore(),
            view = this.getView();

        if (store.getCount() == 0) {
            view.emptyText = '<div class="x-grid-empty">' + PICS.text('Report.execute.table.noResults') + '</div>';
        } else {
            view.emptyText = '';
        }

        view.refresh();
    }
});