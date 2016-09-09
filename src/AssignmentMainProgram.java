import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class AssignmentMainProgram {
	private static Integer totalFileRows =0;
	private static Integer totalFileColumns =3;
	public static Integer[][] processedMatrix_matrixFile ={};
	public static String[] processedMatrix_termFile ={};
	public static double[][] processedMatrix_dataMatrix= {};
	public static Integer NUMBEROFITERATION= 100;
	
	public static void main(String args[]){
		System.out.println("Advance Machine Learning Assignment");
		
		//Reading and Process Term Document File
		String fileName = "bbcnews.mtx";
		totalFileRows= file_read(fileName);
		processedMatrix_matrixFile = matrix_file_process(fileName);
		
		//Reading and Process Term File
		String fileName2= "bbcnews.terms";
		Integer term_rows= file_read(fileName2);
		processedMatrix_termFile= term_file_process(fileName2, term_rows);
		
		//Perform TF IDF Normalization
		processedMatrix_dataMatrix= tf_idf_normalization(processedMatrix_matrixFile, term_rows);
		
		//Receive User Input for the number of cluster, k
		System.out.println("\n Initializing Non Negative Factorization.");
		lineBreaker();
		Scanner reader = new Scanner(System.in);
		System.out.print(" Enter the number of cluster:");
		Integer numberOfCluster= reader.nextInt();
		reader.close();
		
		//Perform NMF
		nmf(processedMatrix_dataMatrix, processedMatrix_termFile, numberOfCluster, NUMBEROFITERATION);
		
		System.out.println("\n Non-negative Matrix Fatorization Completed");
		
		//Test to display Processed Matrix File
		/*for(int a=0; a<totalFileRows; a++){
	    	for(int b=0; b<totalFileColumns; b++){
	    		System.out.print(processedMatrix_matrixFile[a][b] + ", " );
	    	}
	    	System.out.println();
	    }*/
		
		/*//Test to display Processed Data Matrix
		for(int c=0; c<term_rows; c++){
			for(int d=0; d<1400; d++){
				System.out.print(processedMatrix_dataMatrix[c][d] + ",");
			}
			System.out.println();
		}*/
		
		/*//Test to display Term Matrix
		for(int e=0; e<term_rows; e++){
			System.out.println(processedMatrix_termFile[e]);
		}*/
	}
	
	public static void lineBreaker(){
		System.out.println(" ==================================================");
	}
	
	public static int file_read(String fileName){
		System.out.println("\n Reading " + fileName);
		lineBreaker();

		//Read File Declaration
		File file = new File(fileName);
		BufferedReader reader = null;
		Integer fileRows=0;
		
		try {
			//Perform File Read
		    reader = new BufferedReader(new FileReader(file));
		    String readLine = null;
	    	
		    while((readLine = reader.readLine()) != null){
		    	fileRows+=1;
		    }
		    System.out.println(" " + fileRows.toString() + " rows retrieved");
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		    System.exit(0);
		} catch (IOException e) {
		        e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
		return fileRows;
	}
	
	public static Integer[][] matrix_file_process(String fileName){
		System.out.println("\n Processing " + fileName);
		lineBreaker();

		//Read File Declaration
		File file = new File(fileName);
		BufferedReader reader = null;
		
		//Matrix Declaration
	    Integer[][] matrixFileMatrix = new Integer[totalFileRows][totalFileColumns];
		
		try {
			//Reader Declaration
		    reader = new BufferedReader(new FileReader(file));
		    String readLine = null;
		    int rowCount=0;
		    
		    //Validation Check
		    String regexPattern = "^%%[a-zA-Z]";
	    	Pattern p = Pattern.compile(regexPattern);
	    	
		    while((readLine = reader.readLine()) != null){
		    	//Matcher declaration
		    	Matcher m = p.matcher(readLine);
		    	
		    	if(m.find()){
		    		//Do nothing for the first line in the file
		    		//Skipping line 2 with total numbers of rows for the each column
		    		reader.readLine();
		    		rowCount++;
		    	}
		    	else{	    			    		
		    		//Assign value into Matrix
		    		Integer currentColumnCount=-1;

	    			//Filter string
	    			String[] text = readLine.split("\\s+");
	    			for(String filteredText : text){
	    				currentColumnCount++;
			    		switch(currentColumnCount){
				    		case 0:
				    		case 1:
				    		case 2:{
				    			try{
				    				matrixFileMatrix[rowCount][currentColumnCount]=Integer.parseInt(filteredText);
				    			}catch(NumberFormatException e){
				    				//This catch statement to resolve Float value of the frequency
				    				NumberFormat nf = NumberFormat.getInstance();
				    				try{
				    					String frequency =nf.parse(filteredText).toString();
				    					matrixFileMatrix[rowCount][currentColumnCount]=Integer.parseInt(frequency);
				    				}catch(Exception ex){
				    					//Do nothing
				    				}
				    			}
				    		}
				    		default:
				    			//Do nothing
			    		}
	    			}
			    }
		    	rowCount++;
		    }		    
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		        e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
		System.out.println(" Matrix for " + fileName + " is processed.");
		return matrixFileMatrix;
	}
	
	public static String[] term_file_process(String fileName, Integer term_rows){
		System.out.println("\n Processing " + fileName);
		lineBreaker();

		//Reader Declaration
		File file = new File(fileName);
		BufferedReader reader = null;
		
		//Matrix Declaration
	    String[] termFileMatrix = new String[term_rows];
		
		try {
			//File Read
		    reader = new BufferedReader(new FileReader(file));
		    String readLine = null;
		    int rowCount=0;
	    	
		    while((readLine = reader.readLine()) != null){
		    	termFileMatrix[rowCount]= readLine;
		    	rowCount++;
		    }		    
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		        e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
		System.out.println(" Matrix for " + fileName + " is processed.");
		return termFileMatrix;
	}
	
	public static double[][] tf_idf_normalization(Integer[][] processedMatrix_matrixFile, Integer term_rows){
		System.out.println("\n Initializing TF IDF Normalization");
		lineBreaker();
		
		//Retrieve Total Number of Documents
		Integer maxNumber=0;
		
		for(int a=0;a<totalFileRows;a++){
			Integer currentNumber= processedMatrix_matrixFile[a][1];
			
			if(currentNumber!=null){
				if(currentNumber>maxNumber){
					maxNumber = processedMatrix_matrixFile[a][1];
				}
			}else{
				//Do nothing on null item
			}
		}
		Integer totalNumberOfDocuments = maxNumber;
		
		//TFIDF Declaration
		double tfidfMatrix[][] = new double[term_rows][totalNumberOfDocuments]; //4058rows*1400columns
		Map<Integer, Integer> termCountMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> documentTermCountMap = new HashMap<Integer, Integer>();
		
		//Pre-calculation for TFIDF
		for(int tfidfRow=0; tfidfRow<term_rows; tfidfRow++){
			//Find Term Count
			Integer termCount=0;
			for(int i=0; i<totalFileRows; i++){
				try{
					if(processedMatrix_matrixFile[i][0]==tfidfRow+1){
						termCount++;
					}else{
						//Do nothing
					}
				}catch(NullPointerException e){
					//Do nothing //Skip Null Row
				}
			}
			termCountMap.put(tfidfRow+1, termCount);
		}
		
		for(int tfidfColumn=0; tfidfColumn<totalNumberOfDocuments; tfidfColumn++){
			//Find Document Term Count
			Integer documentTermCount=0;
			for(int j=0; j<totalFileRows; j++){
				try{
					if(processedMatrix_matrixFile[j][1]==tfidfColumn+1){
						documentTermCount++;
					}else{
						//Do nothing
					}
				}catch(NullPointerException e){
					//Do nothing //Skip Null Row
				}
			}
			documentTermCountMap.put(tfidfColumn+1, documentTermCount);
		}
		
		/*//Display Hash Map
		System.out.println("Term Count Map: " + termCountMap);
		System.out.println("Document Term Count Map: " + documentTermCountMap);*/
		
		//Write into File
		try{
			System.out.println(" Processing TF IDF Normalization to Data Matrix");
			String dataMatrixFile = "DataMatrix_A.txt";
			File file= new File(dataMatrixFile);
			
			//If file doesn't exist, then create it
			if(!file.exists()){
				try {
					FileWriter fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					file.createNewFile();
					//TFIDF Assign
					System.out.println(" Creating " + dataMatrixFile);
					for(int termRow=0; termRow<term_rows; termRow++){
						for(int termCol=0; termCol<totalNumberOfDocuments; termCol++){
							try{
								Double returnedTfidf=0.00;
								returnedTfidf= tf_idf_check(termRow, termCol, totalNumberOfDocuments, documentTermCountMap, termCountMap);
								System.out.println("Row"+ termRow+ " Col"+termCol+ " Return TFIDF:" + returnedTfidf);
								tfidfMatrix[termRow][termCol]= returnedTfidf;
								bw.write(tfidfMatrix[termRow][termCol] + ",");
							}catch(NullPointerException x){
								//Do nothing
							}
						}
						bw.write("\r\n");
					}
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				System.out.println(" Processing " + dataMatrixFile);
				//Read from file
				//Reader Declaration
				BufferedReader reader = null;
				
				try {
					//File Read
				    reader = new BufferedReader(new FileReader(file));
				    String readLine = null;
				    int currentRowCount=0;
				    int currentColumnCount=0;
				    
				    while((readLine = reader.readLine()) != null){
			    		String[] text = readLine.split(",");
			    		currentColumnCount=0;
			    		for(String filteredText : text){
			    				try{
			    					tfidfMatrix[currentRowCount][currentColumnCount]=Double.parseDouble(filteredText);
			    				}catch(NullPointerException ex){
			    					//Do nothing
			    				}
			    				currentColumnCount++;
			    		}
			    		currentRowCount++;
				    }
				} catch (FileNotFoundException e) {
				    e.printStackTrace();
				}
			}		
		}catch(Exception e){
			//DO nothing
		}	
		
		/*for(int i=194; i<195; i++){
			for(int j=0; j<totalNumberOfDocuments; j++){
				System.out.println("Final TFIDF:" + tfidfMatrix[i][j]);
			}
		}*/
		//System.out.println("Final TFIDF: "+ tfidfMatrix[194][1]);
		
		System.out.println(" Complete Processing Data Matrix. ");
		return tfidfMatrix;
	}
	
	public static double tf_idf_check(Integer term, Integer document, Integer totalNumberOfDocuments, Map<Integer, Integer> documentTermCountMap, Map<Integer, Integer> termCountMap){
		//TFIDF Calculation Declarations
		Double tf=0.00;
		Double calc=0.00;
		Double idf=0.00;
		Double tfidf=0.00;
		
		for(int i=0; i<totalFileRows; i++){
			try{
				if(processedMatrix_matrixFile[i][0]==term+1 && processedMatrix_matrixFile[i][1]==document+1){
					tf= 1/(double)documentTermCountMap.get(document+1);
					calc = totalNumberOfDocuments/(double)termCountMap.get(term+1);
					idf = Math.log10(calc);
					tfidf= tf*idf;
					return tfidf;
				}else{
					tfidf=0.00;
				}
			}catch(NullPointerException ex){
				//Do nothing
			}
		}
		return tfidf;
	}
	
	public static void nmf(double[][] processedDataMatrix, String[] processedTermMatrix, Integer numberOfCluster, Integer maxNumberOfIteration){
		System.out.println("\n Processing Non Negative Factorization");
		lineBreaker();
		//Generating random Numbers declaration
		Random randomGenerator = new Random();
		
		//Basic Vector Matrix Declaration //n*k
		Integer term_rows= processedDataMatrix.length;
		double[][] wMatrix = new double[term_rows][numberOfCluster];
		
		//Build Basic Vector (Rows=Feature)
		for(int i=0; i<term_rows; i++){
			for(int j=0; j<numberOfCluster; j++){
				Double randomNumber = randomGenerator.nextDouble();
				String formattedRandomNumber = String.format("%.4f", randomNumber);
				wMatrix[i][j]=Double.parseDouble(formattedRandomNumber);
			}
		}
		
		//Coefficient Matrix Declaration //k*m
		Integer document_columns= processedDataMatrix[0].length;
		double[][] hMatrix = new double[numberOfCluster][document_columns];
		
		//Build Coefficient Matrix (Columns=Items)
		for(int i=0; i<numberOfCluster; i++){
			for(int j=0; j<document_columns; j++){
				Double randomNumber = randomGenerator.nextDouble();
				String formattedRandomNumber = String.format("%.4f", randomNumber);
				hMatrix[i][j]=Double.parseDouble(formattedRandomNumber);
			}
		}
		
		/*//Display Basic Vector Matrix
		for(int i=0; i<term_rows; i++){
			for(int j=0; j<numberOfCluster; j++){
				System.out.print(wMatrix[i][j] + ",");
			}
			System.out.println();
		}*/
		
		/*//Display Coefficient Matrix
		for(int i=0; i<numberOfCluster; i++){
			for(int j=0; j<document_columns; j++){
				System.out.print(hMatrix[i][j]+ ",");
			}
			System.out.println();
		}*/
		
		//NMF Factorization Step
		for(int iteration=0; iteration<maxNumberOfIteration; iteration++){
			System.out.println("Iteration " + (iteration+1));
			
			/*//Test Display Updated W Matrix
			System.out.println("Display W Matrix:");
			for(int i=0; i<5; i++){
				for(int j=0; j<numberOfCluster; j++){
					System.out.print(wMatrix[i][j]+ ",");
				}
				System.out.println();
			}
			System.out.println("W Matrix");
			System.out.println();*/
			
			/*//Test Display Updated H Matrix
			System.out.println("Display H Matrix:");
			for(int i=0; i<numberOfCluster; i++){
				for(int j=0; j<5; j++){
					System.out.print(hMatrix[i][j]+ ",");
				}
				System.out.println();
			}
			System.out.println("H Matrix");
			System.out.println();*/
			
			//Update Factor H
			//Transposed Basic Vector Matrix (W Matrix)
			RealMatrix realMatrix_wMatrix= MatrixUtils.createRealMatrix(wMatrix);
			RealMatrix transposedMatrix_wMatrix= realMatrix_wMatrix.transpose();
			
			//Assign Transposed Matrix in RealMatrix format into double[][] matrix format
			double[][] transposedWMatrix = new double[numberOfCluster][term_rows];
			for(int i=0; i<numberOfCluster; i++){
				for(int j=0; j<term_rows; j++){
					transposedWMatrix[i][j]=transposedMatrix_wMatrix.getEntry(i,j);
				}
			}
			
			//Update Factor H //Multiplication Steps' Declaration
			double[][] multiplication_Wt_A = new double[numberOfCluster][document_columns];
			double[][] multiplication_Wt_W = new double[numberOfCluster][numberOfCluster];
			double[][] multiplication_Wt_W_H = new double[numberOfCluster][document_columns];
			
			//Multiply transposed W with A
			multiplication_Wt_A = multiplyByMatrix(transposedWMatrix, processedDataMatrix);
			
			//Multiply transposed W with W, then multiply with H
			multiplication_Wt_W = multiplyByMatrix(transposedWMatrix, wMatrix);
			multiplication_Wt_W_H = multiplyByMatrix(multiplication_Wt_W, hMatrix);
			
			//Multiply WTA/WTWH with H matrix //Update every column in H Matrix
			double updatedH=0.00;
			for(int c=0; c<numberOfCluster; c++){
				for(int j=0; j<document_columns; j++){
					updatedH= hMatrix[c][j] * (multiplication_Wt_A[c][j]/multiplication_Wt_W_H[c][j]);
            		/*String formattedString = String.format("%.4f", updatedH);
            		double formattedResult = Double.parseDouble(formattedString);   
            		hMatrix[c][j]= formattedResult;*/
					hMatrix[c][j]= updatedH;
				}
			}
			
			/*//Test to Display W Matrix Multiplication Result
			for(int i=0; i<numberOfCluster; i++){
				for(int j=0;j<totalFileColumns; j++){
					//w_transpose_cj= transposedMatrix_wMatrix.getEntry(i, j);	//Retrieved Transposed W value
					System.out.print(multiplication_Wt_A[i][j] + ",");
				}
				System.out.println();
			}*/
			
			//Update Factor W
			//Transpose Coefficient Matrix (H Matrix)
			RealMatrix realMatrix_hMatrix= MatrixUtils.createRealMatrix(hMatrix);
			RealMatrix transposedMatrix_hMatrix= realMatrix_hMatrix.transpose();
			
			//Assign Transposed Matrix in RealMatrix format into double[][] matrix format
			double[][] transposedHMatrix = new double[document_columns][numberOfCluster];
			for(int i=0; i<document_columns; i++){
				for(int j=0; j<numberOfCluster; j++){
					transposedHMatrix[i][j]=transposedMatrix_hMatrix.getEntry(i,j);
				}
			}
			
			//Update Factor W //Multiplication Steps' Declaration
			double[][] multiplication_A_Ht = new double[term_rows][numberOfCluster];
			double[][] multiplication_W_H = new double[totalFileRows][totalFileColumns];
			double[][] multiplication_W_W_Ht = new double[term_rows][numberOfCluster];
			
			//Multiply A with transposed H
			multiplication_A_Ht = multiplyByMatrix(processedDataMatrix, transposedHMatrix);
			
			//Multiply W with H, then multiply with Transposed H
			multiplication_W_H = multiplyByMatrix(wMatrix, hMatrix);
			multiplication_W_W_Ht = multiplyByMatrix(multiplication_W_H, transposedHMatrix);
			
			//Multiply AHT/WHHT with W matrix
			double updatedW=0.00;
			for(int i=0; i<term_rows; i++){
				for(int j=0; j<numberOfCluster; j++){
					updatedW= wMatrix[i][j] * (multiplication_A_Ht[i][j]/multiplication_W_W_Ht[i][j]);
            		/*String formattedString = String.format("%.2f", updatedW);
            		double formattedResult = Double.parseDouble(formattedString);   
					wMatrix[i][j]= formattedResult;*/
					wMatrix[i][j]= updatedW;
				}
			}
		}
		
		//Display Top 10 Terms for each cluster
		for(int cluster=0; cluster<numberOfCluster; cluster++){
			System.out.println("\n Top 10 Terms for Cluster " + (cluster+1));
			lineBreaker();
			
			//Array Declaration
			double[] topTermsArray= new double[term_rows];
			//Assign into Array
			for(int i=0; i<term_rows; i++){
				topTermsArray[i]= wMatrix[i][cluster];
			}
			
			//Sort Array
			Arrays.sort(topTermsArray);
			double[] top10Terms= Arrays.copyOfRange(topTermsArray, topTermsArray.length-10, topTermsArray.length);
			//System.out.println(Arrays.toString(top10Terms));
			int rankCount=1;
			
			//Display Top 10 Terms
			for(int a=0; a<term_rows; a++){
				for(int b=9; b>=0; b--){
					if(top10Terms[b] == wMatrix[a][cluster]){
						System.out.println(" Rank "+ rankCount + ": " + processedTermMatrix[(a+1)]);
						rankCount++;
					}
				}
			}
		}
	}
	
	public static double[][] multiplyByMatrix(double[][] matrix_one, Integer[][] matrix_two){
		int m1ColLength = matrix_one[0].length; // m1 columns length
        int m2RowLength = matrix_two.length;    // m2 rows length
        if(m1ColLength != m2RowLength) return null; // matrix multiplication is not possible
        int mRRowLength = matrix_one.length;    // m result rows length
        int mRColLength = matrix_two[0].length; // m result columns length
        double[][] mResult = new double[mRRowLength][mRColLength];
        for(int i = 0; i < mRRowLength; i++) {         // rows from m1
            for(int j = 0; j < mRColLength; j++) {     // columns from m2
                for(int k = 0; k < m1ColLength; k++) { // columns from m1
                	try{
                		mResult[i][j] += matrix_one[i][k] * matrix_two[k][j];
                		/*double total=0;
                		total = matrix_one[i][k] * matrix_two[k][j];
                		String formattedMString = String.format("%.2f", total);
                		double formattedMResult = Double.parseDouble(formattedMString);
                		mResult[i][j] += formattedMResult;*/
                	}catch(NullPointerException e){
                		//Do nothing
                	}
                }
            }
        }
        return mResult;
	}
	
	public static double[][] multiplyByMatrix(double[][] matrix_one, double[][] matrix_two){
		int m1ColLength = matrix_one[0].length; // m1 columns length
        int m2RowLength = matrix_two.length;    // m2 rows length
        if(m1ColLength != m2RowLength) return null; // matrix multiplication is not possible
        int mRRowLength = matrix_one.length;    // m result rows length
        int mRColLength = matrix_two[0].length; // m result columns length
        double[][] mResult = new double[mRRowLength][mRColLength];
        for(int i = 0; i < mRRowLength; i++) {         // rows from m1
            for(int j = 0; j < mRColLength; j++) {     // columns from m2
                for(int k = 0; k < m1ColLength; k++) { // columns from m1
                	try{
                		mResult[i][j] += matrix_one[i][k] * matrix_two[k][j];
                	}catch(NullPointerException e){
                		//Do nothing
                	}
                }
            }
        }
        return mResult;
	}
	
	public static double[][] multiplyByMatrix(Integer[][] matrix_one, double[][] matrix_two){
		int m1ColLength = matrix_one[0].length; // m1 columns length
        int m2RowLength = matrix_two.length;    // m2 rows length
        if(m1ColLength != m2RowLength) return null; // matrix multiplication is not possible
        int mRRowLength = matrix_one.length;    // m result rows length
        int mRColLength = matrix_two[0].length; // m result columns length
        double[][] mResult = new double[mRRowLength][mRColLength];
        for(int i = 0; i < mRRowLength; i++) {         // rows from m1
            for(int j = 0; j < mRColLength; j++) {     // columns from m2
                for(int k = 0; k < m1ColLength; k++) { // columns from m1
                	try{
                		mResult[i][j] += matrix_one[i][k] * matrix_two[k][j];
                	}catch(NullPointerException e){
                		//Do nothing
                	}
                }
            }
        }
        return mResult;
	}
}
