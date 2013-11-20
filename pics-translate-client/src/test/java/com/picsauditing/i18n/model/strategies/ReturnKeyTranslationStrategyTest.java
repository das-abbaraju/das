package com.picsauditing.i18n.model.strategies;

import com.picsauditing.i18n.model.TranslationQualityRating;
import com.picsauditing.i18n.model.TranslationWrapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ReturnKeyTranslationStrategyTest {
    private static final String testKey = "Test.Key";
    private static final String testTranslation= "Test Translation";
    private ReturnKeyTranslationStrategy returnKeyTranslationStrategy;

    @Mock
    private TranslationWrapper translationWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        returnKeyTranslationStrategy = new ReturnKeyTranslationStrategy();
    }

    @Test
    public void testTransformTranslation_NullTranslationSetsKeyAsValue() throws Exception {
        when(translationWrapper.getKey()).thenReturn(testKey);
        when(translationWrapper.getTranslation()).thenReturn(null);

        TranslationWrapper translation = returnKeyTranslationStrategy.transformTranslation(translationWrapper);

        assertTrue(testKey.equals(translation.getTranslation()));
    }

    @Test
    public void testTransformTranslation_BadQualitynSetsKeyAsValue() throws Exception {
        when(translationWrapper.getKey()).thenReturn(testKey);
        when(translationWrapper.getTranslation()).thenReturn(testTranslation);
        when(translationWrapper.getQualityRating()).thenReturn(TranslationQualityRating.Bad);

        TranslationWrapper translation = returnKeyTranslationStrategy.transformTranslation(translationWrapper);

        assertTrue(testKey.equals(translation.getTranslation()));
    }

    @Test
    public void testTransformTranslation_EmptyStringTranslationSetsKeyAsValue() throws Exception {
        when(translationWrapper.getKey()).thenReturn(testKey);
        when(translationWrapper.getTranslation()).thenReturn("");

        TranslationWrapper translation = returnKeyTranslationStrategy.transformTranslation(translationWrapper);

        assertTrue(testKey.equals(translation.getTranslation()));
    }

    @Test
    public void testTransformTranslation_NonEmptyTranslationLeavesTranslationUnchanged() throws Exception {
        when(translationWrapper.getKey()).thenReturn(testKey);
        when(translationWrapper.getTranslation()).thenReturn(testTranslation);

        TranslationWrapper translation = returnKeyTranslationStrategy.transformTranslation(translationWrapper);

        assertTrue(testKey.equals(translation.getKey()));
        assertTrue(testTranslation.equals(translation.getTranslation()));
    }

}
