package net.fhirfactory.pegacorn.ladon.model.virtualdb.cache;

import org.hl7.fhir.r4.model.Resource;

import java.time.Instant;
import java.util.Date;

public class CacheResourceEntry {
    private Date touchDate;
    private Resource resource;

    public CacheResourceEntry(Resource resource){
        this.resource = resource;
        touchDate = Date.from(Instant.now());
    }

    public Date getTouchDate() {
        return touchDate;
    }

    public void setTouchDate(Date touchDate) {
        this.touchDate = touchDate;
    }

    public Resource getResource() {
        return resource;
    }
}
