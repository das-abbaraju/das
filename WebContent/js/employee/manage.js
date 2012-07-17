(function($) {
    PICS.define('employee.Manage', {
        methods : {
            init : function() {
                var element = $('.ManageEmployees-page');
                if (element.length) {
                    var manageEmployees = this;

                    this.hashchange.apply(this);
                    this.setUpEmployeeTable(element.find('#employee_table'));
                    
                    element.delegate('#available_employee_roles', 'change', this.addRole);
                    element.delegate('#import_excel', 'click', this.importExcel);
                    
                    element.delegate('.edit.site', 'click', this.editSite);
                    element.delegate('.remove.role', 'click', this.removeRole);
                    
                    $(window).bind('hashchange', function(event) {
                        manageEmployees.hashchange.apply(manageEmployees, [event]);
                    });
                }
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
                
            },
            
            hashchange: function(event) {
                var fragment = $.param.fragment();
                var manageEmployees = this;
                
                if (fragment) {
                    $('#employee_form').html(translate('JS.ManageEmployees.message.AjaxLoad')
                            + ' <img src="images/ajax_process.gif" />');
                    
                    PICS.ajax({
                        url: 'ManageEmployees!loadAjax.action',
                        data: fragment,
                        success: function(data, textStatus, XMLHttpRequest) {
                            $('#employee_form').html(data);
                            
                            var id = fragment.split("=")[1];
                            
                            manageEmployees.initializeCluetip();
                            manageEmployees.highlightRow(id);
                        }
                    });
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
            }
        }
    });
})(jQuery);