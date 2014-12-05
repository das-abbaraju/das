describe('gender filter', function() {

    var genderFilter = null;

    // Require our Angular 'util' module.
    beforeEach(module('util'));

    // Invoke the Angular injector and ask it to inject the $filter service.
    // Then, use $filter to retrieve the gender filter.
    beforeEach(inject(function($filter) {
        genderFilter = $filter('gender');
    }));

    it('should be defined', function() {
        expect(genderFilter).toNotBe(undefined);
    });

    it('should convert an "M" into "male"', function() {
        expect(genderFilter("M")).toBe("male")
    });

    it('should convert an "F" into "female"', function() {
        expect(genderFilter("F")).toBe("female")
    });

    it('should convert something unknown into "unknown"', function() {
        expect(genderFilter("X")).toBe("unknown")
    });
});