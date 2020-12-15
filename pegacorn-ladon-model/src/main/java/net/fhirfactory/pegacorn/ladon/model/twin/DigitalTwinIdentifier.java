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
package net.fhirfactory.pegacorn.ladon.model.twin;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;

public class DigitalTwinIdentifier {
    Identifier twinResource;
    Identifier twinRegentResource;
    IdType twinResourceId;
    IdType twinRegentResourceId;

    public DigitalTwinIdentifier(Identifier twinResource, Identifier twinRegentResource) {
        this.twinResource = twinResource;
        this.twinRegentResource = twinRegentResource;
        this.twinResourceId = null;
        this.twinRegentResourceId = null;
    }

    public DigitalTwinIdentifier(Identifier twinResource, Identifier twinRegentResource, IdType twinResourceId, IdType twinRegentResourceId) {
        this.twinResource = twinResource;
        this.twinRegentResource = twinRegentResource;
        this.twinResourceId = twinResourceId;
        this.twinRegentResourceId = twinRegentResourceId;
    }

    public DigitalTwinIdentifier(IdType twinResourceId, IdType twinRegentResourceId) {
        this.twinResourceId = twinResourceId;
        this.twinRegentResourceId = twinRegentResourceId;
        this.twinResource = null;
        this.twinRegentResource = null;
    }

    public DigitalTwinIdentifier() {
        this.twinResource = null;
        this.twinRegentResource = null;
        this.twinResourceId = null;
        this.twinRegentResourceId = null;
    }

    public Identifier getTwinResource() {
        return twinResource;
    }

    public void setTwinResource(Identifier twinResource) {
        this.twinResource = twinResource;
    }

    public Identifier getTwinRegentResource() {
        return twinRegentResource;
    }

    public void setTwinRegentResource(Identifier twinRegentResource) {
        this.twinRegentResource = twinRegentResource;
    }

    public IdType getTwinResourceId() {
        return twinResourceId;
    }

    public void setTwinResourceId(IdType twinResourceId) {
        this.twinResourceId = twinResourceId;
    }

    public IdType getTwinRegentResourceId() {
        return twinRegentResourceId;
    }

    public void setTwinRegentResourceId(IdType twinRegentResourceId) {
        this.twinRegentResourceId = twinRegentResourceId;
    }
}
