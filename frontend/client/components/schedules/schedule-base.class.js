/**
 * Copyright 2017, deepsense.ai
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

'use strict';

// App
import { isEmail } from 'COMMON/helpers/validators';


export class ScheduleBaseClass {
  constructor($scope) {
    this.$scope = $scope;

    this.valid = true;
    this.error = {
      emailForReports: {},
      presetId: {}
    };
    this.model = {
      id: '',
      schedule: {
        cron: ''
      },
      executionInfo: {
        emailForReports: '',
        presetId: -1
      }
    };
  }


  $onInit() {
    this.$scope.$watch(
      () => this.model,
      (newValue, oldValue) => {
        if (!angular.equals(newValue, oldValue)) {
          this.validate();
        }
      },
      true
    );
  }


  validate() {
    this.error.presetId.required = !_.find(this.clusterPresets, {
      id: this.model.executionInfo.presetId
    });
    this.error.emailForReports.required = !this.model.executionInfo.emailForReports;
    this.error.emailForReports.email = !isEmail(this.model.executionInfo.emailForReports);

    this.valid = !(
      this.error.presetId.required ||
      this.error.emailForReports.required ||
      this.error.emailForReports.email
    );
  }
}