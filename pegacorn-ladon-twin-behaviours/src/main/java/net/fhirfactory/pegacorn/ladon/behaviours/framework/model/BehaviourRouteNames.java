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
package net.fhirfactory.pegacorn.ladon.behaviours.framework.model;

public class BehaviourRouteNames {
    private String behaviourName;

    private static String BEHAVIOUR_INGRES_CONDUIT_INGRES_POINT = ".IngresConduit.Ingres";
    private static String BEHAVIOUR_INGRES_CONDUIT_EGRESS_POINT = ".IngresConduit.Egress";
    private static String BEHAVIOUR_EGRESS_CONDUIT_INGRES_POINT = ".EgressConduit.Ingres";
    private static String BEHAVIOUR_EGRESS_CONDUIT_EGRESS_POINT = ".EgressConduit.Egress";
    private static String BEHAVIOUR_INGRES_POINT = ".Ingres";
    private static String BEHAVIOUR_EGRESS_POINT = ".Egress";

    public BehaviourRouteNames(String behaviourName){
        this.behaviourName = behaviourName;
    }

    public String getBehaviourName(){
        return(this.behaviourName);
    }

    public String getBehaviourIngresConduitIngresPoint(){
        String result = "direct:" + this.behaviourName + BEHAVIOUR_INGRES_CONDUIT_INGRES_POINT;
        return(result);
    }

    public String getBehaviourIngresConduitEgressPoint(){
        String result = "direct:" + this.behaviourName + BEHAVIOUR_INGRES_CONDUIT_EGRESS_POINT;
        return(result);
    }

    public String getBehaviourEgressConduitIngresPoint(){
        String result = "direct:" + this.behaviourName + BEHAVIOUR_EGRESS_CONDUIT_INGRES_POINT;
        return(result);
    }

    public String getBehaviourEgressConduitEgressPoint(){
        String result = "direct:" + this.behaviourName + BEHAVIOUR_INGRES_CONDUIT_INGRES_POINT;
        return(result);
    }

    public String getBehaviourIngresPoint(){
        String result = "direct:" + this.behaviourName + BEHAVIOUR_INGRES_POINT;
        return(result);
    }

    public String getBehaviourEgressPoint(){
        String result = "direct:" + this.behaviourName + BEHAVIOUR_EGRESS_POINT;
        return(result);
    }

    public String getRouteNameIngresConduitIngres2IngresConduitEgress(){
        String result = "RouteID:Behaviour." + this.behaviourName + ".IngresProcessorRoute";
        return(result);
    }

    public String getRouteNameIngressConduitEgress2BehaviourIngres(){
        String result = "RouteID:Behaviour." + this.behaviourName + ".IngresProcessor2BehaviourRoute";
        return(result);
    }

    public String getRouteNameBehaviourIngres2BehaviourEgress(){
        String result = "RouteID:Behaviour." + this.behaviourName;
        return(result);
    }

    public String getRouteNameEgressConduitIngres2EgressConduitEgress(){
        String result = "RouteID:Behaviour." + this.behaviourName + ".EgressProcessorRoute";
        return(result);
    }

    public String getRouteNameBehaviourEgress2EgressConduitIngres(){
        String result = "RouteID:Behaviour." + this.behaviourName + ".Behaviour2EgressProcessorRoute";
        return(result);
    }
}
