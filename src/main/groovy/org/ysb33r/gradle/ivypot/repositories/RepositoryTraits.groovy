//
// ============================================================================
// (C) Copyright Schalk W. Cronje 2013-2019
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

package org.ysb33r.gradle.ivypot.repositories

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.artifacts.repositories.AuthenticationContainer
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.ysb33r.gradle.ivypot.internal.Credentials
import org.ysb33r.grolifant.api.UriUtils

/**
 *
 */
@CompileStatic
trait RepositoryTraits {
    /**
     * The base URL of this repository. This URL is used to find both POMs and artifact files. You can add additional URLs to use to look for artifact files, such as jars, using {@link
     * # setArtifactUrls ( Iterable )}.
     *
     * @return The URL.
     */
    URI getUrl() {
        UriUtils.urize(this.url)
    }

    /**
     * Sets the base URL of this repository. This URL is used to find both POMs and artifact files. You can add additional URLs to use to look for artifact files, such as jars, using {@link
     * # setArtifactUrls ( Iterable )}.
     *
     * <p>The provided value is evaluated as per {@link org.gradle.api.Project#uri(Object)}. This means, for example, you can pass in a {@code File} object, or a relative path to be evaluated relative
     * to the project directory.
     *
     * @param url The base URL.
     */
    void setUrl(Object url_) {
        this.url = url_
    }

    /**
     * Sets the base URL of this repository. This URL is used to find both POMs and artifact files. You can add additional URLs to use to look for artifact files, such as jars, using {@link
     * # setArtifactUrls ( Iterable )}.
     *
     * <p>The provided value is evaluated as per {@link org.gradle.api.Project#uri(Object)}. This means, for example, you can pass in a {@code File} object, or a relative path to be evaluated relative
     * to the project directory.
     *
     * @param uri The base URI.
     *
     * @since 0.5
     */
    void setUrl(URI uri_) {
        this.url = uri_
    }

    /**
     * Sets the base URL of this repository. This URL is used to find both POMs and artifact files. You can add additional URLs to use to look for artifact files, such as jars, using {@link
     * # setArtifactUrls ( Iterable )}.
     *
     * <p>The provided value is evaluated as per {@link org.gradle.api.Project#uri(Object)}. This means, for example, you can pass in a {@code File} object, or a relative path to be evaluated relative
     * to the project directory.
     *
     * @param url The base URL.
     */
    void url(Object url_) {
        this.url = url_
    }

    /**
     * Provides the Credentials used to authenticate to this repository.
     * @return The credentials
     */
    PasswordCredentials getCredentials() {
        this.credentials
    }

    /**
     * Configure the Credentials for this repository using the supplied Closure.
     *
     * @code
     * repositories {
     *   maven {
     *     credentials {
     *       username = 'joe'
     *       password = 'secret'
     *     }
     *   }
     * }
     * @endcode
     */
    void credentials(Closure closure) {
        Closure cfg = (Closure)(closure.clone())
        cfg.delegate = this.credentials
        cfg()
    }

    /** Configure the credientials for this repository using the supplied action.
     *
     * @param action
     *
     * @since 0.5
     */
    void credentials(Action<? super PasswordCredentials> action) {
        action.execute(this.credentials)
    }

    /** Access to the credentials class that is in use.
     *
     * @param aClass
     * @return
     *
     * @since 0.5
     */
    def <T extends org.gradle.api.credentials.Credentials> T getCredentials(Class<T> aClass) {
        if (this.credentials instanceof T) {
            return (T)(this.credentials)
        } else {
            throw new IllegalArgumentException("${Credentials.class.name} cannot be converted to ${aClass.name}")
        }
    }

    /**
     *
     * @param aClass
     * @param action
     *
     * @since 0.5
     */
    def <T extends org.gradle.api.credentials.Credentials> void credentials(Class<T> aClass, Action<? super T> action) {
        T creds = getCredentials(aClass)
        action.execute(creds)
    }

    /**
     * Returns the name for this repository. A name must be unique amongst a repository set. A default name is provided for the repository if none
     * is provided.
     *
     * <p>The name is used in logging output and error reporting to point to information related to this repository.
     *
     * @return The name.
     */
    String getName() {
        this.name
    }

    /**
     * Sets the name for this repository.
     *
     * If this repository is to be added to an {@link org.gradle.api.artifacts.ArtifactRepositoryContainer}
     * (including {@link org.gradle.api.artifacts.dsl.RepositoryHandler}), its name should not be changed after it has
     * been added to the container. This capability has been deprecated and is scheduled to be removed in the next major
     * Gradle version.
     *
     * @param name The name. Must not be null.
     */
    void setName(String name) {
        this.name = name
    }

    void authentication(Action<? super AuthenticationContainer> action) {
        throw new GradleException("Authentication containers are not implemented. If this is a requirement for your use case then register your interest at https://github.com/ysb33r/ivypot-gradle-plugin/issues/24")
    }

    AuthenticationContainer getAuthentication() {
        throw new GradleException("Authentication containers are not implemented. If this is a requirement for your use case then register your interest at https://github.com/ysb33r/ivypot-gradle-plugin/issues/24")
    }


    private String name
    private Object url
    private Credentials credentials = new Credentials()

}
