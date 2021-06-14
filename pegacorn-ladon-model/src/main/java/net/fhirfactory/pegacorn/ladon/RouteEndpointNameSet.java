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
package net.fhirfactory.pegacorn.ladon;

public class RouteEndpointNameSet {

    private static final String BUSINESS_UNIT_MANIFESTOR_INGRESS = "LadonStateSpaceTwinManifestor-BusinessUnit-Ingres";
    private static final String CARE_TEAM_MANIFESTOR_INGRESS = "LadonStateSpaceTwinManifestor-CareTeam-Ingres";
    private static final String ENDPOINT_MANIFESTOR_INGRESS = "LadonStateSpaceTwinManifestor-Endpoint-Ingres";
    private static final String GROUP_MANIFESTOR_INGRESS = "LadonStateSpaceTwinManifestor-Group-Ingres";
    private static final String HEALTHCARE_SERVICE_MANIFESTOR_INGRESS = "LadonStateSpaceTwinManifestor-HealthcareService-Ingres";
    private static final String ICTSYSTEM_MANIFESTOR_INGRESS = "LadonStateSpaceTwinManifestor-ICTSystem-Ingres";
    private static final String LOCATION_MANIFESTOR_INGRESS = "LadonStateSpaceTwinManifestor-Location-Ingres";
    private static final String ORGANIZATION_MANIFESTOR_INGRESS = "LadonStateSpaceTwinManifestor-Organization-Ingres";
    private static final String PATIENT_MANIFESTOR_INGRESS = "LadonStateSpaceTwinManifestor-Patient-Ingres";
    private static final String PERSON_MANIFESTOR_INGRESS = "LadonStateSpaceTwinManifestor-Person-Ingres";
    private static final String PRACTITIONER_MANIFESTOR_INGRESS = "LadonStateSpaceTwinManifestor-Practitioner-Ingres";
    private static final String PRACTITIONER_ROLE_MANIFESTOR_INGRESS = "LadonStateSpaceTwinManifestor-PractitionerRole-Ingres";


    public static String getBusinessUnitManifestorIngress() {
        return BUSINESS_UNIT_MANIFESTOR_INGRESS;
    }

    public static String getCareTeamManifestorIngress() {
        return CARE_TEAM_MANIFESTOR_INGRESS;
    }

    public static String getEndpointManifestorIngress() {
        return ENDPOINT_MANIFESTOR_INGRESS;
    }

    public static String getGroupManifestorIngress() {
        return GROUP_MANIFESTOR_INGRESS;
    }

    public static String getHealthcareServiceManifestorIngress() {
        return HEALTHCARE_SERVICE_MANIFESTOR_INGRESS;
    }

    public static String getIctsystemManifestorIngress() {
        return ICTSYSTEM_MANIFESTOR_INGRESS;
    }

    public static String getLocationManifestorIngress() {
        return LOCATION_MANIFESTOR_INGRESS;
    }

    public static String getOrganizationManifestorIngress() {
        return ORGANIZATION_MANIFESTOR_INGRESS;
    }

    public static String getPatientManifestorIngress() {
        return PATIENT_MANIFESTOR_INGRESS;
    }

    public static String getPersonManifestorIngress() {
        return PERSON_MANIFESTOR_INGRESS;
    }

    public static String getPractitionerManifestorIngress() {
        return PRACTITIONER_MANIFESTOR_INGRESS;
    }

    public static String getPractitionerRoleManifestorIngress() {
        return PRACTITIONER_ROLE_MANIFESTOR_INGRESS;
    }
}
