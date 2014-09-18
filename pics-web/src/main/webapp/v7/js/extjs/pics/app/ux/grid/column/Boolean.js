Ext.define('PICS.ux.grid.column.Boolean', {
	extend: 'PICS.ux.grid.column.Column',

	align: 'center',
	width: 50,

	constructor: function () {
		this.callParent(arguments);
	},

	renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
	    if (value === true) {
            value = '<i class="icon-check"></i>';
        } else if (value === false) {
            value = '<i class="icon-check-empty"></i>';
        } else {
            value = '';
        }

        return this.callParent(arguments);
    }
});