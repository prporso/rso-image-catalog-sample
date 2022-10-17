package si.fri.rso.samples.imagecatalog.models.converters;

import si.fri.rso.samples.imagecatalog.lib.ImageMetadata;
import si.fri.rso.samples.imagecatalog.models.entities.ImageMetadataEntity;

public class ImageMetadataConverter {

    public static ImageMetadata toDto(ImageMetadataEntity entity) {

        ImageMetadata dto = new ImageMetadata();
        dto.setImageId(entity.getId());
        dto.setCreated(entity.getCreated());
        dto.setDescription(entity.getDescription());
        dto.setTitle(entity.getTitle());
        dto.setHeight(entity.getHeight());
        dto.setWidth(entity.getWidth());
        dto.setUri(entity.getUri());

        return dto;

    }

    public static ImageMetadataEntity toEntity(ImageMetadata dto) {

        ImageMetadataEntity entity = new ImageMetadataEntity();
        entity.setCreated(dto.getCreated());
        entity.setDescription(dto.getDescription());
        entity.setTitle(dto.getTitle());
        entity.setHeight(dto.getHeight());
        entity.setWidth(dto.getWidth());
        entity.setUri(dto.getUri());

        return entity;

    }

}
