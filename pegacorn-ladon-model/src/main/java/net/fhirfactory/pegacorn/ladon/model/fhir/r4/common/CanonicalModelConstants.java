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
package net.fhirfactory.pegacorn.ladon.model.fhir.r4.common;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CanonicalModelConstants {

    private static final String CANONICAL_OPERATION_OUTCOME_DETAILS_SYSTEM = "https://www.hl7.org/fhir/codesystem-operation-outcome.html";
    private static final String CANONICAL_OPERATION_OUTCOME_VERSION = "4.0.1";

    public static String getCanonicalOperationOutcomeVersion() {
        return CANONICAL_OPERATION_OUTCOME_VERSION;
    }

    public String getCanonicalOperationOutcomeDetailsSystemPath() {
        return(CANONICAL_OPERATION_OUTCOME_DETAILS_SYSTEM);
    }
}
