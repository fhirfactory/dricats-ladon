/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr;

import net.fhirfactory.pegacorn.ladon.model.virtualdb.searches.SearchNameEnum;
import org.hl7.fhir.r4.model.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface SoTConduitInterface {

    //
    // Conduit Name & Version
    //

    public String getConduitName();
    public String getConduitVersion();

    //
    // Create
    //
    public ResourceSoTConduitActionResponse createResource(Resource resourceToCreate);
    //
    // Review
    //
    public ResourceSoTConduitActionResponse getResourceViaIdentifier(Identifier identifier);
    //
    // Review
    //
    public ResourceSoTConduitActionResponse reviewResource(IdType id);
    //
    // Update
    //
    public ResourceSoTConduitActionResponse updateResource(Resource resourceToUpdate);
    //
    // Delete
    //
    public ResourceSoTConduitActionResponse deleteResource(Resource resourceToDelete);

    //
    // resourceSearches (base set, keep limited or else)
    //
    
    public List<ResourceSoTConduitSearchResponseElement> searchSourceOfTruthUsingCriteria(ResourceType resourceType, SearchNameEnum searchName, Map<Property, Serializable> parameterSet);
    public boolean supportiveOfSearch(SearchNameEnum searchName);
    
    //
    // Resource Conduit Helpers
    //

    public boolean supportsDirectCreateAction(Resource wholeResource);
    public boolean supportsDirectUpdateAction(Resource wholeResource);
    public boolean supportsDirectDeleteAction(Resource wholeResource);
}
