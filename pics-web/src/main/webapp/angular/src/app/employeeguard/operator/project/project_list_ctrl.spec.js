describe('An Operator Project List', function() {
    var scope;

    var projects_url = '/angular/json/operator/project_list_corp.json';

    var corporate_projects = [{
        "id": 4,
        "siteName": "BASF",
        "name": "Install new oven",
        "location": "Bob's Burgers",
        "startDate": "2012-05-01",
        "endDate": "2012-06-23"
    }];

    var operator_projects = [{
        "id": 4,
        "name": "Install new oven",
        "location": "Bob's Burgers",
        "startDate": "2012-05-01",
        "endDate": "2012-06-23"
    }];

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
            httpMock.flush();
        });

        it("should set orderByField to 'site'", function() {
            expect(scope.orderByField).toEqual('site');
        });

        it("should set operator_corp to true", function() {
            expect(scope.operator_corp).toBeTruthy();
        });
    });

    describe('for a operator user', function() {
        beforeEach(function () {
            httpMock.when('GET', projects_url).respond(operator_projects);
            httpMock.flush();
        });

        it("should have the default orderByField", function() {
            expect(scope.orderByField).not.toBeDefined();
        });

        it("should not have operator_corp value", function() {
            expect(scope.operator_corp).not.toBeDefined();
        });
    });
});