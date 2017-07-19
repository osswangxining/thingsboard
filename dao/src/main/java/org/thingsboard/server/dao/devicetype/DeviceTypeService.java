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
package org.thingsboard.server.dao.devicetype;

import java.util.List;
import java.util.Optional;

import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceType;
import org.thingsboard.server.common.data.TenantDeviceType;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.DeviceTypeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.TextPageData;
import org.thingsboard.server.common.data.page.TextPageLink;

import com.google.common.util.concurrent.ListenableFuture;

public interface DeviceTypeService {
    
    DeviceType findDeviceById(DeviceTypeId deviceId);

    ListenableFuture<DeviceType> findDeviceByIdAsync(DeviceTypeId deviceId);

    Optional<DeviceType> findDeviceByTenantIdAndName(TenantId tenantId, String name);

    DeviceType saveDevice(DeviceType device);

    DeviceType assignDeviceToCustomer(DeviceTypeId deviceId, CustomerId customerId);

    DeviceType unassignDeviceFromCustomer(DeviceTypeId deviceId);

    void deleteDevice(DeviceTypeId deviceId);

    TextPageData<DeviceType> findDevicesByTenantId(TenantId tenantId, TextPageLink pageLink);

    TextPageData<DeviceType> findDevicesByTenantIdAndType(TenantId tenantId, String type, TextPageLink pageLink);

    ListenableFuture<List<DeviceType>> findDevicesByTenantIdAndIdsAsync(TenantId tenantId, List<DeviceTypeId> deviceIds);

    void deleteDevicesByTenantId(TenantId tenantId);

    TextPageData<DeviceType> findDevicesByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, TextPageLink pageLink);

    TextPageData<DeviceType> findDevicesByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, TextPageLink pageLink);

    ListenableFuture<List<DeviceType>> findDevicesByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<DeviceTypeId> deviceIds);

    void unassignCustomerDevices(TenantId tenantId, CustomerId customerId);

    ListenableFuture<List<DeviceType>> findDevicesByQuery(DeviceTypeSearchQuery query);

    ListenableFuture<List<TenantDeviceType>> findDeviceTypesByTenantId(TenantId tenantId);

}
