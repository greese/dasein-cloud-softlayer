package org.dasein.cloud.softlayer;

import org.apache.log4j.Logger;
import org.dasein.cloud.AbstractCloud;
import org.dasein.cloud.ProviderContext;
import org.json.JSONObject;

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

        if( pkg.equals("softlayer") ) {
            pkg = "";
        }
        else {
            pkg = pkg + ".";
        }
        return Logger.getLogger("dasein.cloud.softlayer.std." + pkg + getLastItem(cls.getName()));
    }

    static public @Nonnull Logger getWireLogger(@Nonnull Class<?> cls) {
        return Logger.getLogger("dasein.cloud.softlayer.wire." + getLastItem(cls.getPackage().getName()) + "." + getLastItem(cls.getName()));
    }

    public SoftLayer() { }

    @Override
    public @Nonnull String getCloudName() {
        ProviderContext ctx = getContext();
        String name = (ctx == null ? null : ctx.getCloudName());

        return (name == null ? "SoftLayer" : name);
    }

    @Override
    public @Nonnull SoftLayerDataCenterServices getDataCenterServices() {
        return new SoftLayerDataCenterServices(this);
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
                JSONObject account = method.getObject("SoftLayer_Account");

                if( account == null ) {
                    return null;
                }
                return (new String(ctx.getAccessPublic(), "utf-8"));
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