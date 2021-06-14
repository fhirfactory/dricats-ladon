/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.ladon.virtualdb.persistence.servers.common;

import net.fhirfactory.pegacorn.deployment.names.subsystems.LadonVirtualDBIMSubsystemComponentNames;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.ladon.virtualdb.dm.VirtualDBDefaultDMPropertyFile;
import net.fhirfactory.pegacorn.platform.edge.ask.InternalFHIRClientServices;

import javax.inject.Inject;

public abstract class PersistenceServerSecureAccessorBase extends InternalFHIRClientServices {

    @Inject
    private VirtualDBDefaultDMPropertyFile virtualDBProperties;

    @Inject
    private LadonVirtualDBIMSubsystemComponentNames componentNames;

    @Override
    protected String specifyFHIRServerService() {
        return (virtualDBProperties.getSubsystemInstant().getClusterServiceName());
    }

    @Override
    protected String specifyFHIRServerSubsystemName() {
        return (virtualDBProperties.getSubsystemInstant().getSubsystemName());
    }

    @Override
    protected String specifyFHIRServerSubsystemVersion() {
        return (virtualDBProperties.getSubsystemInstant().getSubsystemVersion());
    }
    @Override
    protected String specifyRequiredInterfaceName() {
        return ("FHIR");
    }

    @Override
    protected String specifyRequiredInterfaceVersion() {
        return ("4.0.1");
    }

}
