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
package org.thingsboard.server.common.transport.session;

import java.util.Optional;

import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.security.AssetCredentialsFilter;
import org.thingsboard.server.common.msg.session.SessionContext;
import org.thingsboard.server.common.transport.SessionMsgProcessor;
import org.thingsboard.server.common.transport.auth.AssetAuthResult;
import org.thingsboard.server.common.transport.auth.AssetAuthService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author
 */
@Slf4j
public abstract class AssetAwareSessionContext implements SessionContext {

    protected final AssetAuthService authService;
    protected final SessionMsgProcessor processor;

    protected volatile Asset asset;

    public AssetAwareSessionContext(SessionMsgProcessor processor, AssetAuthService authService) {
        this.processor = processor;
        this.authService = authService;
    }

    public AssetAwareSessionContext(SessionMsgProcessor processor, AssetAuthService authService, Asset asset) {
        this(processor, authService);
        this.asset = asset;
    }


    public boolean login(AssetCredentialsFilter credentials) {
        AssetAuthResult result = authService.process(credentials);
        if (result.isSuccess()) {
            Optional<Asset> assetOpt = authService.findAssetById(result.getAssetId());
            if (assetOpt.isPresent()) {
              asset = assetOpt.get();
            }
            return true;
        } else {
            log.debug("Can't find assetOpt using credentials [{}] due to {}", credentials, result.getErrorMsg());
            return false;
        }
    }

    public AssetAuthService getAuthService() {
        return authService;
    }

    public SessionMsgProcessor getProcessor() {
        return processor;
    }

    public Asset getAsset() {
        return asset;
    }
}
