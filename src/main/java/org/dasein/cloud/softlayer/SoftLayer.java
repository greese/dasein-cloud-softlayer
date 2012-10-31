package org.dasein.cloud.softlayer;

import org.apache.log4j.Logger;
import org.dasein.cloud.AbstractCloud;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.ProviderContext;
import org.dasein.cloud.softlayer.compute.SoftLayerCompute;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
    public @Nonnull SoftLayerCompute getComputeServices() {
        return new SoftLayerCompute(this);
    }

    @Override
    public @Nonnull SoftLayerDataCenterServices getDataCenterServices() {
        return new SoftLayerDataCenterServices(this);
    }

    public void productPackage() throws CloudException, InternalException {
        SoftLayerMethod method = new SoftLayerMethod(this);
        ProviderContext ctx = getContext();

        if( ctx == null ) {
            throw new NoContextException();
        }
        try {
            JSONArray packages = method.list("SoftLayer_Account/" + new String(ctx.getAccessPublic(), "utf-8") + "/ActivePackages");

            if( packages == null ) {
                logger.error("No packages were returned from SoftLayer");
                throw new CloudException("Unable to load packages from SoftLayer");
            }
            int count = 0;
            for( int i=0; i<packages.length(); i++ ) {
                try {
                    JSONObject json = packages.getJSONObject(i);
                    if( json.has("name") ) {
                        String name = json.getString("name");

                        if( name.toLowerCase().contains("cloud") && name.toLowerCase().contains("server") ) { //|| name.toLowerCase().contains("rightscale") ) {
                            System.out.println("");
                            System.out.println("--------------------------------------------------");
                            System.out.println(name + ":");
                            String[] keys = JSONObject.getNames(json);

                            for( String key : keys ) {
                                System.out.println(key + "=" + json.get(key));
                            }
                            System.out.println("--------------------------------------------------");
                            System.out.println("");
                            count++;
                        }
                    }
                }
                catch( JSONException e ) {
                    logger.error("Error parsing JSON from cloud: " + e.getMessage());
                    e.printStackTrace();
                    throw new CloudException(e);
                }
            }
            System.out.println(count + " out of " + packages.length());
        }
        catch( UnsupportedEncodingException e ) {
            logger.error("UTF-8 not supported: " + e.getMessage());
            throw new InternalException(e);
        }

    }
    @Override
    public @Nonnull String getProviderName() {
        ProviderContext ctx = getContext();
        String name = (ctx == null ? null : ctx.getProviderName());

        return (name == null ? "SoftLayer" : name);
    }

    public @Nonnegative long parseTimestamp(@Nullable String date) throws CloudException {
        //"createDate":"2012-02-25T17:34:22-06:00"
        if( date == null || date.equals("") ) {
            return 0L;
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        try {
            return fmt.parse(date).getTime();
        }
        catch( ParseException e ) {
            if( date.length() > 6 ) {
                char c = date.charAt(date.length()-6);

                if( c == '-' || c == '+' ) {
                    date = date.substring(0, date.length()-6) + "GMT" + date.substring(date.length()-6);
                    fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

                    try {
                        return fmt.parse(date).getTime();
                    }
                    catch( ParseException again ) {
                        throw new CloudException("Could not parse date: " + date);
                    }
                }
            }
            throw new CloudException("Could not parse date: " + date);
        }
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