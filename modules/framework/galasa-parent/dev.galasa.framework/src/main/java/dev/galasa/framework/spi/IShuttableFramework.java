/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.spi;

public interface IShuttableFramework extends IFramework {
    public void shutdown() throws FrameworkException ;
}
