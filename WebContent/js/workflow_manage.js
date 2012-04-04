(function ($) {
    PICS.define('workflow.Manage', {
        methods: {
            init: function () {
                if ($('#ManageAuditWorkFlow-page').length) {
                    $('#ManageAuditWorkFlow-page').delegate('.hide-form-steps', 'click', this.toggleFormSteps);
                    $('#ManageAuditWorkFlow-page').delegate('.showAddStep', 'click', {show: true}, this.toggleFormSteps);
                    
                    $('#ManageAuditWorkFlow-page').delegate('.showWorkflow', 'click', this.editWorkflowSteps);
                    $('#ManageAuditWorkFlow-page').delegate('.editWorkflow', 'click', this.editWorkflowSteps);
                    
                    $('#ManageAuditWorkFlow-page').delegate('.closeEdit', 'click', this.closeWorkflowSteps);
                    $('#ManageAuditWorkFlow-page').delegate('.loadSteps', 'click', this.loadWorkflowSteps);
                    
                    $('#ManageAuditWorkFlow-page').delegate('.deleteStep', 'click', this.loadData('deleteStep'));
                    $('#ManageAuditWorkFlow-page').delegate('.editStep', 'click', this.loadData('editStep'));
                    
                    $('#ManageAuditWorkFlow-page').delegate('.addStep', 'click', this.addStep);
                }
            },
            
            toggleFormSteps: function(event) {
                if (event.data.show) {
                    $('#ManageAuditWorkFlow-page #form_steps').show();
                } else {
                    $('#ManageAuditWorkFlow-page #form_steps').hide();
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
                    }
                });
            },
            
            loadData: function(action) {
                var stepID = $('.' + action).closest('tr').attr('id').replace('step_', '');
                var data = $('#step_'+stepID+' :input').serialize();
                var workflowID = $('[name="workflowID"]').val();
                
                data += '&id=' + workflowID + '&stepID=' + stepID + '&button=' + action;
                
                PICS.ajax({
                    url: 'ManageAuditWorkFlowAjax.action',
                    data: data,
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#ManageAuditWorkFlow-page #workflowSteps').html(data);
                    }
                });
            },
            
            addStep: function(event) {
                PICS.ajax({
                   url: 'ManageAuditWorkFlowAjax.action',
                   data: $('#form_steps').serialize(),
                   success: function(data, textStatus, XMLHttpRequest) {
                       $('#ManageAuditWorkFlow-page #workflowSteps').html(data);
                   }
                });
            }
        }
    });
})(jQuery);