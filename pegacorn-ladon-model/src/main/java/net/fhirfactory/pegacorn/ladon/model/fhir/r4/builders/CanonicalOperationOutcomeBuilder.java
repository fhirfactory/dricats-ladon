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
package net.fhirfactory.pegacorn.ladon.model.fhir.r4.builders;

import net.fhirfactory.pegacorn.ladon.model.fhir.r4.common.CanonicalModelConstants;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class CanonicalOperationOutcomeBuilder {

    @Inject
    CanonicalModelConstants canonicalModelConstants;

    public OperationOutcome buildOperationOutcome(OperationOutcome.IssueSeverity severity, OperationOutcome.IssueType issueType, CanonicalOperationOutcomeDetailsCodeEnum detailsCode, IdType resourceId, String additionalText) {
        OperationOutcome outcome = new OperationOutcome();
        OperationOutcome.OperationOutcomeIssueComponent issue = new OperationOutcome.OperationOutcomeIssueComponent();
        // Set the Issue Severity (from http://hl7.org/fhir/ValueSet/issue-severity)
        issue.setSeverity(severity);
        // Set the Issue Code (from http://hl7.org/fhir/ValueSet/issue-type)
        issue.setCode(issueType);
        // Add the diagnostics
        issue.setDiagnostics(additionalText);
        // Now, build the DTCache Specific Details CodeableConcept
        Coding detailsCoding = new Coding();
        detailsCoding.setSystem(canonicalModelConstants.getCanonicalOperationOutcomeDetailsSystemPath());
        detailsCoding.setVersion(canonicalModelConstants.getCanonicalOperationOutcomeVersion());
        detailsCoding.setId(detailsCode.toString().toLowerCase().replace("_", "-"));
        detailsCoding.setCode(detailsCode.toString());
        switch (detailsCode) {
            case MSG_CREATED: {
                detailsCoding.setDisplay("New resource created");
                detailsCoding.setUserSelected(false);
                break;
            }
            case MSG_DELETED: {
                detailsCoding.setDisplay("This resource has been deleted");
                detailsCoding.setUserSelected(false);
                break;
            }
            case MSG_UPDATED: {
                detailsCoding.setDisplay("Existing resource updated");
                detailsCoding.setUserSelected(false);
                break;
            }
            case MSG_NO_MATCH: {
                detailsCoding.setDisplay("No resource found matching the query");
                detailsCoding.setUserSelected(false);
                break;
            }
            case MSG_NO_EXIST: {
                detailsCoding.setDisplay("Resource Id " + resourceId.toString() + " does not exist");
                detailsCoding.setUserSelected(false);
                break;
            }
            case MSG_ID_INVALID: {
                detailsCoding.setDisplay("Id " + resourceId.toString() + " has an invalid character");
                detailsCoding.setUserSelected(false);
                break;
            }
            case MSG_INVALID_ID: {
                detailsCoding.setDisplay("Id not accepted");
                detailsCoding.setUserSelected(false);
                break;
            }
            case MSG_AUTH_REQUIRED: {
                detailsCoding.setDisplay("You must authenticate before you can use this service");
                detailsCoding.setUserSelected(false);
                break;
            }
            case MSG_BAD_SYNTAX: {
                detailsCoding.setDisplay("Bad Syntax in input");
                detailsCoding.setUserSelected(false);
                break;
            }
            default: {
                detailsCoding.setDisplay("---");
                detailsCoding.setUserSelected(false);
                break;
            }
        }
        CodeableConcept details = new CodeableConcept();
        details.addCoding(detailsCoding);
        details.setText(detailsCoding.getDisplay());
        issue.setDetails(details);
        outcome.addIssue(issue);
        return (outcome);
    }
}
