import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MemoryUnit {
	ArrayList<String []> data;
	long startAddress;
	String DataPath;
	
	public MemoryUnit(String Datapth){
		data = new ArrayList<String[]>();
		startAddress = 256;
		
		this.DataPath = Datapth;
		try {
			File file = new File(Datapth);
			FileReader fileReader = null;
			fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			int index = 0;
			String blockData[]  = new String[4];
			while ((line = bufferedReader.readLine()) != null) {
				if((index+1)%4 == 0){
					blockData[index%4] = line;
					data.add(blockData);
					blockData = new String[4];
				}
				blockData[index%4] = line;
				index++;
			}
			fileReader.close();
		}
        catch(FileNotFoundException e){
        	e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public String[] fetchData(long address){
		int index = (int)(address - startAddress)/4;
		index = index/4;
		return data.get(index);
	}
	
	public String[] updateGetData(long address, int offset, String givendata){
		int index = (int)(address - startAddress)/4;
		index = index / 4;
		String mydata[] = this.data.get(index);
		mydata[offset] = givendata;
		return mydata;
	}
	public void updateData(long address, String givendata[]){
		int index = (int)(address-startAddress)/4;
		index = index/4;
		String newData[] = new String[4];
		for(int i =0; i < givendata.length; i++){
			newData[i] = givendata[i];
		}
		data.set(index, newData);
	}
	public void updateSWData(long address, String givendata){
		int index = (int)(address-startAddress)/4;
		int index1 = index/4;
		int offset = index%4; 
		String newData[] = new String[4];
		newData = data.get(index1);
		newData[offset] = givendata;
		data.set(index1, newData);
	}
	public void writeData(){
		try{
		 PrintWriter write = new PrintWriter(this.DataPath, "UTF-8");
		 for(int i = 0;i < data.size();i++){
			 String temp[] = data.get(i);
			 for(int j = 0;j<temp.length;j++){
				 //System.out.println(temp[j]);
				 write.println(temp[j]);
			 }
		 }
		 write.close();
		}catch(IOException e){
			
		}
	}
}
