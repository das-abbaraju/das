//Ext.require('Ext.data.Store');
//Ext.require('Ext.data.reader.JsonView');
//Ext.require('Ext.grid.Panel');
//Ext.require('Ext.util.Format');

ReportDynamic!save.action?showSQL=true&report.base=Contractors&report.summary=testContractorReport&report.id=1&report.parameters={
		"rowsPerPage" : 10,
		"filters" : [ {
			"field" : "accountName",
			"operator" : "BeginsWith",
			"value" : "Ancon"
		} ],
		"columns" : [ "accountID", "accountName", "accountStatus" ],
		"orderBy" : [ {
			"field" : "accountStatus",
			"direction" : "DESC"
		}, {
			"field" : "accountID"
		} ]
	}
ReportDynamic.action?showSQL=true&id=2

var testing = {
	"rowsPerPage" : 10,
	"filters" : [ {
		"field" : "accountName",
		"operator" : "BeginsWith",
		"value" : "Ancon"
	} ],
	"columns" : [ "accountID", "accountName", "accountStatus" ],
	"orderBy" : [ {
		"field" : "accountStatus",
		"direction" : "DESC"
	}, {
		"field" : "accountID"
	} ]
}

testing = {
	"columns" : [ "accountStatus" ],
	"filters" : [ {
		"field" : "accountName",
		"operator" : "StartsWith",
		"value" : "Ancon"
	} ],
	"orderBy" : [ {
		"field" : "total",
		"direction" : "DESC"
	} ],
	"groupBy" : [ {
		"field" : "accountStatus"
	} ]
}

testing = {
	"columns" : [ "accountID", "accountName", "accountStatus" ],
	"filters" : [ {
		"field" : "accountName",
		"operator" : "BeginsWith",
		"value" : "Ancon"
	} ]
}

function main() {
	var baseStore = Ext.create('Ext.data.Store', {
		proxy : {
			type : 'ajax',
			url : 'ReportDynamic.action?report=1',
			reader : {
				type : 'json',
				root : 'data'
			}
		},
		autoLoad : true,
		fields : [ 'accountID', 'accountName', 'accountStatus' ]
	});

	var params = {
		renderTo : Ext.getBody(),
		store : baseStore,
		multiSelect : true,
		width : 800,
		height : 600,
		title : 'Contractors Report',
		columns : [
				{
					xtype : 'rownumberer',
					width : 27
				},
				{
					text : 'ID',
					width : 70,
					hidden : true,
					dataIndex : 'accountID'
				},
				{
					text : 'Contractor Name',
					flex : 1,
					hideable : false,
					renderer : function(value, metaData, record) {
						return Ext.String
								.format(
										'<a href="ContractorView.action?id={0}">{1}</a>',
										record.data.accountID,
										record.data.accountName);
					},
					dataIndex : 'accountName'
				},
				{
					xtype : 'actioncolumn',
					hideable : false,
					sortable : false,
					width : 50,
					items : [ {
						icon : 'images/edit_pencil.png',
						tooltip : 'Edit',
						handler : function(grid, rowIndex, colIndex) {
							var record = grid.getStore().getAt(rowIndex);
							alert("Edit " + record.data.accountID);
						}
					} ]
				},
				{
					hideable : false,
					sortable : false,
					renderer : function(value, metaData, record) {
						return Ext.String
								.format(
										'<a href="ContractorEdit.action?id={0}">Edit</a>',
										record.data.accountID);
					}
				}, {
					text : 'Status',
					dataIndex : 'accountStatus'
				} ]
	};
	Ext.create('Ext.grid.Panel', params);
}

Ext.onReady(function() {
	main();
});
