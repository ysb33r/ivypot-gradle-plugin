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

import org.gradle.api.internal.artifacts.repositories.resolver.IvyResourcePattern
import org.gradle.api.internal.artifacts.repositories.resolver.M2ResourcePattern
import spock.lang.Specification


/**
 * @author Schalk W. Cronjé
 */
class PatternBasedResolverSpec extends Specification {

    def "Creating a default package resolver"() {
        given:
        def pbr = new PatternBasedResolver()

        expect:
        pbr.m2compatible == false
        pbr.artifactPatterns.empty
        pbr.ivyPatterns.empty
    }

    def "Adding artifact locations"() {
        given:
        def pbr = new PatternBasedResolver()
        pbr.addArtifactLocation('http://foo'.toURI(),'[module]-[version].[ext]')
        pbr.addArtifactLocation('http://bar'.toURI(),'[module].[version].[ext]')

        expect:
        pbr.artifactPatterns.size() == 2
        pbr.artifactPatterns[0].pattern == 'http://foo/[module]-[version].[ext]'
        pbr.artifactPatterns[1].pattern == 'http://bar/[module].[version].[ext]'
        pbr.artifactPatterns[0] instanceof IvyResourcePattern
    }

    def "Adding artifact locations M2"() {
        given:
        def pbr = new PatternBasedResolver()
        pbr.m2compatible = true
        pbr.addArtifactLocation('http://foo'.toURI(),'[module]-[version].[ext]')
        pbr.addArtifactLocation('http://bar'.toURI(),'[module].[version].[ext]')

        expect:
        pbr.artifactPatterns.size() == 2
        pbr.artifactPatterns[0].pattern == 'http://foo/[module]-[version].[ext]'
        pbr.artifactPatterns[1].pattern == 'http://bar/[module].[version].[ext]'
        pbr.artifactPatterns[0] instanceof M2ResourcePattern
    }

    def "Adding descriptor locations"() {
        given:
        def pbr = new PatternBasedResolver()
        pbr.addDescriptorLocation('http://foo'.toURI(),'ivy.xml')
        pbr.addDescriptorLocation('http://bar'.toURI(),'ivy2.xml')

        expect:
        pbr.ivyPatterns.size() == 2
        pbr.ivyPatterns[0].pattern == 'http://foo/ivy.xml'
        pbr.ivyPatterns[1].pattern == 'http://bar/ivy2.xml'
        pbr.ivyPatterns[0] instanceof IvyResourcePattern
    }

    def "Adding descriptor locations M2"() {
        given:
        def pbr = new PatternBasedResolver()
        pbr.m2compatible = true
        pbr.addDescriptorLocation('http://foo'.toURI(),'ivy.xml')
        pbr.addDescriptorLocation('http://bar'.toURI(),'ivy2.xml')

        expect:
        pbr.ivyPatterns.size() == 2
        pbr.ivyPatterns[0].pattern == 'http://foo/ivy.xml'
        pbr.ivyPatterns[1].pattern == 'http://bar/ivy2.xml'
        pbr.ivyPatterns[0] instanceof M2ResourcePattern
    }
}
