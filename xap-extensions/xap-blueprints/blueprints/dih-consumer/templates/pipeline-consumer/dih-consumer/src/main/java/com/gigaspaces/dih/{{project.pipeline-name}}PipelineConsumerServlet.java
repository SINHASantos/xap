package com.gigaspaces.dih;

import com.gigaspaces.dih.consumer.web.PipelineConsumerServlet;
import com.gigaspaces.dih.model.PipelineTypeRegistrar;
import com.gigaspaces.internal.client.spaceproxy.ISpaceProxy;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import javax.servlet.ServletContext;
import org.openspaces.core.cluster.ClusterInfo;

/**
 * This class was auto-generated by GigaSpaces
 */
public class {{project.pipeline-name}}PipelineConsumerServlet extends PipelineConsumerServlet {

    private static final long serialVersionUID = 8216313915439061676L;

    @Override
    public void registerTypes(ISpaceProxy spaceProxy) {
        PipelineTypeRegistrar.registerTypes(new GigaSpaceConfigurer(spaceProxy).create());
    }

    @Override
    public String getPuName() {
        return ((ClusterInfo) getServletContext().getAttribute("clusterInfo")).getName();
    }

    @Override
    protected ISpaceProxy getSpace(ServletContext servletContext) {
        GigaSpace gigaSpace = (GigaSpace) servletContext.getAttribute("gigaSpace");
        return (ISpaceProxy) gigaSpace.getSpace();
    }
}