package unused;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

import Jama.Matrix;


public class q3new3 {

	public static void main(String[] args)
	{
		String cvsFileToRead ="C:\\Users\\Ankita\\Desktop\\train-1000-100.csv";
		String testfile = "C:\\Users\\Ankita\\Desktop\\test-1000-100.csv";
		String sfilename = "C:\\Users\\Ankita\\Desktop\\datamining\\thirdque.csv";
		FileWriter writer = null;
		try{
			writer = new FileWriter(sfilename);
		
	    writer.append("Lambda");
	    writer.append(',');
	    writer.append("avgtrain");
	    writer.append(',');
	    writer.append("testavg");
	    writer.append('\n');

		}catch(Exception e)
		{
			
		}


		BufferedReader br = null;
		String line = "";
		String linetest = "";
		String splitBy = ",";
		double arr[][] = new double [1000][100];
		double xtest[][] = new double[1000][100];
		double ytest[] = new double[1000];
		double[] y = new double[1000];
		int r=0;
		int c=0;
		int tr = 0;
		int tc = 0;
		int i;
		double min =1000;
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
				{
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
						arr[r][i] = temp;
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
			{
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
					xtest[tr][i] = temp;
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
		Matrix Y = new Matrix(y,1000);
		Matrix finalXtrain = null;
		Matrix finalYtrain = null;
        double l;
		for(l=0;l<50;l++)
		{
		for(int f=0;f<10;f++)
		{     Matrix xnew = null;
		     Matrix xnew2 = null;
		     Matrix ynew = null;
		     Matrix ynew2 = null;
			 int input = f*100;
			 Matrix tempmt = A.getMatrix(input, (input+99), 0, 99);
			 Matrix ytempmt = Y.getMatrix(input, (input+99),0,0);
			 ytempmt.print(0, 0);
			if(f==0)
			{ finalXtrain = A.getMatrix((input+100), 999, 0, 99);
			  finalYtrain = Y.getMatrix((input+100), 999, 0, 0);
			}
			else if(f==9)
			{
				finalXtrain = A.getMatrix(0, 899, 0, 99);
				finalYtrain = Y.getMatrix(0, 899, 0, 0);
				//finalXtrain.print(0, 0);
			}
			
			else{
				xnew = A.getMatrix(0, (input-1), 0, 99);
				xnew2 = A.getMatrix((input+100), 999, 0, 99);
				ynew = Y.getMatrix(0, (input-1), 0, 0);
				ynew2 = Y.getMatrix((input+100), 999, 0, 0);
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
              // finalXtrain.print(0, 0);

			}
			//end of else
			
		
			Matrix Xtranspose = finalXtrain.transpose();
			Matrix XtransposeX = Xtranspose.times(finalXtrain);
			Matrix I = Jama.Matrix.identity(100, 100);
			Matrix LI = I.timesEquals(l);
			Matrix plus = XtransposeX.plus(LI);
			Matrix plusinverse = plus.inverse();
			Matrix XtransposeY = Xtranspose.times(finalYtrain);
			Matrix w = plusinverse.times(XtransposeY);
			Matrix XW = finalXtrain.times(w);
			Matrix XWminusY = XW.minus(finalYtrain);
			Matrix XWminusYtranspose = XWminusY.transpose();
			Matrix mse = XWminusYtranspose.times(XWminusY);
			double error = mse.get(0, 0)/900;
			sum = sum + error;
			//System.out.println(sum);
			//String lamb = Double.toString(lambda);
			Matrix Xtest = new Matrix(xtest);
			Matrix Ytest = new Matrix(ytest,1000);
			Matrix xtestw = Xtest.times(w);
			Matrix xwytest = xtestw.minus(Ytest);
			Matrix xwytesttranspose = xwytest.transpose();
			Matrix testerror = xwytesttranspose.times(xwytest);
			double yerror = testerror.get(0, 0)/1000;
			//System.out.println(yerror);
			sumtest = sumtest + yerror;
			


			
		}
		avg = sum/10;
		avgtest = sumtest/10;
		//System.out.println(avgtest);
		if(avgtest<min)
		{
			min=avgtest;
			tempmin = l;
		}
		 
       sum =0;
       sumtest =0 ;
	}
	
	System.out.println(tempmin);
	try
	{writer.close();
	}catch(Exception e)
	{
		
	}

		}

	
}
