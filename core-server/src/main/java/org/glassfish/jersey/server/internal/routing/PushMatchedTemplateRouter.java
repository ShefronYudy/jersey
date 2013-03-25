/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.server.internal.routing;

import javax.inject.Inject;
import javax.inject.Provider;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.uri.UriTemplate;


/**
 * Router that pushes {@link UriTemplate uri template} of matched resource of subResource
 * to {@link org.glassfish.jersey.server.internal.routing.RoutingContext routing context}.
 * Before calling this router the {@link PathPatternRouter} must be called which matches the path
 * and pushes the {@link java.util.regex.MatchResult matched result} into the routing context.
 *
 * @author Miroslav Fuksa (miroslav.fuksa at oracle.com)
 * @see RoutingContext#pushTemplates(org.glassfish.jersey.uri.UriTemplate...)
 */
class PushMatchedTemplateRouter implements Router {
    private final Provider<RoutingContext> routingContextProvider;
    private final UriTemplate resourceTemplate;
    private final UriTemplate methodTemplate;

    /**
     * Builder for creating
     * {@link org.glassfish.jersey.server.internal.routing.PushMatchedTemplateRouter push matched template router}
     * instances.  New builder instance must be injected and not directly created by constructor call.
     */
    static class Builder {
        @Inject
        private Provider<RoutingContext> routingContext;

        /**
         * Builds new instance of the router.
         * <p>
         * This builder method should be used in case a path matching has been performed on both a resource and method paths
         * (in case of sub-resource methods and locators).
         * </p>
         *
         * @param resourceTemplate resource URI template that should be pushed.
         * @param methodTemplate (sub-resource) method or locator URI template that should be pushed.
         * @return New instance of router created from this builder.
         */
        PushMatchedTemplateRouter build(UriTemplate resourceTemplate, UriTemplate methodTemplate) {
            return new PushMatchedTemplateRouter(routingContext, resourceTemplate, methodTemplate);
        }

        /**
         * Builds new instance of the router.
         * <p>
         * This builder method should be used in case a single path matching has been performed (in case of resource methods,
         * only the resource path is matched).
         * </p>
         *
         * @param resourceTemplate resource URI template that should be pushed.
         * @return New instance of router created from this builder.
         */
        PushMatchedTemplateRouter build(UriTemplate resourceTemplate) {
            return new PushMatchedTemplateRouter(routingContext, resourceTemplate, null);
        }
    }

    private PushMatchedTemplateRouter(Provider<RoutingContext> routingContextProvider,
                                      UriTemplate resourceTemplate,
                                      UriTemplate methodTemplate) {
        this.routingContextProvider = routingContextProvider;
        this.resourceTemplate = resourceTemplate;
        this.methodTemplate = methodTemplate;

    }

    @Override
    public Continuation apply(final ContainerRequest request) {
        final RoutingContext rc = routingContextProvider.get();
        rc.pushTemplates(resourceTemplate, methodTemplate);

        return Continuation.of(request);
    }
}