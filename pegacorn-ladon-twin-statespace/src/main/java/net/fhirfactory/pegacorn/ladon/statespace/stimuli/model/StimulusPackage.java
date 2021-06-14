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
package net.fhirfactory.pegacorn.ladon.statespace.stimuli.model;

import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourIdentifier;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import org.hl7.fhir.r4.model.Identifier;

public class StimulusPackage {
    private Identifier targetTwinIdentifier;
    private BehaviourIdentifier targetBehaviourIdentifier;
    private UoW stimulusContent;
    private StimulusPackageIdentifier id;

    public StimulusPackage(StimulusPackageIdentifier newId, Identifier targetTwin, BehaviourIdentifier targetBehaviour, UoW stimulusContent){
        this.targetTwinIdentifier = targetTwin;
        this.targetBehaviourIdentifier = targetBehaviour;
        this.id = newId;
        this.stimulusContent = stimulusContent;
    }

    public StimulusPackage(){
        this.targetBehaviourIdentifier = null;
        this.stimulusContent = null;
        this.id = null;
        this.stimulusContent = null;
    }


    public UoW getStimulusContent() {
        return stimulusContent;
    }

    public void setStimulusContent(UoW stimulusContent) {
        this.stimulusContent = stimulusContent;
    }

    public StimulusPackageIdentifier getId() {
        return id;
    }

    public void setId(StimulusPackageIdentifier id) {
        this.id = id;
    }

    public Identifier getTargetTwinIdentifier() {
        return targetTwinIdentifier;
    }

    public void setTargetTwinIdentifier(Identifier targetIdentifier) {
        this.targetTwinIdentifier = targetIdentifier;
    }

    public BehaviourIdentifier getTargetBehaviourIdentifier() {
        return targetBehaviourIdentifier;
    }

    public void setTargetBehaviourIdentifier(BehaviourIdentifier targetBehaviour) {
        this.targetBehaviourIdentifier = targetBehaviour;
    }
}
