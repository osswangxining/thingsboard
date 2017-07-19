package org.thingsboard.server.extensions.webhook.plugin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.thingsboard.server.extensions.api.component.Plugin;
import org.thingsboard.server.extensions.api.plugins.AbstractPlugin;
import org.thingsboard.server.extensions.api.plugins.PluginContext;
import org.thingsboard.server.extensions.api.plugins.handlers.RuleMsgHandler;
import org.thingsboard.server.extensions.webhook.action.WebhookPluginAction;

import java.util.Base64;

@Plugin(name = "Webhook Plugin", actions = {WebhookPluginAction.class},
        descriptor = "WebhookPluginDescriptor.json", configuration = WebhookPluginConfiguration.class)
@Slf4j
public class WebhookPlugin extends AbstractPlugin<WebhookPluginConfiguration> {

    private static final String BASIC_AUTH_METHOD = "BASIC_AUTH";
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORIZATION_HEADER_FORMAT = "Basic %s";
    private static final String CREDENTIALS_TEMPLATE = "%s:%s";
//    private static final String BASE_URL_TEMPLATE = "http://%s:%d%s";
    private WebhookMsgHandler handler;
    private String baseUrl;
    private HttpHeaders headers = new HttpHeaders();

    @Override
    public void init(WebhookPluginConfiguration configuration) {
        this.baseUrl = configuration.getUrl();

        if (configuration.getAuthMethod().equals(BASIC_AUTH_METHOD)) {
            String userName = configuration.getUserName();
            String password = configuration.getPassword();
            String credentials = String.format(CREDENTIALS_TEMPLATE, userName, password);
            byte[] token = Base64.getEncoder().encode(credentials.getBytes());
            this.headers.add(AUTHORIZATION_HEADER_NAME, String.format(AUTHORIZATION_HEADER_FORMAT, new String(token)));
        }

        if (configuration.getHeaders() != null) {
            configuration.getHeaders().forEach(h -> {
                log.debug("Adding header to request object. Key = {}, Value = {}", h.getKey(), h.getValue());
                this.headers.add(h.getKey(), h.getValue());
            });
        }

        init();
    }

    private void init() {
        this.handler = new WebhookMsgHandler(baseUrl, headers);
    }

    @Override
    protected RuleMsgHandler getRuleMsgHandler() {
        return handler;
    }

    @Override
    public void resume(PluginContext ctx) {
        init();
    }

    @Override
    public void suspend(PluginContext ctx) {
        log.debug("Suspend method was called, but no impl provided!");
    }

    @Override
    public void stop(PluginContext ctx) {
        log.debug("Stop method was called, but no impl provided!");
    }
}
