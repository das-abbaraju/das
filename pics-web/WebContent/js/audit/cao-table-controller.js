(function ($) {
    PICS.define('audit.CaoTableController', {
        methods: (function () {
            function init() {

				$('body').on('keyup', '#caoAjax #addToNotes', function(){
					var noteSubmitBtn = $('#yesButton');

					if($(this).val() != '') {
						noteSubmitBtn.removeAttr('disabled');
					} else {
						noteSubmitBtn.attr('disabled', 'disabled');
					}
				});

				$('body').on('click', '#caoAjax #noButton', function () {
					$.unblockUI();
				});

				$('body').on('click', '#caoAjax #yesButton', function () {
					saveCaoTable();
				});
           }

            function initAuditWorkflowChange(audit_parameters, noteText, audit_reload) {
            	getAuditNoteTemplate(audit_parameters, noteText, audit_reload);
            }

           //audit parameters = auditID, caoID, and current audit status(submitted, pending, etc)
            function getAuditNoteTemplate(audit_parameters, noteText, audit_reload) {
				var $cao_table = $('#caoTable');

				PICS.ajax({
					url: 'CaoSaveAjax!loadStatus.action',
					data: audit_parameters,
					headers: {'refresh':'true'},
					success: function(data, textStatus, jqXHR) {
						$cao_table.unblock();

						if (isNoteRequired(data)) {
							showNote(data);
						} else {
							saveCaoTable();
						}
					},
					error: function (jqXHR, textStatus, errorThrown ) {
						$cao_table.block({
							message: 'Error with request, please try again',
							timeout: 1500
						});
					}
				});
            }

            function isNoteRequired(data) {
				return $(data).filter('#noteRequired').val() == 'true';
            }

            function showNote (data) {
				$('#caoAjax').html(data);

				$.blockUI({
					message: $('#caoAjax')
				});
            }

            function saveCaoTable (audit_parameters, audit_reload) {
				var AuditController = PICS.getClass('audit.AuditController'),
					audit_parameters = AuditController.getAuditParameters(),
					$caoTable = $('#caoTable');

				$.blockUI({message: 'Saving Status, please wait...'});

		        //add to audit data
		        audit_parameters.viewCaoTable = true;

		        //get note text
		        if($('#addToNotes').val()) {
		        	audit_parameters.note =  $('#addToNotes').val();
		        }

	            PICS.ajax({
	                url: 'CaoSaveAjax!save.action',
	                data: audit_parameters,
	                success: function (data, textStatus, jqXHR) {
	                    $caoTable.html(data);

	                    $.unblockUI();

	                    if (AuditController.auditNeedsReload()) {
	                        AuditController.refreshAudit();
	                    }
	                }
	            });
            }

            return {
                init: init,
                initAuditWorkflowChange: initAuditWorkflowChange
            };
        }())
    });
}(jQuery));