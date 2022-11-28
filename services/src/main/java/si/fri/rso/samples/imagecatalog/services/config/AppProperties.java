package si.fri.rso.samples.imagecatalog.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("app-properties")
public class AppProperties {

    @ConfigValue(value = "external-services.enabled", watch = true)
    private boolean externalServicesEnabled;

    @ConfigValue(watch = true)
    private boolean healthy;

    @ConfigValue("amazon-rekognition.access-key")
    private String amazonRekognitionAccessKey;

    @ConfigValue("amazon-rekognition.secret-key")
    private String amazonRekognitionSecretKey;

    public boolean isExternalServicesEnabled() {
        return externalServicesEnabled;
    }

    public void setExternalServicesEnabled(boolean externalServicesEnabled) {
        this.externalServicesEnabled = externalServicesEnabled;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public String getAmazonRekognitionSecretKey() {
        return amazonRekognitionSecretKey;
    }

    public void setAmazonRekognitionSecretKey(String amazonRekognitionSecretKey) {
        this.amazonRekognitionSecretKey = amazonRekognitionSecretKey;
    }

    public String getAmazonRekognitionAccessKey() {
        return amazonRekognitionAccessKey;
    }

    public void setAmazonRekognitionAccessKey(String amazonRekognitionAccessKey) {
        this.amazonRekognitionAccessKey = amazonRekognitionAccessKey;
    }
}
