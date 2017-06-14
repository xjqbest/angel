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

package com.tencent.angel.ml.svm;

import com.tencent.angel.client.AngelClient;
import com.tencent.angel.client.AngelClientFactory;
import com.tencent.angel.conf.AngelConfiguration;
import com.tencent.angel.ml.classification.svm.SVMRunner;
import com.tencent.angel.ml.conf.MLConf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class SVMTest {

  private static final Log LOG = LogFactory.getLog(SVMTest.class);

  private Configuration conf = new Configuration();
  String LOCAL_FS = LocalFileSystem.DEFAULT_FS;
  String TMP_PATH = System.getProperty("java.io.tmpdir", "/tmp");

  static {
    PropertyConfigurator.configure("../conf/log4j.properties");
  }

  @Before
  public void setup() {
    // Feature number of train data
    int featureNum = 124;
    // Total iteration number
    int epochNum = 20;
    // Validation Ratio
    double vRatio = 0.1;
    // Data format
    String dataFmt = "libsvm";
    // Train batch number per epoch.
    double spRatio = 0.65;

    // Learning rate
    double learnRate = 0.1;
    // Decay of learning rate
    double decay = 0.01;
    // Regularization coefficient
    double reg = 0.001;

    // Set basic configuration keys
    conf.setBoolean("mapred.mapper.new-api", true);
    conf.setBoolean(AngelConfiguration.ANGEL_JOB_OUTPUT_PATH_DELETEONEXIST, true);

    // Set data format
    conf.set(MLConf.ML_DATAFORMAT(), dataFmt);

    // Use local deploy mode
    conf.set(AngelConfiguration.ANGEL_DEPLOY_MODE, "LOCAL");

    //set angel resource parameters #worker, #task, #PS
    conf.setInt(AngelConfiguration.ANGEL_WORKERGROUP_NUMBER, 1);
    conf.setInt(AngelConfiguration.ANGEL_WORKER_TASK_NUMBER, 1);
    conf.setInt(AngelConfiguration.ANGEL_PS_NUMBER, 1);

    //set sgd SVM algorithm parameters
    conf.set(MLConf.ML_FEATURE_NUM(), String.valueOf(featureNum));
    conf.set(MLConf.ML_EPOCH_NUM(), String.valueOf(epochNum));
    conf.set(MLConf.ML_BATCH_SAMPLE_Ratio(), String.valueOf(spRatio));
    conf.set(MLConf.ML_VALIDATE_RATIO(), String.valueOf(vRatio));
    conf.set(MLConf.ML_LEARN_RATE(), String.valueOf(learnRate));
    conf.set(MLConf.ML_LEARN_DECAY(), String.valueOf(decay));
    conf.set(MLConf.ML_REG_L2(), String.valueOf(reg));
  }

  @Test
  public void trainOnLocalClusterTest() throws Exception {
    // set input, output path
    String inputPath = "./src/test/data/lr/a9a.train";
    String logPath = "./src/test/log";

    conf.set(AngelConfiguration.ANGEL_TRAIN_DATA_PATH, inputPath);
    conf.set(AngelConfiguration.ANGEL_SAVE_MODEL_PATH, LOCAL_FS + TMP_PATH + "/SVMOut");
    // Set save model path
    conf.set(AngelConfiguration.ANGEL_SAVE_MODEL_PATH, LOCAL_FS + TMP_PATH + "/SVMModel");
    // Set actionType train
    conf.set(AngelConfiguration.ANGEL_ACTION_TYPE, MLConf.ANGEL_ML_TRAIN());
    // Set log path
    conf.set(AngelConfiguration.ANGEL_LOG_PATH, logPath);

    // Submit LR Train Task
    SVMRunner runner = new SVMRunner();
    runner.train(conf);
  }


  @Test
  public void incLearnTest()  throws IOException{
    String inputPath = "./src/test/data/lr/a9a.train";
    String logPath = "./src/test/log";

    // Set trainning data path
    conf.set(AngelConfiguration.ANGEL_TRAIN_DATA_PATH, inputPath);
    // Set load model path
    conf.set(AngelConfiguration.ANGEL_LOAD_MODEL_PATH, LOCAL_FS+TMP_PATH+"/SVMModel");
    // Set save model path
    conf.set(AngelConfiguration.ANGEL_SAVE_MODEL_PATH, LOCAL_FS + TMP_PATH + "/newSVMModel");
    // Set actionType incremental train
    conf.set(AngelConfiguration.ANGEL_ACTION_TYPE, MLConf.ANGEL_ML_INC_TRAIN());
    // Set log path
    conf.set(AngelConfiguration.ANGEL_LOG_PATH, logPath);
    SVMRunner runner = new SVMRunner();

    runner.train(conf);

    AngelClient angelClient = AngelClientFactory.get(conf);
    angelClient.stop();
  }
}
