describe('An Employee', function() {
    var scope, $http, $httpBackend, httpMock, employee;

    var employee_info_url = '/angular/json/employee/employee-info.json';

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $http, $httpBackend, $routeParams) {
        employee = {
            "id": 1,
            "firstName": "John",
            "lastName": "Smith",
            "slug": "PBANDJ",
            "email": "thelennyleonard@gmail.com"
        };

        var assignments = {
            "sites": [{
                "name": "BASF Houston Texas",
                "roles": 2,
                "status": "Expired"
            }],
            "projects": [{
                "name": "Dynamic Reporting",
                "status": "Expired",
                "site": "BASF Houston Texas"
            }, {
                "name": "Ninja Dojo",
                "status": "Expiring",
                "site": "BASF Houston Texas"
            }, {
                "name": "BP Oil",
                "status": "Complete",
                "site": "BASF Raleigh"
            }],
            "employers": [{
                "name": "PICS",
                "status": "Expired",
                "groups": 1
            }]
        };

        //Backend definition common for all tests
        $httpBackend.when('GET', '/angular/json/employee/assignments.json').respond(assignments);

        scope = $rootScope.$new();
        $controller("employeeDashboardCtrl", {
            $scope: scope
        });

        httpMock = $httpBackend;
    }));

    afterEach(function() {
        httpMock.verifyNoOutstandingExpectation();
        httpMock.verifyNoOutstandingRequest();
    });

    it('should have an id', function() {
        httpMock.when('GET', employee_info_url).respond(employee);
        httpMock.flush();
        expect(scope.employee.id).toBeDefined();
    });

    it('should have a slug', function() {
        httpMock.when('GET', employee_info_url).respond(employee);
        httpMock.flush();
        expect(scope.employee.slug).toBeDefined();
    });

    it('should use the email as a slug if no one defined', function() {
        var employee = {
            "id": 1,
            "firstName": "John",
            "lastName": "Smith",
            "email": "thelennyleonard@gmail.com"
        };

        httpMock.when('GET', employee_info_url).respond(employee);
        httpMock.flush();

        expect(scope.employee.slug).toBeDefined();
        expect(scope.employee.slug).toEqual(scope.employee.email);
    });
});