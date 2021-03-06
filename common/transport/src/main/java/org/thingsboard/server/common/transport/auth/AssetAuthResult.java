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
package org.thingsboard.server.common.transport.auth;

import org.thingsboard.server.common.data.id.AssetId;

public class AssetAuthResult {

    private final boolean success;
    private final AssetId assetId;
    private final String errorMsg;

    public static AssetAuthResult of(AssetId assetId) {
        return new AssetAuthResult(true, assetId, null);
    }

    public static AssetAuthResult of(String errorMsg) {
        return new AssetAuthResult(false, null, errorMsg);
    }

    private AssetAuthResult(boolean success, AssetId assetId, String errorMsg) {
        super();
        this.success = success;
        this.assetId = assetId;
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public AssetId getAssetId() {
        return assetId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public String toString() {
        return "AssetAuthResult [success=" + success + ", assetId=" + assetId + ", errorMsg=" + errorMsg + "]";
    }

}
