/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.resource.management.internal;

import java.time.Instant;

/**
 * Used to keep track of a test run which has no heartbeat, but we may want to 
 * clean up a certain time after we first noticed it in the DSS.
 */
public class RunWithNoHeartbeatRecord {
    private String testRunName ;

    private Instant whenNoHeartbeatFirstDetected;

    // When was this cache item last matched up with some debris in the DSS ?
    // If this is a long time ago, then the cache item is no longer needed, as the test run has already been 
    // cleaned up from the DSS.
    // In this sense, a long time ago is the defaultDeadHearbeatTimeSecs*2
    private Instant lastCheckedTime;

    public RunWithNoHeartbeatRecord(String testRunName, Instant whenFirstDetected ) {
        this.testRunName = testRunName ;
        this.whenNoHeartbeatFirstDetected = whenFirstDetected ;
        this.lastCheckedTime = whenFirstDetected ;
    }

    public String getTestRunName() {
        return this.testRunName;
    }

    public Instant getLastCheckedTime() {
        return this.lastCheckedTime;
    }
    public void setLastCheckedTime(Instant newLastCheckedTime) {
        this.lastCheckedTime = newLastCheckedTime;
    }

    public Instant getFirstDetectedTime() {
        return this.whenNoHeartbeatFirstDetected;
    }
}
