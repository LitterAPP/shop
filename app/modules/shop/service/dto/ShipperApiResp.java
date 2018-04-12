package modules.shop.service.dto;

import java.util.List;

public class ShipperApiResp {

	private String LogisticCode;
	private String ShipperCode;
	private String State;
	private boolean Success;
	private List<Trace> Traces;
	public String getLogisticCode() {
		return LogisticCode;
	}
	public void setLogisticCode(String logisticCode) {
		LogisticCode = logisticCode;
	}
	public String getShipperCode() {
		return ShipperCode;
	}

	public void setShipperCode(String shipperCode) {
		ShipperCode = shipperCode;
	}



	public String getState() {
		return State;
	}



	public void setState(String state) {
		State = state;
	}



	public boolean isSuccess() {
		return Success;
	}



	public void setSuccess(boolean success) {
		Success = success;
	}



	public List<Trace> getTraces() {
		return Traces;
	}



	public void setTraces(List<Trace> traces) {
		Traces = traces;
	}



	public class Trace implements Comparable<Trace>{
		private String AcceptStation;
		private String AcceptTime;
		public String getAcceptStation() {
			return AcceptStation;
		}
		public void setAcceptStation(String acceptStation) {
			AcceptStation = acceptStation;
		}
		public String getAcceptTime() {
			return AcceptTime;
		}
		public void setAcceptTime(String acceptTime) {
			AcceptTime = acceptTime;
		}
		@Override
		public int compareTo(Trace o) { 
			return o.getAcceptTime().compareTo(this.AcceptTime);
		}
		
	}
}
