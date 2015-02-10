import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

import Jama.Matrix;


public class LinearRegressionQuestion1 {
	
	
	public static void main(String args[]){
		String cvsFileToRead ="train-100-10.csv";
		String sfilename = "output-100-10.csv";
		String testfile = "test-100-10.csv";
		
		String train_100_100 ="train-100-100.csv";
		String output_100_100 = "output-100-100.csv";
		String test_100_100 = "test-100-100.csv";
		
		String train_1000_100 ="train-1000-100.csv";
		String output_1000_100 = "output-1000-100.csv";
		String test_1000_100 = "test-1000-100.csv";
      
		String output_100 = "output-100.csv";
		String output_50_100 = "output-50-100.csv";
		String output_150_100 = "output-150-100.csv";



		
		operation(cvsFileToRead,sfilename,testfile,10,100,1000);
		operation(train_100_100,output_100_100,test_100_100,100,100,1000);
		operation(train_1000_100,output_1000_100,test_1000_100,100,1000,1000);
		operationonsplit(train_1000_100,output_50_100,test_1000_100,100,50,1000);
		operationonsplit(train_1000_100,output_100,test_1000_100,100,100,1000);
		operationonsplit(train_1000_100,output_150_100,test_1000_100,100,150,1000);




	}
	static void operationonsplit(String cvsFileToRead,String sfilename,String testfile, int col, int rowTrain, int rowTest) {
		FileWriter writer = null;
		try{
			writer = new FileWriter(sfilename);
		
	    writer.append("Lambda");
	    writer.append(',');
	    writer.append("MSE");
	    writer.append(',');
	    writer.append("testerror");
	    writer.append('\n');

		}catch(Exception e)
		{
			
		}

		BufferedReader br = null;
		String line = "";
		String linetest = "";
		String splitBy = ",";
		double arr[][] = new double [rowTrain][col+1];
		double xtest[][] = new double[rowTest][col+1];
		double ytest[] = new double[rowTest];
		double y[] = new double[rowTrain];
		int r=0;
		int c=0;
		int tr = 0;
		int tc = 0;
		int i;
		double min = Double.POSITIVE_INFINITY;
		double tempmin=0;

		int count =1;
		
		double temp = 0.00;
	
			try {
				br = new BufferedReader(new FileReader(cvsFileToRead));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				line = br.readLine();
				String line1 = null;
				while((line1=br.readLine())!=null)
				{  if(count>=rowTrain)
					 break;
				arr[r][0]=1;
					String[] x = line1.split(splitBy);
					for( i=0;i<col;i++)
					{   String aux = x[i];
					NumberFormat nf = NumberFormat.getInstance();
						
						try {
							temp = nf.parse(aux).doubleValue();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						arr[r][i+1] = temp;
					}
					String yaux = x[i];
					NumberFormat nf = NumberFormat.getInstance();
					double ytemp = 0;
					try {
						ytemp = nf.parse(yaux).doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                     y[c++] = ytemp;
					r++;
					count++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				BufferedReader brtest = null;
		try {
			brtest = new BufferedReader(new FileReader(testfile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			linetest = brtest.readLine();
			String line1test = null;
			while((line1test=brtest.readLine())!=null)
			{
				xtest[tr][0]=1;
				String[] x = line1test.split(splitBy);
				for( i=0;i<col;i++)
				{   String aux = x[i];
				NumberFormat nf = NumberFormat.getInstance();
					
					try {
						temp = nf.parse(aux).doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					xtest[tr][i+1] = temp;
				}
				String yaux = x[i];
				NumberFormat nf = NumberFormat.getInstance();
				double ytemp = 0;
				try {
					ytemp = nf.parse(yaux).doubleValue();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                 ytest[tc++] = ytemp;
				tr++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
				
		Matrix X = new Matrix(arr);
		Matrix Y = new Matrix(y,rowTrain);
		Matrix Xtest = new Matrix(xtest);
		Matrix Ytest = new Matrix(ytest,rowTest);
		Matrix Xt= X.transpose();
		Matrix xxt = Xt.times(X);
		Matrix I = Jama.Matrix.identity(col+1,col+1);
		double lambda = 1;
		for(;lambda<=150;lambda++)
		{
		Matrix LI = I.times(lambda);
		Matrix add = xxt.plus(LI);
		Matrix inv = add.inverse();
		Matrix sec = Xt.times(Y);
		Matrix w = inv.times(sec);
		Matrix one = X.times(w);
		Matrix two = one.minus(Y);
		Matrix three = two.transpose();
		Matrix half = three.times(two);
		double mse = half.get(0, 0)/rowTrain;
		String error = Double.toString(mse);
		String lamb = Double.toString(lambda);
		Matrix xtestw = Xtest.times(w);
		Matrix xwytest = xtestw.minus(Ytest);
		Matrix xwytesttranspose = xwytest.transpose();
		Matrix testerror = xwytesttranspose.times(xwytest);
		double yerror = testerror.get(0, 0)/rowTest;
		String yeerrors = Double.toString(yerror);
		
		 
		try
		{writer.append(lamb);
	    writer.append(',');
	    writer.append(error);
	    writer.append(',');
	    writer.append(yeerrors);
	    writer.append('\n');
	    
		}catch(Exception e)
		{
			
		}
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}
	static void operation(String cvsFileToRead, String sfilename, String testfile,int col,int rowTrain, int rowTest)
	{	FileWriter writer = null;
		try{
			writer = new FileWriter(sfilename);
		
	    writer.append("Lambda");
	    writer.append(',');
	    writer.append("MSE");
	    writer.append(',');
	    writer.append("testerror");
	    writer.append('\n');

		}catch(Exception e)
		{
			
		}

		BufferedReader br = null;
		String line = "";
		String linetest = "";
		String splitBy = ",";
		double arr[][] = new double [rowTrain][col+1];
		double xtest[][] = new double[rowTest][col+1];
		double ytest[] = new double[rowTest];
		double y[] = new double[rowTrain];
		int r=0;
		int c=0;
		int tr = 0;
		int tc = 0;
		int i;
		double min = Double.POSITIVE_INFINITY;
		double tempmin=0;

		double temp = 0.00;
	
			try {
				br = new BufferedReader(new FileReader(cvsFileToRead));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				line = br.readLine();
				String line1 = null;
				while((line1=br.readLine())!=null)
				{
					arr[r][0]=1;
					String[] x = line1.split(splitBy);
					for( i=0;i<col;i++)
					{   String aux = x[i];
					NumberFormat nf = NumberFormat.getInstance();
						
						try {
							temp = nf.parse(aux).doubleValue();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						arr[r][i+1] = temp;
					}
					String yaux = x[i];
					NumberFormat nf = NumberFormat.getInstance();
					double ytemp = 0;
					try {
						ytemp = nf.parse(yaux).doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                     y[c++] = ytemp;
					r++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				BufferedReader brtest = null;
		try {
			brtest = new BufferedReader(new FileReader(testfile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			linetest = brtest.readLine();
			String line1test = null;
			while((line1test=brtest.readLine())!=null)
			{   xtest[tr][0]=1;
				String[] x = line1test.split(splitBy);
				for( i=0;i<col;i++)
				{   String aux = x[i];
				NumberFormat nf = NumberFormat.getInstance();
					
					try {
						temp = nf.parse(aux).doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					xtest[tr][i+1] = temp;
				}
				String yaux = x[i];
				NumberFormat nf = NumberFormat.getInstance();
				double ytemp = 0;
				try {
					ytemp = nf.parse(yaux).doubleValue();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                 ytest[tc++] = ytemp;
				tr++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Matrix X = new Matrix(arr);
		Matrix Y = new Matrix(y,rowTrain);
		Matrix Xtest = new Matrix(xtest);
		Matrix Ytest = new Matrix(ytest,rowTest);
		Matrix Xt= X.transpose();
		Matrix xxt = Xt.times(X);
		Matrix I = Jama.Matrix.identity(col+1,col+1);
		double lambda = 1;
		for(;lambda<=150;lambda++)
		{
		Matrix LI = I.times(lambda);
		Matrix add = xxt.plus(LI);
		Matrix inv = add.inverse();
		Matrix sec = Xt.times(Y);
		Matrix w = inv.times(sec);
		Matrix one = X.times(w);
		Matrix two = one.minus(Y);
		Matrix three = two.transpose();
		Matrix half = three.times(two);
		double mse = half.get(0, 0)/rowTrain;
		String error = Double.toString(mse);
		String lamb = Double.toString(lambda);
		Matrix xtestw = Xtest.times(w);
		Matrix xwytest = xtestw.minus(Ytest);
		Matrix xwytesttranspose = xwytest.transpose();
		Matrix testerror = xwytesttranspose.times(xwytest);
		double yerror = testerror.get(0, 0)/rowTest;
		String yerrors = Double.toString(yerror);
		
		 
		try
		{writer.append(lamb);
	    writer.append(',');
	    writer.append(error);
	    writer.append(',');
	    writer.append(yerrors);
	    writer.append('\n');
		}catch(Exception e)
		{
			
		}
		
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



}
