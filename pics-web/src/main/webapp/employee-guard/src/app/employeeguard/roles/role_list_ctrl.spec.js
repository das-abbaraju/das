describe('A Role List', function() {
    var scope;

    var roles_url = '/employee-guard/roles';
    var roles_url_dev = '/employee-guard/src/app/employeeguard/roles/json/role_list.json';
    var whoami_url = '/employee-guard/who-am-i';

    var roles = [{
            "id": 4,
            "name": "Safety Supervisor",
            "skills": [{
                    "id": 1,
                    "name": "Bob's Burgers"
                }
            ],
            'employees': [],
            'projects': []
        }];

    var operator_user = {
        "userId":116680,
        "accountId":55654,
        "name":"EmployeeGUARD User",
        "type":"OPERATOR"
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $httpBackend, RoleListResource, WhoAmI) {
        scope = $rootScope.$new();
        $controller("roleListCtrl", {
            $scope: scope
        });

        httpMock = $httpBackend;

        httpMock.when('GET', roles_url).respond(roles);
        httpMock.when('GET', roles_url_dev).respond(roles);
        httpMock.when('GET', whoami_url).respond(operator_user);
        httpMock.flush();
    }));

    it("should have roles", function() {
        expect(scope.roles).toBeDefined();
    });

    it("should have a user object", function() {
        expect(scope.userType).toBeDefined();
    });
});