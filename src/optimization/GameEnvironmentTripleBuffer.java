package optimization;

import environment.GameEnvironment;

public class GameEnvironmentTripleBuffer {

	private GameEnvironment running;
	private GameEnvironment rendering;
	private GameEnvironment readyToRender;
	
	public GameEnvironment getRunning() {
		return running;
	}
	
	public void setRunning(GameEnvironment running) {
		this.running = running;
	}

	public GameEnvironment getReadyToRender() {
		rendering = readyToRender.clone();
		return rendering;
	}
	
	public void setReadyToRender(GameEnvironment readyToRender) {
		this.readyToRender = readyToRender.clone();
	}
}
