package si.fri.rso.samples.imagecatalog.services.clients;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

import si.fri.rso.samples.imagecatalog.services.config.AppProperties;

@ApplicationScoped
public class AmazonRekognitionClient {

    @Inject
    private AppProperties appProperties;

    private AmazonRekognition rekognitionClient;

    @PostConstruct
    private void init() {

        AWSCredentials credentials;
        try {
            credentials = new BasicAWSCredentials(
                    appProperties.getAmazonRekognitionAccessKey(),
                    appProperties.getAmazonRekognitionSecretKey());
        } catch (Exception e) {
            throw new AmazonClientException("Cannot initialise the credentials.", e);
        }

        rekognitionClient = AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.EU_WEST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

    }

    public Integer countFaces(byte[] imageBytes) {

        Image image = new Image().withBytes(ByteBuffer.wrap(imageBytes));

        DetectFacesRequest detectFacesRequest =
                new DetectFacesRequest().withImage(image);

        DetectFacesResult detectFacesResult = rekognitionClient.detectFaces(detectFacesRequest);

        return detectFacesResult.getFaceDetails().size();

    }

    public List<String> checkForCelebrities(byte[] imageBytes) {

        Image image = new Image().withBytes(ByteBuffer.wrap(imageBytes));

        RecognizeCelebritiesRequest recognizeCelebritiesRequest =
                new RecognizeCelebritiesRequest().withImage(image);


        RecognizeCelebritiesResult recognizeCelebritiesResult =
                rekognitionClient.recognizeCelebrities(recognizeCelebritiesRequest);

        return recognizeCelebritiesResult.getCelebrityFaces()
                .stream()
                .map(celebrity -> celebrity.getName())
                .collect(Collectors.toList());

    }

}
