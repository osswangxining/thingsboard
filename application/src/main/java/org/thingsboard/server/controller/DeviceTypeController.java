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
package org.thingsboard.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.DeviceType;
import org.thingsboard.server.common.data.TenantDeviceType;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceTypeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.TextPageData;
import org.thingsboard.server.common.data.page.TextPageLink;
import org.thingsboard.server.dao.devicetype.DeviceTypeSearchQuery;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.exception.ThingsboardException;
import org.thingsboard.server.service.security.model.SecurityUser;

import com.google.common.util.concurrent.ListenableFuture;

@RestController
@RequestMapping("/api")
public class DeviceTypeController extends BaseController {

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/devicetype/{devicetypeId}", method = RequestMethod.GET)
    @ResponseBody
    public DeviceType getDeviceTypeById(@PathVariable("devicetypeId") String strDeviceId) throws ThingsboardException {
        checkParameter("devicetypeId", strDeviceId);
        try {
          DeviceTypeId deviceId = new DeviceTypeId(toUUID(strDeviceId));
            return checkDeviceTypeId(deviceId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/devicetype", method = RequestMethod.POST)
    @ResponseBody
    public DeviceType saveDevice(@RequestBody DeviceType device) throws ThingsboardException {
        try {
            device.setTenantId(getCurrentUser().getTenantId());
            DeviceType savedDevice = checkNotNull(deviceTypeService.saveDevice(device));
            //@ TODO
//            actorService
//                    .onDeviceNameOrTypeUpdate(
//                            savedDevice.getTenantId(),
//                            savedDevice.getId(),
//                            savedDevice.getName(),
//                            savedDevice.getType());
            return savedDevice;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/devicetype/{devicetypeId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteDevice(@PathVariable("devicetypeId") String strDeviceId) throws ThingsboardException {
        checkParameter("devicetypeId", strDeviceId);
        try {
            DeviceTypeId deviceId = new DeviceTypeId(toUUID(strDeviceId));
            checkDeviceTypeId(deviceId);
            deviceTypeService.deleteDevice(deviceId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/{customerId}/devicetype/{devicetypeId}", method = RequestMethod.POST)
    @ResponseBody
    public DeviceType assignDeviceToCustomer(@PathVariable("customerId") String strCustomerId,
                                         @PathVariable("devicetype") String strDeviceId) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        checkParameter("devicetype", strDeviceId);
        try {
            CustomerId customerId = new CustomerId(toUUID(strCustomerId));
            checkCustomerId(customerId);

            DeviceTypeId deviceId = new DeviceTypeId(toUUID(strDeviceId));
            checkDeviceTypeId(deviceId);

            return checkNotNull(deviceTypeService.assignDeviceToCustomer(deviceId, customerId));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/devicetype/{devicetypeId}", method = RequestMethod.DELETE)
    @ResponseBody
    public DeviceType unassignDeviceFromCustomer(@PathVariable("devicetypeId") String strDeviceId) throws ThingsboardException {
        checkParameter("devicetypeId", strDeviceId);
        try {
          DeviceTypeId deviceId = new DeviceTypeId(toUUID(strDeviceId));
          DeviceType device = checkDeviceTypeId(deviceId);
            if (device.getCustomerId() == null || device.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
                throw new IncorrectParameterException("Device isn't assigned to any customer!");
            }
            return checkNotNull(deviceTypeService.unassignDeviceFromCustomer(deviceId));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/public/devicetype/{devicetypeId}", method = RequestMethod.POST)
    @ResponseBody
    public DeviceType assignDeviceToPublicCustomer(@PathVariable("devicetypeId") String strDeviceId) throws ThingsboardException {
        checkParameter("devicetypeId", strDeviceId);
        try {
          DeviceTypeId deviceId = new DeviceTypeId(toUUID(strDeviceId));
          DeviceType device = checkDeviceTypeId(deviceId);
            Customer publicCustomer = customerService.findOrCreatePublicCustomer(device.getTenantId());
            return checkNotNull(deviceTypeService.assignDeviceToCustomer(deviceId, publicCustomer.getId()));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/devicetypes", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<DeviceType> getTenantDevices(
            @RequestParam int limit,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws ThingsboardException {
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            if (type != null && type.trim().length() > 0) {
                return checkNotNull(deviceTypeService.findDevicesByTenantIdAndType(tenantId, type, pageLink));
            } else {
                return checkNotNull(deviceTypeService.findDevicesByTenantId(tenantId, pageLink));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/devicetypes", params = {"deviceName"}, method = RequestMethod.GET)
    @ResponseBody
    public DeviceType getTenantDevice(
            @RequestParam String deviceName) throws ThingsboardException {
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            return checkNotNull(deviceTypeService.findDeviceByTenantIdAndName(tenantId, deviceName));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/customer/{customerId}/devicetypes", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<DeviceType> getCustomerDevices(
            @PathVariable("customerId") String strCustomerId,
            @RequestParam int limit,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            CustomerId customerId = new CustomerId(toUUID(strCustomerId));
            checkCustomerId(customerId);
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            if (type != null && type.trim().length() > 0) {
                return checkNotNull(deviceTypeService.findDevicesByTenantIdAndCustomerIdAndType(tenantId, customerId, type, pageLink));
            } else {
                return checkNotNull(deviceTypeService.findDevicesByTenantIdAndCustomerId(tenantId, customerId, pageLink));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/devicetypes", params = {"devicetypeIds"}, method = RequestMethod.GET)
    @ResponseBody
    public List<DeviceType> getDevicesByIds(
            @RequestParam("devicetypeIds") String[] strDeviceIds) throws ThingsboardException {
        checkArrayParameter("devicetypeIds", strDeviceIds);
        try {
            SecurityUser user = getCurrentUser();
            TenantId tenantId = user.getTenantId();
            CustomerId customerId = user.getCustomerId();
            List<DeviceTypeId> deviceIds = new ArrayList<>();
            for (String strDeviceId : strDeviceIds) {
                deviceIds.add(new DeviceTypeId(toUUID(strDeviceId)));
            }
            ListenableFuture<List<DeviceType>> devices;
            if (customerId == null || customerId.isNullUid()) {
                devices = deviceTypeService.findDevicesByTenantIdAndIdsAsync(tenantId, deviceIds);
            } else {
                devices = deviceTypeService.findDevicesByTenantIdCustomerIdAndIdsAsync(tenantId, customerId, deviceIds);
            }
            return checkNotNull(devices.get());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/devicetypes", method = RequestMethod.POST)
    @ResponseBody
    public List<DeviceType> findByQuery(@RequestBody DeviceTypeSearchQuery query) throws ThingsboardException {
        checkNotNull(query);
        checkNotNull(query.getParameters());
        checkNotNull(query.getDeviceTypes());
        checkEntityId(query.getParameters().getEntityId());
        try {
            List<DeviceType> devices = checkNotNull(deviceTypeService.findDevicesByQuery(query).get());
            devices = devices.stream().filter(device -> {
                try {
                    checkDeviceType(device);
                    return true;
                } catch (ThingsboardException e) {
                    return false;
                }
            }).collect(Collectors.toList());
            return devices;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

//    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
//    @RequestMapping(value = "/devicetype/types", method = RequestMethod.GET)
//    @ResponseBody
//    public List<TenantDeviceType> getDeviceTypes() throws ThingsboardException {
//        try {
//            SecurityUser user = getCurrentUser();
//            TenantId tenantId = user.getTenantId();
//            ListenableFuture<List<TenantDeviceType>> deviceTypes = deviceService.findDeviceTypesByTenantId(tenantId);
//            return checkNotNull(deviceTypes.get());
//        } catch (Exception e) {
//            throw handleException(e);
//        }
//    }

}
