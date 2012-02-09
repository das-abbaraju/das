if (!window.SCHEDULE_AUDIT) {
	SCHEDULE_AUDIT = {};
}

/**
 * SCHEDULE AUDIT
 * 
 * Base Class
 */
SCHEDULE_AUDIT = {
	/**
	 * Base Class
	 * 
	 * isVerified()
	 * submit()
	 */
	Base: {
		isVerified: function () {
			if ($("#conAudit_latitude").val() == 0) {
				return false;
			}
				
			if ($("#conAudit_longitude").val() == 0) {
				return false;
			}

			return true;
		},
		
		submit: function (event) {
			if (!this.isVerified()) {
				
				if ($("#unverifiedCheckbox").val()) {
					return true;
				}
					
				this.verifyAddress();
				
				return false;
			}
			
			return true;
		}
	},
		
	edit: function() {
		var that = Object.create(SCHEDULE_AUDIT.Base);
		
		that.init = function () {
			var processing = false;
			
			$('.schedule-audit-edit-form').bind('submit', function(event) {
				if (processing === false) {
					processing = true;
					
					return that.submit.apply(that, [event]);
				} else {
					return false;
				}
			});
		};
		
		return that;
	},
	
	confirm: function() {
		return {
			init: function () {
				var processing = false;
				
				$('.schedule-audit-confirm-form').bind('submit', function(event) {
					if (processing === false) {
						processing = true;
					} else {
						return false;
					}
				});
			}
		};
	}
};

$(function() {
	SCHEDULE_AUDIT.edit().init();
	SCHEDULE_AUDIT.confirm().init();
});