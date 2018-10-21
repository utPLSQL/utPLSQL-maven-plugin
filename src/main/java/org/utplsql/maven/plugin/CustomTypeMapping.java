package org.utplsql.maven.plugin;

/**
 * Bean used by Maven to populate its model.
 * {@code
 * <customTypeMapping>
 *     <type>...</type>
 *     <mapping>...</mapping>
 * </customTypeMapping>
 * }
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
     * Returns the Object type.
     * 
     * @return The Object type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the Object type
     * 
     * @param type the Object type
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Returns the custom mapping value.
     * 
     * @return the custom mapping value
     */
    public String getCustomMapping() {
        return customMapping;
    }

    /**
     * Sets the custom mapping value
     * 
     * @param customMapping the custom mapping value
     */
    public void setCustomMapping(final String customMapping) {
        this.customMapping = customMapping;
    }
}
