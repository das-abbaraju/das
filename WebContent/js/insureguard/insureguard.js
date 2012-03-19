(function ($) {
    PICS.define('insureguard.InsureGuard', {
        methods: (function () {
            // parameter to dictate if a ajax request is still pending
            var xhr;
            
            /**
             * Revert Policy
             * 
             * Revert a policy that has just been saved.
             * 
             * @data:
             * @success_callback:
             */
            function revertPolicy(data, success_callback) {
                if (!xhr) {
                    xhr = PICS.ajax({
                        url: 'CaoSaveAjax!undo.action',
                        data: data,
                        success: function (data, textStatus, XMLHttpRequest) {
                            if (typeof success_callback == 'function') {
                                success_callback();
                            }
                            
                            xhr = null;
                        }
                    });
                }
            }
            
            /**
             * Save Policy
             * 
             * Save the cao status of a policy.
             * 
             * @data:
             * @success_callback:
             */
            function savePolicy(data, success_callback) {
                if (!xhr) {
                    xhr = PICS.ajax({
                        url: 'CaoSaveAjax!save.action',
                        data: data,
                        success: function (data, textStatus, XMLHttpRequest) {
                            if (typeof success_callback == 'function') {
                                success_callback();
                            }
                            
                            xhr = null;
                        }
                    });
                }
            }
            
            return {
                init: function () {
                    var that = this;
                    
                    if ($('#ReportInsuranceApproval-page').length) {
                        $('#approveInsuranceForm').delegate('.insurance-approval-report .actions .policy-approve a', 'click', function (event) {
                            that.policyApprove.apply(that, [event]);
                        });
                        
                        $('#approveInsuranceForm').delegate('.insurance-approval-report .actions .policy-na a', 'click', function (event) {
                            that.policyNotApplicable.apply(that, [event]);
                        });
                        
                        $('#approveInsuranceForm').delegate('.insurance-approval-report .actions .policy-reject a', 'click', function (event) {
                            that.showPolicyRejectModal.apply(that, [event]);
                        });
                        
                        $('#approveInsuranceForm').delegate('.insurance-approval-report .actions .revert-policy a:not(.disabled)', 'click', function (event) {
                            that.policyRevert.apply(that, [event]);
                        });
                    }
                },
                
                // set policy to approved
                policyApprove: function (event) {
                    var that = this;
                    
                    var element = $(event.target);
                    var row = element.closest('tr');
                    var id = row.attr('data-cao-id');
                    
                    savePolicy({
                        status: 'Approved',
                        caoIDs: id,
                        insurance: true,
                        note: ''
                    }, function () {
                        // disable policy row
                        row.addClass('disable');
                        
                        // toggle action column
                        that.toggleInsuranceApprovalActions(row);
                    });
                },
                
                // set policy to not applicable
                policyNotApplicable: function (event) {
                    var that = this;
                    
                    var element = $(event.target);
                    var row = element.closest('tr');
                    var id = row.attr('data-cao-id');
                    
                    savePolicy({
                        status: 'NotApplicable',
                        caoIDs: id,
                        insurance: true,
                        note: ''
                    }, function () {
                        // disable policy row
                        row.addClass('disable');

                        // toggle action column
                        that.toggleInsuranceApprovalActions(row);
                    });
                },
                
                // set policy to rejected
                policyReject: function (event) {
                    var that = this;
                    
                    var element = $(event.target);
                    var form = $('.insurance-rejection-status-form');
                    var id = form.find('input[name=cao_id]').val();
                    var note = form.find('textarea').val();
                    
                    if (id == undefined) {
                        throw 'InsureGuard.policyReject missing caoID';
                    }
                    
                    // if there is no note alert an error message
                    if (note == '') {
                        if (!$('.insurance-rejection-modal .alert-message').length) {
                            var alert = $('<div class="alert-message warning"><span class="icon warn"></span>' + translate('JS.Validation.Required') + '</div>');
                            
                            form.prepend(alert);
                            
                            // remove error message
                            setTimeout(function () {
                                alert.fadeOut(500, function () {
                                    $(this).remove();
                                });
                            }, 2000);
                        }
                    } else {
                        savePolicy({
                            status: 'Incomplete',
                            caoIDs: id,
                            insurance: true,
                            note: note
                        }, function () {
                            // close modal window
                            var modal = PICS.getClass('modal.Modal');
                            modal.hide();
                            
                            // disable policy row
                            var row = $('.insurance-approval-report tr[data-cao-id=' + id + ']');
                            row.addClass('disable');
                            
                            // toggle action column
                            that.toggleInsuranceApprovalActions(row);
                        });
                    }
                },
                
                // revert policy's save action
                policyRevert: function (event) {
                    var that = this;
                    
                    var element = $(event.target);
                    var row = element.closest('tr');
                    var id = row.attr('data-cao-id');
                    
                    revertPolicy({
                        caoID: id
                    }, function () {
                        // disable policy row
                        row.removeClass('disable');
                        
                        // toggle action column
                        that.toggleInsuranceApprovalActions(row);
                    });
                },
                
                // open modal to show reject policy form
                showPolicyRejectModal: function (event) {
                    var that = this;
                    
                    function createModal(data) {
                        var modal = PICS.modal({
                            title: translate('JS.Modal.Title.InsuranceRejection'), 
                            modal_class: 'modal insurance-rejection-modal',
                            content: data,
                            buttons: [{
                                html: [
                                    '<a href="javascript:;" class="btn cancel-policy">' + translate('JS.button.Cancel') + '</a>',
                                    '<a href="javascript:;" class="btn error reject-policy">' + translate('JS.button.Reject') + '</a>'
                                ].join('')
                            }]
                        });
                        
                        return modal;
                    }
                    
                    var element = $(event.target);
                    var row = element.closest('tr');
                    var id = row.attr('data-cao-id');
                    
                    PICS.ajax({
                        url: 'ReportInsuranceApproval!ajaxFormInsuranceRejectionStatus.action',
                        data: {
                            id: id
                        },
                        success: function (data, textStatus, XMLHttpRequest) {
                            // show modal window
                            var modal = createModal(data);
                            modal.show();
                            
                            // bind events to modal window for hide window + reject policy
                            $('.insurance-rejection-modal').delegate('.cancel-policy', 'click', function (event) {
                                modal.hide();
                            });
                            
                            $('.insurance-rejection-modal').delegate('.reject-policy', 'click', function (event) {
                                that.policyReject.apply(that, [event]);
                            });
                        }
                    });
                },
                
                // toggle action column between approve + reject + na and undo
                toggleInsuranceApprovalActions: function (row) {
                    var change_policy = $('.actions .change-policy', row);
                    var revert_policy = $('.actions .revert-policy', row);
                    var revert_policy_link = revert_policy.find('a');
                    
                    if (change_policy.is(':visible')) {
                        change_policy.hide();
                        revert_policy.show();
                        revert_policy_link.addClass('disabled');
                        
                        setTimeout(function () {
                            revert_policy_link.removeClass('disabled');
                        }, 1000);
                    } else {
                        change_policy.show();
                        revert_policy.hide();
                        revert_policy_link.addClass('disabled');
                    }
                }
            }
        }())
    });
})(jQuery);