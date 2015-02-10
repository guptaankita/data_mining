import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

import Jama.Matrix;


public class CVon_50_100_train {
	
	
	public static void main(String[] args)
	{
		String cvsFileToRead ="train-1000-100.csv";
		String testfile = "test-1000-100.csv";
		
	
		BufferedReader br = null;
		String line = "";
		String linetest = "";
		String splitBy = ",";
		double arr[][] = new double [50][101];
		double xtest[][] = new double[1000][101];
		double ytest[] = new double[1000];
		double y[] = new double[50];
		int r=0;
		int c=0;
		int tr = 0;
		int tc = 0;
		int i;
		int linecount =1;
		double min = Double.POSITIVE_INFINITY;
		double tempmin=0;
		int avgcount =0;
		double sumtest=0;
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
				{  if(linecount>=50)
					 break;
				arr[r][0]=1;
					String[] x = line1.split(splitBy);
					for( i=0;i<100;i++)
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
					linecount++;
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
				for( i=0;i<100;i++)
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
		

		int count =0;
		double sum=0;
		double avg =0;
		double avgtest =0;
		Matrix A = new Matrix(arr);
		Matrix Y = new Matrix(y,50);
		Matrix finalXtrain = null;
		Matrix finalYtrain = null;
        double l;
		for(l=0;l<80;l++)
		{
		for(int f=0;f<10;f++)
		{     Matrix xnew = null;
		     Matrix xnew2 = null;
		     Matrix ynew = null;
		     Matrix ynew2 = null;
			 int input = f*5;
			 Matrix tempmt = A.getMatrix(input, (input+4), 0, 100);
			 Matrix ytempmt = Y.getMatrix(input, (input+4),0,0);
			if(f==0)
			{ finalXtrain = A.getMatrix((input+5), 49, 0, 100);
			  finalYtrain = Y.getMatrix((input+5), 49, 0, 0);
			}
			else if(f==9)
			{
				finalXtrain = A.getMatrix(0, 44, 0, 100);
				finalYtrain = Y.getMatrix(0, 44, 0, 0);
			
			}
			
			else{
				xnew = A.getMatrix(0, (input-1), 0, 100);
				xnew2 = A.getMatrix((input+5), 49, 0, 100);
				ynew = Y.getMatrix(0, (input-1), 0, 0);
				ynew2 = Y.getMatrix((input+5), 49, 0, 0);
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
	            //finalXtrain.print(0,5);
               

			}
			//end of else
			
		
			Matrix Xtranspose = finalXtrain.transpose();
			System.out.println(finalXtrain.getColumnDimension()+ " " + finalXtrain.getRowDimension());
			System.out.println(finalYtrain.getColumnDimension()+ " " + finalYtrain.getRowDimension());

			Matrix XtransposeX = Xtranspose.times(finalXtrain);
			Matrix I = Jama.Matrix.identity(101, 101);
			Matrix LI = I.timesEquals(l);
			Matrix plus = XtransposeX.plus(LI);
			Matrix plusinverse = plus.inverse();
			Matrix XtransposeY = Xtranspose.times(finalYtrain);
			Matrix w = plusinverse.times(XtransposeY);
			Matrix XW = finalXtrain.times(w);
			Matrix XWminusY = XW.minus(finalYtrain);
			Matrix XWminusYtranspose = XWminusY.transpose();
			Matrix mse = XWminusYtranspose.times(XWminusY);
			double error = mse.get(0, 0)/45;
			sum = sum + error;
			Matrix xtestw = tempmt.times(w);
			Matrix xwytest = xtestw.minus(ytempmt);
			Matrix xwytesttranspose = xwytest.transpose();
			Matrix testerror = xwytesttranspose.times(xwytest);
			double yerror = testerror.get(0, 0)/5;
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
	
	System.out.println("The value of lambda which gives minimum error:"+tempmin);
	
		}
	}


