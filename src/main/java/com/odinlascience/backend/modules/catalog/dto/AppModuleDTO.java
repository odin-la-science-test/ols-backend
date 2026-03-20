package com.odinlascience.backend.modules.catalog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppModuleDTO {

    private String moduleKey; 
    
    private String title;
    private String icon;
    private String description;
    
    private String routePath;
    
    private Double price;
    
    private boolean locked;

    private boolean adminOnly;
}