/*
 * Copyright 2013-2024, Seqera Labs
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
 *
 */

package nextflow.k8s.client

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import nextflow.util.Duration

/**
 * Model retry policy configuration
 * 
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
@ToString(includePackage = false, includeNames = true)
@EqualsAndHashCode
@CompileStatic
class K8sRetryConfig {
    Duration delay = Duration.of('250ms')
    Duration maxDelay = Duration.of('90s')
    int maxAttempts = 4
    double jitter = 0.25

    K8sRetryConfig() {
        this(Collections.emptyMap())
    }

    K8sRetryConfig(Map config) {
        if( config.delay )
            delay = config.delay as Duration
        if( config.maxDelay )
            maxDelay = config.maxDelay as Duration
        if( config.maxAttempts )
            maxAttempts = config.maxAttempts as int
        if( config.jitter )
            jitter = config.jitter as double
    }
}
