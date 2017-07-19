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
package org.thingsboard.server.extensions.api.asset;

import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.TenantId;

import lombok.Data;

/**
 * @author Andrew Shvayka
 */
@Data
public class AssetCredentialsUpdateNotificationMsg implements ToAssetActorNotificationMsg {

    /**
   * 
   */
  private static final long serialVersionUID = -1796113638158672428L;
    private final TenantId tenantId;
    private final AssetId assetId;

}
