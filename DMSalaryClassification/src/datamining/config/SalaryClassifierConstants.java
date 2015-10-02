package datamining.config;


/**
 * SalaryClassifierConstants defines all the constants
 * used in the SalaryClassifier project
 *
 */
public class SalaryClassifierConstants {

//	public static String RESOURCES       = "resources/";
	public static String RESOURCES       = "";
	public static String DATASET         = "census-income";
	
	public static String ORIGINAL_INPUT  = RESOURCES + "census-income.data";
	public static String ORIGINAL_TEST   = RESOURCES + "census-income.test";
	public static String NAMES_FILE      = RESOURCES + "census-income.names";
	
	public static String PROCESSED_INPUT = RESOURCES + "census-income-input.arff";
	public static String PROCESSED_TEST  = RESOURCES + "census-income-test.arff";
	
	public static String PROPERTIES_FILE = "sc.properties";
	public static boolean PROPERTIES_ENABLED = true;

}
