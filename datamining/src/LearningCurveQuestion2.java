import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Random;

import Jama.Matrix;


public class LearningCurveQuestion2 {

	static int k ;
	public static void main(String[] args)
	{
		String cvsFileToRead ="train-1000-100.csv";
		String testfile = "test-1000-100.csv";
		String sfilenamel1 = "l1question2.csv";
		String sfilenamel25 = "l25question2.csv";
		String sfilenamel150 = "l150question2.csv";

		
		operations(cvsFileToRead,testfile,sfilenamel1,1);
		operations(cvsFileToRead,testfile,sfilenamel25,25);
		operations(cvsFileToRead,testfile,sfilenamel150,150);

		
	}
	static void operations(String cvsFileToRead,String testfile, String sfilename, double l1)
	{
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
		double arr[][] = new double [1000][101];
		double xtest[][] = new double[1000][101];
		double ytest[] = new double[1000];
		double y[] = new double[1000];
		int r=0;
		int c=0;
		int tr = 0;
		int tc = 0;
		int i;
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
		//now reading test file
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
	//("Done with reading");
   //matrix computation starts
	Random random = new Random();
	double sum =0;
	int rtemp = 0;
	int yrtemp = 0;
		for(k=10; k<950; k=k+10)
		{
	    
			for(int m=0 ;m<15;m++)
			{  double [][]tempx = new double[k][101];
			   double[] tempy = new double[k];
				for(int n=0; n<k;n++)
				{   int randomno = random.nextInt(1000);
				    for(int p =0; p<101;p++)
				    { tempx[n][p] = arr[randomno][p];
				    }
				    tempy[n] = y[randomno];
				    
				}
				Matrix X = new Matrix(tempx);
				Matrix Y = new Matrix(tempy,k);
				Matrix Xtranspose = X.transpose();
				Matrix XtransposeX = Xtranspose.times(X);
				Matrix I = Jama.Matrix.identity(101, 101);
				Matrix LI = I.timesEquals(l1);
				Matrix plus = XtransposeX.plus(LI);
				Matrix plusinverse = plus.inverse();
				Matrix XtransposeY = Xtranspose.times(Y);
				Matrix w = plusinverse.times(XtransposeY);
				Matrix XW = X.times(w);
				Matrix XWminusY = XW.minus(Y);
				Matrix XWminusYtranspose = XWminusY.transpose();
				Matrix mse = XWminusYtranspose.times(XWminusY);
				double error = mse.get(0, 0)/k;
				sum = sum + error;
				Matrix Xtest = new Matrix(xtest);
				Matrix Ytest = new Matrix(ytest,1000);
				Matrix xtestw = Xtest.times(w);
				Matrix xwytest = xtestw.minus(Ytest);
				Matrix xwytesttranspose = xwytest.transpose();
				Matrix testerror = xwytesttranspose.times(xwytest);
				double yerror = testerror.get(0, 0)/1000;
				sumtest = sumtest + yerror;
				

				
			}
			double avg = sum/15;
			double avgtest = sumtest/15;
			String avgtrain = Double.toString(avg);
			String testavg = Double.toString(avgtest);
			String K = Integer.toString(k);
			try{
				writer.append(K);
		        writer.append(',');
		        writer.append(avgtrain);
		        writer.append(',');
		        writer.append(testavg);
		        writer.append('\n');
			}catch(Exception e)
			{
				
			}
     
           sum =0;
           sumtest =0 ;
			avgcount++;
		}
		
		try
		{writer.close();
		}catch(Exception e)
		{
			
		}
		

	}
	
	
}
