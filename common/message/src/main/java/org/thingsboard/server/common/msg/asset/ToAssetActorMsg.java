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
package org.thingsboard.server.common.msg.asset;

import java.io.Serializable;
import java.util.Optional;

import org.thingsboard.server.common.data.id.SessionId;
import org.thingsboard.server.common.msg.aware.AssetAwareMsg;
import org.thingsboard.server.common.msg.aware.CustomerAwareMsg;
import org.thingsboard.server.common.msg.aware.TenantAwareMsg;
import org.thingsboard.server.common.msg.cluster.ServerAddress;
import org.thingsboard.server.common.msg.session.FromDeviceMsg;
import org.thingsboard.server.common.msg.session.SessionType;

public interface ToAssetActorMsg extends AssetAwareMsg, CustomerAwareMsg, TenantAwareMsg, Serializable {

    SessionId getSessionId();

    SessionType getSessionType();

    Optional<ServerAddress> getServerAddress();
    
    FromDeviceMsg getPayload();

    ToAssetActorMsg toOtherAddress(ServerAddress otherAddress);
}
