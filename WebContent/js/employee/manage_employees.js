(function($) {
    PICS.define('employee.Manage', {
        methods : {
            init : function() {
                var element = $('.ManageEmployees-page');
                if (element.length) {
                    this.hashchange.apply(this);
                    this.initializeEmployee.apply(this);
                    this.setUpEmployeeTable(element.find('#employee_table'));
                    
                    element.delegate('#available_employee_roles', 'change', this.addRole);
                    element.delegate('#hse_operator_list', 'change', this.addOperatorSite);
                    element.delegate('#non_eg_operator_list', 'change', this.addOperatorSite);
                    element.delegate('#oq_project_list', 'change', this.addJobSite);
                    
                    element.delegate('#employee_nccer_link', 'click', this.nccer);
                    element.delegate('#import_excel', 'click', this.importExcel);
                    element.delegate('#new_project_link', 'click', this.newJobSite);
                    
                    element.delegate('.edit.site', 'click', this.editSite);
                    element.delegate('.remove.role', 'click', this.removeRole);
                    element.delegate('.remove.site', 'click', this.expireSite);
                    element.delegate('.preview.tasks', 'click', this.showTask);

                    var manageEmployees = this;
                    $(window).bind('hashchange', function(event) {
                        manageEmployees.hashchange.apply(manageEmployees, [event]);
                    });
                }
            },
            
            addJobSite: function(event) {
                var employee = $(this).attr('data-employee');
                var jobSite = $(this).val();
                
                PICS.ajax({
                    url: 'ManageEmployeeSite!add.action',
                    data: {
                        employee: employee,
                        jobSite: jobSite
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#employee_site').html(data);
                    }
                });
            },
            
            addOperatorSite: function(event) {
                var employee = $(this).attr('data-employee');
                var operator = $(this).val();
                
                PICS.ajax({
                    url: 'ManageEmployeeSite!add.action',
                    data: {
                        employee: employee,
                        operator: operator
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#employee_site').html(data);
                    }
                });
            },
            
            addRole: function(event) {
                var element = $(this);
                var employee = $(this).attr('data-employee');
                var role = element.val();
                
                $('#employee_role').html(translate('JS.ManageEmployees.message.AjaxLoad')
                        + ' <img src="images/ajax_process.gif" />');
                
                PICS.ajax({
                    url: 'ManageEmployeeRole!add.action',
                    data: {
                        employee: employee,
                        jobRole: role
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#employee_role').html(data);
                    }
                });
            },
            
            editSite: function(event) {
                var employeeSite = $(this).attr('data-site');
                var title = $(this).attr('title');
            
                PICS.ajax({
                    url: 'ManageEmployeeSite!edit.action',
                    data: {
                        employeeSite: employeeSite
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        var modal = PICS.modal({
                            title: title,
                            content: data,
                            buttons: [
                                {
                                    html: '<a href="javascript:;" class="btn cancel">' + translate('JS.button.Cancel') + '</a>'
                                },
                                {
                                    html: '<a href="javascript:;" class="btn danger">' + translate('JS.RegistrationAddClientSite.RemoveSite') + '</a>'
                                },
                                {
                                    html: '<a href="javascript:;" class="btn success">' + translate('JS.button.Accept') + '</a>'
                                }
                            ]
                        });
                        
                        modal.show();
                        
                        $(modal.getElement()).delegate('.success', 'click', function() {
                            var formData = $('#edit_site_form').serialize();
                            
                            PICS.ajax({
                                url: 'ManageEmployeeSite!save.action',
                                data: formData,
                                success: function(data, textStatus, XMLHttpRequest) {
                                    $('#employee_site').html(data);
                                },
                                complete: function(XMLHttpRequest, textStatus) {
                                    modal.hide();
                                }
                            });
                        });
                        
                        $(modal.getElement()).delegate('.danger', 'click', function() {
                            var formData = $('#edit_site_form').serialize();
                            
                            PICS.ajax({
                                url: 'ManageEmployeeSite!expire.action',
                                data: formData,
                                success: function(data, textStatus, XMLHttpRequest) {
                                    $('#employee_site').html(data);
                                },
                                complete: function(XMLHttpRequest, textStatus) {
                                    modal.hide();
                                }
                            });
                        });
                        
                        $(modal.getElement()).delegate('.cancel', 'click', function() {
                            modal.hide();
                        });
                    },
                    complete: function(XMLHttpRequest, textStatus) {
                        $('.datepicker').datepicker({
                            changeMonth : true,
                            changeYear : true,
                            yearRange : '1940:2039',
                            showOn : 'button',
                            buttonImage : 'images/icon_calendar.gif',
                            buttonImageOnly : true,
                            buttonText : translate('JS.ChooseADate'),
                            constrainInput : true,
                            showAnim : 'fadeIn'
                        });                        
                    }
                });
            },
            
            expireSite: function(event) {
                var employee = $(this).attr('data-employee');
                var operator = $(this).attr('data-operator');
                
                PICS.ajax({
                    url: 'ManageEmployeeSite!expire.action',
                    data: {
                        employee: employee,
                        operator: operator
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#employee_site').html(data);
                    },
                    complete: function(XMLHttpRequest, textStatus) {
                        modal.hide();
                    }
                });
            },
            
            hashchange: function(event) {
                var fragment = $.param.fragment();
                var manageEmployees = this;
                
                if (fragment) {
                    manageEmployees.loadEmployee(fragment, manageEmployees);
                }
            },
            
            highlightRow: function(id) {
                $('#employee_table tr').each(function() {
                    $(this).removeClass('highlight');
                });
                
                $('#employee_table').find('#employee_' + id).addClass('highlight');
            },
            
            importExcel: function(event) {
                event.preventDefault();
                
                var url = 'ManageEmployeesUpload.action?account='
                    + $(this).attr('data-account');
                var title = translate('JS.ManageEmployees.message.UploadEmployee');
                var pars = 'scrollbars=yes,resizable=yes,width=650,height=400,toolbar=0,directories=0,menubar=0';
                
                fileUpload = window.open(url, title, pars);
                fileUpload.focus();
            },
            
            initializeCluetip: function() {
                $('.cluetip').cluetip({
                    closeText : "<img src='images/cross.png' width='16' height='16'>",
                    arrows : true,
                    cluetipClass : 'jtip',
                    local : true,
                    clickThrough : false
                });
            },
            
            initializeEmployee: function() {
                var employee = $('#employee_form').attr('data-employee');
                var manageEmployees = this;
                
                if (employee) {
                    manageEmployees.loadEmployee('employee=' + employee, manageEmployees);
                }
            },
            
            loadEmployee: function(fragment, manageEmployees) {
                $('#employee_form').html(translate('JS.ManageEmployees.message.AjaxLoad')
                        + ' <img src="images/ajax_process.gif" />');
                
                PICS.ajax({
                    url: 'ManageEmployees!load.action',
                    data: fragment,
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#employee_form').html(data);
                        
                        var id = fragment.split("=")[1];
                        
                        manageEmployees.initializeCluetip();
                        manageEmployees.highlightRow(id);
                        
                        manageEmployees.suggestTitle();
                    }
                });
            },
            
            nccer: function(event) {
                var employeeID = $(this).attr('data-employee');
                
                var url = 'EmployeeNCCERUpload.action?employee=' + employeeID;
                var title = translate('JS.ManageEmployees.message.UploadEmployees');
                var pars = 'scrollbars=yes,resizable=yes,width=650,height=500,toolbar=0,directories=0,menubar=0';
                var fileUpload = window.open(url, title, pars);
                
                fileUpload.focus();
            },
            
            newJobSite: function(event) {
                var modal = null;
                var title = $(this).text();
                
                PICS.ajax({
                    url: 'ManageEmployeeSite!addNew.action',
                    success: function(data, textStatus, XMLHttpRequest) {
                        modal = PICS.modal({
                            title: title,
                            content: data,
                            buttons: [
                                {
                                    html: '<a href="javascript:;" class="btn cancel">' + translate('JS.button.Cancel') + '</a>'
                                },
                                {
                                    html: '<a href="javascript:;" class="btn success">' + translate('JS.button.Accept') + '</a>'
                                }
                            ]
                        });
                    },
                    complete: function(XMLHttpRequest, textStatus) {
                        modal.show();
                        
                        $(modal.getElement()).delegate('.success', 'click', function(event) {
                            var formData = $('#new_project_form').serialize();
                            
                            PICS.ajax({
                                url: 'ManageEmployeeSite!addNew.action',
                                data: formData,
                                success: function(data, textStatus, XMLHttpRequest) {
                                    $('#employee_site').html(data);
                                },
                                complete: function(XMLHttpRequest, textStatus) {
                                    modal.hide();
                                }
                            });
                        });
                        
                        $(modal.getElement()).delegate('.cancel', 'click', function(event) {
                            modal.hide();
                        });
                    }
                });
            },
            
            removeRole: function(event) {
                var role = $(this).attr('data-role');
                var remove = confirm(translate('JS.ManageEmployees.confirm.RemoveRole'));

                if (remove) {
                    $('#employee_role').html(translate('JS.ManageEmployees.message.AjaxLoad')
                            + ' <img src="images/ajax_process.gif" />');
                    
                    PICS.ajax({
                        url: 'ManageEmployeeRole!remove.action',
                        data: {
                            employeeRole: role
                        },
                        success: function(data, textStatus, XMLHttpRequest) {
                            $('#employee_role').html(data);
                        }
                    });
                }
            },
            
            setUpEmployeeTable: function(element) {
                element.dataTable({
                    aoColumns: [
                            {bVisible: false},
                            {sType: "html"},
                            {sType: "html"},
                            null,
                            null,
                            null,
                            null
                    ],
                    aaSorting: [[1, 'asc']],
                    bJQueryUi: true,
                    bStateSave: true,
                    bLengthChange: false,
                    oLanguage: {
                        sSearch:"Search",
                        sLengthMenu: '_MENU_', 
                        sInfo:"_START_ to _END_ of _TOTAL_",
                        sInfoEmpty:"",
                        sInfoFiltered:"(filtered from _MAX_)"
                    }
                });
            },
            
            showTask: function(event) {
                var order = $(this).attr('data-order');
                
                $('.qualified-tasks').each(function() {
                    if ($(this).attr('data-order') == order) {
                        $(this).toggle();
                    }
                });
            },
            
            suggestTitle: function() {
                var titleTextfield = $('#titleSuggest');
                var json = titleTextfield.attr('data-json');
                
                titleTextfield.autocomplete($.parseJSON(json));
            }
        }
    });
})(jQuery);