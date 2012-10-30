package org.dasein.cloud.cloudsigma;

import org.apache.log4j.Logger;
import org.dasein.cloud.AbstractCloud;
import org.dasein.cloud.ProviderContext;
import org.dasein.cloud.cloudsigma.compute.SoftLayerComputeServices;
import org.dasein.cloud.cloudsigma.network.SoftLayerNetworkServices;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Support for the SoftLayer cloud. This implementation owes a lot to the work done by the jclouds team
 * in prior support for Dasein Cloud SoftLayer. Though the Dasein Cloud native version is done from
 * scratch, it would not have been possible so quickly without their help.
 * <p>Created by George Reese: 10/25/12 6:30 PM</p>
 * @author George Reese
 * @version 2012.09 initial version
 * @since 2012.09
 */
public class SoftLayer extends AbstractCloud {
    static private final Logger logger = getLogger(SoftLayer.class);

    static private @Nonnull String getLastItem(@Nonnull String name) {
        int idx = name.lastIndexOf('.');

        if( idx < 0 ) {
            return name;
        }
        else if( idx == (name.length()-1) ) {
            return "";
        }
        return name.substring(idx+1);
    }

    static public @Nonnull Logger getLogger(@Nonnull Class<?> cls) {
        String pkg = getLastItem(cls.getPackage().getName());

        if( pkg.equals("cloudsigma") ) {
            pkg = "";
        }
        else {
            pkg = pkg + ".";
        }
        return Logger.getLogger("dasein.cloud.cloudsigma.std." + pkg + getLastItem(cls.getName()));
    }

    static public @Nonnull Logger getWireLogger(@Nonnull Class<?> cls) {
        return Logger.getLogger("dasein.cloud.cloudsigma.wire." + getLastItem(cls.getPackage().getName()) + "." + getLastItem(cls.getName()));
    }

    public SoftLayer() { }

    @Override
    public @Nonnull String getCloudName() {
        ProviderContext ctx = getContext();
        String name = (ctx == null ? null : ctx.getCloudName());

        return (name == null ? "SoftLayer" : name);
    }

    @Override
    public @Nonnull SoftLayerComputeServices getComputeServices() {
        return new SoftLayerComputeServices(this);
    }

    @Override
    public @Nonnull SoftLayerDataCenterServices getDataCenterServices() {
        return new SoftLayerDataCenterServices(this);
    }

    @Override
    public @Nonnull SoftLayerNetworkServices getNetworkServices() {
        return new SoftLayerNetworkServices(this);
    }

    @Override
    public @Nonnull String getProviderName() {
        ProviderContext ctx = getContext();
        String name = (ctx == null ? null : ctx.getProviderName());

        return (name == null ? "SoftLayer" : name);
    }

    @Override
    public @Nullable String testContext() {
        if( logger.isTraceEnabled() ) {
            logger.trace("ENTER - " + SoftLayer.class.getName() + ".testContext()");
        }
        try {
            ProviderContext ctx = getContext();

            if( ctx == null ) {
                logger.warn("No context was provided for testing");
                return null;
            }
            try {
                SoftLayerMethod method = new SoftLayerMethod(this);
                String body = method.getString("/profile/info");

                if( body == null ) {
                    return null;
                }
                String uuid = SoftLayerMethod.seekValue(body, "uuid");

                if( logger.isDebugEnabled() ) {
                    logger.debug("UUID=" + uuid);
                }
                if( uuid == null ) {
                    logger.warn("No valid UUID was provided in the response during context testing");
                    return null;
                }
                return uuid;
            }
            catch( Throwable t ) {
                logger.error("Error testing SoftLayer credentials for " + ctx.getAccountNumber() + ": " + t.getMessage());
                return null;
            }
        }
        finally {
            if( logger.isTraceEnabled() ) {
                logger.trace("EXIT - " + SoftLayer.class.getName() + ".textContext()");
            }
        }
    }
}