package cn.teclub.ha3.net;



@SuppressWarnings("ALL")
public class StExpConnectionLoss extends StExpNet {
	public StExpConnectionLoss(String msg){
		super("[StExpConnectionLoss]" + msg);
	}
	
	public StExpConnectionLoss(){
		super("[StExpConnectionLoss]");
	}
}
