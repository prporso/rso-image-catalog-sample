package si.fri.rso.samples.imagecatalog.services.clients;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.Dependent;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.concurrent.CompletionStage;

import si.fri.rso.samples.imagecatalog.services.dtos.ImageProcessRequest;

@Path("process")
@RegisterRestClient(configKey="image-processing-api")
@Dependent
public interface ImageProcessingApi {

    @POST
    CompletionStage<String> processImageAsynch(ImageProcessRequest imageProcessRequest);

}
