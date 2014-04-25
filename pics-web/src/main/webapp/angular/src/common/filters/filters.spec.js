describe('A filters module', function() {
    var filter;

    beforeEach(angular.mock.module('PICS.filters'));

    beforeEach(inject(function($filter) {
        filter = $filter;
    }));

    describe('duplicate filter', function() {
        it("should filter out duplicate array values", function() {
            var test_array = [0, 2, 3, 3],
                formattedArray = filter('removeDuplicateItemsFromArray')(test_array);

            expect(formattedArray).toEqual([0, 2, 3]);
        });

        it("should filter out duplicate objects", function() {
            var test_array = [
                {
                     "id":5,
                     "name":"Volcano Base Skill",
                },
                {
                     "id":6,
                     "name":"Spectre Site Required Skill 1",
                },
                {
                     "id":5,
                     "name":"Volcano Base Skill",
                }],
                formattedArray = filter('removeDuplicateItemsFromArray')(test_array);

            var expectedValue = [
                {
                     "id":5,
                     "name":"Volcano Base Skill",
                },
                {
                     "id":6,
                     "name":"Spectre Site Required Skill 1",
                }
            ];

            expect(formattedArray).toEqual(expectedValue);
        });
    });

    describe('url formatter', function() {
        it("should replace spaces with hyphens", function() {
            var url = 'BASF Corporate',
                formattedUrl = filter('removeInvalidCharactersFromUrl')(url);

            expect(formattedUrl).toEqual('basf-corporate');
        });

        it("should return a lowercase url", function() {
            var url = 'SPECTRE',
                formattedUrl = filter('removeInvalidCharactersFromUrl')(url);

            expect(formattedUrl).toEqual('spectre');
        });
    });
});