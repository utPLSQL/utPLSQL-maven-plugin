package org.utplsql.model;

import org.utplsql.helper.ReporterDefault;

public class ReporterParameter {
    
    private ReporterDefault id;

    private ReporterConfiguration configuration;

    public ReporterDefault getId() {
        return id;
    }

    public void setId(ReporterDefault id) {
        this.id = id;
    }

    public ReporterConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ReporterConfiguration configuration) {
        this.configuration = configuration;
    }
    

}
