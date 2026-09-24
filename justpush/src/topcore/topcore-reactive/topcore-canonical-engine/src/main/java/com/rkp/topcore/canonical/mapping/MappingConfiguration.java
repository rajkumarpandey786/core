package com.rkp.topcore.canonical.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MappingConfiguration {

    private String integrationId;

    private MappingDirection direction;

    private List<FieldMapping> mappings =
            new ArrayList<>();


    public String getIntegrationId() {
        return integrationId;
    }


    public void setIntegrationId(
            String integrationId) {

        this.integrationId =
                integrationId;
    }


    public MappingDirection getDirection() {
        return direction;
    }


    public void setDirection(
            MappingDirection direction) {

        this.direction =
                direction;
    }


    public List<FieldMapping> getMappings() {

        return Collections.unmodifiableList(
                mappings
        );
    }


    public void setMappings(
            List<FieldMapping> mappings) {

        this.mappings =
                mappings == null
                        ? new ArrayList<>()
                        : new ArrayList<>(mappings);
    }
}