//
// ============================================================================
// (C) Copyright Schalk W. Cronje 2013-2017
//
// This software is licensed under the Apache License 2.0
// See http://www.apache.org/licenses/LICENSE-2.0 for license details
//
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and limitations under the License.
//
// ============================================================================
//

package org.ysb33r.gradle.ivypot.internal

import org.apache.commons.lang.NotImplementedException
import org.gradle.api.internal.artifacts.repositories.resolver.IvyResourcePattern
import org.gradle.api.internal.artifacts.repositories.resolver.M2ResourcePattern
import org.gradle.api.internal.artifacts.repositories.resolver.ResourcePattern
import org.gradle.api.internal.artifacts.repositories.resolver.PatternBasedResolver as GradlePatternBasedResolver

/**
 * @author Schalk W. Cronjé
 */
class PatternBasedResolver implements GradlePatternBasedResolver {

    boolean m2compatible = false
    List<ResourcePattern> artifactPatterns = []
    List<ResourcePattern> ivyPatterns = []

    @Override
    void addArtifactLocation(URI baseUri, String pattern) {
        ResourcePattern rp
        if(m2compatible) {
            rp = new M2ResourcePattern(baseUri, pattern)
        } else {
            rp = new IvyResourcePattern(baseUri, pattern)
        }
        artifactPatterns+= rp
    }

    @Override
    void addDescriptorLocation(URI baseUri, String pattern) {
        ResourcePattern rp
        if(m2compatible) {
            rp = new M2ResourcePattern(baseUri, pattern)
        } else {
            rp = new IvyResourcePattern(baseUri, pattern)
        }
        ivyPatterns+= rp
    }

}

