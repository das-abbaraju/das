// describe('Address Confirmation Controller', function () {
//     var scope, $http, httpMock, result;

//     beforeEach(angular.mock.module('PICS.registration'));
//     beforeEach(inject(function($rootScope, $controller, $http, $httpBackend, $routeParams) {
       
//         $httpBackend.when('GET', '/address').respond({
//             formatted: '17701 Cowan STE 100\nIrvine, CA 92614-6061\nUnited States'
//         });

//         scope = $rootScope.$new();
        

//         $controller("addressConfirmation", {
//             $scope: scope
//         });
        
//         httpMock = $httpBackend;
        
//     }));
//     it('should convert an address string to an array', function () {
//         httpMock.flush();
//     });
// });