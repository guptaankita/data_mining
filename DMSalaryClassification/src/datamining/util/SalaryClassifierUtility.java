package datamining.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.C45Loader;
import weka.filters.Filter;
import weka.filters.supervised.instance.SMOTE;
import datamining.SalaryClassifier;
import datamining.config.SalaryClassifierConstants;

/**
 * SalaryClassifierUtility contains all the utility functions related to
 * SalaryClassifier
 * 
 */
public class SalaryClassifierUtility {

	/**
	 * Function to process input files. if processInput=true, then create the
	 * arff files for input and then read if processInput=false, then directly
	 * load train and test data Output: load train and test data
	 * 
	 * @param processInput
	 */
	public static void processFiles(boolean processInput) {

		C45Loader c45DataLoader = new C45Loader(); /* Loads .names file */
		ArffLoader arffDataLoader = new ArffLoader(); /* Loads .arff file */

		try {
			if (processInput) {
				c45DataLoader.setSource(new File(
						SalaryClassifierConstants.NAMES_FILE));

				BufferedWriter bwInput;
				BufferedWriter bwTest;

				BufferedReader brInput;
				BufferedReader brTest;

				bwInput = new BufferedWriter(new FileWriter(
						SalaryClassifierConstants.PROCESSED_INPUT));
				bwTest = new BufferedWriter(new FileWriter(
						SalaryClassifierConstants.PROCESSED_TEST));

				bwInput.write(c45DataLoader.getStructure().toString());
				bwTest.write(c45DataLoader.getStructure().toString());

				brInput = new BufferedReader(new FileReader(
						SalaryClassifierConstants.ORIGINAL_INPUT));
				brTest = new BufferedReader(new FileReader(
						SalaryClassifierConstants.ORIGINAL_TEST));

				String line;
				while ((line = brInput.readLine()) != null) {
					bwInput.newLine();
					bwInput.write(line);
				}

				brInput.close();
				bwInput.close();

				while ((line = brTest.readLine()) != null) {
					bwTest.newLine();
					bwTest.write(line.replaceAll("(\\.)$", ""));
				}

				bwTest.close();
				brTest.close();
			}

			/* Load the arff file into the input dataset */
			arffDataLoader.setSource(new File(
					SalaryClassifierConstants.PROCESSED_INPUT));
			SalaryClassifier.inputDataSet = arffDataLoader.getDataSet();

			/* Load the arff file into the test dataset */
			arffDataLoader.setSource(new File(
					SalaryClassifierConstants.PROCESSED_TEST));
			SalaryClassifier.testDataSet = arffDataLoader.getDataSet();

			if (SalaryClassifier.inputDataSet.classIndex() == -1)
				SalaryClassifier.inputDataSet
						.setClassIndex(SalaryClassifier.inputDataSet
								.numAttributes() - 1);

			SalaryClassifier.outputWriter.write("Train Instances : "
					+ SalaryClassifier.inputDataSet.numInstances()+"\n");

			if (SalaryClassifier.testDataSet.classIndex() == -1)
				SalaryClassifier.testDataSet
						.setClassIndex(SalaryClassifier.inputDataSet
								.numAttributes() - 1);

			SalaryClassifier.outputWriter.write("Test Instances  : "
					+ SalaryClassifier.testDataSet.numInstances() + "\n");

		} catch (IOException e) {
			System.out
					.println("Error in retrieving file. Exiting the Program now.");
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * //TODO: please fill
	 * @throws IOException 
	 */
	// TODO: This function needs to be modified to work for different types of
	// feature selection
	// as well as for bagging
	// Modification can be - function will take some dataset as input and return
	// back
	// feature selected dataset
	public static void featureSelection() throws IOException {
		try {
			weka.filters.supervised.attribute.AttributeSelection filter = new weka.filters.supervised.attribute.AttributeSelection();
			filter.setInputFormat(SalaryClassifier.inputDataSet);
			CfsSubsetEval eval = new CfsSubsetEval();
			GreedyStepwise search = new GreedyStepwise();
			filter.setEvaluator(eval);
			filter.setSearch(search);
			filter.setInputFormat(SalaryClassifier.inputDataSet);
			SalaryClassifier.inputDataSet = Filter.useFilter(
					SalaryClassifier.inputDataSet, filter);
			SalaryClassifier.testDataSet = Filter.useFilter(
					SalaryClassifier.testDataSet, filter);
		} catch (Exception e) {
			SalaryClassifier.outputWriter.write("Error in Feature Selection!");
			e.printStackTrace();
		}

	}
	
	/**
	 * Function - smote 
	 * Resamples a dataset by applying the Synthetic Minority Oversampling TEchnique (SMOTE).
	 * @throws Exception
	 */
	public static void smote() {
		try {
			Instances testInstances = new Instances(
					SalaryClassifier.testDataSet);
			SMOTE filters = new SMOTE();
			filters.setInputFormat(SalaryClassifier.inputDataSet); // Instances
			filters.setPercentage(100);
			filters.setNearestNeighbors(3);
			Instances smoteInstances = Filter.useFilter(
					SalaryClassifier.inputDataSet, filters);
			smoteInstances.randomize(new Random(45));
			runAllTheClassifiers(smoteInstances, testInstances);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to run individual classifier. Trains complete input data set and
	 * classifies as per given classifier on the test data set.
	 * 
	 * Output: Classifier name & accuracy over all the data
	 * 
	 * @param classifier
	 * @throws IOException 
	 */
	public static void runModel(Classifier classifier,Instances inputDataSet, Instances testDataSet) throws IOException {

		FastVector predictions = SalaryClassifierUtility.classify(classifier,
				inputDataSet, inputDataSet);
		
		double trainAccuracyOfModel = SalaryClassifierUtility
				.calcAccuracyFromNominalPrediction(predictions);
		
		predictions = SalaryClassifierUtility.classify(classifier,
				inputDataSet, testDataSet);
		
		double testAccuracyOfModel = SalaryClassifierUtility
				.calcAccuracyFromNominalPrediction(predictions);
		
		SalaryClassifierUtility.printClassifier(classifier, trainAccuracyOfModel, testAccuracyOfModel);
	}

	/**
	 * Function - separateClassInstances (Bagging with replacement + Voting)
	 * 
	 * Tasks: 1. Divides input data set in different instances
	 * 
	 * Output: An array with two instances separated by class labels
	 */
	public static Instances[] separateClassInstances(Instances trainData, Instances testData) {
		Instances[] classLabels = new Instances[2];
		Instances moreThanFifty = new Instances(trainData);
		Instances lessThanFifty = new Instances(trainData);
		moreThanFifty.delete();
		lessThanFifty.delete();
		for (int numInstance = 0; numInstance < (trainData
				.numInstances()); numInstance++) {
			double currentClass = trainData.instance(
					numInstance).classValue();
			if (currentClass == 0.0) {
				lessThanFifty.add(trainData.instance(numInstance));
			} else {
				moreThanFifty.add(trainData.instance(numInstance));
			}
		}
		classLabels[0] = lessThanFifty;
		classLabels[1] = moreThanFifty;
		return classLabels;

	}
	
	/**
	 * Function - overSamplingWithFeatureSelection
	 * 
	 * Over-sample the given train data and apply feature selection 
	 * Run all the classification models on this modified train data
	 * 
	 */
	public static void overSamplingWithFeatureSelection() throws IOException
	{
		Instances trainInstances = new Instances(SalaryClassifier.inputDataSet);
		Instances testInstances = new Instances(SalaryClassifier.testDataSet);
		Instances overSampledTrainInstances = new Instances(SalaryClassifier.inputDataSet);
		overSampledTrainInstances.delete();
		for(int i=0;i<trainInstances.numInstances();i++){
			Instance trainInstance = trainInstances.instance(i);
			
			if(trainInstance.classValue()==0)
			{
				overSampledTrainInstances.add(trainInstance);
				overSampledTrainInstances.add(trainInstance);
			}
			overSampledTrainInstances.add(trainInstance);
		}
		SalaryClassifier.outputWriter.write("Total Instances:"+ overSampledTrainInstances.numInstances()+"\n");
		int count=0;
		int ocount=0;
		for(int i=0;i<overSampledTrainInstances.numInstances();i++)
		{
			Instance t = overSampledTrainInstances.instance(i);
			if(t.classValue()==0)
			{
				count++;
			}
			else
			{
				ocount++;
			}
		}
		SalaryClassifier.outputWriter.write("Instances of Class : >50K - "+count+"\n");
		SalaryClassifier.outputWriter.write("Instances of Class : <=50K - "+ocount+"\n");
		trainInstances = new Instances(overSampledTrainInstances);
		int[] features = new int[]{2,3,4,6,7,14};
		int k = 0;
		for(int i : features)
		{
			trainInstances.deleteAttributeAt(i-k-1);
			testInstances.deleteAttributeAt(i-k-1);
			k++;
		}
		SalaryClassifier.outputWriter.write("Removing attributes 2,3,4,6,7,14\n");
		/*-----Run All other classifiers after over-sampling and feature selection-----*/
		runAllTheClassifiers(trainInstances, testInstances);
	}
	
	/**
	 * Function - overSampling
	 * 
	 * Over-sample the given train data 
	 * Run all the classification models on this modified train data
	 * 
	 */
	public static void overSampling() throws IOException
	{
		Instances trainInstances = new Instances(SalaryClassifier.inputDataSet);
		Instances testInstances = new Instances(SalaryClassifier.testDataSet);
		Instances overSampledTrainInstances = new Instances(SalaryClassifier.inputDataSet);
		overSampledTrainInstances.delete();
		for(int i=0;i<trainInstances.numInstances();i++){
			Instance trainInstance = trainInstances.instance(i);
			
			if(trainInstance.classValue()==0)
			{
				overSampledTrainInstances.add(trainInstance);
				overSampledTrainInstances.add(trainInstance);
			}
			overSampledTrainInstances.add(trainInstance);
		}
		SalaryClassifier.outputWriter.write("Total Instances:"+ overSampledTrainInstances.numInstances()+"\n");
		int count=0;
		int ocount=0;
		for(int i=0;i<overSampledTrainInstances.numInstances();i++)
		{
			Instance t = overSampledTrainInstances.instance(i);
			if(t.classValue()==0)
			{
				count++;
			}
			else
			{
				ocount++;
			}
		}
		SalaryClassifier.outputWriter.write("Instances of Class : >50 - "+count+"\n");
		SalaryClassifier.outputWriter.write("Instances of Class : <=50 - "+ocount+"\n");
		trainInstances = new Instances(overSampledTrainInstances);
		SalaryClassifier.outputWriter.write("-----OverSampling-----\n");
		/*-----Run All other classifiers on oversampled dataset-----*/
		SalaryClassifier.outputWriter.write("-----Run all other classifiers-----\n");
		runAllTheClassifiers(trainInstances, testInstances);
		
	}
	/**
	 * Function - runAllTheClassifiers method runs all the classifiers 
	 * we want to run on our train and test set
	 * @param trainInstances
	 * @param testInstances
	 * @throws IOException 
	 */
	public static void runAllTheClassifiers(Instances trainInstances,
			Instances testInstances) throws IOException {
		
		/* Decision Tree */
		Classifier classifierJ48DT = new J48();
//		SalaryClassifier.outputWriter.write("---------Running J48---------\n");
		SalaryClassifierUtility.runModel(classifierJ48DT,trainInstances,testInstances);
		
		/* NBTree */
//		SalaryClassifier.outputWriter.write("---------Running NBTree---------\n");
		Classifier classifierNBTree = new weka.classifiers.trees.NBTree();
		SalaryClassifierUtility.runModel(classifierNBTree,trainInstances,testInstances);		
		
		/* NaiveBayes */
//		SalaryClassifier.outputWriter.write("---------Running Naive Bayes---------\n");
		Classifier classifierNB = new NaiveBayes();
		SalaryClassifierUtility.runModel(classifierNB,trainInstances,testInstances);
		
		/*Logistic */
//		SalaryClassifier.outputWriter.write("---------Running Logistic Regression---------\n");
		Classifier classifierLogistic = new Logistic();
		SalaryClassifierUtility.runModel(classifierLogistic,trainInstances,testInstances);
	
		/* SMO */
//		SalaryClassifier.outputWriter.write("---------Running SMO---------\n");
		SMO smo = new SMO();
		smo.setC(0.01);
		SalaryClassifierUtility.runModel(smo, trainInstances, testInstances);
	}
	
	
	/**
	 * Function - Remove Features (2,3,4,6,7,14)
	 * 
	 * Output: Accuracy
	 * @throws IOException 
	 */
	public static void useSelectedFeatures() throws IOException
	{
		Instances trainInstances = new Instances(SalaryClassifier.inputDataSet);
		Instances testInstances = new Instances(SalaryClassifier.testDataSet);
		int[] features = new int[]{2,3,6,7,14};
		int k = 0;
		for(int i : features)
		{
			trainInstances.deleteAttributeAt(i-k-1);
			testInstances.deleteAttributeAt(i-k-1);
			k++;
		}
		SalaryClassifier.outputWriter.write("-----Remove Features workclass,fnlwgt,education,marital-status,occupation,native-country-----\n");
		/*-----Run All other classifiers-----*/
		runAllTheClassifiers(trainInstances, testInstances);
		/*-----Running Ensemble-----*/
		SalaryClassifier.outputWriter.write("-----Ensemble with feature selection-----\n");
		SalaryClassifierUtility.ensembleLearning(trainInstances, testInstances);
	}
	/**
	 * Function - Ensemble (Bagging with replacement + Voting)
	 * 
	 * Tasks: 1. Divides input data set in different bags 2. Runs different
	 * classifiers individually on the bags 3. Classifier test data on each of
	 * the bags 4. Predicts value by counting maximum number of votes
	 * 
	 * Output: Accuracy
	 * @throws IOException 
	 */
	public static void ensembleLearning(Instances trainData, Instances testData) throws IOException {

		Random randomNumber = new Random(75); // TODO: find best one. Varying
												// this number varies output
		Instances[] classLabels = null;
		if (SalaryClassifier.runImbalancedSelection) {
			//Generate 2 sets of data
			classLabels = separateClassInstances(trainData,testData);

		}
		/* For printing classifiers that are used on bags */
		SalaryClassifier.outputWriter.write("Following classifiers implemented in bags : \n"); 
		/* Creating Bags for creating individual classifiers */
		for (int num = 0; num < SalaryClassifier.numOfBags; num++) {

			SalaryClassifier.bags[num] = new Instances(
					trainData); // TODO: Modify this code to
													// just provide structure
			SalaryClassifier.bags[num].delete();

			/*
			 * Bag size approximately 2*dataSet/numOfBags - Creating Bags with
			 * replacement
			 */
			for (int numInstance = 0; numInstance < trainData
					.numInstances()*2/SalaryClassifier.numOfBags; numInstance++) {

				if (!SalaryClassifier.runImbalancedSelection) {
					Instance instance = trainData
							.instance(randomNumber
									.nextInt(trainData
											.numInstances()));
					SalaryClassifier.bags[num].add(instance);
				} else {
					Instance instance;
					if (numInstance % 2 == 0) {
						instance = classLabels[1].instance((randomNumber
								.nextInt(classLabels[1].numInstances())));
					} else {
						instance = classLabels[0].instance((randomNumber
								.nextInt(classLabels[0].numInstances())));
					}
					SalaryClassifier.bags[num].add(instance);

				}
			}

			// TODO: Remove these lines during submission
			// Remove comment to get the size of the bags
			// SalaryClassifier.outputWriter.write("Size of Bag "+ num + " : " +
			// SalaryClassifier.bags[num].numInstances());

			// TODO: Feature Selection/Missing Data Experimentation on different
			// bags
			// keeping the code modular/generic so that it runs different types
			// on different bags and combinations can be tried without
			// commenting the code. Feature selection code can be modified to
			// take dataset as input and output the feature selected dataset

			Classifier classifier = SalaryClassifier.classifiers[num];
			try {
				SalaryClassifier.outputWriter.write(classifier.getClass().getSimpleName()+"\n");
				classifier.buildClassifier(SalaryClassifier.bags[num]);
				
			} catch (Exception e) {
				System.err
						.println("Error in building classifier for bag" + num);
				e.printStackTrace();
			}
		}

		/* Testing over all instances with voting */
		try {
			Evaluation evaluation = new Evaluation(
					SalaryClassifier.inputDataSet);
			FastVector prediction = new FastVector();
			double[] classPredictions = { 0.0, 1.0 }; /*
													 * Defining 2 class
													 * prediction for the 2
													 * classes <=50k and >50K
													 * salary predictions
													 */

			for (int num = 0; num < testData.numInstances(); num++) {

				int[] classes = new int[trainData
						.numClasses()];
				Instance instance = testData.instance(num);

				for (int model = 0; model < SalaryClassifier.classifiers.length; model++) {
					double predictionValue = evaluation.evaluateModelOnce(
							SalaryClassifier.classifiers[model], instance);
					classes[(int) predictionValue]++;
				}

				// Predicting either of the 2 classes
				if (classes[0] >= classes[1]) {
					prediction.addElement(classPredictions[0]);
				} else {
					prediction.addElement(classPredictions[1]);
				}
			}

			double accuracyOfModel = SalaryClassifierUtility
					.calcAccuracyFromNumericalPrediction(prediction,
							testData);
			SalaryClassifier.outputWriter.write("Test Accuracy : "
					+ String.format("%.4f %%", accuracyOfModel) + "\n");
			double accuracyOfTrainSet = SalaryClassifierUtility
					.calcAccuracyFromNumericalPrediction(prediction,
							trainData);
			SalaryClassifier.outputWriter.write("Train Accuracy : "
					+ String.format("%.4f %%", accuracyOfTrainSet) + "\n");

		} catch (Exception e) {
			System.err.println("Error in evaluation - bagging");
			e.printStackTrace();
		}
	}

	/**
	 * Function to classify training data and evaluate testing data as per
	 * specified model
	 * 
	 * @param classifier
	 * @param trainDataSet
	 * @param testDataSet
	 * @return FastVector
	 * @throws IOException 
	 */
	public static FastVector classify(Classifier classifier,
			Instances trainDataSet, Instances testDataSet) throws IOException {

		FastVector predictions = new FastVector();
		Evaluation evaluation;
		try {

			classifier.buildClassifier(trainDataSet);

			evaluation = new Evaluation(trainDataSet);
			evaluation.evaluateModel(classifier, testDataSet);

			predictions.appendElements(evaluation.predictions());

		} catch (Exception e) {
			SalaryClassifier.outputWriter.write("Error in evaluating classifer: "
					+ classifier.toString());
			e.printStackTrace();
		}

		return predictions;
	}

	/**
	 * Function to calculate the accuracy of the nominal predictions
	 * 
	 * @param predictions
	 * @return accuracy
	 */
	public static double calcAccuracyFromNominalPrediction(
			FastVector predictions) {

		double accuratelyPredicted = 0;

		for (int num = 0; num < predictions.size(); num++) {
			NominalPrediction np = (NominalPrediction) predictions
					.elementAt(num);
			if (np.predicted() == np.actual()) {
				accuratelyPredicted++;
			}
		}

		return (accuratelyPredicted / predictions.size()) * 100;
	}

	/**
	 * Function to calculate the accuracy of the numerical predictions
	 * 
	 * @param predictions
	 * @return accuracy
	 */
	public static double calcAccuracyFromNumericalPrediction(
			FastVector predictions, Instances testDataSet) {

		double accuratelyPredicted = 0;
		for (int num = 0; num < predictions.size(); num++) {
			double predictionValue = (Double) predictions.elementAt(num);
			if (predictionValue == testDataSet.instance(num).classValue()) {
				accuratelyPredicted++;
			}
		}

		return (accuratelyPredicted / predictions.size()) * 100;
	}

	/**
	 * Function to print the classifier name, accuracy
	 * 
	 * @param classifierName
	 * @throws IOException 
	 */
	public static void printClassifier(Classifier classifierName,
			double accuracy, double testAccuracy) throws IOException {

		SalaryClassifier.outputWriter.write("Model    : "
				+ classifierName.getClass().getSimpleName()+"\n");
		SalaryClassifier.outputWriter.write("Train Accuracy : " + String.format("%.4f %%", accuracy)
				+ "\n");
		SalaryClassifier.outputWriter.write("Test Accuracy : " + String.format("%.4f %%", testAccuracy)
				+ "\n\n");
	}

}