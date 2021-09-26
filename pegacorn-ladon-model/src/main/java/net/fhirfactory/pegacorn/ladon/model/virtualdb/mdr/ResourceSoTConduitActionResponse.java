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

import ca.uhn.fhir.rest.api.MethodOutcome;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.operations.VirtualDBActionStatusEnum;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.operations.VirtualDBActionTypeEnum;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.operations.VirtualDBMethodOutcome;
import net.fhirfactory.pegacorn.petasos.model.itops.metrics.valuesets.ComponentFunctionStatusEnum;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Property;
import org.hl7.fhir.r4.model.Reference;

import java.util.HashSet;

public class ResourceSoTConduitActionResponse extends VirtualDBMethodOutcome implements Comparable<ResourceSoTConduitActionResponse>{
    @Deprecated
    private Reference sourceOfTruthEndpoint;
    @Deprecated
    private Reference sourceOfTruthOwningOrganization;
    private String sourceOfTruthName;
    private ComponentFunctionStatusEnum functionStatusEnum;
    private ResourceGradeEnum responseResourceGrade;
    private SoTConduitGradeEnum soTGrade;
    private HashSet<String> immutableAttributes;
    private HashSet<String> authoritativeAttributes;
    private HashSet<String> informationalAttributes;
    private HashSet<String> anecdotalAttributes;

    public ResourceSoTConduitActionResponse(String sotName, ComponentFunctionStatusEnum functionStatus){
        super();
        sourceOfTruthName = sotName;
        functionStatusEnum = functionStatus;
        responseResourceGrade = null;
        soTGrade = null;
        immutableAttributes = new HashSet<>();
        authoritativeAttributes = new HashSet<>();
        informationalAttributes = new HashSet<>();
        anecdotalAttributes = new HashSet<>();
        sourceOfTruthEndpoint = null;
        sourceOfTruthOwningOrganization = null;
    }

    public ResourceSoTConduitActionResponse(String sotName, ComponentFunctionStatusEnum functionStatus, VirtualDBActionTypeEnum action, Identifier identifier, MethodOutcome ori){
        super(action,identifier,ori);
        sourceOfTruthName = sotName;
        functionStatusEnum = functionStatus;
        immutableAttributes = new HashSet<>();
        authoritativeAttributes = new HashSet<>();
        informationalAttributes = new HashSet<>();
        anecdotalAttributes = new HashSet<>();
        responseResourceGrade = null;
        soTGrade = null;
        sourceOfTruthEndpoint = null;
        sourceOfTruthOwningOrganization = null;
    }

    @Deprecated
    public ResourceSoTConduitActionResponse(Reference originatingSystem, Reference originatingOrganization){
        super();
        responseResourceGrade = null;
        soTGrade = null;
        this.sourceOfTruthEndpoint = originatingSystem;
        this.sourceOfTruthOwningOrganization = originatingOrganization;
        immutableAttributes = new HashSet<>();
        authoritativeAttributes = new HashSet<>();
        informationalAttributes = new HashSet<>();
        anecdotalAttributes = new HashSet<>();
    }

    public ResourceSoTConduitActionResponse(Reference originatingSystem, Reference originatingOrganization, VirtualDBActionTypeEnum action, Identifier identifier, MethodOutcome ori){
        super(action,identifier,ori);
        immutableAttributes = new HashSet<>();
        authoritativeAttributes = new HashSet<>();
        informationalAttributes = new HashSet<>();
        anecdotalAttributes = new HashSet<>();
        responseResourceGrade = null;
        soTGrade = null;
        sourceOfTruthEndpoint = originatingSystem;
        sourceOfTruthOwningOrganization = originatingOrganization;
    }

    public ResourceGradeEnum getResponseResourceGrade() {
        return responseResourceGrade;
    }

    public void setResponseResourceGrade(ResourceGradeEnum responseResourceGrade) {
        this.responseResourceGrade = responseResourceGrade;
    }

    public SoTConduitGradeEnum getSoTGrade() {
        return soTGrade;
    }

    public void setSoTGrade(SoTConduitGradeEnum soTGrade) {
        this.soTGrade = soTGrade;
    }

    public void addAuthoritativeAttribute(String authoritativeAttribute){
        if(authoritativeAttribute != null){
            authoritativeAttributes.add(authoritativeAttribute);
        }
    }

    public Reference getSourceOfTruthEndpoint() {
        return sourceOfTruthEndpoint;
    }

    public void setSourceOfTruthEndpoint(Reference sourceOfTruthEndpoint) {
        this.sourceOfTruthEndpoint = sourceOfTruthEndpoint;
    }

    public Reference getSourceOfTruthOwningOrganization() {
        return sourceOfTruthOwningOrganization;
    }

    public void setSourceOfTruthOwningOrganization(Reference sourceOfTruthOwningOrganization) {
        this.sourceOfTruthOwningOrganization = sourceOfTruthOwningOrganization;
    }

    public void addInformationalAttribute(String informationalAttribute){
        if(informationalAttribute != null){
            informationalAttributes.add(informationalAttribute);
        }
    }

    public void addAnecdotalAttribute(String anecdotalAttribute){
        if(anecdotalAttribute != null){
            anecdotalAttributes.add(anecdotalAttribute);
        }
    }

    public void addImmutableAttribute(String immutableAttribute){
        if(immutableAttribute != null){
            immutableAttributes.add(immutableAttribute);
        }
    }

    public HashSet<String> getAuthoritativeAttributes() {
        return authoritativeAttributes;
    }

    public void setAuthoritativeAttributes(HashSet<String> authoritativeAttributes) {
        this.authoritativeAttributes = authoritativeAttributes;
    }

    public HashSet<String> getInformationalAttributes() {
        return informationalAttributes;
    }

    public void setInformationalAttributes(HashSet<String> informationalAttributes) {
        this.informationalAttributes = informationalAttributes;
    }

    public HashSet<String> getAnecdotalAttributes() {
        return anecdotalAttributes;
    }

    public void setAnecdotalAttributes(HashSet<String> anecdotalAttributes) {
        this.anecdotalAttributes = anecdotalAttributes;
    }

    public HashSet<String> getImmutableAttributes() {
        return immutableAttributes;
    }

    public void setImmutableAttributes(HashSet<String> immutableAttributes) {
        this.immutableAttributes = immutableAttributes;
    }

    public String getSourceOfTruthName() {
        return sourceOfTruthName;
    }

    public void setSourceOfTruthName(String sourceOfTruthName) {
        this.sourceOfTruthName = sourceOfTruthName;
    }

    public ComponentFunctionStatusEnum getFunctionStatusEnum() {
        return functionStatusEnum;
    }

    public void setFunctionStatusEnum(ComponentFunctionStatusEnum functionStatusEnum) {
        this.functionStatusEnum = functionStatusEnum;
    }

    @Override
    public int compareTo(ResourceSoTConduitActionResponse other) {
        if(other == null){
            return(1);
        }
        if(this.getSoTGrade() == other.getSoTGrade() ) {
            return (0);
        }
        if(this.getResponseResourceGrade() == other.getResponseResourceGrade()){
            return(0);
        }
        if(other.responseResourceGrade == null){
            return(1);
        }
        if(this.responseResourceGrade == null){
            return(-1);
        }
        if(this.getSoTGrade() == other.getSoTGrade()){
            if(this.getResponseResourceGrade() == ResourceGradeEnum.THOROUGH){
                return(1);
            }
            if(other.getResponseResourceGrade() == ResourceGradeEnum.THOROUGH){
                return(-1);
            }
            if(this.getResponseResourceGrade() == ResourceGradeEnum.ESTABLISHED){
                return(1);
            }
            if(other.getResponseResourceGrade() == ResourceGradeEnum.ESTABLISHED){
                return(-1);
            }
            if(this.getResponseResourceGrade() == ResourceGradeEnum.LIMITED){
                return(1);
            }
            if(other.getResponseResourceGrade() == ResourceGradeEnum.LIMITED){
                return(-1);
            }
        }
        if(other.getSoTGrade() == null ){
            return(1);
        }
        if(this.getSoTGrade() == null){
            return(-1);
        }
        if(this.getSoTGrade() == SoTConduitGradeEnum.AUTHORITATIVE || this.getSoTGrade() == SoTConduitGradeEnum.PARTIALLY_AUTHORITATIVE) {
            if(this.getResponseResourceGrade() == ResourceGradeEnum.THOROUGH || this.getResponseResourceGrade() == ResourceGradeEnum.ESTABLISHED){
                return(1);
            }
            if(other.getSoTGrade() == SoTConduitGradeEnum.PARTIALLY_AUTHORITATIVE && other.getSoTGrade() == SoTConduitGradeEnum.INFORMATIVE){
                if(other.getResponseResourceGrade() == ResourceGradeEnum.THOROUGH || other.getResponseResourceGrade() == ResourceGradeEnum.ESTABLISHED){
                    return(-1);
                }
            }
            return(1);
        }
        if(this.getSoTGrade() == SoTConduitGradeEnum.INFORMATIVE || this.getSoTGrade() == SoTConduitGradeEnum.PARTIALLY_INFORMATIVE) {
            if(other.getSoTGrade() == SoTConduitGradeEnum.AUTHORITATIVE && other.getSoTGrade() == SoTConduitGradeEnum.PARTIALLY_AUTHORITATIVE){
                if(other.getResponseResourceGrade() == ResourceGradeEnum.THOROUGH || other.getResponseResourceGrade() == ResourceGradeEnum.ESTABLISHED){
                    return(-1);
                }
            }
            if(this.getResponseResourceGrade() == ResourceGradeEnum.THOROUGH || this.getResponseResourceGrade() == ResourceGradeEnum.ESTABLISHED){
                return(1);
            }
            if(other.getSoTGrade() == SoTConduitGradeEnum.AUTHORITATIVE && other.getSoTGrade() == SoTConduitGradeEnum.PARTIALLY_AUTHORITATIVE){
                if(other.getResponseResourceGrade() == ResourceGradeEnum.LIMITED){
                    return(-1);
                }
            }
            if(other.getSoTGrade() == SoTConduitGradeEnum.INFORMATIVE && other.getSoTGrade() == SoTConduitGradeEnum.PARTIALLY_INFORMATIVE){
                if(other.getResponseResourceGrade() == ResourceGradeEnum.THOROUGH | this.getResponseResourceGrade() == ResourceGradeEnum.ESTABLISHED){
                    return(-1);
                }
            }
            return(1);
        }
        if(this.getSoTGrade() == SoTConduitGradeEnum.ANECDOTAL){
            if(other.getSoTGrade() != SoTConduitGradeEnum.ANECDOTAL){
                return(-1);
            }
        }
        return(1);
    }
}
