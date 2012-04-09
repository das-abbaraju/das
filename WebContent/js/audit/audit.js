(function ($) {
    PICS.define('audit.Audit', {
        methods: {
            init: function () {
             // TODO:
                // comment wtf this block is for and doing
                // bind custom event
                // fire custom event from the ajax success of reject policy
                /*if (operator_visible) {
                    var audit_id = $('#auditID').val();
                    var cao_id = element.find('.bCaoID').val();
                    var status = element.find('.bStatus').val();
                    
                    var data = {
                        auditID: audit_id, 
                        caoID: cao_id, 
                        status: status,
                        viewCaoTable: true
                    };
                    
                    $('body').bind('update-cao-table', function (event) {
                        PICS.ajax({
                            url: 'CaoSaveAjax!save.action',
                            data: data,
                            success: function (data, textStatus, XMLHttpRequest) {
                                $('#caoTable').html(data);
                            }
                        });
                    });
                }*/
            }
        }
    });
})(jQuery);