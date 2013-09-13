(function ($) {
    PICS.define('workflow.Manage', {
        methods: {
            init: function () {
                if ($('#ManageAuditWorkFlow-page').length) {
                    $('#ManageAuditWorkFlow-page').delegate('.hide-form-steps', 'click', {show: false}, this.toggleFormSteps);
                    $('#ManageAuditWorkFlow-page').delegate('.showAddStep', 'click', {show: true}, this.toggleFormSteps);
                    
                    $('#ManageAuditWorkFlow-page').delegate('.hide-form-status', 'click', {show: false}, this.toggleFormStatus);
                    $('#ManageAuditWorkFlow-page').delegate('.showAddStatus', 'click', {show: true}, this.toggleFormStatus);
                    
                    $('#ManageAuditWorkFlow-page').delegate('.addStep', 'click', this.addStep);
                    $('#ManageAuditWorkFlow-page').delegate('.addStatus', 'click', this.addStatus);

                    $('#ManageAuditWorkFlow-page').delegate('.showWorkflow', 'click', this.editWorkflowSteps);
                    $('#ManageAuditWorkFlow-page').delegate('.editWorkflow', 'click', this.editWorkflowSteps);
                    
                    $('#ManageAuditWorkFlow-page').delegate('.closeEdit', 'click', this.closeWorkflowSteps);
                    $('#ManageAuditWorkFlow-page').delegate('.loadSteps', 'click', this.loadWorkflowSteps);
                }
            },
            
            toggleFormSteps: function(event) {
                if (event.data.show) {
                    $('#ManageAuditWorkFlow-page #form_steps').show();
                } else {
                    $('#ManageAuditWorkFlow-page #form_steps').hide();
                }
            },
            
            toggleFormStatus: function(event) {
                if (event.data.show) {
                    $('#ManageAuditWorkFlow-page #form_status').show();
                } else {
                    $('#ManageAuditWorkFlow-page #form_status').hide();
                }
            },
            
            editWorkflowSteps: function(event) {
                var id = $(this).closest('tr').attr('id');
                
                PICS.ajax({
                   url: 'ManageAuditWorkFlowAjax.action' + (id > 0 ? '?id=' + id : ''),
                   success: function(data, textStatus, XMLHttpRequest) {
                       $('#ManageAuditWorkFlow-page #workflow_edit').html(data);
                       $('#ManageAuditWorkFlow-page #workflow_edit').show();
                       $('#ManageAuditWorkFlow-page #workflowSteps').hide();
                   }
                });
            },
            
            closeWorkflowSteps: function(event) {
                $('#ManageAuditWorkFlow-page #workflow_edit').hide();
            },
            
            loadWorkflowSteps: function(event) {
                var workflowID = $(this).closest('tr').attr('id');
                
                PICS.ajax({
                    url: 'ManageAuditWorkFlowAjax.action',
                    data: {
                        button: 'getSteps',
                        id: workflowID
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#ManageAuditWorkFlow-page #workflowSteps').html(data);
                        $('#ManageAuditWorkFlow-page #workflow_edit').hide();
                        $('#ManageAuditWorkFlow-page #workflowSteps').show();
                        var that = PICS.getClass('workflow.Manage');
                        $('#ManageAuditWorkFlow-page .deleteStatus').click(function () {
                            var id = $(this).closest('tr').attr('id');
                            that.loadStatusData('deleteStatus', id);
                        });
                        $('#ManageAuditWorkFlow-page .editStatus').click(function () {
                            var id = $(this).closest('tr').attr('id');
                            that.loadStatusData('editStatus', id);
                        });
                        $('#ManageAuditWorkFlow-page .deleteStep').click(function () {
                            var id = $(this).closest('tr').attr('id');
                            that.loadData('deleteStep', id);
                        });
                        $('#ManageAuditWorkFlow-page .editStep').click(function (event) {
                            var id = $(this).closest('tr').attr('id');
                            that.loadData('editStep', id);
                        });                            
                    }
                });
            },
            
            loadData: function(action, rowID) {
                var stepID = rowID.replace('step_', '');
                var data = $('#step_'+stepID+' :input').serialize();
                var workflowID = $('[name="workflowID"]').val();
                
                data += '&id=' + workflowID + '&stepID=' + stepID + '&button=' + action;
                
                PICS.ajax({
                    url: 'ManageAuditWorkFlowAjax.action',
                    data: data,
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#ManageAuditWorkFlow-page #workflowSteps').html(data);
                        $('#ManageAuditWorkFlow-page .deleteStatus').click(function (event) {
                            var id = $(this).closest('tr').attr('id');
                            PICS.getClass('workflow.Manage').loadStatusData('deleteStatus', id);
                        });
                        $('#ManageAuditWorkFlow-page .editStatus').click(function () {
                            var id = $(this).closest('tr').attr('id');
                            that.loadStatusData('editStatus', id);
                        });
                        $('#ManageAuditWorkFlow-page .deleteStep').click(function (event) {
                            var id = $(this).closest('tr').attr('id');
                            PICS.getClass('workflow.Manage').loadData('deleteStep', id);
                        });                        
                        $('#ManageAuditWorkFlow-page .editStep').click(function (event) {
                            var id = $(this).closest('tr').attr('id');
                            PICS.getClass('workflow.Manage').loadData('editStep', id);
                        });                        
                    }
                });
            },
            
            loadStatusData: function(action, rowID) {
                var stateID = rowID.replace('state_', '');
                var data = $('#state_'+stateID+' :input').serialize();
                var workflowID = $('[name="workflowID"]').val();
                
                data += '&id=' + workflowID + '&statusID=' + stateID + '&button=' + action;
                
                PICS.ajax({
                    url: 'ManageAuditWorkFlowAjax.action',
                    data: data,
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#ManageAuditWorkFlow-page #workflowSteps').html(data);
                        $('#ManageAuditWorkFlow-page .editStatus').click(function (event) {
                            var id = $(this).closest('tr').attr('id');
                            PICS.getClass('workflow.Manage').loadStatusData('editStatus', id);
                        });
                        $('#ManageAuditWorkFlow-page .deleteStatus').click(function (event) {
                            var id = $(this).closest('tr').attr('id');
                            PICS.getClass('workflow.Manage').loadStatusData('deleteStatus', id);
                        });                        
                        $('#ManageAuditWorkFlow-page .deleteStep').click(function (event) {
                            var id = $(this).closest('tr').attr('id');
                            PICS.getClass('workflow.Manage').loadData('deleteStep', id);
                        });                        
                        $('#ManageAuditWorkFlow-page .editStep').click(function (event) {
                            var id = $(this).closest('tr').attr('id');
                            PICS.getClass('workflow.Manage').loadData('editStep', id);
                        });                        
                    }
                });
            },
            
           addStep: function(event) {
                PICS.ajax({
                   url: 'ManageAuditWorkFlowAjax.action',
                   data: $('#form_steps').serialize(),
                   success: function(data, textStatus, XMLHttpRequest) {
                       $('#ManageAuditWorkFlow-page #workflowSteps').html(data);
                       $('#ManageAuditWorkFlow-page .deleteStatus').click(function (event) {
                           var id = $(this).closest('tr').attr('id');
                           PICS.getClass('workflow.Manage').loadStatusData('deleteStatus', id);
                       });                        
                       $('#ManageAuditWorkFlow-page .deleteStep').click(function (event) {
                           var id = $(this).closest('tr').attr('id');
                           PICS.getClass('workflow.Manage').loadData('deleteStep', id);
                       });                        
                       $('#ManageAuditWorkFlow-page .editStep').click(function (event) {
                           var id = $(this).closest('tr').attr('id');
                           PICS.getClass('workflow.Manage').loadData('editStep', id);
                       });                        
                   }
                });
            },

            addStatus: function(event) {
                var something = $('#form_status').serialize();
                PICS.ajax({
                   url: 'ManageAuditWorkFlowAjax.action',
                   data: $('#form_status').serialize(),
                   success: function(data, textStatus, XMLHttpRequest) {
                       $('#ManageAuditWorkFlow-page #workflowSteps').html(data);
                      $('#ManageAuditWorkFlow-page .deleteStatus').click(function (event) {
                           var id = $(this).closest('tr').attr('id');
                           PICS.getClass('workflow.Manage').loadStatusData('deleteStatus', id);
                       });                        
                       $('#ManageAuditWorkFlow-page .deleteStep').click(function (event) {
                           var id = $(this).closest('tr').attr('id');
                           PICS.getClass('workflow.Manage').loadData('deleteStep', id);
                       });                        
                       $('#ManageAuditWorkFlow-page .editStep').click(function (event) {
                           var id = $(this).closest('tr').attr('id');
                           PICS.getClass('workflow.Manage').loadData('editStep', id);
                       });                        
                   }
                });
            }
        }
    });
})(jQuery);