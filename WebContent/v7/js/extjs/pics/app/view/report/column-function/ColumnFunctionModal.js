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

//      this.getFunctions(this.column, this.addDockedItems);
        var functions = ['Max', 'Min'];
        this.addDockedItems(functions);
        this.height = 40 * functions.length + 55;
    },

    addDockedItems: function (functions) {
       var items = this.getFunctionItems(functions);

       this.addDocked({
           xtype: 'toolbar',
           border: 0,
           defaults: {
               height: 35
           },
           dock: 'top',
           id: 'column_function_list',
           items: items,
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
           action: fn,
           height: 40,
           text: fn,
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
   },

   getFunctions: function (column, callback) {
       /*
       var displayType = this.column.raw.field.displayType;
           url = 'ReportDynamic!copy.action';

       Ext.Ajax.request({
           url: url,
           params: displayType,
           success: function (result) {
               var result = Ext.decode(result.responseText);

               if (result.error) {
                   Ext.Msg.alert('Status', result.error);
               } else {
                   callback(result);
               }
           }
       });
       */
       var result = ['Max', 'Min'];
       callback(result).apply(this);
   },
});