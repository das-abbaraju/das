(function($) {
	PICS.define('employee.ManageJobRoles', {
		methods : {
			init : function() {
				this.setUpAutocomplete();
				$('#jobCompetencyList .addCompetency').live('click', this.addCompetency);
				$('#roleCell .removeCompetency').live('click', this.removeCompetency);
				$('#roleCell .cancelButton').live('click', this.cancel);
				$('#roleCell .deleteButton').live('click', this.deleteJobRole);
				$('#addLink').bind('click', this.addJobRole);
				$('.roleLink').bind('click', this.getJobRole);
			},

			setUpAutocomplete : function() {
				$('#roleInputBox').autocomplete(
					'RoleSuggestAjax.action',
					{
						minChars : 1,
						formatItem : function(data, i,
								count) {
							return data[1];
						}
					}
				);
			},

			addCompetency : function(event) {
				event.preventDefault();
				$("a.compEditor").hide();

				var accountID = $(this).attr('data-account');
				var competencyID = $(this).closest('tr').attr('id');
				var roleID = $(this).closest('table').attr('id');

				$('#jobCompetencyList').load(
					'ManageJobRoles!addCompetency.action',
					{
						account : accountID,
						role : roleID,
						competency : competencyID
					}
				);
			},

			removeCompetency : function(event) {
				event.preventDefault();
				$("a.compEditor").hide();

				var accountID = $(this).attr('data-account');
				var competencyID = $(this).closest('tr').attr('id');
				var roleID = $(this).closest('table').attr('id');

				$('#jobCompetencyList').load(
					'ManageJobRoles!removeCompetency.action',
					{
						account : accountID,
						role : roleID,
						competency : competencyID
					}
				);
			},

			cancel : function(event) {
				event.preventDefault();
				$('#roleCell').empty();
			},

			deleteJobRole : function(event) {
				return confirm(translate('JS.ManageJobRoles.confirm.RemoveJobRole'));
			},

			addJobRole : function(event) {
				event.preventDefault();
				var account = $(this).attr('data-account');
				var audit = $(this).attr('data-audit');
				var questionId = $(this).attr('data-questionId');

				$('#roleCell').load(
					'ManageJobRoles!get.action', {
						account : account,
						audit: audit,
						questionId : questionId
					}
				);
			},

			getJobRole : function(event) {
				event.preventDefault();

				var accountID = $('#accountID').val();
				var audit = $(this).attr('data-audit');
				var questionId = $(this).attr('data-questionId');

				startThinking({
					div : 'roleCell',
					message : translate('JS.ManageJobRoles.message.LoadingJobRole')
				});
				
				$('#roleCell').load(
					'ManageJobRoles!get.action', {
						role : $(this).attr('id'),
						account : accountID,
						audit : audit,
						questionId : questionId
					}
				);
			}
		}
	});
})(jQuery);