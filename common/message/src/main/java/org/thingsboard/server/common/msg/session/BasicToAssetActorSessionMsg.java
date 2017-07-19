/**
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.msg.session;

import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.SessionId;
import org.thingsboard.server.common.data.id.TenantId;

public class BasicToAssetActorSessionMsg implements ToAssetActorSessionMsg {

    private final TenantId tenantId;
    private final CustomerId customerId;
    private final AssetId assetId;
    private final AdaptorToSessionActorMsg msg;

    public BasicToAssetActorSessionMsg(Asset asset, AdaptorToSessionActorMsg msg) {
        super();
        this.tenantId = asset.getTenantId();
        this.customerId = asset.getCustomerId();
        this.assetId = asset.getId();
        this.msg = msg;
    }

    public BasicToAssetActorSessionMsg(ToAssetActorSessionMsg assetMsg) {
        this.tenantId = assetMsg.getTenantId();
        this.customerId = assetMsg.getCustomerId();
        this.assetId = assetMsg.getAssetId();
        this.msg = assetMsg.getSessionMsg();
    }

    @Override
    public AssetId getAssetId() {
        return assetId;
    }

    @Override
    public CustomerId getCustomerId() {
        return customerId;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    @Override
    public SessionId getSessionId() {
        return msg.getSessionId();
    }

    @Override
    public AdaptorToSessionActorMsg getSessionMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "BasicToAssetActorSessionMsg [tenantId=" + tenantId + ", customerId=" + customerId + ", assetId=" + assetId + ", msg=" + msg
                + "]";
    }

}
