package controllers.dto;

import java.util.ArrayList;
import java.util.List;

public class SelectSourceDto {
	public String selected;
	public List<Soruce> options = new ArrayList<Soruce>();
	
	 
	public class Soruce{
		public String text;
		public String value;
		public SelectSourceDto subCategory;
	}
}

