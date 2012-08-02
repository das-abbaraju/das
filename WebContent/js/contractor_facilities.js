(function ($) {
    PICS.define('contractor.Facilities', {
        methods: {
            init: function () {
                var element = $('.ContractorFacilities-page');

                if (element.length) {
                    this.search();
                    
                    $('#assigned_operators').delegate('.remove', 'click', {contractorFacilities : this}, this.remove);
                    
                    var facilitySearch = $('#facility_search');
                    facilitySearch.delegate('.picsbutton.positive', 'click', this.search);
                    facilitySearch.delegate('#search_location', 'change', this.search);
                    facilitySearch.delegate('#search_operator', 'change', this.search);
                    
                    var results = $('#results');
                    results.delegate('#show_all_operators', 'click', this.showAllOperators);
                    results.delegate('.add', 'click', {contractorFacilities : this}, this.validateBidOnly);
                    
                    element.delegate('#switch_to_trial_account', 'click', {contractorFacilities : this}, this.switchToBid);
                    element.delegate('#requested_by_operator', 'change', {contractorFacilities : this}, this.setRequestedBy);
                }
            },
            
            add: function(contractor, operator, callback) {
                var contractorFacilities = this;
                
                var message = translate('JS.ContractorFacilities.message.LinkingOperator');
                $('#thinkingDiv').html(message + ' <img src="images/ajax_process.gif" alt="' + message + '" />');
                
                PICS.ajax({
                    url: 'ContractorFacilities!add.action',
                    data: {
                        contractor: contractor,
                        operator: operator
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#thinkingDiv').empty();
                        
                        contractorFacilities.reload(contractor);
                        contractorFacilities.search();
                        refreshNoteCategory(contractor, 'OperatorChanges');
                        
                        if (callback && typeof callback == 'function') {
                            callback();
                        }
                    }
                });
            },
            
            hideModalAndAddSite: function(event) {
                var contractorFacilities = event.data.contractorFacilities;
                var modal = PICS.getClass('modal.Modal');
                modal.hide();
                
                var contractor = $(this).attr('data-contractor');
                var generalContractor = $(this).attr('data-general-contractor');
                var operator = $(this).attr('data-operator');
                
                contractorFacilities.add.apply(contractorFacilities, 
                    [contractor, operator, function() {
                        contractorFacilities.add.apply(contractorFacilities, [contractor, generalContractor]);
                    }]);
            },
            
            reload: function(contractor) {
                var message = translate('JS.ContractorFacilities.message.RefreshingList');
                $('#thinkingDiv').html(message + ' <img src="images/ajax_process.gif" alt="' + message + '" />');
                
                PICS.ajax({
                    url: 'ContractorFacilities!load.action',
                    data: {
                        id: contractor
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#thinkingDiv').empty();
                        $('#facilities').html(data);
                    }
                });
            },
            
            remove: function(event) {
                var contractorFacilities = event.data.contractorFacilities;
                
                var contractor = $(this).attr('data-contractor');
                var operator = $(this).attr('data-operator');
                var row = $(this).closest('tr');
                
                var message = translate('JS.ContractorFacilities.message.UnLinkingOperator');
                $('#thinkingDiv').html(message + ' <img src="images/ajax_process.gif" alt="' + message + '" />');
                
                PICS.ajax({
                    url: 'ContractorFacilities!remove.action',
                    data: {
                        contractor: contractor,
                        operator: operator
                    },
                    dataType: "json",
                    success: function(data, textStatus, XMLHttpRequest) {
                        row.fadeOut();
                        $('#thinkingDiv').empty();
                        $('#facility_search .clearable').val('');
                        
                        contractorFacilities.search();
                        contractorFacilities.reload(contractor);
                        refreshNoteCategory(contractor, 'OperatorChanges');
                    }
                });
            },
            
            search: function(event) {
                if (event) {
                    event.preventDefault();
                }
                
                var data = $('#facility_search').serialize();
                
                var message = translate('JS.ContractorFacilities.message.SearchingForMatches');
                $('#results').html(message + ' <img src="images/ajax_process.gif" alt="' + message + '" />');
                
                PICS.ajax({
                    url: 'ContractorFacilities!search.action',
                    data: data,
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#results').html(data);
                    }
                });
            },
            
            setRequestedBy: function(event) {
                var contractorFacilities = event.data.contractorFacilities;
                
                var contractor = $(this).attr('data-contractor');
                var operator = $(this).val();
                
                var message = translate('JS.ContractorFacilities.message.SavingRequestedBy');
                $('#thinkingDiv').html(message + ' <img src="images/ajax_process.gif" alt="' + message + '" />');
                
                PICS.ajax({
                    url: 'ContractorFacilities!setRequestedBy.action',
                    data: {
                        contractor: contractor,
                        operator: operator
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        $("#thinkingDiv").empty();
                        
                        contractorFacilities.reload(contractor);
                        refreshNoteCategory(contractor, 'OperatorChanges');
                    }
                });
            },
            
            showAllOperators: function(event) {
                var data = $('#facility_search').serialize();
                var elementID = $(this).attr('id');
                
                var message = translate('JS.ContractorFacilities.message.SearchingForMatches');
                $('#results').html(message + ' <img src="images/ajax_process.gif" alt="' + message + '" />');
                
                PICS.ajax({
                    url: 'ContractorFacilities!searchShowAll.action',
                    data: data,
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#results').html(data);
                        
                        $('#' + elementID).closest('tr').hide();
                        $('#help').hide();
                    }
                });
            },
            
            showGeneralContractorModal: function (contractor, operator, operatorName) {
                var contractorFacilities = this;
                var modal_title = translate('JS.RegistrationAddClientSite.SelectedClientIsGC', [operatorName]);
                
                PICS.ajax({
                    url: 'ContractorFacilities!generalContractorOperators.action',
                    data: {
                        contractor: contractor,
                        operator: operator
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        var modal = PICS.modal({
                            title: modal_title,
                            content: data,
                            buttons: [
                                {
                                    html: '<a href="javascript:;" class="btn danger">' + translate('JS.button.Close') + '</a>',
                                    callback: function() {
                                        PICS.getClass('modal.Modal').hide();
                                    }
                                }
                            ]
                        });
                        
                        modal.show();
                        
                        modal.getElement().delegate('.add', 'click', 
                            {contractorFacilities : contractorFacilities},
                            contractorFacilities.hideModalAndAddSite);
                    }
                });
            },
            
            switchToBid: function(event) {
                var contractor = $(this).attr('data-contractor');
                var contractorFacilities = event.data.contractorFacilities;
                
                if (confirm(translate("JS.ContractorFacilities.message.BidOnly"))) {
                    var message = translate('JS.ContractorFacilities.message.SwitchingToTrial');
                    $('#thinkingDiv').html(message + ' <img src="images/ajax_process.gif" alt="' + message + '" />');
                    
                    PICS.ajax({
                        url: 'ContractorFacilities!switchToTrialAccount.action',
                        data: {
                            contractor: contractor
                        },
                        success: function(data, textStatus, XMLHttpRequest) {
                            $('#thinkingDiv').empty();
                            $('#next_button').show();
                            
                            contractorFacilities.reload(contractor);
                            contractorFacilities.search();
                        }
                    });
                }
            },
            
            validateBidOnly: function(event) {
                var contractorFacilities = event.data.contractorFacilities;
                
                var contractor = $(this).attr('data-contractor');
                var needsModal = $(this).attr('data-needs-modal');
                var operator = $(this).attr('data-operator');
                var operatorName = $(this).attr('data-operator-name');
                
                PICS.ajax({
                    url: 'ContractorFacilities!validateBidOnly.action',
                    data: {
                        contractor: contractor,
                        operator: operator
                    },
                    dataType: "json",
                    success: function(data, textStatus, XMLHttpRequest) {
                        if (needsModal === 'true') {
                            contractorFacilities.showGeneralContractorModal.apply(
                                contractorFacilities, [contractor, operator, operatorName]
                            );
                        } else {
                            if (data.isBidOnlyContractor && !data.isBidOnlyOperator) {
                                if (confirm(translate("JS.ContractorFacilities.message.UpgradeOffer"))) {
                                    contractorFacilities.addOperator.apply(contractorFacilities, [contractor, operator]);
                                } else {
                                    return;
                                }
                            }
                            
                            contractorFacilities.add.apply(contractorFacilities, [contractor, operator]);
                        }
                    }
                });
            }
        }
    });
})(jQuery);