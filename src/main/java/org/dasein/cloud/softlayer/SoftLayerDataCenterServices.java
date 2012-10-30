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
package org.dasein.cloud.cloudsigma;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.ProviderContext;
import org.dasein.cloud.dc.DataCenter;
import org.dasein.cloud.dc.DataCenterServices;
import org.dasein.cloud.dc.Region;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * Implements data center services for SoftLayer describing the different SoftLayer regions.
 * <p>Created by George Reese: 10/25/12 7:18 PM</p>
 * @author George Reese
 * @version 2012.09 initial version
 * @since 2012.09
 */
public class SoftLayerDataCenterServices implements DataCenterServices {
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
            return Collections.emptyList();
        }
        DataCenter dc = new DataCenter();

        dc.setActive(r.isActive());
        dc.setAvailable(r.isAvailable());
        if( providerRegionId.equals("eu-ch1") ) {
            dc.setActive(true);
            dc.setAvailable(true);
            dc.setName("Zurich");
            dc.setProviderDataCenterId(providerRegionId+ "-a");
            dc.setRegionId(providerRegionId);
        }
        else if( providerRegionId.equals("us-nv1") ) {
            dc.setActive(true);
            dc.setAvailable(true);
            dc.setName("Las Vegas");
            dc.setProviderDataCenterId(providerRegionId+ "-a");
            dc.setRegionId(providerRegionId);
        }
        return Collections.singletonList(dc);
    }

    @Override
    public Collection<Region> listRegions() throws InternalException, CloudException {
        ProviderContext ctx = provider.getContext();

        if( ctx == null ) {
            throw new CloudException("No context was defined for this request");
        }
        String endpoint = ctx.getEndpoint();
        Region region = new Region();
        URI uri;

        try {
            if( endpoint == null || endpoint.trim().equals("") || endpoint.contains("api.cloudsigma.com") ) {
                uri = new URI("https://api.zrh.cloudsigma.com");
            }
            else {
                uri = new URI(endpoint);
            }
        }
        catch( URISyntaxException e ) {
            throw new CloudException("Unknown region endpoint: " + endpoint);
        }
        if( uri.getHost().equals("api.zrh.cloudsigma.com") ) {
            region.setActive(true);
            region.setAvailable(true);
            region.setName("Switzerland 1");
            region.setProviderRegionId("eu-ch1");
            region.setJurisdiction("CH");
        }
        else if( uri.getHost().equals("api.lvs.cloudsigma.com") ) {
            region.setActive(true);
            region.setAvailable(true);
            region.setName("Nevada 1");
            region.setProviderRegionId("us-nv1");
            region.setJurisdiction("US");
        }
        else {
            String[] parts = uri.getHost().split("\\.");

            if( parts.length == 4 && parts[0].equals("api") && parts[2].equals("cloudsigma") && parts[3].equals("com") ) {
                region.setActive(true);
                region.setAvailable(true);
                region.setName(parts[1]);
                region.setProviderRegionId(parts[1]);
                region.setJurisdiction("EU");
            }
            else {
                throw new CloudException("Unknown region endpoint: " + endpoint);
            }
        }
        return Collections.singletonList(region);
    }
}
