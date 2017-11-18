import java.util.HashMap;

public class InstructionCache {
	private int blockNumber;
	private int blockSize;
	HashMap<Integer,Integer> cache;
	ClockCycle c;
	MemoryBus bus;
	long blockOffset;
	int countOffset,requests, hits;
	public InstructionCache(int blockNumber, int blockSize, ClockCycle c, MemoryBus bus){
		this.blockNumber = blockNumber;this.blockSize = blockSize;this.c = c;this.bus = bus;
		cache = new HashMap<Integer, Integer>();
		refreshoffset();
		hits = 0;requests = 0;
	}
	public int instructionFetch(long address){
		
		int blockNumber = (int) ((address >> countOffset)%this.blockNumber);
		int given_tag = (int) (address >> countOffset);
		this.requests++;
		if(cache.containsKey(blockNumber)){
			if(cache.get(blockNumber) == given_tag){
				this.hits++;
				return 1;
			}else{
				cache.put(blockNumber,given_tag);
				return clockCyclesRequired(this.blockSize*3 )+1;	
			}
		}else{
			cache.put(blockNumber,given_tag);
			return clockCyclesRequired(this.blockSize*3 )+1;
		}
	}
	private void refreshoffset(){
		int tempBlockSize = this.blockSize;
		this.blockOffset = 0;
		this.countOffset = 0;
		
		while(tempBlockSize != 0){
			tempBlockSize = tempBlockSize/2;
			this.blockOffset = this.blockOffset << 1;
			this.blockOffset = this.blockOffset | 1;
			this.countOffset++;
		}
		this.blockOffset = this.blockOffset >> 1;
		this.countOffset = this.countOffset -1;
	}
	
	int clockCyclesRequired(int inClockCycles){
		if(bus.checkIfBusy(c.count()) == false){
			bus.updatebusInUse(c.count()+inClockCycles);
			return inClockCycles;
		}else{
			int busyCycle = (int)((bus.getbusInUse() - c.count())+inClockCycles);
			bus.updatebusInUse(bus.getbusInUse()+inClockCycles);
			return busyCycle;
		}
	}
	
	public String getStatus(){
		String result1 = "Access requests for instruction cache: "+this.requests;
		String hits = "Instruction cache hits: "+this.hits;
		String result2 = result1+"\n"+hits;
		return result2;
	}
}
