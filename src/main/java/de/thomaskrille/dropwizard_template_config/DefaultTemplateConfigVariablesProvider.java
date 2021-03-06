package de.thomaskrille.dropwizard_template_config;

import java.util.HashMap;
import java.util.Map;

public class DefaultTemplateConfigVariablesProvider implements TemplateConfigVariablesProvider {
    private final String namespace;
    private final Map<String, String> data;

    public DefaultTemplateConfigVariablesProvider(String namespace) {
        this(namespace, new HashMap<>());
    }

    public DefaultTemplateConfigVariablesProvider(String namespace, Map<String, String> data) {
        this.namespace = namespace;
        this.data = data;
    }

    public void put(String name, String value) {
        data.put(name, value);
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public Map<String, String> getVariables() {
        return this.data;
    }
}
