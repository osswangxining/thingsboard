package org.thingsboard.server.extensions.webhook.action;

import lombok.Data;
import org.thingsboard.server.extensions.core.action.template.TemplateActionConfiguration;

@Data
public class WebhookPluginActionConfiguration implements TemplateActionConfiguration {
  private String actionPath;
  private String requestMethod;
  private String contentType;
  private String template;
  private boolean sync;
  private int expectedResultCode;
}
