/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.common;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.galasa.framework.spi.FrameworkException;
import dev.galasa.framework.spi.rbac.Action;

/**
 * IRoute provides methods for endpoints to implement when a request is sent through a servlet,
 * allowing for new routes to be added without needing servlets to know which route handles the request.
 *
 * Route paths represent the regex patterns that are used to match request paths against.
 */
public interface IRoute {
    Pattern getPathRegex();

    HttpServletResponse handleGetRequest(String pathInfo, QueryParameters queryParams, HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, FrameworkException;

    HttpServletResponse handlePutRequest(String pathInfo, QueryParameters queryParameters, HttpServletRequest request , HttpServletResponse response)
        throws ServletException, IOException, FrameworkException;

    HttpServletResponse handlePostRequest(String pathInfo, QueryParameters queryParameters, HttpServletRequest request , HttpServletResponse response)
        throws ServletException, IOException, FrameworkException;

    HttpServletResponse handleDeleteRequest(String pathInfo, QueryParameters queryParameters, HttpServletRequest request ,HttpServletResponse response)
    throws ServletException, IOException, FrameworkException;

    /**
     * Checks if the given action is permitted for the user that sent the given request
     * 
     * @param action the action being performed
     * @param request the request containing an auth header with a bearer token for the current user
     * @return true if the user is allowed to perform the given action, false otherwise
     * @throws InternalServletException if the action is not permitted or if there was an issue accessing the RBAC service
     */
    boolean isActionPermitted(Action action, HttpServletRequest request) throws InternalServletException;
}
