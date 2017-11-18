import java.util.ArrayList;

public class Scoreboarding {
 boolean raw,war,waw,struct;
 int FetchCycles,clk,fe,is,rd,ex,wb;
 int counter=0;
 String var1,var2,var3,inst,branch;
 DataCache DCache;
 InstructionPointer instructionPointer;
 FunctionalUnit result;
 ArrayList<InstructionPointer> arraylist;
 boolean isNextStageFree,isOkToPass, stageLast;
 
 boolean hzWAW(InstructionPointer pcInstruction){
	 int valueReg = 0;
	 if(pcInstruction.var1 != null && pcInstruction.var2 != null){
		 if(pcInstruction.var1.length() == 3){
			 valueReg = Integer.parseInt(pcInstruction.var1.substring(1, 3));
		 }
		 else if(pcInstruction.var1.length() == 2){
			 valueReg = Character.getNumericValue(pcInstruction.var1.charAt(1));
		 }
	 }
	 else{
		 return false;
	 }
	 char temp;
	 temp = pcInstruction.var1.charAt(0);
	 if(temp == 'r'){
		 if(result.Reg[valueReg][1] > 0){
			 return true;
		 }
	 }
	 if(temp == 'f'){
		 if(result.FloatingReg[valueReg][1] > 0){
			 return true;
		 }
	 }
	 
	 return false;
 }

 boolean hzWAR(String var1, String var2, String var3, int ProgramCounter){
	 int valueReg = 0;
	 if(var2 != null){
		 valueReg = result.registerNumReturn(var2);
		 char temp;
		 temp = result.registersReturn(var2);
		 if(temp == 'r' && valueReg>0 ){
			 if(result.Reg[valueReg][1] > 0 && result.Reg[valueReg][1] < ProgramCounter && !var1.equals(var2)){
				 return true;
			 }
		 }
		 if(temp == 'f' && valueReg>0 ){
			 if(result.FloatingReg[valueReg][1] > 0 && result.FloatingReg[valueReg][1] < ProgramCounter && !var1.equals(var2)){
				 return true;
			 }
		 }
	 }
	 else {
		 return false;
	 }
	 if(instructionPointer.var3 != null){
		 valueReg = result.registerNumReturn(var3);
		 char temp;
		 temp = result.registersReturn(var3);
		 if(temp == 'r' && valueReg>0){
			 if(result.Reg[valueReg][1] > 0 && result.Reg[valueReg][1] < ProgramCounter &&  !var1.equals(var3)){
				 return true;
			 }
		 }
		 if(temp == 'f' && valueReg>0){
			 if(result.FloatingReg[valueReg][1] > 0 && result.FloatingReg[valueReg][1] < ProgramCounter && !var1.equals(var3)){
				 return true;
			 }
		 }
	 }
	 else {
		 return false;
	 }
	 return false;
 }
 
 boolean hzRAW(InstructionPointer pcInstruction){
	 int valueReg = 0;
	 if(pcInstruction.var1 != null && pcInstruction.var2 != null){
		 if(pcInstruction.var1.length() == 3){
			 valueReg = Integer.parseInt(pcInstruction.var1.substring(1, 3));
		 }
		 else if(pcInstruction.var1.length() == 2){
			 valueReg = Character.getNumericValue(pcInstruction.var1.charAt(1));
		 }
	 }
	 else{
		 return false;
	 }
	 char temp;
	 temp = pcInstruction.var1.charAt(0);
	 if(temp == 'r' && valueReg>0){
		 if(result.Reg[valueReg][2] == 1){
			 return true;
		 }
	 }
	 if(temp == 'f' && valueReg>0){
		 if(result.FloatingReg[valueReg][2] == 1){
			 return true;
		 }
	 }
	 return false;
 }
 
 
 boolean CheckDCacheHit = false;
 String chkLoadfromCache(int offset){
	if( instructionPointer.inst.equals("lw") || instructionPointer.inst.equals("l.d") || instructionPointer.inst.equals("sw") || instructionPointer.inst.equals("s.d")){
		int r2 = result.registerNumReturn(instructionPointer.var2);
		int r1 = result.registerNumReturn(instructionPointer.var1);
		int x = instructionPointer.var2.indexOf('(');
		int address;
		if(x>1){
		address = result.Reg[r2][0]+Integer.parseInt(instructionPointer.var2.substring(0, x));
		}
		else{
			address = result.Reg[r2][0]+Character.getNumericValue(instructionPointer.var2.charAt(0));
		}
		DataCache.HitOrMiss DcInfo = null;
		//DcInfo = DCache.getData_fetch(255);
		//if(address > 255){
		//if(instructionPointer.inst.equals("sw")){
			if(address < 255){
				return "00000000000000000";
			}
		//}
			DcInfo = DCache.getData_fetch(address+offset);
			this.CyclesFrExecution = this.CyclesFrExecution+DcInfo.clockCycles;
		//}
		
		if(instructionPointer.inst.equals("lw") || instructionPointer.inst.equals("sw")){
			this.CyclesFrExecution--;
		}
		if(instructionPointer.inst.equals("sw")){
		if(address > 255){
			DCache.newValue(address+offset, StringValidFormat.toBinary(result.Reg[r1][0]));
		}
		}
		this.CheckDCacheHit = true;
		return DcInfo.data;
	}
	return "000";
 }
 
void updateRegFlags(InstructionPointer pcInstruction){
	 int valueReg = 0;
	 if(pcInstruction.var2 != null){
		 char temp;
		 temp = result.registersReturn(pcInstruction.var2);
		 valueReg = result.registerNumReturn(pcInstruction.var2);
		 if(temp == 'r' && valueReg>0){
			 result.Reg[valueReg][2] = 0;
		 }
		 if(temp == 'f' && valueReg>0){
			 result.FloatingReg[valueReg][2] = 0;
		 }
	 }
	 if(pcInstruction.var3 != null){
		 char temp;
		 temp = result.registersReturn(pcInstruction.var3);//pcInstruction.var2.charAt(0);
		 valueReg = result.registerNumReturn(pcInstruction.var3);
		 if(temp == 'r' && valueReg>0){
			 result.Reg[valueReg][2] = 0;
		 }
		 if(temp == 'f' && valueReg>0){
			 result.FloatingReg[valueReg][2] = 0;
		 }
	 }
 }
void updatewriteRegFlags(InstructionPointer pcInstruction){
	 int valueReg = 0;
	 if(pcInstruction.var1 != null){
		 char temp;
		 temp = result.registersReturn(pcInstruction.var1);
		 valueReg = result.registerNumReturn(pcInstruction.var1);
		 if(temp == 'r' && valueReg>0){
			 result.Reg[valueReg][1] = 0;
		 }
		 if(temp == 'f' && valueReg>0){
			 result.FloatingReg[valueReg][1] = 0;
		 }
	 }
	 
}
void isOkToPass(int stagePresent){
	 this.isOkToPass = false;
	 switch(stagePresent){
	 case 0:{
		 if(this.isNextStageFree){
			 this.isOkToPass = true;
		 }
	 break;}
	 case 1:{
		 if(this.struct == false && this.waw == false && this.isNextStageFree){
			 this.isOkToPass = true;
		 }
	 break;}
	 case 2:{
		 if(this.raw == false && this.isNextStageFree){
			 this.isOkToPass = true;
		 }
	 break;}
	 case 3:{
		 if(this.isNextStageFree){
			 this.isOkToPass = true;
		 }
	 break;}
	 case 4:{
		 if(this.isNextStageFree && !this.war){
			 this.isOkToPass = true;
		 }
	 break;}
	 case 5:{
		 if(this.isNextStageFree){
			 this.isOkToPass = true;
		 }
	 break;}
	 }
}
int CyclesFrExecution;
boolean isFunctionalUnitFree(InstructionPointer pointInst){
	 switch(pointInst.inst){
	 	case "mul.d":{
	 		this.CyclesFrExecution = result.cyclesMultiplication;
	 		for(int i=0;i<result.sizeMultiplication;i++){
	 			if(result.mult[i] == false){
	 				return true;
	 			}
	 		}
	 	break;}
	 	case "add.d":
	 	case "sub.d":{
	 		this.CyclesFrExecution = result.cyclesAddition;
	 		for(int i=0;i<result.sizeAddition;i++){
	 			if(result.add[i] == false){
	 				return true;
	 			}
	 		}
	 	break;}
	 	case "div.d":{
			this.CyclesFrExecution = result.cyclesDivision;
	 		for(int i=0;i<result.sizeDivision;i++){
	 			if(result.div[i] == false){
	 				return true;
	 			}
	 		}
	 	break;}
	 	case "and":
	 	case "andi":
	 	case "dadd":
	 	case "daddi":
	 	case "dsub":
	 	case "dsubi":
	 	case "or":
	 	case "ori":
	 	case "lui":
	 	case "li":{
	 		this.CyclesFrExecution = 1;
	 		if(result.unitInteger == false){
	 			return true;
	 		}
	 	break;}
	 	case "lw":
	 	case "sw":
	 		this.CyclesFrExecution = 1;
	 		if(result.unitLoad == false){	
	 			return true;
	 		}
	 	case "l.d":
	 	case "s.d":{
	 		this.CyclesFrExecution = 1;
	 		if(result.unitLoad == false){
	 			return true;
	 		}
	 	break;}
	 	case "beq":
	 	case "bne":
	 	case "j":
	 	case "hlt":
	 		this.CyclesFrExecution = 1;
	 		if(result.unitBranch == false){
	 			return true;
	 		}
	 		break;	
	 }
	 return false;
}
 
 
 int branch(String branch, int prgramCounter){
	 int ProgramCounter=prgramCounter;
	 int r2 = 0;
	 int r1 = 0;
	 switch(branch){
	 case "bne":
		 r2 = result.registerNumReturn(instructionPointer.var2);
		 r1 = result.registerNumReturn(instructionPointer.var1);
		 if(result.Reg[r1][0] != result.Reg[r2][0]){
			 for(int i = 0;i<arraylist.size()-1;i++){
				 if (arraylist.get(i).branch != null){
					 if(arraylist.get(i).branch.equals(instructionPointer.var3)){
						 return i;
					 }
				 }
			 }
		 }
		 break;
	 case "beq":
		 r1 = result.registerNumReturn(instructionPointer.var1);
		 if(result.Reg[r1][0] == 0){
			 for(int i = 0;i<arraylist.size()-1;i++){
				 if (arraylist.get(i).branch != null){
					 if(arraylist.get(i).branch.equals(instructionPointer.var2)){
						 return i;
					 }
				 }
			 }
		 }
		 break;
	 case "j":
		 for(int i = 0;i<arraylist.size()-1;i++){
			 if (arraylist.get(i).branch != null){
				 if(arraylist.get(i).branch.equals(instructionPointer.var1)){
					 return i;
				 }
			 }
		 }
		 break;
	 }
	 return ProgramCounter;
 }
 
void setWriteReg(String arg, int ProgramCounter){
	int valueReg = 0;
	if(arg != null){
		 if(arg.length() == 3){
			 valueReg = Integer.parseInt(arg.substring(1, 2));
		 }
		 else if(arg.length() == 2){
			 valueReg = Character.getNumericValue(arg.charAt(1));
		 }
	
	char temp = arg.charAt(0);
	 if(temp == 'r' && valueReg>0){
		 result.Reg[valueReg][1] = ProgramCounter;
	 }
	 if(temp == 'f' && valueReg>0){
		 result.FloatingReg[valueReg][1] = ProgramCounter;
	 }
	}
}
void setReadReg(String arg){
	int valueReg = 0;
	if(arg != null){
		 if(arg.length() == 3){
			 valueReg = Integer.parseInt(arg.substring(1, 3));
		 }
		 else if(arg.length() == 2){
			 valueReg = Character.getNumericValue(arg.charAt(1));
		 }
	 char temp = arg.charAt(0);
	 if(temp == 'r'){
		 result.Reg[valueReg][2] = 1;
	 }
	 if(temp == 'f'){
		 result.FloatingReg[valueReg][2] = 1;
	 }
	}
}
void setFunctionalUnitBusy(InstructionPointer pcInstruction){
	 switch(pcInstruction.inst){
	 	case "mul.d":{
	 		for(int i=0;i<result.sizeMultiplication;i++){
	 			if(result.mult[i] == false){
	 				result.mult[i] = true;
	 				return;
	 			}
	 		}
	 	break;}
	 	case "add.d":
	 	case "sub.d":{
	 		for(int i=0;i<result.sizeAddition;i++){
	 			if(result.add[i] == false){
	 				result.add[i] = true;
	 				return;
	 			}
	 		}
	 	break;}
	 	case "div.d":{
	 		for(int i=0;i<result.sizeDivision;i++){
	 			if(result.div[i] == false){
	 				result.div[i] = true;
	 				return;
	 			}
	 		}
	 	break;}
	 	case "and":
	 	case "andi":
	 	case "dadd":
	 	case "daddi":
	 	case "dsub":
	 	case "dsubi":
	 	case "or":
	 	case "ori":
	 	case "lui":
	 	case "li":{
	 		result.unitInteger = true;
	 	break;}
	 	case "lw":
	 	case "sw":
	 	case "l.d":
	 	case "s.d":{
	 		result.unitLoad = true;
	 	break;}
	 	case "beq":
	 	case "bne":
	 	case "j":
	 	case "hlt":
	 		result.unitBranch = true;
	 		break;
	 	
	 }
	 return;
}

void makeFunctionalUnitFree(InstructionPointer pcInstruction){
	 switch(pcInstruction.inst){
	 	case "mul.d":{
	 		for(int i=0;i<result.sizeMultiplication;i++){
	 			if(result.mult[i] == true){
	 				result.mult[i] = false;
	 				return;
	 			}
	 		}
	 	break;}
	 	case "add.d":
	 	case "sub.d":{
	 		for(int i=0;i<result.sizeAddition;i++){
	 			if(result.add[i] == true){
	 				result.add[i] = false;
	 				return;
	 			}
	 		}
	 	break;}
	 	case "div.d":{
	 		for(int i=0;i<result.sizeDivision;i++){
	 			if(result.div[i] == true){
	 				result.div[i] = false;
	 				return;
	 			}
	 		}
	 	break;}
	 	case "and":
	 	case "andi":
	 	case "dadd":
	 	case "daddi":
	 	case "dsub":
	 	case "dsubi":
	 	case "or":
	 	case "ori":
	 	case "lui":
	 	case "li":{
	 		if(result.unitInteger == true){result.unitInteger = false;}
	 	break;}
	 	case "lw":
	 	case "sw":
	 	case "l.d":
	 	case "s.d":{
	 		if(result.unitLoad == true){result.unitLoad = false;}
	 	break;}
	 
	 }
	 return;
}
	int stagePresent,stageNext;
	char struct_haz, waw_haz, war_haz, raw_haz;
	boolean isFetchFree = false;
 int everycClockLoader(int clkCounter, int prgramCounter, int warProgramCounter){
	 if(this.stageLast){
		 isOkToPass(this.stagePresent);
	 }
	 if(this.isOkToPass == true){
		 this.stagePresent = this.stageNext;
		 this.stageNext = this.stageNext+1;
	 }
	 this.stageLast = false; this.isNextStageFree = false;this.isFetchFree = false;this.isOkToPass =false;
	 
	 switch(this.stagePresent){
	 	case 0:{
	 		this.FetchCycles--;
	 		this.var1= instructionPointer.var1;this.var2= instructionPointer.var2;this.var3= instructionPointer.var3;this.inst= instructionPointer.inst;this.branch = instructionPointer.branch;
	 		if(!result.issue && instructionPointer.inst.equals("hlt") && this.FetchCycles <= 0){
	 			this.stageLast = true;this.isFetchFree = true;this.fe = clkCounter;this.isNextStageFree = true;
	 			break;
	 		}
	 		if(!result.issue && this.FetchCycles <= 0){
	 			this.stageLast = true;this.isFetchFree = true;this.fe = clkCounter;this.isNextStageFree = true;
	 		}
	 	break;}
	 	case 1:{
	 		result.issue = true;
	 		this.waw = hzWAW(this.instructionPointer);
	 		
	 		boolean busyFU = true;
	 		busyFU = isFunctionalUnitFree(this.instructionPointer);
	 		
	 		if(instructionPointer.inst.equals("bne")){
	 			this.stageLast = true;
	 			result.issue = false;
	 			this.is = clkCounter;this.isNextStageFree = true;this.waw = false;this.struct =false;
	 			prgramCounter = branch("bne", prgramCounter);
	 			break;
	 		}
	 		if(instructionPointer.inst.equals("j")){
	 			this.stageLast = true;
	 			result.issue = false;
	 			this.is = clkCounter;this.isNextStageFree = true;this.waw = false;this.struct =false;
	 			prgramCounter = branch("j", prgramCounter);
	 			break;
	 		}
	 		if(instructionPointer.inst.equals("beq")){
	 			this.stageLast = true;
	 			result.issue = false;
	 			this.is = clkCounter;this.isNextStageFree = true;this.waw = false;this.struct =false;
	 			prgramCounter = branch("beq", prgramCounter);
	 			break;
	 		}
	 		if(instructionPointer.inst.equals("s.d")||instructionPointer.inst.equals("sw")){
	 			this.waw = false;
	 		}
	 		if(this.waw && !instructionPointer.inst.equals("hlt")){
	 			this.waw_haz = 'Y';
	 		}
	 		if(!busyFU &&  !instructionPointer.inst.equals("hlt")){this.struct_haz = 'Y';}
	 		if(busyFU && !this.waw){
	 			setFunctionalUnitBusy(this.instructionPointer);
	 			this.stageLast = true;
	 			result.issue = false;
	 			if(!(instructionPointer.inst.equals("s.d")||instructionPointer.inst.equals("sw"))){
	 				setWriteReg(instructionPointer.var1, warProgramCounter);
	 			}
	 			this.is = clkCounter;this.isNextStageFree = true;
	 		}
	 	break;}
	 	case 2:{
	 		if(instructionPointer.inst.equals("hlt")){
	 			this.stageLast = true;this.rd = 0;this.isNextStageFree = true;this.stagePresent = 4;this.stageNext = 5;
	 			break;
	 		}
	 		if(instructionPointer.inst.equals("bne") || instructionPointer.inst.equals("beq") || instructionPointer.inst.equals("j")){
	 			setFunctionalUnitBusy(this.instructionPointer);
	 		}
	 		if(instructionPointer.inst.equals("bne") || instructionPointer.inst.equals("beq") || instructionPointer.inst.equals("sw")|| instructionPointer.inst.equals("s.d")){
	 			this.war = hzWAR("Hello", this.instructionPointer.var1, this.instructionPointer.var2, warProgramCounter);
	 		}
	 		else{
	 			this.war = hzWAR(this.instructionPointer.var1, this.instructionPointer.var2, this.instructionPointer.var3, warProgramCounter);
	 		}	
	 		if(this.war){
	 			this.war_haz='Y';
	 		}
	 		if(!this.war){
	 			this.stageLast = true;
	 			result.unitBranch = false;
	 			this.rd = clkCounter;
	 			setReadReg(instructionPointer.var2);
	 			setReadReg(instructionPointer.var3);
	 			this.isNextStageFree = true;
	 			if(!CheckDCacheHit){
	 				String data = "";
		 			data = chkLoadfromCache(0); 
		 			Execution exec = new Execution(result, DCache);
		 			exec.exeInstruction(this.instructionPointer, data);
		 			CheckDCacheHit = true;
		 			if(instructionPointer.inst.equals("l.d") || instructionPointer.inst.equals("s.d")){
		 				CheckDCacheHit = false;
		 			}
		 		}
	 		}
	 	break;}
	 	case 3:{
	 		if(!CheckDCacheHit){
	 			chkLoadfromCache(4);
	 			CheckDCacheHit = true;
	 			this.CyclesFrExecution--;
	 		}
	 		if(this.CyclesFrExecution == 1){
	 			updateRegFlags(this.instructionPointer);
	 			this.stageLast = true;this.ex = clkCounter;this.isNextStageFree = true;
	 			break;
	 		}
	 		this.CyclesFrExecution--;
	 	break;}
	 	case 4:{
	 		if(this.raw){this.raw_haz='Y';}
	 		if(!this.raw){
	 			this.stageLast = true;this.wb = clkCounter;this.isNextStageFree = true;
	 		}
	 	break;}
	 	case 5: 
	 		updatewriteRegFlags(this.instructionPointer);
 			makeFunctionalUnitFree(this.instructionPointer);
 			this.stageLast = true;this.isNextStageFree = true;this.var1= instructionPointer.var1;this.var2= instructionPointer.var2;this.var3= instructionPointer.var3;this.inst= instructionPointer.inst;this.branch = instructionPointer.branch;
 			if(instructionPointer.inst.equals("bne") || instructionPointer.inst.equals("j") || instructionPointer.inst.equals("beq")){
 				this.wb = 0;
 				this.ex = 0;
	 		}
 			break;
 		default: break;
 		
	 }
	 
	 return prgramCounter;
 }
 public Scoreboarding(FunctionalUnit res, int FetchCycles, InstructionPointer pointInst, int clk, ClockCycle c, MemoryBus MB, ArrayList<InstructionPointer> list, DataCache DCache) {
	 this.raw = false;this.war=false; this.waw = false;
	 this.stagePresent = 0;this.stageNext = 1; this.isOkToPass = false;this.stageLast=false; 
	 this.fe = 0; this.is = 0; this.rd = 0; this.ex = 0; this.wb = 0;
	 this.war_haz = 'N'; this.waw_haz = 'N'; this.raw_haz = 'N'; this.struct_haz = 'N';
	 this.result = res; this.FetchCycles = FetchCycles; this.instructionPointer = pointInst;
	 this.clk = clk;
	 this.DCache = DCache;
	 this.var1= null; this.var2=null;this.var3=null;this.inst=null; this.branch=null;this.arraylist = list;
 }
}
