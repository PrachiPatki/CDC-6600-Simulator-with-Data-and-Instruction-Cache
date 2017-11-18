public class MemoryBus {
	private long busInUse;
	public MemoryBus(){
		busInUse = 0;
	}
	
	public boolean checkIfBusy(long clockCount){
		if(busInUse > clockCount){
			return true;
		}
		return false;
	}
	
	public void updatebusInUse(long clockCount){
		busInUse = clockCount;
	}
	public long getbusInUse(){
		return busInUse;
	}
}
