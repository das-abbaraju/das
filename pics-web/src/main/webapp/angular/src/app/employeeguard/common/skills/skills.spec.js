describe('Skill Status', function() {
    var skillConvert;

    beforeEach(angular.mock.module('PICS.skills'));

    beforeEach(inject(function(SkillStatus) {
        skillConvert = SkillStatus;
    }));

    it('should return the correct css class based on a skill status', function() {
        expect(skillConvert.getClassNameFromStatus('expired')).toEqual('danger');
        expect(skillConvert.getClassNameFromStatus('expiring')).toEqual('warning');
        expect(skillConvert.getClassNameFromStatus('pending')).toEqual('success');
        expect(skillConvert.getClassNameFromStatus('completed')).toEqual('success');
    });
});