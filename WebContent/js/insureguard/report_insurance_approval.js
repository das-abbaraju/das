(function ($) {
    PICS.define('insureguard.ReportInsuranceApproval', {
        methods: (function () {
            // parameter to dictate if a ajax request is still pending
            var xhr;

            return {
                init: function () {
                    var that = this;

                    var form = $('#approveInsuranceForm');

                    form.delegate('.policy-approve', 'approve', function (event, cao_id, success_callback) {
                        that.policyApprove.apply(that, [cao_id, success_callback]);
                    });

                    form.delegate('.policy-na', 'na', function (event, cao_id, success_callback) {
                        that.policyNotApplicable.apply(that, [cao_id, success_callback]);
                    });

                    form.delegate('.policy-reject', 'reject', function (event, cao_id, success_callback) {
                        that.showPolicyRejectModal.apply(that, [cao_id, false, success_callback]);
                    });

                    form.delegate('.policy-revert', 'revert', function (event, cao_id, success_callback) {
                        that.policyRevert.apply(that, [cao_id, success_callback]);
                    });

                    var audit_header = $('#auditHeader');

                    audit_header.delegate('#caoTable .policy-reject', 'reject', function (event, cao_id, success_callback) {
                        that.showPolicyRejectModal.apply(that, [cao_id, true, success_callback]);
                    });
                },

                // set policy to approved
                policyApprove: function (cao_id, success_callback) {
                    if (!cao_id) {
                        throw 'insureguard.ReportInsuranceApproval:policyApprove requires cao_id';
                    }

                    if (!xhr) {
                        var data = {
                            status: 'Approved',
                            caoIDs: cao_id,
                            insurance: true,
                            note: ''
                        };

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
                },

                // set policy to not applicable
                policyNotApplicable: function (cao_id, success_callback) {
                    if (!cao_id) {
                        throw 'insureguard.ReportInsuranceApproval:policyNotApplicable requires cao_id';
                    }

                    if (!xhr) {
                        var data = {
                            status: 'NotApplicable',
                            caoIDs: cao_id,
                            insurance: true,
                            note: ''
                        };

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
                },

                // set policy to rejected
                policyReject: function (cao_id, success_callback) {
                    if (!cao_id) {
                        throw 'insureguard.ReportInsuranceApproval:policyReject requires cao_id';
                    }

                    var that = this;

                    var form = $('.insurance-rejection-status-form');
                    var note = form.find('textarea').val();

                    function validateInsuranceRejectionForm() {
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
                    }

                    if (note == '') {
                        // if there is no note alert an error message
                        validateInsuranceRejectionForm();
                    } else {
                        var json = form.find('[name=jsonArray]').val() || [];

                        if (!xhr) {
                            var data = {
                                status: 'Incomplete',
                                caoIDs: cao_id,
                                insurance: true,
                                jsonArray: json,
                                note: note
                            };

                            xhr = PICS.ajax({
                                url: 'CaoSaveAjax!saveRejectionReasons.action',
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
                },

                // revert policy's save action
                policyRevert: function (cao_id, success_callback) {
                    if (!cao_id) {
                        throw 'insureguard.ReportInsuranceApproval:policyRevert requires cao_id';
                    }

                    var that = this;

                    if (!xhr) {
                        xhr = PICS.ajax({
                            url: 'CaoSaveAjax!undo.action',
                            data: {
                                caoID: cao_id
                            },
                            success: function (data, textStatus, XMLHttpRequest) {
                                if (typeof success_callback == 'function') {
                                    success_callback();
                                }

                                xhr = null;
                            }
                        });
                    }
                },

                // open modal to show reject policy form
                showPolicyRejectModal: function (cao_id, operator_visible, success_callback) {

                    if (!cao_id) {
                        throw 'insureguard.ReportInsuranceApproval:showPolicyRejectModal requires cao_id';
                    }

                    var that = this;

                    var data = {
                        id: cao_id,
                        operator_visible: operator_visible
                    };

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

                    function initializeModal(modal) {
                        // if the insurance reject policy modal does not have tagit input then
                        // do not apply the tagit plugin to the form element
                        if (operator_visible) {
                            var tagit_element = $('.insurance-rejection-tagit'),
                                rejection_tag_list = $('#rejection_tag_list');

                            if (tagit_element.length) {
                                tagit_element.tagit({
                                    postType: 'string',
                                    source: 'AuditRejectLookupAjax.action?caoId=' + cao_id,
                                    formatter_drop_down: '%id%',
                                    formatter_tag: '%id%'
                                });


                                tagit_element.on('add-item', function (event, item) {
                                    rejection_tag_list.append('<li id="' + item.id + '">' + item.value + '</li>');
                                });

                                tagit_element.on('remove-item', function (event, item) {
                                    var selected_item = rejection_tag_list.find('#' + item.id);

                                    if (selected_item) {
                                        selected_item.remove();
                                    }
                                });
                            }
                        }

                        // bind events to modal window for hide window + reject policy
                        $('.insurance-rejection-modal').delegate('.cancel-policy', 'click', function (event) {
                            modal.hide();
                        });

                        $('.insurance-rejection-modal').delegate('.reject-policy', 'click', function (event) {
                            that.policyReject.apply(that, [cao_id, success_callback]);
                        });
                    }

                    PICS.ajax({
                        url: 'ReportInsuranceApproval!ajaxFormInsuranceRejectionStatus.action',
                        data: data,
                        success: function (data, textStatus, XMLHttpRequest) {
                            // show modal window
                            var modal = createModal(data);

                            initializeModal(modal);

                            modal.show();
                        }
                    });
                }
            }
        }())
    });
})(jQuery);