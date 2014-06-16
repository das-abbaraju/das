angular.module('PICS.services')

.factory('User', function () {
    function User(data) {
        this.isLoggedIn = data.isLoggedIn || false;
        this.countryId = data.countryId || 'US';
        this.languageId = data.languageId || 'en';
    }
})

.factory('Account', function () {
    function Account(data) {
        angular.forEach(data.users, function (userData, index) {
            user = new User(userData);
        });
    }
})

.factory('userService', function () {
    var users = [];

    function createUser(data) {
        var user = new User(data);
        
        users.push(user);

        return user;
    }
})


.factory('accountService', function ($resource) {
    var resource = $resource('/accounts.action', {}, {
        _validateCreateAccountParams: {
            method: 'POST',
            url: '/registration/validation.action'
        }
    });

    function createAccount(account) {
        return resource.save(account).$promise;
    }

    function validateCreateAccountParams(params) {
        return resource._validateCreateAccountParams(params).$promise;
    }

    return {
        createAccount: createAccount,
        validateCreateAccountParams: validateCreateAccountParams
    };
});