package cn.teclub.ha.net;



@SuppressWarnings("serial")
public class StExpConnectionLoss extends StExpNet {
	public StExpConnectionLoss(String msg){
		super("[StExpConnectionLoss]" + msg);
	}
	
	public StExpConnectionLoss(){
		super("[StExpConnectionLoss]");
	}
}
