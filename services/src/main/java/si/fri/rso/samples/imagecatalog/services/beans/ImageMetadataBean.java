package si.fri.rso.samples.imagecatalog.services.beans;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rso.samples.imagecatalog.lib.ImageMetadata;
import si.fri.rso.samples.imagecatalog.models.converters.ImageMetadataConverter;
import si.fri.rso.samples.imagecatalog.models.entities.ImageMetadataEntity;


@RequestScoped
public class ImageMetadataBean {

    private Logger log = Logger.getLogger(ImageMetadataBean.class.getName());

    @Inject
    private ImageMetadataBean imageMetadataBeanProxy;

    @Inject
    private EntityManager em;

    private Client httpClient;
    private String baseUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        baseUrl = "http://localhost:8081"; // only for demonstration
    }

    public List<ImageMetadata> getImageMetadata() {

        TypedQuery<ImageMetadataEntity> query = em.createNamedQuery(
                "ImageMetadataEntity.getAll", ImageMetadataEntity.class);

        List<ImageMetadataEntity> resultList = query.getResultList();

        return resultList.stream().map(ImageMetadataConverter::toDto).collect(Collectors.toList());

    }

    @Timed
    public List<ImageMetadata> getImageMetadataFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, ImageMetadataEntity.class, queryParameters).stream()
                .map(ImageMetadataConverter::toDto).collect(Collectors.toList());
    }

    public ImageMetadata getImageMetadata(Integer id) {

        ImageMetadataEntity imageMetadataEntity = em.find(ImageMetadataEntity.class, id);

        if (imageMetadataEntity == null) {
            throw new NotFoundException();
        }

        ImageMetadata imageMetadata = ImageMetadataConverter.toDto(imageMetadataEntity);
        imageMetadata.setNumberOfComments(imageMetadataBeanProxy.getCommentCount(id));
        return imageMetadata;
    }

    public ImageMetadata createImageMetadata(ImageMetadata imageMetadata) {

        ImageMetadataEntity imageMetadataEntity = ImageMetadataConverter.toEntity(imageMetadata);

        try {
            beginTx();
            em.persist(imageMetadataEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (imageMetadataEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return ImageMetadataConverter.toDto(imageMetadataEntity);
    }

    public ImageMetadata putImageMetadata(Integer id, ImageMetadata imageMetadata) {

        ImageMetadataEntity c = em.find(ImageMetadataEntity.class, id);

        if (c == null) {
            return null;
        }

        ImageMetadataEntity updatedImageMetadataEntity = ImageMetadataConverter.toEntity(imageMetadata);

        try {
            beginTx();
            updatedImageMetadataEntity.setId(c.getId());
            updatedImageMetadataEntity = em.merge(updatedImageMetadataEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return ImageMetadataConverter.toDto(updatedImageMetadataEntity);
    }

    public boolean deleteImageMetadata(Integer id) {

        ImageMetadataEntity imageMetadata = em.find(ImageMetadataEntity.class, id);

        if (imageMetadata != null) {
            try {
                beginTx();
                em.remove(imageMetadata);
                commitTx();
            }
            catch (Exception e) {
                rollbackTx();
            }
        }
        else {
            return false;
        }

        return true;
    }

    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Fallback(fallbackMethod = "getCommentCountFallback")
    public Integer getCommentCount(Integer imageId) {

        log.info("Calling comments service: getting comment count.");

        try {
            return httpClient
                    .target(baseUrl + "/v1/comments/count")
                    .queryParam("imageId", imageId)
                    .request().get(new GenericType<Integer>() {
                    });
        }
        catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }
    }

    public Integer getCommentCountFallback(Integer imageId) {
        return null;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
