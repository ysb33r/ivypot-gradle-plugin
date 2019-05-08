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

package org.ysb33r.gradle.ivypot.remote

import groovy.transform.CompileStatic

@CompileStatic
class ExecutionData implements Serializable {

    boolean overwrite
    int logLevel
    File ivySettings
    File ivyRepoRoot
    File binaryRepoRoot

    List<IvyDependency> dependencies = []
    Map<String,BinaryRepositoryDescriptor> binaryRepositories = [:]
    List<BinaryDependency> binaries = []

    static void serializeData(File destination, ExecutionData data) {
        destination.parentFile.mkdirs()
        destination.withOutputStream { out ->
            new ObjectOutputStream(out).withCloseable { oos ->
                oos.writeObject(data)
            }
        }
    }

    static ExecutionData deserializeData(File source) {
        ExecutionData ret
        source.withInputStream { input ->
            new ObjectInputStream(input).withCloseable { ois ->
                ret = (ExecutionData) ois.readObject()
            }
        }
        ret
    }
}
