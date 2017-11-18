
public class FunctionalUnit {
	boolean fetch,issue,read,exe,wb;
	boolean[] mult,add,div;
	boolean unitInteger,unitLoad,unitBranch;
	int cyclesMultiplication, cyclesAddition, cyclesDivision;
	int sizeMultiplication,sizeAddition,sizeDivision;
	int[][] Reg;
	
	public FunctionalUnit(int[] config) {
		this.Reg = new int[33][3]; 
		this.FloatingReg = new int[33][3]; 
		this.fetch = false; this.issue =false;
		this.mult = new boolean[config[2]];this.cyclesMultiplication = config[3];
		this.add = new boolean[config[0]]; this.cyclesAddition = config[1];
		this.div = new boolean[config[4]];this.cyclesDivision = config[5]; 
		this.unitBranch=false;this.unitInteger = false; this.unitLoad = false; 
		this.sizeMultiplication = config[2];this.sizeAddition = config[0];this.sizeDivision = config[4];
	}
	
	int[][] FloatingReg;
	void registerStatusUpdate(InstructionPointer pInst, int var1){
		if(pInst.var1 != null){
			char temp;
			int valueReg = 0;
			if(pInst.var1.length() == 3){
				valueReg = Integer.parseInt(pInst.var1.substring(1, 3));
			}
			else{
				valueReg = Character.getNumericValue(pInst.var1.charAt(1));
			}
			temp = pInst.var1.charAt(0);
			if(temp == 'r'){
				this.Reg[valueReg][0] = var1;
			}
			if(temp == 'f'){
				this.FloatingReg[valueReg][0] = var1;
			}
		}
	}
	
	char registersReturn(String arg) {
		if(arg == null){
			return 'x';
		}
		char temp = 'x';
		if(arg.indexOf('r') != -1){
			return 'r';
		}
		else if (arg.indexOf('f') != -1){
			return 'f';
		}
		else {
			return temp;
		}
	}
	
	int registerNumReturn(String arg) {
		if(arg == null){
			return -1;
		}
		char type = registersReturn(arg);
		if(type == 'r' ){
			int ten;
			int one = -1;
			ten  = Character.getNumericValue(arg.charAt(arg.indexOf('r')+1));
			if(arg.length() < arg.indexOf('r')+2){
				one  = Character.getNumericValue(arg.charAt(arg.indexOf('r')+2));
			}
			if(one > 0 && one < 10){
				return (ten*10+one);
			}
			else if(ten > 0 && ten < 10){
				return ten;
			}
			
		}
		if(type == 'f'){
			int ten;
			int one = -1;
			ten  = Character.getNumericValue(arg.charAt(arg.indexOf('f')+1));
			if(arg.length() < arg.indexOf('f')+2){
				one  = Character.getNumericValue(arg.charAt(arg.indexOf('f')+2));
			}
			if(one > 0 && one < 10){
				return (ten*10+one);
			}
			else if(ten > 0 && ten < 10){
				return ten;
			}
		}
		return -1;
	}
}
