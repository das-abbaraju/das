describe('An Operator Project List', function() {
    var scope;

    var projects_url = '/employee-guard/operators/projects/list';
    var whoami_url = '/employee-guard/who-am-i';

    var corporate_projects = [{
        "id": 4,
        "site": "BASF",
        "name": "Install new oven",
        "location": "Bob's Burgers",
        "startDate": "2012-05-01",
        "endDate": "2012-06-23"
    }];

    var corporate_user = {
       "userId":116679,
       "accountId":55653,
       "name":"Lydia Rodarte-Quayle",
       "type":"CORPORATE"
    };

    var operator_projects = [{
        "id": 4,
        "name": "Install new oven",
        "location": "Bob's Burgers",
        "startDate": "2012-05-01",
        "endDate": "2012-06-23"
    }];

    var operator_user = {
        "userId":116680,
        "accountId":55654,
        "name":"EmployeeGUARD User",
        "type":"OPERATOR"
    };

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $httpBackend, ProjectList) {
        scope = $rootScope.$new();
        $controller("operatorProjectListCtrl", {
            $scope: scope
        });

        httpMock = $httpBackend;
        mockResource = ProjectList;
    }));

    describe('for a corporate user', function() {
        beforeEach(function () {
            httpMock.when('GET', projects_url).respond(corporate_projects);
            httpMock.when('GET', whoami_url).respond(corporate_user);
            httpMock.flush();
        });

        it("should set orderByField to 'site'", function() {
            expect(scope.orderByField).toEqual('site');
        });
    });

    describe('for a operator user', function() {
        beforeEach(function () {
            httpMock.when('GET', projects_url).respond(operator_projects);
            httpMock.when('GET', whoami_url).respond(operator_user);
            httpMock.flush();
        });

        it("should have the default orderByField", function() {
            expect(scope.orderByField).not.toBeDefined();
        });
    });
});