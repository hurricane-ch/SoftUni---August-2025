package org.atechtrade.rent.custom;

import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;

@Getter
public class DescribedByteArrayResource extends ByteArrayResource {
    private final String description;

    public DescribedByteArrayResource(byte[] byteArray, String description) {
        super(byteArray);
        this.description = description;
    }
}
