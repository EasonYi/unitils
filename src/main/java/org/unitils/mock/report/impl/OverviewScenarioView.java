/*
 * Copyright 2008,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.mock.report.impl;

import org.unitils.mock.core.Scenario;
import org.unitils.mock.invocation.ObservedInvocation;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.report.ScenarioView;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of a {@link ScenarioView} that just displays all the executed invocations.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class OverviewScenarioView implements ScenarioView {


    /**
     * Creates a string representation of the given scenario.
     *
     * @param testObject The test instance, null if there is no test object
     * @param scenario   The sceneario, not null
     * @return The string representation, not null
     */
    public String createView(Object testObject, Scenario scenario) {
        StringBuilder result = new StringBuilder();

        int invocationIndex = 1;
        Map<String, Integer> argumentIndexes = new HashMap<String, Integer>();

        for (ObservedInvocation mockInvocation : scenario.getObservedInvocations()) {
            result.append(invocationIndex++);
            result.append(".  ");
            if (invocationIndex > 10) {
                result.setLength(result.length() - 1);
            }
            result.append(createView(mockInvocation, argumentIndexes));
            result.append("\n");
        }

        result.append("\n");

        int argumentIndex = 1;
        for (ObservedInvocation mockInvocation : scenario.getObservedInvocations()) {
            if (!mockInvocation.getArgumentsAsStrings().isEmpty()) {
                for (String argument : mockInvocation.getArgumentsAsStrings()) {
                    if (argument.length() >= 10) {
                        result.append(" testClass");
                        result.append(argumentIndex++);
                        result.append(" -> ");
                        result.append(argument);
                        result.append("\n");
                    }
                }
            }
        }
        return result.toString();
    }


    protected String createView(ObservedInvocation observedInvocation, Map<String, Integer> argumentIndexes) {
        StringBuilder result = new StringBuilder();

        ProxyInvocation proxyInvocation = observedInvocation.getProxyInvocation();

        result.append(observedInvocation.getMockName());
        result.append('.');
        result.append(proxyInvocation.getMethod().getName());
        result.append('(');

        if (!observedInvocation.getArgumentsAsStrings().isEmpty()) {
            for (String argument : observedInvocation.getArgumentsAsStrings()) {
                if (argument.length() < 10) {
                    result.append(argument);
                } else {
                    result.append("testClass");

                    Integer index = argumentIndexes.get("testClass");
                    if (index == null) {
                        index = 0;
                    }
                    index++;
                    argumentIndexes.put("testClass", index);

                    result.append(index);
                }
                result.append(", ");
            }
            result.setLength(result.length() - 2);
        }

        result.append(")");
        if (observedInvocation.hasMockBehavior()) {
            result.append(") -> ");
            result.append(observedInvocation.getResultAsString());
        }
        result.append("  ..... at ");
        result.append(proxyInvocation.getInvokedAt());
        return result.toString();
    }

}