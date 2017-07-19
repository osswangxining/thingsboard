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

import static org.thingsboard.server.dao.DaoUtil.toUUIDs;
import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;
import static org.thingsboard.server.dao.service.Validator.validateId;
import static org.thingsboard.server.dao.service.Validator.validateIds;
import static org.thingsboard.server.dao.service.Validator.validatePageLink;
import static org.thingsboard.server.dao.service.Validator.validateString;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceType;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.TenantDeviceType;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.DeviceTypeId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.TextPageData;
import org.thingsboard.server.common.data.page.TextPageLink;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.common.data.security.DeviceCredentialsType;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.relation.EntitySearchDirection;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.PaginatedRemover;
import org.thingsboard.server.dao.tenant.TenantDao;

import com.google.common.base.Function;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DeviceTypeServiceImpl extends AbstractEntityService implements DeviceTypeService {

    @Autowired
    private DeviceTypeDao deviceDao;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private CustomerDao customerDao;

    @Override
    public DeviceType findDeviceById(DeviceTypeId deviceId) {
        log.trace("Executing findDeviceById [{}]", deviceId);
        validateId(deviceId, "Incorrect deviceId " + deviceId);
        return deviceDao.findById(deviceId.getId());
    }

    @Override
    public ListenableFuture<DeviceType> findDeviceByIdAsync(DeviceTypeId deviceId) {
        log.trace("Executing findDeviceById [{}]", deviceId);
        validateId(deviceId, "Incorrect deviceId " + deviceId);
        return deviceDao.findByIdAsync(deviceId.getId());
    }

    @Override
    public Optional<DeviceType> findDeviceByTenantIdAndName(TenantId tenantId, String name) {
        log.trace("Executing findDeviceByTenantIdAndName [{}][{}]", tenantId, name);
        validateId(tenantId, "Incorrect tenantId " + tenantId);
        Optional<DeviceType> deviceOpt = deviceDao.findDeviceTypeByTenantIdAndName(tenantId.getId(), name);
        if (deviceOpt.isPresent()) {
            return Optional.of(deviceOpt.get());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public DeviceType saveDevice(DeviceType device) {
        log.trace("Executing saveDevice [{}]", device);
        deviceValidator.validate(device);
        DeviceType savedDevice = deviceDao.save(device);
        if (device.getId() == null) {
        }
        return savedDevice;
    }

    @Override
    public DeviceType assignDeviceToCustomer(DeviceTypeId deviceId, CustomerId customerId) {
      DeviceType device = findDeviceById(deviceId);
        device.setCustomerId(customerId);
        return saveDevice(device);
    }

    @Override
    public DeviceType unassignDeviceFromCustomer(DeviceTypeId deviceId) {
        DeviceType device = findDeviceById(deviceId);
        device.setCustomerId(null);
        return saveDevice(device);
    }

    @Override
    public void deleteDevice(DeviceTypeId deviceId) {
        log.trace("Executing deleteDevice [{}]", deviceId);
        validateId(deviceId, "Incorrect deviceId " + deviceId);
        deleteEntityRelations(deviceId);
        deviceDao.removeById(deviceId.getId());
    }

    @Override
    public TextPageData<DeviceType> findDevicesByTenantId(TenantId tenantId, TextPageLink pageLink) {
        log.trace("Executing findDevicesByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, "Incorrect tenantId " + tenantId);
        validatePageLink(pageLink, "Incorrect page link " + pageLink);
        List<DeviceType> devices = deviceDao.findDeviceTypesByTenantId(tenantId.getId(), pageLink);
        return new TextPageData<>(devices, pageLink);
    }

    @Override
    public TextPageData<DeviceType> findDevicesByTenantIdAndType(TenantId tenantId, String type, TextPageLink pageLink) {
        log.trace("Executing findDevicesByTenantIdAndType, tenantId [{}], type [{}], pageLink [{}]", tenantId, type, pageLink);
        validateId(tenantId, "Incorrect tenantId " + tenantId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink, "Incorrect page link " + pageLink);
        List<DeviceType> devices = deviceDao.findDeviceTypesByTenantIdAndTypeName(tenantId.getId(), type, pageLink);
        return new TextPageData<>(devices, pageLink);
    }

    @Override
    public ListenableFuture<List<DeviceType>> findDevicesByTenantIdAndIdsAsync(TenantId tenantId, List<DeviceTypeId> deviceIds) {
        log.trace("Executing findDevicesByTenantIdAndIdsAsync, tenantId [{}], deviceIds [{}]", tenantId, deviceIds);
        validateId(tenantId, "Incorrect tenantId " + tenantId);
        validateIds(deviceIds, "Incorrect deviceIds " + deviceIds);
        return deviceDao.findDeviceTypesByTenantIdAndIdsAsync(tenantId.getId(), toUUIDs(deviceIds));
    }


    @Override
    public void deleteDevicesByTenantId(TenantId tenantId) {
        log.trace("Executing deleteDevicesByTenantId, tenantId [{}]", tenantId);
        validateId(tenantId, "Incorrect tenantId " + tenantId);
        tenantDevicesRemover.removeEntities(tenantId);
    }

    @Override
    public TextPageData<DeviceType> findDevicesByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, TextPageLink pageLink) {
        log.trace("Executing findDevicesByTenantIdAndCustomerId, tenantId [{}], customerId [{}], pageLink [{}]", tenantId, customerId, pageLink);
        validateId(tenantId, "Incorrect tenantId " + tenantId);
        validateId(customerId, "Incorrect customerId " + customerId);
        validatePageLink(pageLink, "Incorrect page link " + pageLink);
        List<DeviceType> devices = deviceDao.findDeviceTypesByTenantIdAndCustomerId(tenantId.getId(), customerId.getId(), pageLink);
        return new TextPageData<>(devices, pageLink);
    }

    @Override
    public TextPageData<DeviceType> findDevicesByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, TextPageLink pageLink) {
        log.trace("Executing findDevicesByTenantIdAndCustomerIdAndType, tenantId [{}], customerId [{}], type [{}], pageLink [{}]", tenantId, customerId, type, pageLink);
        validateId(tenantId, "Incorrect tenantId " + tenantId);
        validateId(customerId, "Incorrect customerId " + customerId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink, "Incorrect page link " + pageLink);
        List<DeviceType> devices =  deviceDao.findDeviceTypesByTenantIdAndCustomerIdAndTypeName(tenantId.getId(), customerId.getId(), type, pageLink);
        return new TextPageData<>(devices, pageLink);
    }

    @Override
    public ListenableFuture<List<DeviceType>> findDevicesByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<DeviceTypeId> deviceIds) {
        log.trace("Executing findDevicesByTenantIdCustomerIdAndIdsAsync, tenantId [{}], customerId [{}], deviceIds [{}]", tenantId, customerId, deviceIds);
        validateId(tenantId, "Incorrect tenantId " + tenantId);
        validateId(customerId, "Incorrect customerId " + customerId);
        validateIds(deviceIds, "Incorrect deviceIds " + deviceIds);
        return deviceDao.findDeviceTypesByTenantIdCustomerIdAndIdsAsync(tenantId.getId(),
                customerId.getId(), toUUIDs(deviceIds));
    }

    @Override
    public void unassignCustomerDevices(TenantId tenantId, CustomerId customerId) {
        log.trace("Executing unassignCustomerDevices, tenantId [{}], customerId [{}]", tenantId, customerId);
        validateId(tenantId, "Incorrect tenantId " + tenantId);
        validateId(customerId, "Incorrect customerId " + customerId);
        new CustomerDevicesUnassigner(tenantId).removeEntities(customerId);
    }

    @Override
    public ListenableFuture<List<DeviceType>> findDevicesByQuery(DeviceTypeSearchQuery query) {
        ListenableFuture<List<EntityRelation>> relations = relationService.findByQuery(query.toEntitySearchQuery());
        ListenableFuture<List<DeviceType>> devices = Futures.transform(relations, (AsyncFunction<List<EntityRelation>, List<DeviceType>>) relations1 -> {
            EntitySearchDirection direction = query.toEntitySearchQuery().getParameters().getDirection();
            List<ListenableFuture<DeviceType>> futures = new ArrayList<>();
            for (EntityRelation relation : relations1) {
                EntityId entityId = direction == EntitySearchDirection.FROM ? relation.getTo() : relation.getFrom();
                if (entityId.getEntityType() == EntityType.DEVICE) {
                    futures.add(findDeviceByIdAsync(new DeviceTypeId(entityId.getId())));
                }
            }
            return Futures.successfulAsList(futures);
        });

        devices = Futures.transform(devices, new Function<List<DeviceType>, List<DeviceType>>() {
            @Nullable
            @Override
            public List<DeviceType> apply(@Nullable List<DeviceType> deviceList) {
                return deviceList.stream().collect(Collectors.toList());
            }
        });

        return devices;
    }

    @Override
    public ListenableFuture<List<TenantDeviceType>> findDeviceTypesByTenantId(TenantId tenantId) {
        log.trace("Executing findDeviceTypesByTenantId, tenantId [{}]", tenantId);
        validateId(tenantId, "Incorrect tenantId " + tenantId);
        ListenableFuture<List<TenantDeviceType>> tenantDeviceTypeEntities = deviceDao.findTenantDeviceTypesAsync();
        ListenableFuture<List<TenantDeviceType>> tenantDeviceTypes = Futures.transform(tenantDeviceTypeEntities,
            (Function<List<TenantDeviceType>, List<TenantDeviceType>>) deviceTypeEntities -> {
                List<TenantDeviceType> deviceTypes = new ArrayList<>();
                for (TenantDeviceType deviceType : deviceTypeEntities) {
                    if (deviceType.getTenantId().equals(tenantId)) {
                        deviceTypes.add(deviceType);
                    }
                }
                deviceTypes.sort(Comparator.comparing(TenantDeviceType::getType));
                return deviceTypes;
            });
        return tenantDeviceTypes;
    }

    private DataValidator<DeviceType> deviceValidator =
            new DataValidator<DeviceType>() {

                @Override
                protected void validateCreate(DeviceType device) {
                    deviceDao.findDeviceTypeByTenantIdAndName(device.getTenantId().getId(), device.getName()).ifPresent(
                            d -> {
                                throw new DataValidationException("Device with such name already exists!");
                            }
                    );
                }

                @Override
                protected void validateUpdate(DeviceType device) {
                    deviceDao.findDeviceTypeByTenantIdAndName(device.getTenantId().getId(), device.getName()).ifPresent(
                            d -> {
                                if (!d.getUuidId().equals(device.getUuidId())) {
                                    throw new DataValidationException("Device with such name already exists!");
                                }
                            }
                    );
                }

                @Override
                protected void validateDataImpl(DeviceType device) {
                    if (StringUtils.isEmpty(device.getName())) {
                        throw new DataValidationException("Device name should be specified!");
                    }
                    if (device.getTenantId() == null) {
                        throw new DataValidationException("Device should be assigned to tenant!");
                    } else {
                        Tenant tenant = tenantDao.findById(device.getTenantId().getId());
                        if (tenant == null) {
                            throw new DataValidationException("Device is referencing to non-existent tenant!");
                        }
                    }
                    if (device.getCustomerId() == null) {
                        device.setCustomerId(new CustomerId(NULL_UUID));
                    } else if (!device.getCustomerId().getId().equals(NULL_UUID)) {
                        Customer customer = customerDao.findById(device.getCustomerId().getId());
                        if (customer == null) {
                            throw new DataValidationException("Can't assign device to non-existent customer!");
                        }
                        if (!customer.getTenantId().getId().equals(device.getTenantId().getId())) {
                            throw new DataValidationException("Can't assign device to customer from different tenant!");
                        }
                    }
                }
            };

    private PaginatedRemover<TenantId, DeviceType> tenantDevicesRemover =
            new PaginatedRemover<TenantId, DeviceType>() {

                @Override
                protected List<DeviceType> findEntities(TenantId id, TextPageLink pageLink) {
                    return deviceDao.findDeviceTypesByTenantId(id.getId(), pageLink);
                }

                @Override
                protected void removeEntity(DeviceType entity) {
                    deleteDevice(new DeviceTypeId(entity.getUuidId()));
                }
            };

    private class CustomerDevicesUnassigner extends PaginatedRemover<CustomerId, DeviceType> {

        private TenantId tenantId;

        CustomerDevicesUnassigner(TenantId tenantId) {
            this.tenantId = tenantId;
        }

        @Override
        protected List<DeviceType> findEntities(CustomerId id, TextPageLink pageLink) {
            return deviceDao.findDeviceTypesByTenantIdAndCustomerId(tenantId.getId(), id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(DeviceType entity) {
            unassignDeviceFromCustomer(new DeviceTypeId(entity.getUuidId()));
        }

    }
}
