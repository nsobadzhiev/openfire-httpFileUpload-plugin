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

    @Override
    public void initializePlugin( PluginManager manager, File pluginDirectory )
    {
        try
        {
            SlotManager.getInstance().setMaxFileSize( JiveGlobals.getLongProperty( "plugin.httpfileupload.maxFileSize", SlotManager.DEFAULT_MAX_FILE_SIZE ) );
            SlotManager.getInstance().setUploadServicePath( JiveGlobals.getProperty( "plugin.httpfileupload.uploadServiceHost", SlotManager.DEFAULT_UPLOAD_SERVICE_HOST ) );
            SlotManager.getInstance().setSlotCreationTimeout( JiveGlobals.getIntProperty( "plugin.httpfileupload.slotCreationTimeout", SlotManager.DEFAULT_SLOT_TIMEOUT ) );

            PropertyEventDispatcher.addListener( this );

            component = new Component( XMPPServer.getInstance().getServerInfo().getXMPPDomain());

            InternalComponentManager.getInstance().addComponent( "httpfileupload", component );
        }
        catch ( Exception e )
        {
            Log.error( "Unable to register component!", e );
        }
        Log.error( "HTTP file upload initialized" );

    }

    @Override
    public void destroyPlugin()
    {
        PropertyEventDispatcher.removeListener( this );

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
