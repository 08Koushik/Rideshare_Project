package com.rideshare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map the web access path (defined in FileStorageService) to the physical directory
        // The 'file:' prefix is CRITICAL for pointing to an external file system path.
        // Use the same external path as defined in FileStorageService.java (Path.get() call).
        registry.addResourceHandler("/vehicle-images/**")
                .addResourceLocations("file:/tmp/rideshare-uploads/vehicles/");
    }
}