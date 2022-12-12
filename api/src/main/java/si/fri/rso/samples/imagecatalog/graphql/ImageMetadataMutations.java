package si.fri.rso.samples.imagecatalog.graphql;

import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import si.fri.rso.samples.imagecatalog.lib.ImageMetadata;
import si.fri.rso.samples.imagecatalog.services.beans.ImageMetadataBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@GraphQLClass
@ApplicationScoped
public class ImageMetadataMutations {

    @Inject
    private ImageMetadataBean imageMetadataBean;

    @GraphQLMutation
    public ImageMetadata addImageMetadata(@GraphQLArgument(name = "imageMetadata") ImageMetadata imageMetadata) {
        imageMetadataBean.createImageMetadata(imageMetadata);
        return imageMetadata;
    }

    @GraphQLMutation
    public DeleteResponse deleteImageMetadata(@GraphQLArgument(name = "id") Integer id) {
        return new DeleteResponse(imageMetadataBean.deleteImageMetadata(id));
    }

}
