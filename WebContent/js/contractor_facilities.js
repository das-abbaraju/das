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
                    results.delegate('.add', 'click', {contractorFacilities : this}, this.validateBidOnly);
                    results.delegate('#show_all_operators', 'click', this.showAllOperators);
                    
                    element.delegate('#switch_to_trial_account', 'click', {contractorFacilities : this}, this.switchToBid);
                    element.delegate('#requested_by_operator', 'change', {contractorFacilities : this}, this.setRequestedBy);
                }
            },
            
            add: function(contractor, operator) {
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
                    }
                });
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
                var operator = $(this).attr('data-operator');
                
                PICS.ajax({
                    url: 'ContractorFacilities!validateBidOnly.action',
                    data: {
                        contractor: contractor,
                        operator: operator
                    },
                    dataType: "json",
                    success: function(data, textStatus, XMLHttpRequest) {
                        if (data.isBidOnlyContractor && !data.isBidOnlyOperator) {
                            if (confirm(translate("JS.ContractorFacilities.message.UpgradeOffer"))) {
                                contractorFacilities.addOperator.apply(contractorFacilities, [contractor, operator]);
                            } else {
                                return;
                            }
                        }
                        
                        contractorFacilities.add.apply(contractorFacilities, [contractor, operator]);
                    }
                });
            }
        }
    });
})(jQuery);