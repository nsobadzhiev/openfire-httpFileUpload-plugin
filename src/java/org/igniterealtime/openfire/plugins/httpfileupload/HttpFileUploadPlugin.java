package org.igniterealtime.openfire.plugins.httpfileupload;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.component.InternalComponentManager;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.http.HttpBindManager;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.PropertyEventDispatcher;
import org.jivesoftware.util.PropertyEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guus on 18-11-17.
 */
public class HttpFileUploadPlugin implements Plugin, PropertyEventListener
{
    private static final Logger Log = LoggerFactory.getLogger( HttpFileUploadPlugin.class );

    private Component component;
    private WebAppContext context;

    private final String[] publicResources = new String[]
        {
            "httpfileupload/*",
            "httpFileUpload/*"
        };

    @Override
    public void initializePlugin( PluginManager manager, File pluginDirectory )
    {
        try
        {
            SlotManager.getInstance().setMaxFileSize( JiveGlobals.getLongProperty( "plugin.httpfileupload.maxFileSize", SlotManager.DEFAULT_MAX_FILE_SIZE ) );
            SlotManager.getInstance().setUploadServicePath( JiveGlobals.getProperty( "plugin.httpfileupload.uploadServiceHost", SlotManager.DEFAULT_UPLOAD_SERVICE_HOST ) );

            PropertyEventDispatcher.addListener( this );

            component = new Component( XMPPServer.getInstance().getServerInfo().getXMPPDomain());

            // Add the Webchat sources to the same context as the one that's providing the BOSH interface.
            context = new WebAppContext( null, pluginDirectory.getPath() + File.separator + "classes", "/httpfileupload" );
            context.setClassLoader( this.getClass().getClassLoader() );

            // Ensure the JSP engine is initialized correctly (in order to be able to cope with Tomcat/Jasper precompiled JSPs).
            final List<ContainerInitializer> initializers = new ArrayList<>();
            initializers.add( new ContainerInitializer( new JettyJasperInitializer(), null ) );
            context.setAttribute("org.eclipse.jetty.containerInitializers", initializers);
            context.setAttribute( InstanceManager.class.getName(), new SimpleInstanceManager());

            HttpBindManager.getInstance().addJettyHandler( context );

            InternalComponentManager.getInstance().addComponent( "httpfileupload", component );

            for ( final String publicResource : publicResources )
            {
                AuthCheckFilter.addExclude( publicResource );
            }
        }
        catch ( Exception e )
        {
            Log.error( "Unable to register component!", e );
        }

    }

    @Override
    public void destroyPlugin()
    {
        PropertyEventDispatcher.removeListener( this );

        for ( final String publicResource : publicResources )
        {
            AuthCheckFilter.removeExclude( publicResource );
        }

        if ( context != null )
        {
            HttpBindManager.getInstance().removeJettyHandler( context );
            context.destroy();
            context = null;
        }

        if ( component != null )
        {
            InternalComponentManager.getInstance().removeComponent( "httpfileupload" );
        }
    }

    @Override
    public void propertySet( String property, Map params )
    {
        setProperty( property );
    }

    @Override
    public void propertyDeleted( String property, Map params )
    {
        deleteProperty( property );
    }

    @Override
    public void xmlPropertySet( String property, Map params )
    {
        setProperty( property );
    }

    @Override
    public void xmlPropertyDeleted( String property, Map params )
    {
        deleteProperty( property );
    }

    public final void setProperty( String property )
    {
        if ( "plugin.httpfileupload.maxFileSize".equals( property ) )
        {
            SlotManager.getInstance().setMaxFileSize( JiveGlobals.getLongProperty( "plugin.httpfileupload.maxFileSize", SlotManager.DEFAULT_MAX_FILE_SIZE ) );
        } else if ( "plugin.httpfileupload.uploadServiceHost".equals( property ) )
        {
            SlotManager.getInstance().setUploadServicePath( JiveGlobals.getProperty( "plugin.httpfileupload.uploadServiceHost", SlotManager.DEFAULT_UPLOAD_SERVICE_HOST ) );
        }
    }

    public final void deleteProperty( String property )
    {
        if ( "plugin.httpfileupload.maxFileSize".equals( property ) )
        {
            SlotManager.getInstance().setMaxFileSize( SlotManager.DEFAULT_MAX_FILE_SIZE );
        } else if ( "plugin.httpfileupload.uploadServiceHost".equals( property ) )
        {
            SlotManager.getInstance().setUploadServicePath( SlotManager.DEFAULT_UPLOAD_SERVICE_HOST );
        }
    }
}
