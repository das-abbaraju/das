describe('An Employee', function() {
    var scope, $http, $httpBackend, httpMock, employee;

    var employee_info_url = '/employee-guard/employee/summary/employee-info';

    beforeEach(angular.mock.module('PICS.employeeguard'));

    beforeEach(inject(function($rootScope, $controller, $http, $httpBackend, $routeParams, EmployeeInfo) {
        employee = {
            "id": 1,
            "firstName": "John",
            "lastName": "Smith",
            "slug": "PBANDJ",
            "email": "thelennyleonard@gmail.com"
        };

        scope = $rootScope.$new();

        httpMock = $httpBackend;

        scope.employee = EmployeeInfo.get();
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
});