public class ClockCycle {
	private long clock_tick;
	public ClockCycle(){
	clock_tick = 1;	
	}
	public void tick(){
		clock_tick++;
	}
	public long count(){
		return clock_tick;
	}
}
