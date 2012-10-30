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
package org.dasein.cloud.softlayer;

import org.apache.log4j.Logger;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.dc.DataCenter;
import org.dasein.cloud.dc.DataCenterServices;
import org.dasein.cloud.dc.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * Implements data center services for SoftLayer describing the different SoftLayer regions. This class maps
 * SoftLayer data centers to Dasein Cloud regions. SoftLayer regions are ignored.
 * <p>Created by George Reese: 10/25/12 7:18 PM</p>
 * @author George Reese
 * @version 2012.09 initial version
 * @since 2012.09
 */
public class SoftLayerDataCenterServices implements DataCenterServices {
    static private final Logger logger = SoftLayer.getLogger(SoftLayerDataCenterServices.class);

    private SoftLayer provider;

    SoftLayerDataCenterServices(@Nonnull SoftLayer provider) { this.provider = provider; }

    @Override
    public @Nullable DataCenter getDataCenter(@Nonnull String dataCenterId) throws InternalException, CloudException {
        for( Region region : listRegions() ) {
            for( DataCenter dc : listDataCenters(region.getProviderRegionId()) ) {
                if( dataCenterId.equals(dc.getProviderDataCenterId()) ) {
                    return dc;
                }
            }
        }
        return null;
    }

    @Override
    public @Nonnull String getProviderTermForDataCenter(@Nonnull Locale locale) {
        return "data center";
    }

    @Override
    public @Nonnull String getProviderTermForRegion(@Nonnull Locale locale) {
        return "region";
    }

    @Override
    public @Nullable Region getRegion(@Nonnull String providerRegionId) throws InternalException, CloudException {
        for( Region r : listRegions() ) {
            if( providerRegionId.equals(r.getProviderRegionId()) ) {
                return r;
            }
        }
        return null;
    }

    @Override
    public @Nonnull Collection<DataCenter> listDataCenters(@Nonnull String providerRegionId) throws InternalException, CloudException {
        Region r = getRegion(providerRegionId);

        if( r == null ) {
            throw new CloudException("No such region: " + providerRegionId);
        }
        DataCenter dc = new DataCenter();

        dc.setActive(r.isActive());
        dc.setAvailable(r.isAvailable());
        dc.setName(r.getName());
        dc.setProviderDataCenterId(providerRegionId);
        dc.setRegionId(providerRegionId);
        return Collections.singletonList(dc);
    }

    @Override
    public Collection<Region> listRegions() throws InternalException, CloudException {
        SoftLayerMethod method = new SoftLayerMethod(provider);
        JSONArray list = method.list("SoftLayer_Location/getDatacenters");

        if( list == null ) {
            throw new CloudException("No data was returned from endpoint");
        }

        ArrayList<Region> regions = new ArrayList<Region>();

        for( int i=0; i<list.length(); i++ ) {
            try {
                Region r = toRegion(list.getJSONObject(i));

                if( r != null ) {
                    regions.add(r);
                }
            }
            catch( JSONException e ) {
                logger.error("Error parsing response from cloud: " + e.getMessage());
                e.printStackTrace();
                throw new CloudException(e);
            }
        }
        return regions;
    }

    private @Nullable Region toRegion(@Nullable JSONObject json) throws CloudException, InternalException {
        if( json == null ) {
            return null;
        }
        if( !json.has("id") || !json.has("name") ) {
            return null;
        }

        Region region = new Region();

        region.setActive(true);
        region.setAvailable(true);
        region.setJurisdiction("US");

        try {
            region.setProviderRegionId(json.getString("name"));
            if( json.has("longName") ) {
                region.setName(json.getString("longName"));
            }
        }
        catch( JSONException e ) {
            logger.error("Error parsing JSON from cloud: " + e.getMessage());
            e.printStackTrace();
            throw new CloudException(e);
        }

        if( region.getName() == null ) {
            region.setName(region.getProviderRegionId());
        }
        if( region.getProviderRegionId().startsWith("sng") ) {
            region.setJurisdiction("SG");
        }
        else if( region.getProviderRegionId().startsWith("ams") ) {
            region.setJurisdiction("EU");
        }
        return region;
    }
}
