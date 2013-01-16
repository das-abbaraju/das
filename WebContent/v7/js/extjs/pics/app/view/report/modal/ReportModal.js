Ext.define('PICS.view.report.modal.ReportModal', {
    extend: 'Ext.window.Window',

    initComponent: function () {
        var that = this;
        
        this.callParent(arguments);
        
        this.on('show', function (cmp, eOpts) {
            // Close the modal when the user clicks outside of it.
            Ext.get(Ext.query('.x-mask:last')).on('click', function () {
                that.close();
            });
        });
    }
});