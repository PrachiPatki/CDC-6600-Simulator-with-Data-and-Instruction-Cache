public class DataCache {
	int ntest ;
	ClockCycle cycle;
	MemoryBus bus;
	int hits,requests,misses;
	String cache[][];
	MemoryUnit mem;
	
	public class HitOrMiss{
		public String data;
		public int clockCycles;
		HitOrMiss(String data, int clockCycles){
			this.data = data;this.clockCycles = clockCycles;
		}
	}
	
	int validbits[],invalidbit[],least_recent_C[];
	int Size_cache,Size_block,tag[];
	public DataCache(ClockCycle c, MemoryBus bus, MemoryUnit memory){
		ntest = 2;Size_cache = 4;Size_block = 4;
		validbits = new int[Size_cache];
		least_recent_C = new int[Size_cache];
		invalidbit = new int[Size_cache];
		tag = new int[Size_cache];
		this.cycle = c;this.bus = bus;
		cache = new String[Size_cache][];
		for(int i =0 ; i < Size_cache; i++){
		  cache[i] = new String[Size_block];
		}
		this.mem = memory;
		requests = 0;hits = 0;
	}
	public HitOrMiss getData_fetch(long address){
		long address_old = address;
		address = address >> 2;
		long blockOffsetMask = 3;
		int blockOffset = (int)(address & blockOffsetMask);
		long addr = address >> 2;
		int setNum = (int)(addr %2);
		this.requests++;
		int i = setNum * this.ntest;
		int templeast_recent_C[] = new int[4];
		templeast_recent_C[i] = this.least_recent_C[i];
		templeast_recent_C[i+1] = this.least_recent_C[i+1];
		this.least_recent_C[i] = 0;
		this.least_recent_C[i+1] = 0;
		if(this.validbits[i] == 1 && this.tag[i] == addr){
			this.least_recent_C[i] = 1;
			this.hits++;
			return new HitOrMiss(cache[i][blockOffset],1);
		}
		if(this.validbits[i+1] == 1 && this.tag[i+1] == addr){
			this.least_recent_C[i+1] = 1;
			this.hits++;
			return new HitOrMiss(cache[i+1][blockOffset],1);
		}
		
		String data[] = mem.fetchData(address_old);
		
		if(this.validbits[i] == 0){
			this.validbits[i] = 1;this.least_recent_C[i] = 1;this.invalidbit[i] = 0;this.tag[i] = (int)addr;
			for(int k = 0 ; k < data.length; k++){
				cache[i][k] = data[k];
			}
			return new HitOrMiss(cache[i][blockOffset],numClockCyclesNeed(12)+1);
		}
		if(this.validbits[i+1] == 0){
			this.validbits[i+1] = 1;this.invalidbit[i+1] = 0;this.least_recent_C[i+1] = 1;this.tag[i+1] = (int)addr;
			for(int k = 0 ; k < data.length; k++){
				cache[i+1][k] = data[k];
			}
			return new HitOrMiss(cache[i+1][blockOffset],numClockCyclesNeed(12)+1);
		}
		
	
		if(templeast_recent_C[i] == 0){
			this.validbits[i] = 1;this.least_recent_C[i] = 1;
			int extraCycles = 0;
			if(this.invalidbit[i] == 1){
				extraCycles = 12;
				mem.updateData(address_old, cache[i]);
			}
			this.invalidbit[i] = 0;this.tag[i] = (int)addr;
			for(int k = 0 ; k < data.length; k++){
				cache[i][k] = data[k];
			}
			return new HitOrMiss(cache[i][blockOffset],numClockCyclesNeed(12+extraCycles)+1);
		}
		if(templeast_recent_C[i+1] == 0){
			this.validbits[i+1] = 1;this.least_recent_C[i+1] = 1;this.tag[i+1] = (int)addr;
			int extraCycles = 0;
			if(this.invalidbit[i+1] == 1){
				extraCycles = 12;
				mem.updateData(address_old, cache[i]);
			}
			this.invalidbit[i+1] = 0;
			for(int k = 0 ; k < data.length; k++){
				cache[i+1][k] = data[k];
			}
			return new HitOrMiss(cache[i+1][blockOffset],numClockCyclesNeed(12+extraCycles)+1);
		}
		return null;
	}
	int numClockCyclesNeed(int clockcyclestake){
		if(bus.checkIfBusy(cycle.count()) == false){
			bus.updatebusInUse(cycle.count()+clockcyclestake);
			return clockcyclestake;
		}else{
			int busyCount = (int)((bus.getbusInUse() - cycle.count())+clockcyclestake);
		    bus.updatebusInUse(bus.getbusInUse()+clockcyclestake);
			return busyCount;
		}
	}
	
	public int newValue(long address, String data){
		long address_old = address;
		address = address >> 2;
		long blockOffsetMask = 3;
		int blockOffset = (int)(address & blockOffsetMask);
		long addr = address >> 2;
		int setNum = (int)(addr %2);
		int index = setNum * this.ntest;
		int templeast_recent_C[] = new int[4];
		templeast_recent_C[index] = this.least_recent_C[index];
		templeast_recent_C[index+1] = this.least_recent_C[index+1];
		this.least_recent_C[index] = 0;
		this.least_recent_C[index+1] = 0;
		mem.updateSWData(address_old, data);
		if(this.validbits[index] == 1 && this.tag[index] == addr){
			this.least_recent_C[index] = 1;
			this.invalidbit[index] = 1;
			cache[index][blockOffset] = data;
			return 1;
		}
		if(this.validbits[index+1] == 1 && this.tag[index+1] == addr){
			this.least_recent_C[index+1] = 1;this.invalidbit[index+1] = 1;
			cache[index+1][blockOffset] = data;
			return 1;
		}
				String newdata[] = mem.updateGetData(address_old, blockOffset,data);
		if(this.validbits[index] == 0){
			this.validbits[index] = 1;
			this.least_recent_C[index] = 1;
			this.invalidbit[index] = 0;
			this.tag[index] = (int)addr;
			for(int i = 0 ; i < newdata.length; i++){
				cache[index][i] = newdata[i];
			}
			return numClockCyclesNeed(12)+1;			
		}
		if(this.validbits[index+1] == 0){
			this.validbits[index+1] = 1;
			this.least_recent_C[index+1] = 1;
			this.invalidbit[index+1] = 0;
			this.tag[index+1] = (int)addr;
			for(int i = 0 ; i < newdata.length; i++){
				cache[index+1][i] = newdata[i];
			}
			return numClockCyclesNeed(12)+1;	
		}

		if(templeast_recent_C[index] == 0){
			this.validbits[index] = 1;
			this.least_recent_C[index] = 1;
			this.tag[index] = (int)addr;
			int extraCycles = 0;
			if(this.invalidbit[index] == 1){
				extraCycles = 12;
				mem.updateData(address_old, cache[index]);
			}
			this.invalidbit[index] = 0;
			for(int k = 0 ; k < newdata.length; k++){
				cache[index][k] = newdata[k];
			}
			return numClockCyclesNeed(12+extraCycles)+1;
		}
		if(templeast_recent_C[index+1] == 0){
			this.validbits[index+1] = 1;
			this.least_recent_C[index+1] = 1;
			this.tag[index+1] = (int)addr;
			int extraCycles = 0;
			if(this.invalidbit[index+1] == 1){
				mem.updateData(address_old, cache[index+1]);
				extraCycles =12;
			}
			this.invalidbit[index+1] = 0;
			for(int k = 0 ; k < newdata.length; k++){
				cache[index+1][k] = newdata[k];
			}
			return numClockCyclesNeed(12+extraCycles)+1;
		}
		return -1;
	}	
	
	public String getStatus(){
		String requests = "Total number of access requests for data cache: "+this.requests;
		String hits = "Number of data cache hits: "+this.hits;
		String myResult = requests+"\n"+hits;
		return myResult;
	}
}
