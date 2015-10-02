package datamining;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import datamining.config.SalaryClassifierConstants;
import datamining.util.SalaryClassifierUtility;

/**
 * SalaryClassifier
 * 
 * Classify census data //TODO:...........
 * 
 */
public class SalaryClassifier {

	public static Instances inputDataSet;
	public static Instances testDataSet;
	
	
	/* Classifiers to be used in ensemble method */
	public static Classifier[] classifiers = {
			new J48(),
			new weka.classifiers.trees.NBTree(),
			new NaiveBayes(),
			};
	
	public static final int numOfBags = classifiers.length;
	public static Instances[] bags = new Instances[numOfBags];
	
	public static boolean runFeatureSelection = false; // Feature Selection flag
	public static boolean runImbalancedSelection = true; //Imbalance Selection Flag
	public static BufferedWriter outputWriter;
	public static void main(String[] args) throws Exception {
//		outputWriter = new BufferedWriter(new FileWriter("results"+System.currentTimeMillis()+".txt"));
		outputWriter = new BufferedWriter(new FileWriter("results.txt"));
		if(args.length!=3)
		{
			System.err.println("Incorrect usage! java -jar SalaryClassifier.jar <datafile> <testfile> <names file>");
			System.exit(3);
		}
		else
		{
			/* Take train file and test file given from input */
			SalaryClassifierConstants.ORIGINAL_INPUT = args[0];
			SalaryClassifierConstants.ORIGINAL_TEST = args[1];
			SalaryClassifierConstants.NAMES_FILE = args[2];
		}
		
		
		
		/* Properties file is not required for final submission
		 * for local its a great tool to save time by not processing
		 * input file again and again */
		
		SalaryClassifierUtility.processFiles(true);
		System.out.println("Executing various models on the given dataset.\nIt will take about 20 minutes to complete the execution.");
		
		
		/* Running different classifiers on data */
		outputWriter.write("---------------------Running Classifiers on given DataSet---------------------\n");
		SalaryClassifierUtility.runAllTheClassifiers(SalaryClassifier.inputDataSet,SalaryClassifier.testDataSet);
		System.out.println("Completed Basic Run");
		
		/* Ensemble method - Bagging with replacement -> Classify based on Voting (max of all classifier prediction)*/
		outputWriter.write("\n-------Ensemble Method--------\n");
		runImbalancedSelection=true;
        SalaryClassifierUtility.ensembleLearning(SalaryClassifier.inputDataSet,SalaryClassifier.testDataSet);
        System.out.println("Completed Ensemble Method");
		
		/* Selected Features*/
		outputWriter.write("\n---------------------Feature Selection---------------------\n");
		runImbalancedSelection=false;
		SalaryClassifierUtility.useSelectedFeatures();
		
		System.out.println("Completed Feature Selection");
		
		/* Over Sampling*/
		outputWriter.write("\n---------------------Over Sampling---------------------\n");
		SalaryClassifierUtility.overSampling();
		System.out.println("Completed Over Sampling");
		
		
		/* Over Sampling With Feature Selection */
		outputWriter.write("\n---------------------Over Sampling With Feature Selection---------------------\n");
		SalaryClassifierUtility.overSamplingWithFeatureSelection();
		System.out.println("Completed Over Sampling With Feature Selection");
		
		/*  SMOTE method to handle data Imbalance - Resamples a dataset by applying the Synthetic Minority Oversampling TEchnique (SMOTE)*/
		outputWriter.write("\n---------------------Running SMOTE---------------------\n");
		SalaryClassifierUtility.smote();
		System.out.println("Completed SMOTE");
		System.out.println("--------- Project Execution Completion ----------");
		outputWriter.write("\n--------- Project Execution Completion ----------\n");
		outputWriter.close();
	}
}
