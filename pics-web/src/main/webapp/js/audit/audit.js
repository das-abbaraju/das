(function ($) {
    PICS.define('audit.Audit', {
        methods: {
            init: function () {
                var that = this;
                
                $('#auditHeader').delegate('#caoTable', 'refresh', function (event, audit_id, cao_id, status) {
                    var cao_table = $(this);
                    
                    that.refreshCaoTable.apply(that, [cao_table, audit_id, cao_id, status]);
                });
            },
            
            refreshCaoTable: function (cao_table, audit_id, cao_id, status) {
                if (!cao_table) {
                    throw 'audit.Audit:refreshCaoTable requires cao_table';
                }
                
                if (!audit_id) {
                    throw 'audit.Audit:refreshCaoTable requires audit_id';
                }
                
                if (!cao_id) {
                    throw 'audit.Audit:refreshCaoTable requires cao_id';
                }
                
                if (!status) {
                    throw 'audit.Audit:refreshCaoTable requires status';
                }
                
                var data = {
                    auditID: audit_id, 
                    caoID: cao_id, 
                    status: status
                };
                
                PICS.ajax({
                    url: 'CaoSaveAjax!loadCaoTable.action',
                    data: data,
                    beforeSend: function (XMLHttpRequest, settings) {
                        PICS.loading(cao_table);
                    },
                    success: function (data, textStatus, XMLHttpRequest) {
                        cao_table.html(data);
                    }
                });
            }
        }
    });
})(jQuery);