package si.fri.rso.samples.imagecatalog.models.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.Instant;

@Converter(autoApply = true)
public class InstantAtributeConverter implements AttributeConverter<Instant, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(Instant instant) {
        return (instant == null ? null : Timestamp.from(instant));
    }

    @Override
    public Instant convertToEntityAttribute(Timestamp timestamp) {
        return (timestamp == null ? null : timestamp.toInstant());
    }
}
