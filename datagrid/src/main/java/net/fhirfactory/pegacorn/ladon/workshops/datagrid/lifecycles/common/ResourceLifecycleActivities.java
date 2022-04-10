/*
 * Copyright (c) 2021 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.ladon.workshops.datagrid.lifecycles.common;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;

import java.time.Instant;

public class ResourceLifecycleActivities {

    public Instant loadResource(IdType resourceId){

        Instant loadInstant = Instant.now();
        return(loadInstant);
    }

    public Instant saveResource(IdType resourceId){

        Instant saveInstant = Instant.now();
        return(saveInstant);
    }

    public Instant refreshInstant(IdType resourceId){

        Instant refreshInstant = Instant.now();
        return(refreshInstant);
    }

    public Resource addResource(Resource resource){

        return(resource);
    }

    public Resource updateResource(Resource resource){

        return(resource);
    }

    public Resource deleteResource(Resource resource){

        return(resource);
    }

    public Resource getResource(IdType resourceId){

        Resource resource = new Patient();
        return(resource);
    }
}