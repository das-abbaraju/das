function showPermDesc(item) {
	var x = $(item).val();
	$('#permDescription').html(permTypes[x][0]);
}

function addPermission() {
	$('#addPermissionButton').html('Processing: <img src="images/ajax_process.gif" />');
	var opPerm = $('#newPermissionSelect').val();
	var data = {
		button: 'AddPerm',
		user: currentUserID,
		opPerm: opPerm,
		accountId: accountID
	};
	$('#permissionReport').load('UserAccessSaveAjax.action', data);
}

function removePermission(accessId) {
	$('#permissionReport').html('Processing: <img src="images/ajax_process.gif" />');
	var data = {
		button: 'RemovePerm',
		accessId: accessId,
		user: currentUserID,
		accountId: accountID
	};
	$('#permissionReport').load('UserAccessSaveAjax.action', data);
}

function updatePermission(accessId, typeName, theValue) {
	var data = {
		accessId: accessId,
		type: typeName,
		permValue: $(theValue).val()
	};
	$.post('UserAccessUpdateAjax.action', data, 
		function(){
			$('#permission_'+accessId).effect('highlight', {color: '#FFFF11'}, 1000);
		}
	);
}

function addGroup(groupID) {
	$('#groupReport').html('Processing: <img src="images/ajax_process.gif" />');
	var data = {
		button: 'AddGroup',
		groupId: groupID,
		user: currentUserID,
		accountId: accountID
	};
	$('#groupReport').load('UserGroupSaveAjax.action', data);
}

function removeGroup(userGroupID) {
	$('#groupReport').html('Processing: <img src="images/ajax_process.gif" />');
	var data = {
		button: 'RemoveGroup',
		userGroupId: userGroupID,
		user: currentUserID,
		accountId: accountID
	};
	$('#groupReport').load('UserGroupSaveAjax.action', data);
}

function addMember(memberId) {
	$('#memberReport').html('Processing: <img src="images/ajax_process.gif" />');
	var data = {
		button: 'AddMember',
		memberId: memberId,
		user: currentUserID,
		accountId: accountID
	};
	$('#memberReport').load('UserGroupSaveAjax.action', data);
}

function removeMember(userGroupID) {
	$('#memberReport').html('Processing: <img src="images/ajax_process.gif" />');
	var data = {
		button: 'RemoveMember',
		userGroupId: userGroupID,
		user: currentUserID,
		accountId: accountID
	};
	$('#memberReport').load('UserGroupSaveAjax.action', data);
}

function checkGroupName(groupName) {
    $('#UserSave').attr({'disabled':'disabled'});
    $('#groupname_status').html(translate('JS.UsersManage.CheckingAvailabilityOfGroupName'));
    
    var data = {
        accountId: accountID,
        userID: currentUserID,
        groupName: groupName
    };
    $('#groupname_status').load('group_ajax.jsp', data, function() {
        if($('#groupname_status').html().indexOf('error') == -1)
            $('#UserSave').attr({'disabled': false});
        }
    );
}

function checkUsername(username) {
	$('#UserSave').attr({'disabled':'disabled'});
	$('#username_status').html(translate('JS.UsersManage.CheckingAvailabilityOfUsername'));
	var data = {
		userID: currentUserID,
		username: username
	};
	$('#username_status').load('user_ajax.jsp', data, function() {
		if($('#username_status').html().indexOf('error') == -1)
			$('#UserSave').attr({'disabled': false});
		}
	);
}

function addUserSwitch(userID) {
	var data = {
			button: 'AddSwitchFrom',
			user: currentUserID,
			'memberId': userID,
			'accountId':accountID
	};
	$('#userSwitch').load('UserGroupSaveAjax.action', data);
}

function removeUserSwitch(userID) {
	var data = {
			button: 'RemoveSwitchFrom',
			user: currentUserID,
			'memberId': userID,
			'accountId':accountID
	};
	$('#userSwitch').load('UserGroupSaveAjax.action', data);
}

function showUserList() {
	$('#manage_controls').show();
	$('#user_edit').hide();
}
