describe('Skill Details', function() {
    var scope,
        whoami_url = '/employee-guard/who-am-i',
        corporate_user = {
            "userId":116679,
            "accountId":55653,
            "name":"Lydia Rodarte-Quayle",
            "type":"CORPORATE"
        };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($injector, $rootScope, $controller, $httpBackend, WhoAmI, SkillDetail, EmployeeService) {
        scope = $rootScope.$new();
        $controller("skillDetailCtrl", {
            $scope: scope
        });

        $httpBackend.when('GET', whoami_url).respond(corporate_user);
        $httpBackend.when('GET', '/employee-guard/operators/contractors/employees').respond('');
        $httpBackend.when('GET', '/employee-guard/skillreview/employee/skill/info').respond('');
        $httpBackend.flush();

    }));

    it("userType should be defined", function() {
        expect(scope.userType).toBeDefined();
    });

    it("employee should be defined", function() {
        expect(scope.employee).toBeDefined();
    });

    it("skill should be defined", function() {
        expect(scope.skill).toBeDefined();
    });

    it("skill should have a status class if completed", function() {
        //This is just testing the controller method only.  The service is tested elsewhere.
        expect(scope.getSkillStatusClassName('Expired')).toEqual('danger');
    });
});