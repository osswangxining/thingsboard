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
package org.thingsboard.server.service.security.asset;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.security.AssetCredentials;
import org.thingsboard.server.common.data.security.AssetCredentialsFilter;
import org.thingsboard.server.common.transport.auth.AssetAuthResult;
import org.thingsboard.server.common.transport.auth.AssetAuthService;
import org.thingsboard.server.dao.asset.AssetCredentialsService;
import org.thingsboard.server.dao.asset.AssetService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultAssetAuthService implements AssetAuthService {

    @Autowired
    AssetService assetService;

    @Autowired
    AssetCredentialsService assetCredentialsService;

    @Override
    public AssetAuthResult process(AssetCredentialsFilter credentialsFilter) {
        log.trace("Lookup asset credentials using filter {}", credentialsFilter);
        AssetCredentials credentials = assetCredentialsService.findAssetCredentialsByCredentialsId(credentialsFilter.getCredentialsId());
        if (credentials != null) {
            log.trace("Credentials found {}", credentials);
            if (credentials.getCredentialsType() == credentialsFilter.getCredentialsType()) {
                switch (credentials.getCredentialsType()) {
                    case ACCESS_TOKEN:
                        // Credentials ID matches Credentials value in this
                        // primitive case;
                        return AssetAuthResult.of(credentials.getAssetId());
                    default:
                        return AssetAuthResult.of("Credentials Type is not supported yet!");
                }
            } else {
                return AssetAuthResult.of("Credentials Type mismatch!");
            }
        } else {
            log.trace("Credentials not found!");
            return AssetAuthResult.of("Credentials Not Found!");
        }
    }

    @Override
    public Optional<Asset> findAssetById(AssetId assetId) {
        return Optional.ofNullable(assetService.findAssetById(assetId));
    }
}
