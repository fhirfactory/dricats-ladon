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
package net.fhirfactory.pegacorn.ladon.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;

import java.io.Serializable;
import java.time.Instant;

public class TwinStatusType implements Serializable {
    private boolean active;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant activityStartInstant;
    private TwinIdType twinId;
    private TaskIdType activityTask;

    //
    // Constructor
    //

    public TwinStatusType(){
        active = false;
        activityStartInstant = Instant.now();
        twinId = null;
        activityTask = null;
    }

    //
    // Getters and Setters
    //

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getActivityStartInstant() {
        return activityStartInstant;
    }

    public void setActivityStartInstant(Instant activityStartInstant) {
        this.activityStartInstant = activityStartInstant;
    }

    public TwinIdType getTwinId() {
        return twinId;
    }

    public void setTwinId(TwinIdType twinId) {
        this.twinId = twinId;
    }

    public TaskIdType getActivityTask() {
        return activityTask;
    }

    public void setActivityTask(TaskIdType activityTask) {
        this.activityTask = activityTask;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TwinStatusType{" +
                "active=" + active +
                ", activityStartInstant=" + activityStartInstant +
                ", twinId=" + twinId +
                ", activityTask=" + activityTask +
                '}';
    }
}
