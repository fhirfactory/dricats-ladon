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
package net.fhirfactory.pegacorn.ladon.edge.answer.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


import net.fhirfactory.pegacorn.internals.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.ladon.edge.answer.resourceproxies.*;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;

import org.slf4j.Logger;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;

public abstract class EdgeAnswerServlet extends RestfulServer {
    abstract protected Logger getLogger();

    private static final long serialVersionUID = 1L;

    @Inject
    PegacornReferenceProperties systemWideProperties;

    @Inject
    protected CareTeamProxy careTeamProxy;

    @Inject
    protected CommunicationProxy communicationProxy;

    @Inject
    protected CommunicationRequestProxy communicationRequestProxy;

    @Inject
    protected DocumentReferenceProxy documentReferenceProxy;

    @Inject
    protected EncounterProxy encounterProxy;

    @Inject
    protected EndpointProxy endpointProxy;

    @Inject
    protected GroupProxy groupProxy;

    @Inject
    protected HealthcareServiceProxy healthcareServiceProxy;

    @Inject
    protected LocationProxy locationProxy;

    @Inject
    protected OrganizationProxy organizationProxy;

    @Inject
    protected PatientProxy patientProxy;

    @Inject
    protected PractitionerProxy practitionerProxy;

    @Inject
    protected PractitionerRoleProxy practitionerRoleProxy;

    @Inject
    protected ProcedureProxy procedureProxy;

    @Inject
    protected TaskProxy taskProxy;

    @Inject
    protected ValueSetProxy valueSetProxy;

    @Inject
    protected FHIRContextUtility fHIRContextUtility;   
    
    /**
     * Constructor
     */
    public EdgeAnswerServlet() {

    }

    /**
     * This method is called automatically when the
     * servlet is initializing.
     */
    @Override
    public void initialize() {
        getLogger().debug(".initialise(): entry");
        /*
         * Two resource providers are defined. Each one handles a specific
         * type of resource.
         */
        FhirContext myFHIRContext = fHIRContextUtility.getFhirContext();
        setFhirContext(myFHIRContext);
        List<IResourceProvider> providers = new ArrayList<IResourceProvider>();
        providers.add(careTeamProxy);
        providers.add(communicationProxy);
        providers.add(communicationRequestProxy);
        providers.add(documentReferenceProxy);
        providers.add(encounterProxy);
        providers.add(endpointProxy);
        providers.add(groupProxy);
        providers.add(healthcareServiceProxy);
        providers.add(locationProxy);
        providers.add(organizationProxy);
        providers.add(patientProxy);
        providers.add(practitionerProxy);
        providers.add(practitionerRoleProxy);
        providers.add(procedureProxy);
        providers.add(taskProxy);
        providers.add(valueSetProxy);
        setResourceProviders(providers);

        /*
         * Use a narrative generator. This is a completely optional step,
         * but can be useful as it causes HAPI to generate narratives for
         * resources which don't otherwise have one.
         */
//        INarrativeGenerator narrativeGen = new DefaultThymeleafNarrativeGenerator();
//        myFHIRContext.setNarrativeGenerator(narrativeGen);

//        ApiKeyValidatorInterceptor apiKeyValidatorInterceptor = new ApiKeyValidatorInterceptor(PegacornHapiFhirProxy.API_KEY_HEADER_NAME, PegacornHapiFhirProxy.DEFAULT_API_KEY_PROPERTY_NAME);
//        registerInterceptor(apiKeyValidatorInterceptor);
        
        /*
         * Use nice coloured HTML when a browser is used to request the content
         */
        registerInterceptor(new ResponseHighlighterInterceptor());        
        getLogger().debug(".initialize(): Exit");
    }

}
