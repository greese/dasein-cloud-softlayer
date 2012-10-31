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
package org.dasein.cloud.softlayer.compute;

import org.dasein.cloud.compute.AbstractComputeServices;
import org.dasein.cloud.softlayer.SoftLayer;
import org.dasein.cloud.softlayer.compute.image.SLImageSupport;
import org.dasein.cloud.softlayer.compute.vm.VirtualGuestSupport;

import javax.annotation.Nonnull;

/**
 * Implements compute services for SoftLayer in accordance with the Dasein Cloud API.
 * <p>Created by George Reese: 10/30/12 2:51 PM</p>
 * @author George Reese
 * @version 2012.09 initial version
 * @since 2012.09 initial version
 */
public class SoftLayerCompute extends AbstractComputeServices {
    private SoftLayer provider;

    public SoftLayerCompute(@Nonnull SoftLayer provider) { this.provider = provider; }

    @Override
    public @Nonnull SLImageSupport getImageSupport() {
        return new SLImageSupport(provider);
    }

    @Override
    public @Nonnull VirtualGuestSupport getVirtualMachineSupport() {
        return new VirtualGuestSupport(provider);
    }
}
