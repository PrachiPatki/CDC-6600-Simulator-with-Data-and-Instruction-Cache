
public class Execution {
	
	int getArgument(String var1){
		int valueRegister;
		if(var1 != null){
			if(var1.length() == 3){
				valueRegister = Integer.parseInt(var1.substring(1, 3));
			}
			else{
				valueRegister = Character.getNumericValue(var1.charAt(1));
			}
			return valueRegister;
		}
		return 0;
	}
	FunctionalUnit result;
	void exeInstruction(InstructionPointer pInst, String data){
		int r1,r2,r3;
		switch(pInst.inst){
			case "lw":
				r1 = result.registerNumReturn(pInst.var1);
				int value = StringValidFormat.toInt(data);
				result.Reg[r1][0] = value; 
				break;
			case "sw":
				r1 = result.registerNumReturn(pInst.var1);
				r2 = result.registerNumReturn(pInst.var2);
				String data1 = StringValidFormat.toBinary(result.Reg[r1][0]);
				int off = Integer.parseInt(pInst.var2.substring(0, pInst.var2.indexOf('(')));
				int adress = result.Reg[r2][0]+off; 
				if(adress > 255){
					DCache.newValue(adress, data1);
				}
				break;
			case "l.d":
			case "s.d":
			case "hlt":
			case "j":
			case "beq":
			case "bne":
			case "add.d":
			case "div.d":
			case "mul.d":
			case "sub.d":
				break;
			case "dadd":
				r1 = getArgument(pInst.var1);
				r2 = getArgument(pInst.var2);
				r3 = getArgument(pInst.var3);
				result.Reg[r1][0] = result.Reg[r2][0]+result.Reg[r3][0];
				break;
			case "daddi":
				r1 = getArgument(pInst.var1);
				r2 = getArgument(pInst.var2);
				r3 = Integer.parseInt(pInst.var3);
				result.Reg[r1][0] = result.Reg[r2][0] + r3;
				break;
			case "dsub":
				r1 = getArgument(pInst.var1);
				r2 = getArgument(pInst.var2);
				r3 = getArgument(pInst.var3);
				result.Reg[r1][0] = result.Reg[r2][0]-result.Reg[r3][0];
				break;
			case "dsubi":
				r1 = getArgument(pInst.var1);
				r2 = getArgument(pInst.var2);
				r3 = Integer.parseInt(pInst.var3);
				result.Reg[r1][0] = result.Reg[r2][0] - r3;
				break;
			case "and":
				r1 = getArgument(pInst.var1);
				r2 = getArgument(pInst.var2);
				r3 = getArgument(pInst.var3);
				result.Reg[r1][0] = result.Reg[r2][0]&result.Reg[r3][0];
				break;
			case "andi":
				r1 = getArgument(pInst.var1);
				r2 = getArgument(pInst.var2);
				r3 = Integer.parseInt(pInst.var3);
				result.Reg[r1][0] = result.Reg[r2][0] & r3;
				break;
			case "li":
				r1 = getArgument(pInst.var1);
				r2 = Integer.parseInt(pInst.var2);
				result.Reg[r1][0] = r2;
				break;
			case "lui":
				r1 = getArgument(pInst.var1);
				r2 = getArgument(pInst.var2);
				r3 = Integer.parseInt(pInst.var3);
				result.Reg[r1][0] = result.Reg[r2][0] | r3;
				break;
			default: 
				System.out.println("Invalid Instruction given as Input");
				System.exit(0);
		}
	}
	DataCache DCache;
	public Execution(FunctionalUnit Resource, DataCache DCache) {
		// TODO Auto-generated constructor stub
		this.result = Resource;
		this.DCache = DCache;
	}
}
