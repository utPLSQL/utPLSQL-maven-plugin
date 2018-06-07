package org.utplsql.maven.plugin;

/**
 * Bean used by maven to populate its model.
 * 
 * <customTypeMapping>
 *      <type>...</type>
 *      <mapping>...</mapping>
 * </customTypeMapping>
 *
 */
public class CustomTypeMapping {
    /**
     * Object type.
     */
    private String type;
    
    /**
     * Custom mapping value.
     */
    private String customMapping;

    /**
     * @return The Object type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The Object type.
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * @return Custom mapping value.
     */
    public String getCustomMapping() {
        return customMapping;
    }

    /**
     * @param mapping Custom mapping value.
     */
    public void setCustomMapping(final String customMapping) {
        this.customMapping = customMapping;
    }
}
