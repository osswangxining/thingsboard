package org.thingsboard.server.extensions.webhook.action;

import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.extensions.api.plugins.msg.AbstractRuleToPluginMsg;

public class WebhookActionMsg extends AbstractRuleToPluginMsg<WebhookActionPayload> {

    public WebhookActionMsg(TenantId tenantId, CustomerId customerId, DeviceId deviceId, WebhookActionPayload payload) {
        super(tenantId, customerId, deviceId, payload);
    }
}
