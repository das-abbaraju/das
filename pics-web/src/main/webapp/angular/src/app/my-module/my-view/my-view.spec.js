describe('My View', function() {
    var $http;

    beforeEach(angular.mock.module('PICS.myModule'));

    beforeEach(inject(function(_$http_ /* other required services */ ) {
        $http = _$http_;
        /* other required services */
    }));

    it('should do something', function() {
        // test code here
    });
});