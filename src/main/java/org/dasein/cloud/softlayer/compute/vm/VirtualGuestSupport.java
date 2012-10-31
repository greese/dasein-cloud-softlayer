/**
 * ========= CONFIDENTIAL =========
 *
 * Copyright (C) 2012 enStratus Networks Inc - ALL RIGHTS RESERVED
 *
 * ====================================================================
 *  NOTICE: All information contained herein is, and remains the
 *  property of enStratus Networks Inc. The intellectual and technical
 *  concepts contained herein are proprietary to enStratus Networks Inc
 *  and may be covered by U.S. and Foreign Patents, patents in process,
 *  and are protected by trade secret or copyright law. Dissemination
 *  of this information or reproduction of this material is strictly
 *  forbidden unless prior written permission is obtained from
 *  enStratus Networks Inc.
 * ====================================================================
 */
package org.dasein.cloud.softlayer.compute.vm;

import org.apache.log4j.Logger;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.ProviderContext;
import org.dasein.cloud.Requirement;
import org.dasein.cloud.Tag;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.Platform;
import org.dasein.cloud.compute.VMLaunchOptions;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VirtualMachineProduct;
import org.dasein.cloud.compute.VirtualMachineSupport;
import org.dasein.cloud.compute.VmState;
import org.dasein.cloud.compute.VmStatistics;
import org.dasein.cloud.identity.ServiceAction;
import org.dasein.cloud.softlayer.NoContextException;
import org.dasein.cloud.softlayer.SoftLayer;
import org.dasein.cloud.softlayer.SoftLayerMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.PortableServer.CurrentPackage.NoContext;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Implements support for SoftLayer virtual guests as Dasein Cloud virtual machines.
 * <p>Created by George Reese: 10/30/12 2:52 PM</p>
 * @author George Reese
 * @version 2012.09 initial version
 * @since 2012.09
 */
public class VirtualGuestSupport implements VirtualMachineSupport {
    static private final Logger logger = SoftLayer.getLogger(VirtualGuestSupport.class);

    private SoftLayer provider;

    public VirtualGuestSupport(@Nonnull SoftLayer provider) { this.provider = provider; }

    @Override
    public @Nonnull VirtualMachine clone(@Nonnull String vmId, @Nonnull String intoDcId, @Nonnull String name, @Nonnull String description, boolean powerOn, @Nullable String... firewallIds) throws InternalException, CloudException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void disableAnalytics(String vmId) throws InternalException, CloudException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enableAnalytics(String vmId) throws InternalException, CloudException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public String getConsoleOutput(@Nonnull String vmId) throws InternalException, CloudException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getMaximumVirtualMachineCount() throws CloudException, InternalException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public VirtualMachineProduct getProduct(@Nonnull String productId) throws InternalException, CloudException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public String getProviderTermForServer(@Nonnull Locale locale) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public VirtualMachine getVirtualMachine(@Nonnull String vmId) throws InternalException, CloudException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public VmStatistics getVMStatistics(String vmId, long from, long to) throws InternalException, CloudException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public Iterable<VmStatistics> getVMStatisticsForPeriod(@Nonnull String vmId, @Nonnegative long from, @Nonnegative long to) throws InternalException, CloudException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public Requirement identifyPasswordRequirement() throws CloudException, InternalException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public Requirement identifyRootVolumeRequirement() throws CloudException, InternalException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public Requirement identifyShellKeyRequirement() throws CloudException, InternalException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public Requirement identifyVlanRequirement() throws CloudException, InternalException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isAPITerminationPreventable() throws CloudException, InternalException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBasicAnalyticsSupported() throws CloudException, InternalException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isExtendedAnalyticsSupported() throws CloudException, InternalException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSubscribed() throws CloudException, InternalException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isUserDataSupported() throws CloudException, InternalException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public VirtualMachine launch(VMLaunchOptions withLaunchOptions) throws CloudException, InternalException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public VirtualMachine launch(@Nonnull String fromMachineImageId, @Nonnull VirtualMachineProduct product, @Nonnull String dataCenterId, @Nonnull String name, @Nonnull String description, @Nullable String withKeypairId, @Nullable String inVlanId, boolean withAnalytics, boolean asSandbox, @Nullable String... firewallIds) throws InternalException, CloudException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public VirtualMachine launch(@Nonnull String fromMachineImageId, @Nonnull VirtualMachineProduct product, @Nonnull String dataCenterId, @Nonnull String name, @Nonnull String description, @Nullable String withKeypairId, @Nullable String inVlanId, boolean withAnalytics, boolean asSandbox, @Nullable String[] firewallIds, @Nullable Tag... tags) throws InternalException, CloudException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public Iterable<String> listFirewalls(@Nonnull String vmId) throws InternalException, CloudException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<VirtualMachineProduct> listProducts(Architecture architecture) throws InternalException, CloudException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<Architecture> listSupportedArchitectures() throws InternalException, CloudException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public Iterable<VirtualMachine> listVirtualMachines() throws InternalException, CloudException {
        ProviderContext ctx = provider.getContext();

        if( ctx == null ) {
            throw new NoContextException();
        }
        SoftLayerMethod method = new SoftLayerMethod(provider);

        JSONArray list = method.list("SoftLayer_Account/" + ctx.getAccountNumber() + "/getVirtualGuests");
        ArrayList<VirtualMachine> vms = new ArrayList<VirtualMachine>();

        for( int i=0; i<list.length(); i++ ) {
            try {
                VirtualMachine vm = toVirtualMachine(list.getJSONObject(i));

                if( vm != null ) {
                    vms.add(vm);
                }
            }
            catch( JSONException e ) {
                logger.error("Failed to parse JSON from cloud: " + e.getMessage());
                e.printStackTrace();
                throw new CloudException(e);
            }
        }
        return vms;
    }

    private @Nullable String lookupDatacenter(@Nonnull VirtualMachine vm) throws CloudException, InternalException {
        SoftLayerMethod method = new SoftLayerMethod(provider);

        JSONObject json = method.getObject("SoftLayer_Virtual_Guest/" + vm.getProviderVirtualMachineId() + "/getDatacenter");

        if( json != null && json.has("name") ) {
            try {
                return json.getString("name");
            }
            catch( JSONException e ) {
                logger.error("Error parsing JSON from cloud: " + e.getMessage());
                e.printStackTrace();
                throw new CloudException(e);
            }
        }
        return null;
    }

    @Override
    public void pause(@Nonnull String vmId) throws InternalException, CloudException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void reboot(@Nonnull String vmId) throws CloudException, InternalException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void resume(@Nonnull String vmId) throws CloudException, InternalException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void start(@Nonnull String vmId) throws InternalException, CloudException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void stop(@Nonnull String vmId) throws InternalException, CloudException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean supportsAnalytics() throws CloudException, InternalException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean supportsPauseUnpause(@Nonnull VirtualMachine vm) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean supportsStartStop(@Nonnull VirtualMachine vm) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean supportsSuspendResume(@Nonnull VirtualMachine vm) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void suspend(@Nonnull String vmId) throws CloudException, InternalException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void terminate(@Nonnull String vmId) throws InternalException, CloudException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unpause(@Nonnull String vmId) throws CloudException, InternalException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public String[] mapServiceAction(@Nonnull ServiceAction action) {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void setImage(@Nonnull VirtualMachine vm) {
        try {
            SoftLayerMethod method = new SoftLayerMethod(provider);

            JSONObject json = method.getObject("SoftLayer_Virtual_Guest/" + vm.getProviderVirtualMachineId() + "/getOrderTemplate/HOURLY");

            if( json != null ) {
               // TODO: implement me
            }
        }
        catch( Throwable ignore ) {
            // SoftLayer throws crap for no good reason here
        }
    }

    private void setNetworking(@Nonnull VirtualMachine vm) throws CloudException, InternalException {
        SoftLayerMethod method = new SoftLayerMethod(provider);

        JSONArray nics = method.list("SoftLayer_Virtual_Guest/" + vm.getProviderVirtualMachineId() + "/getNetworkComponents");

        if( nics != null ) {
            String vlanId = null;
            int port = -1;

            for( int i=0; i<nics.length(); i++ ) {
                //{"createDate":"2012-02-25T17:35:21-06:00","guestId":658275,"id":637240,"macAddress":"06:4f:09:1a:0a:ab","maxSpeed":10,"modifyDate":"2012-02-25T17:37:22-06:00","name":"eth","networkId":508068,"port":0,"speed":10,"status":"ACTIVE","uuid":"c51f1e8e-7e59-54b9-53d0-8e4014ccb449","primaryIpAddress":"10.70.77.194"},
                //{"createDate":"2012-02-25T17:35:21-06:00","guestId":658275,"id":637239,"macAddress":"06:72:61:2b:3e:04","maxSpeed":10,"modifyDate":"2012-02-25T17:37:28-06:00","name":"eth","networkId":508069,"port":1,"speed":10,"status":"ACTIVE","uuid":"270ba206-cba4-b0d7-639e-6811137cbe61","primaryIpAddress":"37.58.65.250"}
                try {
                    JSONObject json = nics.getJSONObject(i);
                    String id;

                    if( !json.has("id") || !json.has("port") ) {
                        continue;
                    }
                    if( vlanId == null || json.getInt("port") < port ) {
                        vlanId = json.getString("id");
                    }
                }
                catch( JSONException e ) {
                    logger.error("Error parsing JSON from cloud: " + e.getMessage());
                    e.printStackTrace();
                    throw new CloudException(e);
                }
            }
            vm.setProviderVlanId(vlanId);
        }
    }

    private void setState(@Nonnull VirtualMachine vm) throws CloudException, InternalException {
        SoftLayerMethod method = new SoftLayerMethod(provider);

        JSONObject json = method.getObject("SoftLayer_Virtual_Guest/" + vm.getProviderVirtualMachineId() + "/getStatus");

        if( json == null ) {
            vm.setCurrentState(VmState.PENDING);
        }
        else if( json.has("keyName") ) {
            try {
                String status = json.getString("keyName");

                if( status == null || status.equals("") ) {
                    vm.setCurrentState(VmState.PENDING);
                }
                else if( status.equalsIgnoreCase("active") ) {
                    vm.setCurrentState(VmState.RUNNING);
                }
                else {
                    vm.setCurrentState(VmState.PENDING);
                    logger.warn("DEBUG: Unknown SoftLayer VM state: " + status);
                    System.out.println("Unknown state: " + status);
                }
            }
            catch( JSONException e ) {
                logger.error("Error parsing JSON from cloud: " + e.getMessage());
                e.printStackTrace();
                throw new CloudException(e);
            }
        }
    }

    private @Nullable VirtualMachine toVirtualMachine(@Nullable JSONObject json) throws CloudException, InternalException {
        if( json == null ) {
            return null;
        }
        ProviderContext ctx = provider.getContext();

        if( ctx == null ) {
            throw new NoContextException();
        }

        VirtualMachine vm = new VirtualMachine();

        vm.setProviderOwnerId(ctx.getAccountNumber());
        vm.setPausable(true);
        vm.setPersistent(true);
        vm.setPlatform(Platform.UNKNOWN);
        vm.setRebootable(true);
        vm.setArchitecture(Architecture.I64);

        vm.setClonable(false);
        vm.setCurrentState(VmState.PENDING);
        vm.setImagable(true);

        try {
            // "primaryBackendIpAddress":"10.70.77.194","primaryIpAddress":"37.58.65.250"}
            if( json.has("id") ) {
                vm.setProviderVirtualMachineId(json.getString("id"));
            }
            if( json.has("hostname") ) {
                vm.setName(json.getString("hostname"));
            }
            else if( json.has("fullyQualifiedDomainName") ) {
                vm.setName(json.getString("fullyQualifiedDomainName"));
            }
            if( json.has("createDate") ) {
                vm.setCreationTimestamp(provider.parseTimestamp(json.getString("createDate")));
            }
            if( json.has("primaryBackendIpAddress") ) {
                vm.setPrivateIpAddresses(new String[] { json.getString("primaryBackendIpAddress") } );
            }
            if( json.has("primaryIpAddress") ) {
                vm.setPublicIpAddresses(new String[] { json.getString("primaryIpAddress") });
            }
            int cpu = (json.has("maxCpu") ? json.getInt("maxCpu") : 1);
            int memory = (json.has("maxMemory") ? json.getInt("maxMemory") : 512);

            if( json.has("uuid") ) {
                vm.setTag("uuid", json.getString("uuid"));
            }
            if( json.has("globalIdentifier") ) {
                vm.setTag("globalIdentifier", json.getString("globalIdentifier"));
            }
        }
        catch( JSONException e ) {
            logger.error("Error parsing JSON from cloud: " + e.getMessage());
            e.printStackTrace();
            throw new CloudException(e);
        }

        if( vm.getProviderVirtualMachineId() == null ) {
            return null;
        }
        String regionId = lookupDatacenter(vm);

        if( regionId == null ) {
            return null;
        }
        if( !regionId.equals(ctx.getRegionId()) ) {
            return null;
        }
        vm.setProviderRegionId(regionId);
        vm.setProviderDataCenterId(regionId);

        setNetworking(vm);
        setState(vm);
        setImage(vm);

        //vm.setProductId(product);

        if( vm.getName() == null ) {
            vm.setName(vm.getProviderVirtualMachineId());
        }
        if( vm.getDescription() == null ) {
            vm.setDescription(vm.getName());
        }
        return vm;
    }
}
