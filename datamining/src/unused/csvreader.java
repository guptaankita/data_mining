package unused;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

import Jama.Matrix;


public class csvreader {
	
	public static void main(String args[]){
		String cvsFileToRead ="C:\\Users\\Ankita\\Desktop\\train-100-10.csv";
		String sfilename = "C:\\Users\\Ankita\\Desktop\\testinput.csv";
		String testfile = "C:\\Users\\Ankita\\Desktop\\test-100-10.csv";
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
		double arr[][] = new double [100][10];
		double xtest[][] = new double[1000][10];
		double ytest[] = new double[1000];
		double y[] = new double[100];
		int r=0;
		int c=0;
		int tr = 0;
		int tc = 0;
		int i;
		
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
					for( i=0;i<10;i++)
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
		for(int k=1;k<r;k++)
		{  //System.out.println("row:"+ k);
			for(int j=0;j<10;j++)
			{   
				//System.out.println(arr[k][j]);
			}
		}
		for(int k=0;k<c;k++)
		{
			//System.out.println("outputing y");
			//System.out.println(y[k]);
		}
		//System.out.println("Done with reading");
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
				for( i=0;i<10;i++)
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
	for(int k=1;k<tr;k++)
	{  //System.out.println("row:"+ k);
		for(int j=0;j<10;j++)
		{   
			//System.out.println(xtest[k][j]);
		}
	}
	for(int k=0;k<tc;k++)
	{
		//System.out.println("outputing y");
		//System.out.println(ytest[k]);
	}
	//System.out.println("Done with reading");
	
		
		
				double input = 1/1000;
		Matrix X = new Matrix(arr);
		Matrix Y = new Matrix(y,100);
		Matrix Xtest = new Matrix(xtest);
		Matrix Ytest = new Matrix(ytest,1000);
		Matrix Xt= X.transpose();
		Matrix xxt = Xt.times(X);
		Matrix I = Jama.Matrix.identity(10, 10);
		double lambda = 0;
		for(;lambda<150;lambda++)
		{
		Matrix LI = I.times(lambda);
		//System.out.println(xxt.getColumnDimension()+" "+xxt.getRowDimension()+ " "+LI.getColumnDimension()+" "+LI.getRowDimension());
		
		Matrix add = xxt.plus(LI);
		Matrix inv = add.inverse();
		Matrix sec = Xt.times(Y);
		Matrix w = inv.times(sec);
		Matrix one = X.times(w);
		Matrix two = one.minus(Y);
		Matrix three = two.transpose();
		Matrix half = three.times(two);
		//System.out.println(half.get(0, 0)/100.0); 
		double mse = half.get(0, 0)/100;
		String error = Double.toString(mse);
		//Matrix mse = half.times(input);
		String lamb = Double.toString(lambda);
		Matrix xtestw = Xtest.times(w);
		Matrix xwytest = xtestw.minus(Ytest);
		Matrix xwytesttranspose = xwytest.transpose();
		Matrix testerror = xwytesttranspose.times(xwytest);
		double yerror = testerror.get(0, 0)/1000;
		//System.out.println(yerror);
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
