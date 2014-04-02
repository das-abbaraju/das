describe('Skill Status', function() {
    var skillConvert;

    beforeEach(angular.mock.module('PICS.skills'));

    beforeEach(inject(function(SkillStatus) {
        skillConvert = SkillStatus;
    }));

    it('should return the correct css class based on a skill status', function() {
        expect(skillConvert.toClassName('expired')).toEqual('danger');
        expect(skillConvert.toClassName('expiring')).toEqual('warning');
        expect(skillConvert.toClassName('pending')).toEqual('success');
        expect(skillConvert.toClassName('completed')).toEqual('success');
    });
});