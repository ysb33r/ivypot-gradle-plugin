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
import org.apache.tools.ant.Project

@CompileStatic
class Downloader {
    static void main(String[] args) {
        if (args.size() != 1) {
            throw new RuntimeException('No serialised location specified')
        }

        ExecutionData executionData = ExecutionData.deserializeData(new File(args[0]))

        if(!executionData.dependencies.empty) {
            IvyAnt ivyAnt = new IvyAnt(executionData.ivySettings)
            ivyAnt.logLevel = executionData.logLevel
            ivyAnt.resolve(
                    executionData.ivyRepoRoot,
                    executionData.dependencies,
                    executionData.overwrite
            )
        }

        if(!executionData.binaries.empty) {
            BinaryDownloader binaries = new BinaryDownloader(
                    executionData.binaryRepoRoot,
                    executionData.binaryRepositories,
                    executionData.logLevel >= Project.MSG_INFO
            )
            binaries.resolve(
                    executionData.binaries,
                    executionData.overwrite
            )
        }
    }
}
