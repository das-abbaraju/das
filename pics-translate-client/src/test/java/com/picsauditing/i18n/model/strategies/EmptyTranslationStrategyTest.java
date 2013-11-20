package com.picsauditing.i18n.model.strategies;

import com.picsauditing.i18n.model.TranslationQualityRating;
import com.picsauditing.i18n.model.TranslationWrapper;
import com.picsauditing.i18n.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class EmptyTranslationStrategyTest {
    private static final String testKey = "Test.Key";
    private static final String testTranslation= "Test Translation";

    private EmptyTranslationStrategy emptyTranslationStrategy;

    @Mock
    private TranslationWrapper translationWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        emptyTranslationStrategy = new EmptyTranslationStrategy();
    }

    @Test
    public void testTransformTranslation_EmptyStringTranslationReturnsEmptyValue() throws Exception {
        when(translationWrapper.getKey()).thenReturn(testKey);
        when(translationWrapper.getTranslation()).thenReturn("");

        TranslationWrapper translation = emptyTranslationStrategy.transformTranslation(translationWrapper);

        assertTrue(Strings.EMPTY_STRING.equals(translation.getTranslation()));
    }

    @Test
    public void testTransformTranslation_BadTranslationReturnsEmptyValue() throws Exception {
        when(translationWrapper.getKey()).thenReturn(testKey);
        when(translationWrapper.getTranslation()).thenReturn("Bad Translation");
        when(translationWrapper.getQualityRating()).thenReturn(TranslationQualityRating.Bad);

        TranslationWrapper translation = emptyTranslationStrategy.transformTranslation(translationWrapper);

        assertTrue(Strings.EMPTY_STRING.equals(translation.getTranslation()));
    }

    @Test
    public void testTransformTranslation_NullStringTranslationReturnsEmptyValue() throws Exception {
        when(translationWrapper.getKey()).thenReturn(testKey);
        when(translationWrapper.getTranslation()).thenReturn(null);

        TranslationWrapper translation = emptyTranslationStrategy.transformTranslation(translationWrapper);

        assertTrue(Strings.EMPTY_STRING.equals(translation.getTranslation()));
    }

    @Test
    public void testTransformTranslation_KeyEqualsTranslationReturnsEmptyValue() throws Exception {
        when(translationWrapper.getKey()).thenReturn(testKey);
        when(translationWrapper.getTranslation()).thenReturn(testKey);

        TranslationWrapper translation = emptyTranslationStrategy.transformTranslation(translationWrapper);

        assertTrue(Strings.EMPTY_STRING.equals(translation.getTranslation()));
    }

    @Test
    public void testTransformTranslation_NonEmptyTranslationLeavesTranslationUnchanged() throws Exception {
        when(translationWrapper.getKey()).thenReturn(testKey);
        when(translationWrapper.getTranslation()).thenReturn(testTranslation);

        TranslationWrapper translation = emptyTranslationStrategy.transformTranslation(translationWrapper);

        assertTrue(testKey.equals(translation.getKey()));
        assertTrue(testTranslation.equals(translation.getTranslation()));
    }


}
