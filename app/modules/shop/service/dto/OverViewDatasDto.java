package modules.shop.service.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import util.DateUtil;

public class OverViewDatasDto {
    private Map<String, Integer> index = new HashMap<String, Integer>();
    public Set<String> xAxis = new TreeSet<String>();

    public List<Integer> orderCount = new ArrayList<Integer>();
    public List<Integer> orderTogetherCount = new ArrayList<Integer>();
    public List<Integer> productCount = new ArrayList<Integer>();
    public List<Integer> deliverCount = new ArrayList<Integer>();
    public List<Double> orderAmountSum = new ArrayList<Double>();

    public OverViewDatasDto(long begin, long end) {
        calculateTime(begin, end);
        for (int i = 0; i < xAxis.size(); i++) {
            orderCount.add(0);
            orderTogetherCount.add(0);
            productCount.add(0);
            deliverCount.add(0);
            orderAmountSum.add(0.00d);
        }
    }

    private void calculateTime(long begin, long end) {

        String startDateStr = DateUtil.format(begin, "yyyy-MM-dd");
        begin = DateUtil.getTime(startDateStr + " 00:00:00");
        String endDateStr = DateUtil.format(end, "yyyy-MM-dd");
        end = DateUtil.getTime(endDateStr + " 00:00:00");
        xAxis.add(startDateStr);
        index.put(startDateStr, xAxis.size() - 1);
        if (begin >= end) {
            return;
        }
        begin = begin + 24 * 60 * 60 * 1000;
        if (begin > end) {
            return;
        } else {
            calculateTime(begin, end);
        }
    }

    public Integer getIndex(String time) {
        return index.get(time);
    }

    public static void main(String[] args) {
		/*OverViewDatasDto x = new OverViewDatasDto(
				DateUtil.getTime("2018-04-01"),
				DateUtil.getTime("2018-04-01 09:00:00"));
		for(String t : x.xAxis){
			System.out.println(t);
		}*/

        String test = DateUtil.format(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000l, "yyyy-MM-dd");
        System.out.println(test);

    }
}
