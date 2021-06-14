/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.ladon.virtualdb.persistence;

import ca.uhn.fhir.rest.api.MethodOutcome;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierDataTypeHelpers;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcome;
import net.fhirfactory.pegacorn.ladon.virtualdb.persistence.common.PersistenceServiceBase;
import net.fhirfactory.pegacorn.ladon.virtualdb.persistence.servers.BaseManagementPersistenceServerSecureAccessor;
import net.fhirfactory.pegacorn.platform.edge.ask.InternalFHIRClientServices;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EncounterPersistenceService extends PersistenceServiceBase {
    private static final Logger LOG = LoggerFactory.getLogger(EncounterPersistenceService.class);

    @Inject
    private BaseManagementPersistenceServerSecureAccessor persistenceServerSecureAccessor;

    @Inject
    PegacornIdentifierDataTypeHelpers virtualDBKeyResolver;

    @Override
    protected String specifyPersistenceServiceName() {
        return ("EncounterPersistenceService");
    }

    @Override
    protected String specifyPersistenceServiceVersion() {
        return ("4.0.1");
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected InternalFHIRClientServices getFHIRClientServices() {
        return (persistenceServerSecureAccessor);
    }

    @Override
    public TransactionMethodOutcome synchroniseResource(ResourceType resourceType, Resource resource) {
        return null;
    }

    @Override
    protected Identifier getBestIdentifier(MethodOutcome outcome) {
        if(outcome == null){
            return(null);
        }
        Resource containedResource = (Resource)outcome.getResource();
        if(containedResource == null){
            return(null);
        }
        Encounter actualResource = (Encounter) containedResource;
        if(actualResource.hasIdentifier()){
            Identifier bestIdentifier = virtualDBKeyResolver.getBestIdentifier(actualResource.getIdentifier());
            return(bestIdentifier);
        }
        return(null);
    }
}
