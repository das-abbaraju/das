describe('The Operator Assignment Page', function() {
    var scope;
    var corporate_user = {
       "userId":116679,
       "accountId":55653,
       "name":"Lydia Rodarte-Quayle",
       "type":"CORPORATE"
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));
    beforeEach(inject(function($injector, $rootScope, $controller, $httpBackend, $routeParams, WhoAmI) {
            scope = $rootScope.$new();
            $controller("changeLogCtrl", {
                $scope: scope
            });

        $httpBackend.when('GET', '/employee-guard/who-am-i').respond(corporate_user);
        $httpBackend.flush();
    }));

    it("should get a user", function() {
        expect(scope.user).toBeDefined();
    });
});
