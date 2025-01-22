/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SupportedQueryParameterNames {

    public static final SupportedQueryParameterNames NO_QUERY_PARAMETERS_SUPPORTED = new SupportedQueryParameterNames();

    // A set of the names, in lower-case, optimized for lookup 
    private Set<String> supportedNamesSet = new HashSet<String>();

    // A pre-rendered sorted string containing the names
    private String renderedNames ;


    public SupportedQueryParameterNames( String... names ) {
        for( String name : names) {
            supportedNamesSet.add(name.trim().toLowerCase());
        }

        // Pre-render the list to a string so we only ever do it once.
        renderedNames = renderToString();
    }

    public String toString() {
        return renderedNames ;
    }

    private String renderToString() {
        List<String> supportedParamNamesSorted = new ArrayList<String>();
		supportedParamNamesSorted.addAll(supportedNamesSet);
		Collections.sort(supportedParamNamesSorted);
		String rendered = listToString(supportedParamNamesSorted);
        return rendered;
    }

    private String listToString( List<String> listToRender) {

		// Make sure the list we are passed is sorted.
		// We take a copy in  case the list is immutable.
		List<String> sortedlistToRender = new ArrayList<String>();
		sortedlistToRender.addAll(listToRender);
		Collections.sort(sortedlistToRender);

		StringBuilder buff = new StringBuilder();
		boolean isFirst = true ;
		for( String listItem : sortedlistToRender ) {
			if(!isFirst) {
				buff.append(",");
			}
			buff.append("'");
			buff.append(listItem);
			buff.append("'");
			isFirst = false ;
		}
		return buff.toString();
	}

    public boolean isSupported( String nameToCheck ) {
        return supportedNamesSet.contains(nameToCheck.trim().toLowerCase());
    }
    
}
