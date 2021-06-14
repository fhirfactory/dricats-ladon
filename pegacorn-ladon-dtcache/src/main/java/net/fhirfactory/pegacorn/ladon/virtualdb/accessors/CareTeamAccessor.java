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
package net.fhirfactory.pegacorn.ladon.virtualdb.accessors;

import ca.uhn.fhir.rest.api.MethodOutcome;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcome;
import net.fhirfactory.pegacorn.ladon.virtualdb.accessors.common.AccessorBase;
import net.fhirfactory.pegacorn.ladon.virtualdb.engine.CareTeamDBEngine;
import net.fhirfactory.pegacorn.ladon.virtualdb.engine.common.ResourceDBEngine;
import net.fhirfactory.pegacorn.workshops.base.Workshop;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CareTeamAccessor extends AccessorBase {
    private static final Logger LOG = LoggerFactory.getLogger(CareTeamAccessor.class);
    private static final String ACCESSOR_VERSION = "4.0.1";
    private static final String ACCESSOR_RESOURCE = "CareTeam";

    @Inject
    private CareTeamDBEngine careTeamDBEngine;

    public CareTeamAccessor(){
        super();
    }

    public Class<CareTeam> getResourceType() {
        return (CareTeam.class);
    }

    @Override
    protected String specifyAccessorResourceTypeName() {
        return (ACCESSOR_RESOURCE);
    }

    @Override
    protected String specifyAccessorResourceTypeVersion() {
        return (ACCESSOR_VERSION);
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ResourceDBEngine getResourceDBEngine() {
        return (careTeamDBEngine);
    }

    @Override
    protected List<Identifier> resolveIdentifierList(Resource resource) {
        if(resource == null){
            return(new ArrayList<>());
        }
        CareTeam careTeam = (CareTeam)resource;
        List<Identifier> identifierList = careTeam.getIdentifier();
        return(identifierList);
    }

    @Override
    public boolean supportsSearch(String searchName, Map<Property, Serializable> parameterSet) {
        return false;
    }

    @Override
    public TransactionMethodOutcome searchUsingCriteria(ResourceType resourceType, String searchName, Map<Property, Serializable> parameterSet) {
        return null;
    }

    @Override
    public TransactionMethodOutcome searchUsingIdentifier(Identifier identifier) {
        return null;
    }
}
