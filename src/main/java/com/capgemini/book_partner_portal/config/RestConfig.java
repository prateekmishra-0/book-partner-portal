package com.capgemini.book_partner_portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.capgemini.book_partner_portal.projection.AuthorTitlesProjection;
import com.capgemini.book_partner_portal.projection.BookAuthorsProjection;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {

        config.getProjectionConfiguration()
            .addProjection(AuthorTitlesProjection.class)
            .addProjection(BookAuthorsProjection.class); 
    }
}