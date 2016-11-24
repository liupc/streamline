package org.apache.streamline.streams.service.metadata;

import com.codahale.metrics.annotation.Timed;
import org.apache.streamline.common.util.WSUtils;
import org.apache.streamline.streams.catalog.Cluster;
import org.apache.streamline.streams.catalog.Service;
import org.apache.streamline.streams.catalog.exception.ServiceNotFoundException;
import org.apache.streamline.streams.catalog.service.StreamCatalogService;

import org.apache.streamline.streams.catalog.exception.EntityNotFoundException;
import org.apache.streamline.streams.catalog.service.metadata.StormMetadataService;
import org.apache.streamline.streams.cluster.discovery.ambari.ComponentPropertyPattern;
import org.apache.streamline.streams.cluster.discovery.ambari.ServiceConfigurations;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.apache.streamline.common.catalog.CatalogResponse.ResponseMessage.ENTITY_BY_NAME_NOT_FOUND;
import static org.apache.streamline.common.catalog.CatalogResponse.ResponseMessage.ENTITY_NOT_FOUND;
import static org.apache.streamline.common.catalog.CatalogResponse.ResponseMessage.EXCEPTION;
import static org.apache.streamline.common.catalog.CatalogResponse.ResponseMessage.SUCCESS;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;

@Path("/v1/catalog")
@Produces(MediaType.APPLICATION_JSON)
public class StormMetadataResource {
    private final StreamCatalogService catalogService;

    public StormMetadataResource(StreamCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GET
    @Path("/clusters/name/{clusterName}/services/storm/topologies")
    @Timed
    public Response getTopologiesByClusterName(@PathParam("clusterName") String clusterName) {
        final Cluster cluster = catalogService.getClusterByName(clusterName);
        if (cluster == null) {
            return WSUtils.respond(NOT_FOUND, ENTITY_BY_NAME_NOT_FOUND, "cluster name " + clusterName);
        }
        return getTopologiesByClusterId(cluster.getId());
    }

    @GET
    @Path("/clusters/{clusterId}/services/storm/topologies")
    @Timed
    public Response getTopologiesByClusterId(@PathParam("clusterId") Long clusterId) {
        try {
            StormMetadataService stormMetadataService = new StormMetadataService.Builder(catalogService, clusterId).build();
            return WSUtils.respond(stormMetadataService.getTopologies(), OK, SUCCESS);
        } catch (EntityNotFoundException ex) {
            return WSUtils.respond(NOT_FOUND, ENTITY_NOT_FOUND, ex.getMessage());
        } catch (Exception ex) {
            return WSUtils.respond(INTERNAL_SERVER_ERROR, EXCEPTION, ex.getMessage());
        }
    }

    @GET
    @Path("/clusters/{clusterId}/services/storm/mainpage/url")
    @Timed
    public Response getMainPageByClusterId(@PathParam("clusterId") Long clusterId) {
        try {
            StormMetadataService stormMetadataService = new StormMetadataService.Builder(catalogService, clusterId).build();
            return WSUtils.respond(stormMetadataService.getMainPageUrl(), OK, SUCCESS);
        } catch (EntityNotFoundException ex) {
            return WSUtils.respond(NOT_FOUND, ENTITY_NOT_FOUND, ex.getMessage());
        } catch (Exception ex) {
            return WSUtils.respond(INTERNAL_SERVER_ERROR, EXCEPTION, ex.getMessage());
        }
    }
}
