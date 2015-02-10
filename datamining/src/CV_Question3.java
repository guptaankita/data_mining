import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

import Jama.Matrix;


public class CV_Question3 {

	public static void main(String[] args)
	{
		//input files
		String train_1000_100 ="train-1000-100.csv";
		String test_1000_100 = "test-1000-100.csv";
		String train_100_100 ="train-100-100.csv";
		String test_100_100 = "test-100-100.csv";
		String train_100_10 ="train-100-10.csv";
		String test_100_10 = "test-100-10.csv";

		  operations(train_1000_100,test_1000_100,100,1000,1000,100);
		  operations(train_100_100,test_100_100,100,100,1000,10);
		  operations(train_100_10,test_100_10,10,100,1000,10);
		

	}	
	
			static void operations(String cvsFileToRead, String testfile,int col, int rowTrain,int rowTest,int split)
		{
		

		BufferedReader br = null;
		String line = "";
		String linetest = "";
		String splitBy = ",";
		double arr[][] = new double [rowTrain][col+1];
		double xtest[][] = new double[rowTest][col+1];
		double ytest[] = new double[rowTest];
		double[] y = new double[rowTrain];
		int r=0;
		int c=0;
		int tr = 0;
		int tc = 0;
		int i;
		double min = Double.POSITIVE_INFINITY;
		double tempmin=0;
		int avgcount =0;
		double sumtest=0;
		double temp = 0.00;
	
			try {
				br = new BufferedReader(new FileReader(cvsFileToRead));
			} catch (FileNotFoundException e1) {
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
						e.printStackTrace();
					}
                     y[c++] = ytemp;
					r++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
				BufferedReader brtest = null;
		try {
			brtest = new BufferedReader(new FileReader(testfile));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			linetest = brtest.readLine();
			String line1test = null;
			while((line1test=brtest.readLine())!=null)
			{
				xtest[tr][0] =1;
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
					e.printStackTrace();
				}
                 ytest[tc++] = ytemp;
				tr++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		int count =0;
		double sum=0;
		double avg =0;
		double avgtest =0;
		Matrix A = new Matrix(arr);
		Matrix Y = new Matrix(y,rowTrain);
		Matrix finalXtrain = null;
		Matrix finalYtrain = null;
        double l;
		for(l=0;l<80;l++)
		{
		for(int f=0;f<10;f++)
		{    Matrix xnew = null;
		     Matrix xnew2 = null;
		     Matrix ynew = null;
		     Matrix ynew2 = null;
			 int input = f*split;
			 int low = split-1;
			 Matrix tempmt = A.getMatrix(input, (input+low), 0, col);
			 Matrix ytempmt = Y.getMatrix(input, (input+low),0,0);
			if(f==0)
			{ finalXtrain = A.getMatrix((input+split), (rowTrain-1), 0, col);
			  finalYtrain = Y.getMatrix((input+split), (rowTrain-1), 0, 0);
			}
			else if(f==9)
			{
				finalXtrain = A.getMatrix(0, (f*split-1), 0, col);
				finalYtrain = Y.getMatrix(0, (f*split-1), 0, 0);
			
			}
			
			else{
				xnew = A.getMatrix(0, (input-1), 0, col);
				xnew2 = A.getMatrix((input+split), (rowTrain-1), 0, col);
				ynew = Y.getMatrix(0, (input-1), 0, 0);
				ynew2 = Y.getMatrix((input+split), (rowTrain-1), 0, 0);
				double xnewarr[][]=xnew.getArray();
				double xnew2arr[][] = xnew2.getArray();
				//for y
				double ynewarr[][]=ynew.getArray();
				double ynew2arr[][] = ynew2.getArray();
				double my[][] = new double[ynewarr.length+ynew2arr.length][];
				System.arraycopy(ynewarr, 0, my, 0, ynewarr.length);
				System.arraycopy(ynew2arr, 0, my, ynewarr.length, ynew2arr.length);
				//for x
				double m[][] = new double[xnewarr.length+xnew2arr.length][];
				System.arraycopy(xnewarr, 0, m, 0, xnewarr.length);
				System.arraycopy(xnew2arr, 0, m, xnewarr.length, xnew2arr.length);
	            finalXtrain = new Matrix(m);
	            finalYtrain = new Matrix(my);
	            
               

			}
			//end of else
			
		
			Matrix Xtranspose = finalXtrain.transpose();
			Matrix XtransposeX = Xtranspose.times(finalXtrain);
			Matrix I = Jama.Matrix.identity(col+1, col+1);
			Matrix LI = I.timesEquals(l);
			Matrix plus = XtransposeX.plus(LI);
			Matrix plusinverse = plus.inverse();
			Matrix XtransposeY = Xtranspose.times(finalYtrain);
			Matrix w = plusinverse.times(XtransposeY);
			Matrix XW = finalXtrain.times(w);
			Matrix XWminusY = XW.minus(finalYtrain);
			Matrix XWminusYtranspose = XWminusY.transpose();
			Matrix mse = XWminusYtranspose.times(XWminusY);
			int size  = 9*split;
			double error = mse.get(0, 0)/size;
			sum = sum + error;
			Matrix xtestw = tempmt.times(w);
			Matrix xwytest = xtestw.minus(ytempmt);
			Matrix xwytesttranspose = xwytest.transpose();
			Matrix testerror = xwytesttranspose.times(xwytest);
			double yerror = testerror.get(0, 0)/split;
			sumtest = sumtest + yerror;



			
		}
		avg = sum/10;
		avgtest = sumtest/10;
		if(avgtest<min)
		{
			min=avgtest;
			tempmin = l;
		}
		 
       sum =0;
       sumtest =0 ;
	}
	
	System.out.println("The value of lambda which gives minimum error"+ tempmin);
	
		}

	
}
