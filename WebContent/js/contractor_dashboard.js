(function($) {
    PICS.define('contractor.Dashboard', {
        methods : {
            init : function() {
                var contractor_dashboard = $('.ContractorView-page');

                if (contractor_dashboard.length > 0) {
                    var that = this;
                    contractor_dashboard.delegate(
                            '#start_watch_link', 'click', {
                                action : 'Add',
                                method : 'start'
                            }, this.controlWatch);
                    contractor_dashboard.delegate(
                            '#start_watch_link', 'click', {
                                action : 'Remove',
                                method : 'stop'
                            }, this.controlWatch);

                    contractor_dashboard.delegate(
                            '#contractor_operator_numbers a.add, #contractor_operator_numbers a.edit',
                            'click',
                            { callback : this.updateContractorOperatorNumbers, dashboard_class: that },
                            this.openModalForNumbers);
                    contractor_dashboard.delegate(
                            '#contractor_operator_numbers a.remove',
                            'click',
                            this.deleteContractorOperatorNumber);

                    contractor_dashboard.delegate(
                            '.reloadPage', 'click', function() {
                                location.reload();
                            });

                    contractor_dashboard.delegate(
                            '#con_pending_gcs .positive', 'click',
                            {
                                approved : true
                            }, this.updateGeneralContractor);
                    contractor_dashboard.delegate(
                            '#con_pending_gcs .negative', 'click',
                            {
                                approved : false
                            }, this.updateGeneralContractor);

                    this.requestOpenTasks();
                }
            },

            controlWatch : function(event) {
                event.preventDefault();

                var action = event.data.action;
                var method = event.data.method;

                var conID = $(this).attr('data-conid');
                var previouslyVisible = $('#contractorWatch .watch:visible');
                var oldText = previouslyVisible.html();

                previouslyVisible.html('<img src="images/ajax_process.gif" alt="Loading" />'
                        + translate('JS.ContractorView.' + action + 'Watch'));

                PICS.ajax({
                    url : 'ContractorView!' + method + 'Watch.action',
                    data : {
                        contractor : conID
                    },
                    success : function(data, textStatus, XMLHttpRequest) {
                        var visibleNow = $('#contractorWatch .watch').not(':visible');
                        visibleNow.show();
                        visibleNow.effect('highlight', {
                            color : '#FFFF11'
                        }, 1000);
                    },
                    complete : function(XMLHttpRequest, textStatus) {
                        previouslyVisible.html(oldText);
                        previouslyVisible.hide();
                    }
                });
            },

            openModalForNumbers : function(event) {
                event.preventDefault();
                var that = event.data.dashboard_class;

                var url = $(this).attr('href');
                var contractor = $(this).attr('data-contractor');
                var number = $(this).attr('data-number');
                var name = $('#contractor_operator_numbers_label').text();
                var callback = event.data.callback;

                PICS.ajax({
                    url : url,
                    data : {
                        contractor : contractor,
                        number : number
                    },
                    success : function(data, textStatus, XMLHttpRequest) {
                        var modal = PICS.modal({
                            height : 550,
                            width : 700,
                            title : name,
                            modal_class: 'modal contractor-operator-number-modal',
                            content : data
                        });

                        var contractor_modal =  $('.contractor-operator-number-modal');

                        contractor_modal.delegate(
                            '.negative.closeButton',
                            'click',
                            function(event) {
                                var modal = PICS.getClass('modal.Modal');
                                modal.hide();
                            }
                        );

                        contractor_modal.delegate(
                                '.positive',
                                'click',
                                callback
                        );

                        contractor_modal.delegate('#contractor_numbers_client, #contractor_numbers_type', 'change', function (event) {
                            that.userListParameterCheck.apply(that, [event]);
                        });

                        //check for saved value
                        var saved_value = $('#contractor_operator_numbers_form_number');

                        if (saved_value.val() !== '') {
                            that.userListParameterCheck();
                        }
                    },
                    complete : function(XMLHttpRequest, textStatus) {
                        var modal = PICS.getClass('modal.Modal');
                        modal.show();
                    }
                });
            },

            convertValueToSelect: function (userlist) {
                var value = $('#contractor_numbers_value'),
                    saved_value = value.val();

                value.replaceWith(userlist);

                //grab newly created userList and set saved value
                if (saved_value !== '') {
                    $('select#contractor_numbers_value').val(saved_value);
                }
            },

            convertValuetoInput: function () {
                var input = $('<input type="text" id="contractor_numbers_value" name="number.value" />');

                $('#contractor_numbers_value').replaceWith(input);
            },

            deleteContractorOperatorNumber : function(event) {
                event.preventDefault();

                if (confirm(translate('JS.ManageContractorOperatorNumber.ConfirmDelete'))) {
                    var contractor = $(this).attr('data-contractor');
                    var number = $(this).attr('data-number');
                    var url = $(this).attr('href');

                    PICS.ajax({
                        url : url,
                        data : {
                            contractor : contractor,
                            number : number
                        },
                        success : function(data, textStatus, XMLHttpRequest) {
                            $('#contractor_operator_numbers').html(data);
                        }
                    });
                }
            },

            getUserList: function (client_id) {
                var that = this;

                PICS.ajax({
                    url : 'ContractorNumbersOperatorUserListAjax.action',
                    data : {
                        opID : client_id
                    },
                    success : function(data, textStatus, jqXHR) {
                        that.convertValueToSelect(data);
                    }
                });
            },

            requestOpenTasks: function () {
                var tasks = $('#con_tasks');

                if (tasks.length) {
                    PICS.ajax({
                        url : 'ContractorTasksAjax.action',
                        data : {
                            id : tasks.attr('data-conid')
                        },
                        success : function(data, textStatus, XMLHttpRequest) {
                            tasks.html(data);
                        }
                    });
                }
            },

            updateContractorOperatorNumbers : function(event) {
                event.preventDefault();
                var element = $(this);
                var data = element.closest('form').serialize();
                var url = element.attr('data-url');

                PICS.ajax({
                    url : url,
                    data : data,
                    success : function(data, textStatus, XMLHttpRequest) {
                        if (data.indexOf('error') > 0) {
                            element.parent().parent().html(data);
                        } else {
                            $('#contractor_operator_numbers').html(data);

                            var modal = PICS.getClass('modal.Modal');
                            modal.hide();
                        }
                    }
                });
            },

            updateGeneralContractor : function(event) {
                var contractor = $(this).attr('data-contractor');
                var operator = $(this).attr('data-operator');
                var approved = event.data.approved;

                PICS.ajax({
                    url : 'ContractorView!updateGeneralContractor.action',
                    data : {
                        approveGeneralContractorRelationship : approved,
                        contractor : contractor,
                        opID : operator
                    },
                    success : function(data,textStatus, XMLHttpRequest) {
                        $('#contractor_dashboard #con_pending_gcs').html(data);
                    }
                });
            },

            userListParameterCheck: function (event) {
                var client = $('#contractor_numbers_client'),
                    type = $('#contractor_numbers_type'),
                    that = this;

                if ((client.val() !== '') && ((type.val() === 'Buyer') || (type.val() === 'EHS'))) {
                    that.getUserList(client.val());
                } else {
                    if ($('#contractor_numbers_value').is('select')) {
                        that.convertValuetoInput();
                    }
                }
            }
        }
    });
})(jQuery);