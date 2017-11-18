import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

class InstructionPointer{ 
	String var1, var2, var3,inst,branch;
	InstructionPointer(){}
	void set(String a, String b, String c, String i, String br){
		this.var1 = a;this.var2 = b;this.var3 = c;this.inst = i;this.branch = br;
	}
	void print(){
		System.out.println(this.branch+" :"+this.inst+" "+this.var1+" "+this.var2+" "+this.var3);
	}
}
public class Main {
	public static String[] memory = new String[32];
	public static int[] config_data = new int[8];
	public static ArrayList<InstructionPointer> instructions_given =new ArrayList<InstructionPointer>();
	public static int counter_clock = 0;
	public static int instruction_number = 0;
	
	public static void error(String str){
		System.out.println(str);
		System.exit(0);
	}
	
	public static void print(ArrayList<Scoreboarding> queueInstruction, String file, String cacheStatus){
		try{
		PrintWriter out = new PrintWriter(file);
		out.println("----------------------------------------------------------------------------------------------");
		out.println("\t\tInstructions\t\tFetch\tIssue\tRead\tExecute\tWrite\tRAW WAW Struct");
		out.println("----------------------------------------------------------------------------------------------");
		
		for(int i=0;i<queueInstruction.size();i++){
			String Inst = "";String Branch = "";String var1 = "";String var2 = "";String var3 = "";
			if(queueInstruction.get(i).branch != null){
				Branch = queueInstruction.get(i).branch+":";
			}
			if(queueInstruction.get(i).inst != null){
				Inst = queueInstruction.get(i).inst;
			}
			if(queueInstruction.get(i).var1 != null){
				var1 = queueInstruction.get(i).var1;
			}
			if(queueInstruction.get(i).var2 != null){
				var2 = queueInstruction.get(i).var2;
			}
			if(queueInstruction.get(i).var3 != null){
				var3 = queueInstruction.get(i).var3;
			}
			out.print(Branch+"\t"+Inst+"\t"+var1+"\t"+var2+"\t"+var3+"\t"+queueInstruction.get(i).fe+"\t"+queueInstruction.get(i).is +"\t"+queueInstruction.get(i).rd+"\t"+
	            queueInstruction.get(i).ex +"\t"+queueInstruction.get(i).wb+"\t");
        	out.println(" "+queueInstruction.get(i).war_haz+"   "+queueInstruction.get(i).waw_haz+"    "+queueInstruction.get(i).struct_haz);
        	
		}
		out.println("\n"+cacheStatus);
		out.close();
		}
		
		
		catch (IOException e){
			System.out.println(e);
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length != 4) {
			System.out.println("Usage: ./simulator inst.txt data.txt config.txt result.txt");
			System.exit(0);
		}
		else{
            File inst = new File(args[0]);
            File data = new File(args[1]);
            File config = new File(args[2]);
            try {
    			FileReader fileReader = new FileReader(data);
    			BufferedReader bufferedReader = new BufferedReader(fileReader);
    			String line;
    			int counter = 0;
    			while ((line = bufferedReader.readLine()) != null) {
    				memory[counter] = line;
    				counter++;
    			}
    			fileReader.close();
    			counter = 0;
    			FileReader fileReader1 = new FileReader(config);
    			BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
    			while ((line = bufferedReader1.readLine()) != null) {
    				String[] f_splited = line.split(":");
    				String[] s_splited = f_splited[1].split(",");
    				config_data[counter] =  Integer.parseInt(s_splited[0].trim());
    				counter++;
    				config_data[counter] =  Integer.parseInt(s_splited[1].trim());
    				counter++;
    			}
    			fileReader1.close();
    			FileReader fileReader2 = new FileReader(inst);
    			BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
    			
    			String branch, var1, var2, var3, sinst;
    			
    			var3=null;var2=null;var1=null;branch=null;sinst=null;
    			
    			while ((line = bufferedReader2.readLine()) != null) {
    				InstructionPointer ProgramCounter = new InstructionPointer();
    				
    				String[] f_splited = line.split(":");
    				String[] a_splited = f_splited[f_splited.length-1].split(",");
    				String[] i_Splited = a_splited[0].trim().split(" ");
    				if(f_splited.length == 1){
    					branch = null;
    				}
    				else if(f_splited.length == 2){
    					branch = f_splited[0].trim().toLowerCase();
    				}
    				else{
    					error("Incorrect instruction file");
    				}
    				if(a_splited.length == 3){
						var3 = a_splited[2].trim().toLowerCase();
						var2 = a_splited[1].trim().toLowerCase();
					}
					else if(a_splited.length == 2){
						var3=null;
						var2 = a_splited[1].trim().toLowerCase();
					}
					else if(a_splited.length == 1){
						var2=null;var3=null;
					}
					else{error("Incorrect instruction file:: Improper nubuser of arguments");}
    				if(i_Splited.length == 1){
    					sinst = "hlt";
    					var1 = null;
    				}
    				else{
    					sinst = i_Splited[0].trim().toLowerCase();
    					var1 = i_Splited[i_Splited.length-1].trim().toLowerCase();
    				}
    				ProgramCounter.set(var1, var2, var3, sinst, branch);
    				instructions_given.add(ProgramCounter);
    				instruction_number++;
  
    			}
    			fileReader2.close();
    		} catch (IOException e) {e.printStackTrace();}
           
            ArrayList<Scoreboarding> queueInstruction	= new ArrayList<Scoreboarding>();
            ClockCycle c = new ClockCycle();
            MemoryBus bus = new MemoryBus();
            FunctionalUnit res =  new FunctionalUnit(config_data);
            MemoryUnit mem = new MemoryUnit(args[1]);
            DataCache DCache = new DataCache(c, bus, mem);
            InstructionCache ICache = new InstructionCache(config_data[6], config_data[7], c, bus);
            
            int programCounter = 0;
            int clock = ICache.instructionFetch(programCounter);
            int clkCounter = 1;
            Scoreboarding p = new Scoreboarding(res, clock, instructions_given.get(programCounter), clkCounter, c, bus, instructions_given, DCache);
            queueInstruction.add(p); 
            for(programCounter = 0;programCounter < instructions_given.size()-1;){
            	for(int i = 0;i<queueInstruction.size();i++){
            		programCounter = queueInstruction.get(i).everycClockLoader(clkCounter, programCounter+1, i+1)-1;
            	}
            	if(queueInstruction.get(queueInstruction.size()-1).isFetchFree){
            		programCounter++;
            		clock = ICache.instructionFetch(programCounter);
            		p = new Scoreboarding(res, clock, instructions_given.get(programCounter), clkCounter, c, bus, instructions_given, DCache);
            		queueInstruction.add(p);
            	
            	}
            	clkCounter++;
            	c.tick();
            }
            for(int i =0;i<100;i++){
            	for(int j = 0;j<queueInstruction.size();j++){
            		queueInstruction.get(j).everycClockLoader(clkCounter, programCounter, j+1);
            	}
            	clkCounter++;
            	c.tick();
            }
            mem.writeData();
             String cacheStatus= ICache.getStatus()+"\n"+DCache.getStatus();
            print(queueInstruction, args[3], cacheStatus);

		}
	}

}
