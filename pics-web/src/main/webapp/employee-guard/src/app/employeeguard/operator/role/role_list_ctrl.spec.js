describe('An Operator Role List', function() {
    var scope;

    var roles_url = '/employee-guard/operators/role/list';
    var roles_url_dev = '/angular/json/operator/role_list.json';
    var whoami_url = '/employee-guard/who-am-i';

    var operator_roles = [{
            "id": 4,
            "name": "Safety Supervisor",
            "requiredSkills": [{
                    "id": 1,
                    "name": "Bob's Burgers"
                }
            ]
        }];

    var operator_user = {
        "userId":116680,
        "accountId":55654,
        "name":"EmployeeGUARD User",
        "type":"OPERATOR"
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $httpBackend, RoleList, WhoAmI) {
        scope = $rootScope.$new();
        $controller("operatorRoleListCtrl", {
            $scope: scope
        });

        httpMock = $httpBackend;
        mockResource = RoleList;

        httpMock.when('GET', roles_url).respond(operator_roles);
        httpMock.when('GET', roles_url_dev).respond(operator_roles);
        httpMock.when('GET', whoami_url).respond(operator_user);
        httpMock.flush();
    }));

    it("should have roles", function() {
        expect(scope.roles).toBeDefined();
        expect(scope.roles).toBeDefined();
    });

    it("should have a user object", function() {
        expect(scope.user).toBeDefined();
        expect(scope.user.type).toBeDefined();
    });
});