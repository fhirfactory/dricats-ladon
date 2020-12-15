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

import net.fhirfactory.pegacorn.ladon.model.virtualdb.operations.VirtualDBActionStatusEnum;
import org.hl7.fhir.r4.model.Resource;

import java.util.ArrayList;
import java.util.List;

public class ResourceSoTConduitSearchResponseElement {
    List<Resource> resourceList;
    ResourceGradeEnum responseResourceGrade;
    SoTConduitGradeEnum soTConduitGrade;
    VirtualDBActionStatusEnum statusEnum;
    String conduitName;
    String errorMessage;

    public ResourceSoTConduitSearchResponseElement(){
        resourceList = new ArrayList<>();
        responseResourceGrade = ResourceGradeEnum.EMPTY;
        statusEnum = VirtualDBActionStatusEnum.INDETERMINANT;
        conduitName = null;
        errorMessage = null;
    }

    public ResourceSoTConduitSearchResponseElement(List<Resource> newResourceList, ResourceGradeEnum responseEnum){
        resourceList = new ArrayList<>();
        this.resourceList.addAll(newResourceList);
        this.responseResourceGrade = responseEnum;
    }

    public List<Resource> getResources() {
        return resourceList;
    }

    public void setResources(List<Resource> resources) {
        this.resourceList = resources;
    }

    public ResourceGradeEnum getResponseResourceGrade() {
        return responseResourceGrade;
    }

    public void setResponseResourceGrade(ResourceGradeEnum responseResourceGrade) {
        this.responseResourceGrade = responseResourceGrade;
    }

    public void addResource(Resource resource){
        if(resource != null){
            if(!resourceList.contains(resource)){
                resourceList.add(resource);
            }
        }
    }

    public SoTConduitGradeEnum getSoTConduitGrade() {
        return soTConduitGrade;
    }

    public void setSoTConduitGrade(SoTConduitGradeEnum soTConduitGrade) {
        this.soTConduitGrade = soTConduitGrade;
    }

    public VirtualDBActionStatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(VirtualDBActionStatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    public String getConduitName() {
        return conduitName;
    }

    public void setConduitName(String conduitName) {
        this.conduitName = conduitName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
