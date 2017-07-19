package org.thingsboard.server.extensions.webhook.plugin;

import lombok.Data;
import org.thingsboard.server.extensions.core.plugin.KeyValuePluginProperties;

import java.util.List;

@Data
public class WebhookPluginConfiguration {
    private String url;

    private String authMethod;

    private String userName;
    private String password;

    private List<KeyValuePluginProperties> headers;
}
