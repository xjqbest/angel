/*
 * Tencent is pleased to support the open source community by making Angel available.
 *
 * Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.tencent.angel.ml.matrix.psf.update.enhance.zip3;

import com.tencent.angel.common.Serialize;

/**
 * `Zip3MapFunc` is a parameter of [[Zip3Map]], if you want to  call a `Zip3Map` for a row in
 * matrix, you must implement a `Zip3MapFunc` as parameter of `Zip3Map`
 */
public interface Zip3MapFunc extends Serialize {
  double call(double value1, double value2, double value3);
}
