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
package net.fhirfactory.pegacorn.ladon.model.virtualdb.operations;

import org.hl7.fhir.r4.model.*;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VirtualDBMethodOutcomeFactory {

    public VirtualDBMethodOutcome createResourceActivityOutcome(IdType resourceId, VirtualDBActionStatusEnum status, String activityLocation) {
        VirtualDBMethodOutcome vdbOutcome = new VirtualDBMethodOutcome();
        populateResourceActivityOutcome(vdbOutcome, resourceId, status, activityLocation );
        return(vdbOutcome);
    }

    public void populateResourceActivityOutcome(VirtualDBMethodOutcome vdbOutcome, IdType resourceId, VirtualDBActionStatusEnum status, String activityLocation)
    {
        vdbOutcome.setCreated(false);
        vdbOutcome.setId(resourceId);
        OperationOutcome opOutcome = new OperationOutcome();
        OperationOutcome.OperationOutcomeIssueComponent newOutcomeComponent = new OperationOutcome.OperationOutcomeIssueComponent();
        CodeableConcept details = new CodeableConcept();
        Coding detailsCoding = new Coding();
        switch(status) {
            case DELETE_FINISH: {
                vdbOutcome.setStatusEnum(VirtualDBActionStatusEnum.DELETE_FINISH);
                vdbOutcome.setCausalAction(VirtualDBActionTypeEnum.DELETE);
                newOutcomeComponent.setCode(OperationOutcome.IssueType.DELETED);
                newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.INFORMATION);
                detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
                detailsCoding.setCode("MSG_DELETED");
                detailsCoding.setDisplay("This resource has been deleted");
                details.setText("This resource has been deleted");
                break;
            }
            case DELETE_FAILURE: {
                vdbOutcome.setStatusEnum(VirtualDBActionStatusEnum.DELETE_FAILURE);
                vdbOutcome.setCausalAction(VirtualDBActionTypeEnum.DELETE);
                newOutcomeComponent.setCode(OperationOutcome.IssueType.NOTFOUND);
                newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.INFORMATION);
                detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
                detailsCoding.setCode("MSG_NO_MATCH");
                String text = "No Resource found matching the query: " + resourceId;
                break;
            }
            case CREATION_NOT_REQUIRED: {
                vdbOutcome.setStatusEnum(VirtualDBActionStatusEnum.CREATION_NOT_REQUIRED);
                vdbOutcome.setCausalAction(VirtualDBActionTypeEnum.CREATE);
                newOutcomeComponent.setCode(OperationOutcome.IssueType.INFORMATIONAL);
                newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.INFORMATION);
                detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
                detailsCoding.setCode("MSG_CREATED");
                detailsCoding.setDisplay("New Resource Created");
                details.setText("New Resource Created");
                break;
            }
            case CREATION_FINISH:{
                vdbOutcome.setStatusEnum(VirtualDBActionStatusEnum.CREATION_FINISH);
                vdbOutcome.setCausalAction(VirtualDBActionTypeEnum.CREATE);
                newOutcomeComponent.setCode(OperationOutcome.IssueType.INFORMATIONAL);
                newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.INFORMATION);
                detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
                detailsCoding.setCode("MSG_CREATED");
                detailsCoding.setDisplay("New Resource Created");
                details.setText("New Resource Created");
                break;
            }
            case REVIEW_FAILURE: {
                vdbOutcome.setStatusEnum(VirtualDBActionStatusEnum.REVIEW_FAILURE);
                vdbOutcome.setCausalAction(VirtualDBActionTypeEnum.REVIEW);
                newOutcomeComponent.setCode(OperationOutcome.IssueType.NOTFOUND);
                newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.WARNING);
                detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
                detailsCoding.setCode("MSG_NO_EXIST");
                String text = "Resource Id " + resourceId + "does not exist";
                detailsCoding.setDisplay(text);
                details.setText(text);
                break;
            }
            case REVIEW_FINISH:{
                vdbOutcome.setStatusEnum(VirtualDBActionStatusEnum.REVIEW_FINISH);
                vdbOutcome.setCausalAction(VirtualDBActionTypeEnum.REVIEW);
                newOutcomeComponent.setCode(OperationOutcome.IssueType.INFORMATIONAL);
                newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.INFORMATION);
                detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html"); // TODO Pegacorn specific encoding --> need to check validity
                detailsCoding.setCode("MSG_RESOURCE_RETRIEVED"); // TODO Pegacorn specific encoding --> need to check validity
                details.setText("Resource Id ("+ resourceId +") has been retrieved");
                String text = "Resource Id " + resourceId + "does not exist";
                details.setText(text);
                detailsCoding.setDisplay(text);
                break;
            }
        }
        details.addCoding(detailsCoding);
        newOutcomeComponent.setDiagnostics(activityLocation);
        newOutcomeComponent.setDetails(details);
        opOutcome.addIssue(newOutcomeComponent);
        vdbOutcome.setOperationOutcome(opOutcome);
    }

    public VirtualDBMethodOutcome generateEmptyGetResponse(ResourceType resourceType, IdType id){
        VirtualDBMethodOutcome outcome = new VirtualDBMethodOutcome();
        outcome.setCreated(false);
        outcome.setCausalAction(VirtualDBActionTypeEnum.REVIEW);
        outcome.setStatusEnum(VirtualDBActionStatusEnum.REVIEW_FAILURE);
        CodeableConcept details = new CodeableConcept();
        Coding detailsCoding = new Coding();
        detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
        detailsCoding.setCode("MSG_NO_EXIST");
        String text = "Resource Id " + id.toString() + " does not exist";
        detailsCoding.setDisplay(text);
        details.setText(text);
        details.addCoding(detailsCoding);
        OperationOutcome opOutcome = new OperationOutcome();
        OperationOutcome.OperationOutcomeIssueComponent newOutcomeComponent = new OperationOutcome.OperationOutcomeIssueComponent();
        newOutcomeComponent.setDiagnostics(resourceType.toString());
        newOutcomeComponent.setDetails(details);
        newOutcomeComponent.setCode(OperationOutcome.IssueType.NOTFOUND);
        newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.WARNING);
        opOutcome.addIssue(newOutcomeComponent);
        outcome.setOperationOutcome(opOutcome);
        return(outcome);
    }

    public VirtualDBMethodOutcome generateBadAttributeOutcome(String method, VirtualDBActionTypeEnum action, VirtualDBActionStatusEnum actionStatus, String text){
        VirtualDBMethodOutcome vdbOutcome = new VirtualDBMethodOutcome();
        vdbOutcome.setCreated(false);
        vdbOutcome.setCausalAction(action);
        vdbOutcome.setStatusEnum(actionStatus);
        CodeableConcept details = new CodeableConcept();
        Coding detailsCoding = new Coding();
        detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
        detailsCoding.setCode("MSG_PARAM_INVALID");
        detailsCoding.setDisplay(text);
        details.setText(text);
        details.addCoding(detailsCoding);
        OperationOutcome opOutcome = new OperationOutcome();
        OperationOutcome.OperationOutcomeIssueComponent newOutcomeComponent = new OperationOutcome.OperationOutcomeIssueComponent();
        newOutcomeComponent.setDiagnostics(method);
        newOutcomeComponent.setDetails(details);
        newOutcomeComponent.setCode(OperationOutcome.IssueType.INVALID);
        newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.ERROR);
        opOutcome.addIssue(newOutcomeComponent);
        vdbOutcome.setOperationOutcome(opOutcome);
        return(vdbOutcome);
    }

    public VirtualDBMethodOutcome generateEmptySearchResponse(ResourceType resourceType){
        VirtualDBMethodOutcome outcome = new VirtualDBMethodOutcome();
        outcome.setCreated(false);
        outcome.setCausalAction(VirtualDBActionTypeEnum.SEARCH);
        outcome.setStatusEnum(VirtualDBActionStatusEnum.SEARCH_FAILURE);
        CodeableConcept details = new CodeableConcept();
        Coding detailsCoding = new Coding();
        detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
        detailsCoding.setCode("TODO:: Document");
        String text = "TODO:: Document";
        detailsCoding.setDisplay(text);
        details.setText(text);
        details.addCoding(detailsCoding);
        OperationOutcome opOutcome = new OperationOutcome();
        OperationOutcome.OperationOutcomeIssueComponent newOutcomeComponent = new OperationOutcome.OperationOutcomeIssueComponent();
        newOutcomeComponent.setDiagnostics(resourceType.toString());
        newOutcomeComponent.setDetails(details);
        newOutcomeComponent.setCode(OperationOutcome.IssueType.NOTFOUND);
        newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.WARNING);
        opOutcome.addIssue(newOutcomeComponent);
        outcome.setOperationOutcome(opOutcome);
        return(outcome);
    }

}
