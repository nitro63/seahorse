/**
 * Copyright 2016, deepsense.ai
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
 */

package io.deepsense.deeplang.doperables

import io.deepsense.deeplang.UnitSpec
import io.deepsense.deeplang.doperables.spark.wrappers.models.MultiColumnStringIndexerModel
import io.deepsense.deeplang.params.ParamMap

class MultiColumnModelSpec extends UnitSpec {

  "MultiColumnModel" should {
    "not fail during replicate" in {
      val model = new MultiColumnStringIndexerModel()
      model.setModels(Seq())
      model.replicate(ParamMap.empty)
    }
  }
}