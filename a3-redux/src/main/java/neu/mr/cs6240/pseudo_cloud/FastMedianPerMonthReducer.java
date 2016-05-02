package neu.mr.cs6240.pseudo_cloud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import neu.mr.cs6240.common.FlightContants;
import neu.mr.cs6240.common.MeanMedianUtility;

public class FastMedianPerMonthReducer extends Reducer<Text, Text, Text, Text> {

	@Override
	/**
	 * writes to context if the carrier(key) is active in 2015
	 * 
	 * @author Smitha Bangalore Naresh
	 * @author Ajay Subramanya
	 * 
	 * @param key
	 *            the carrier
	 * @param value
	 *            month, avg_price, year
	 * @param context
	 *            the output written is in the format "m C v" . 'm' : month, 'C'
	 *            : carrier , 'v' : fast median for the month
	 * 
	 */
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		Map<Short, ArrayList<Integer>> prcMonth = new TreeMap<>();
		boolean active = false;
		for (Text val : values) {
			String[] params = val.toString().split(",");
			if (params[2].equals("2015")) active = true; // params[2] = year
			addToMap(prcMonth, params);
		}
		if (!active) return;
		for (Short month : prcMonth.keySet()) {
			double avgP = MeanMedianUtility.getFastMedian(prcMonth.get(month)) / FlightContants.CENTS;
			context.write(new Text(month + " "), new Text(key.toString() + " " + avgP));
		}

	}

	/**
	 * adds prices/month to HashMap
	 * 
	 * @author Smitha Bangalore Naresh
	 * @author Ajay Subramanya
	 * 
	 * @param prcMonth
	 *            the treeMap of month and the prices for that month
	 * @param params
	 *            month, avg_price, year
	 */
	private void addToMap(Map<Short, ArrayList<Integer>> prcMonth, String[] params) {
		int price = Integer.parseInt(params[1]);
		short month = Short.parseShort(params[0]);
		if (prcMonth.containsKey(month))
			prcMonth.get(month).add(price);
		else {
			ArrayList<Integer> prices = new ArrayList<>();
			prices.add(price);
			prcMonth.put(month, prices);
		}
	}

}
