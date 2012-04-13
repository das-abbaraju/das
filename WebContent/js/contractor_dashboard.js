(function($) {
    var _modal;

    PICS.define('contractor.Dashboard', {
        methods : {
            init : function() {
                $('#contractor_dashboard').delegate(
                        '#start_watch_link', 'click', {
                            action : 'Add',
                            method : 'start'
                        }, this.controlWatch);
                $('#contractor_dashboard').delegate(
                        '#start_watch_link', 'click', {
                            action : 'Remove',
                            method : 'stop'
                        }, this.controlWatch);

                $('#contractor_dashboard').delegate(
                        '#contractor_operator_numbers a.add, #contractor_operator_numbers a.edit',
                        'click',
                        this.openModalForNumbers);

                $('#contractor_dashboard').delegate(
                        '#contractor_operator_numbers_form',
                        'submit',
                        this.updateContractorOperatorNumbers);
                $('#contractor_dashboard').delegate(
                        '#contractor_operator_numbers a.remove',
                        'click',
                        this.deleteContractorOperatorNumber);

                $('#contractor_dashboard').delegate(
                        '.reloadPage', 'click', function() {
                            location.reload();
                        });

                $('#contractor_dashboard').delegate(
                        '#con_pending_gcs .positive', 'click',
                        {
                            approved : true
                        }, this.updateGeneralContractor);
                $('#contractor_dashboard').delegate(
                        '#con_pending_gcs .negative', 'click',
                        {
                            approved : false
                        }, this.updateGeneralContractor);
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

                var url = $(this).attr('href');
                var contractor = $(this).attr('data-contractor');
                var number = $(this).attr('data-number');
                var name = $('#contractor_operator_numbers_label').text();
                _modal = null;

                PICS.ajax({
                    url : url,
                    data : {
                        contractor : contractor,
                        number : number
                    },
                    success : function(data, textStatus, XMLHttpRequest) {
                        _modal = PICS.modal({
                            height : 550,
                            width : 700,
                            title : name,
                            modal_class: 'modal contractor-operator-number-modal',
                            content : data
                        });
                        
                        $('.contractor-operator-number-modal').delegate(
                            '.negative.closeButton',
                            'click',
                            function(event) {
                                var modal = PICS.getClass('modal.Modal');
                                modal.hide();
                            }
                        );
                    },
                    complete : function(XMLHttpRequest, textStatus) {
                        if (_modal) {
                            _modal.show();
                        }
                    }
                });
            },

            updateContractorOperatorNumbers : function(event) {
                event.preventDefault();
                var element = $(this);
                var data = element.serialize();
                var url = element.attr('data-url');

                PICS.ajax({
                    url : url,
                    data : data,
                    success : function(data, textStatus, XMLHttpRequest) {
                        if (data.indexOf('error') > 0) {
                            element.parent().parent().html(data);
                        } else {
                            $('#contractor_operator_numbers').html(data);

                            if (_modal) {
                                _modal.hide();
                            }
                        }
                    }
                });
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
            }
        }
    });
})(jQuery);