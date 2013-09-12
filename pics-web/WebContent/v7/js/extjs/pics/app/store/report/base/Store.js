Ext.define('PICS.store.report.base.Store', {
    extend: 'Ext.data.Store',

    listeners: {
        beforeload: function (store, records, successful, eOpts) {
            this.loaded = false;
        },
        load: function (store, records, successful, eOpts) {
            this.loaded = true;
        }
    },
    loaded: false,

    isLoaded: function () {
        return !!this.loaded;
    }
});