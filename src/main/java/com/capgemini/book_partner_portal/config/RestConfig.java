package com.capgemini.book_partner_portal.config;

import com.capgemini.book_partner_portal.projection.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {

        config.getProjectionConfiguration()
            .addProjection(AuthorTitlesProjection.class)
            .addProjection(BookAuthorsProjection.class)
                .addProjection(EmployeeDetailProjection.class)
                .addProjection(SaleDetailProjection.class)
                .addProjection(StoreSummaryProjection.class)
                .addProjection(TitleSummaryProjection.class);
    }
}