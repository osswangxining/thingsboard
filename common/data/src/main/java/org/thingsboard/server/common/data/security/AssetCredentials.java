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
package org.thingsboard.server.common.data.security;

import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.id.AssetCredentialsId;
import org.thingsboard.server.common.data.id.AssetId;

public class AssetCredentials extends BaseData<AssetCredentialsId> implements AssetCredentialsFilter {

    private static final long serialVersionUID = -7869261127032837765L;
    
    private AssetId assetId;
    private AssetCredentialsType credentialsType;
    private String credentialsId;
    private String credentialsValue;
    
    public AssetCredentials() {
        super();
    }

    public AssetCredentials(AssetCredentialsId id) {
        super(id);
    }

    public AssetCredentials(AssetCredentials assetCredentials) {
        super(assetCredentials);
        this.assetId = assetCredentials.getAssetId();
        this.credentialsType = assetCredentials.getCredentialsType();
        this.credentialsId = assetCredentials.getCredentialsId();
        this.credentialsValue = assetCredentials.getCredentialsValue();
    }

    public AssetId getAssetId() {
        return this.assetId;
    }

    public void setAssetId(AssetId assetId) {
        this.assetId = assetId;
    }

    @Override
    public AssetCredentialsType getCredentialsType() {
        return credentialsType;
    }

    public void setCredentialsType(AssetCredentialsType credentialsType) {
        this.credentialsType = credentialsType;
    }

    @Override
    public String getCredentialsId() {
        return credentialsId;
    }

    public void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId;
    }

    public String getCredentialsValue() {
        return credentialsValue;
    }

    public void setCredentialsValue(String credentialsValue) {
        this.credentialsValue = credentialsValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((credentialsId == null) ? 0 : credentialsId.hashCode());
        result = prime * result + ((credentialsType == null) ? 0 : credentialsType.hashCode());
        result = prime * result + ((credentialsValue == null) ? 0 : credentialsValue.hashCode());
        result = prime * result + ((assetId == null) ? 0 : assetId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssetCredentials other = (AssetCredentials) obj;
        if (credentialsId == null) {
            if (other.credentialsId != null)
                return false;
        } else if (!credentialsId.equals(other.credentialsId))
            return false;
        if (credentialsType != other.credentialsType)
            return false;
        if (credentialsValue == null) {
            if (other.credentialsValue != null)
                return false;
        } else if (!credentialsValue.equals(other.credentialsValue))
            return false;
        if (assetId == null) {
            if (other.assetId != null)
                return false;
        } else if (!assetId.equals(other.assetId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AssetCredentials [assetId=" + assetId + ", credentialsType=" + credentialsType + ", credentialsId="
                + credentialsId + ", credentialsValue=" + credentialsValue + ", createdTime=" + createdTime + ", id="
                + id + "]";
    }

}
