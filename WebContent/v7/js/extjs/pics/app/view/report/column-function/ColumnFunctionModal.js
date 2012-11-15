Ext.define('PICS.view.report.column-function.ColumnFunctionModal', {
    extend: 'PICS.view.report.ReportModal',
    alias: 'widget.reportcolumnfunctionmodal',

    border: 0,
    closeAction: 'destroy',
    draggable: false,
    header: {
        height: 44
    },
    id: 'column_function_modal',
    layout: 'fit',
    modal: true,
    resizable: false,
    shadow: 'frame',
    title: 'Column Functions',
    width: 300,

    initComponent: function () {
        if (!this.column || this.column.modelName != 'PICS.model.report.Column') {
            Ext.Error.raise('Invalid column record');
        }

        this.callParent(arguments);

        var functions = this.column.raw.field.functions,
            function_items = this.getFunctionItems(functions);
        this.addDockedItems(function_items);

        this.height = 40 * functions.length + 95;
    },

    addDockedItems: function (function_items) {
       this.addDocked({
           xtype: 'toolbar',
           border: 0,
           defaults: {
               height: 35
           },
           dock: 'top',
           id: 'column_function_list',
           items: function_items,
           layout: 'vbox'
           });

       this.addDocked({
           xtype: 'panel',
           border: 0,
           dock: 'bottom',
           height: 10,
           id: 'column_function_modal_footer'
       });
   },

   getFunctionItem: function (fn) {
       return {
           action: fn.key,
           height: 40,
           text: fn.value,
           textAlign: 'left'
       };
   },

   getFunctionItems: function (functions) {
       var items = [{
           action: '',
           height: 40,
           text: 'None',
           textAlign: 'left'
       }];

       var that = this;
       Ext.each(functions, function (fn) {
           items.push(that.getFunctionItem(fn));
       });

       return items;
   }
});