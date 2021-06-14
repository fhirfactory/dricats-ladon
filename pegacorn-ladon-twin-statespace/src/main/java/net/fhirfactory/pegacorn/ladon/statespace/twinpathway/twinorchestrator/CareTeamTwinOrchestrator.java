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
package net.fhirfactory.pegacorn.ladon.statespace.twinpathway.twinorchestrator;

import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.twinorchestrator.common.TwinOrchestratorBase;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathwaycontroller.common.TwinPathwayControllerBase;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathwaycontroller.common.TwinTypeEnum;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CareTeamTwinOrchestrator extends TwinOrchestratorBase {


    @Override
    protected TwinPathwayControllerBase specifyPathwayController() {
        return null;
    }

    @Override
    protected TwinTypeEnum specifyTwinType() {
        return (TwinTypeEnum.CARE_TEAM_TWIN);
    }
}
