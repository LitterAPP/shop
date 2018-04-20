package controllers.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectSourceDto implements Serializable  {
	public String selected;
	public List<Soruce> options = new ArrayList<Soruce>();
	
	 
	public class Soruce implements Serializable {
		public String text;
		public String value;
		public SelectSourceDto subCategory;
	}
}

