// =======================================================
// Copyright Mylife.com Inc., 2013. All rights reserved.
//
// =======================================================

package com.klark.web.configuration.springmvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

/**
 * Web application context initialization for "springmvc".
 * 
 * 
 * @author
 */
@Configuration
public class WebAppContextConfig extends WebMvcConfigurerAdapter {

    private static final String VIEWS = "/**/*.view";
    private static final String TOOLS = "/**/*.tool";
    private static final String PUBVIEWS = "/**/*.pubview";

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/*.html").addResourceLocations("/");
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
    }

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController(VIEWS);
        registry.addViewController(PUBVIEWS);
        registry.addViewController(TOOLS);
    }

    /**
     * WIP
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] allPaths = new String[] { VIEWS, PUBVIEWS, TOOLS };
        String[] userPaths = new String[] { VIEWS, PUBVIEWS };
        String[] internalPaths = new String[] { TOOLS };
    }

    @Bean
    public ViewResolver viewResolver() {
        final TilesViewResolver viewResolver = new TilesViewResolver();
        // set env attributes here?
        // viewResolver.setAttributes(props)
        return viewResolver;
    }

    /**
     * TODO: Custom Tiles initializer to disable freemarker, velocity and mustache rendereres.
     * 
     * @return
     */
    @Bean(destroyMethod = "destroy")
    public TilesConfigurer tilesConfigurer() {
        final TilesConfigurer tilesConfigurer = new TilesConfigurer();
        tilesConfigurer.setCompleteAutoload(true);
        // tilesConfigurer.afterPropertiesSet();
        return tilesConfigurer;
    }

}
