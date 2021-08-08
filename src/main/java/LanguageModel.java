
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class LanguageModel {
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {

		int threashold;
		// get the threashold parameter from the configuration
		@Override
		public void setup(Context context) {
			Configuration conf = context.getConfiguration();
			threashold = conf.getInt("threashold", 20);
		}

		
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {			
			//input: output of last reducer, big data\t123
			//split into inputphrase and following word+count
			//outputKey = inputPhrase
			//outputValue = fllowingWord + count
			//outputkey:this is  outputvalue:cool = 20
			if((value == null) || (value.toString().trim()).length() == 0) {
				return;
			}
			String line = value.toString().trim();
			
			String[] wordsPlusCount = line.split("\t");
			if(wordsPlusCount.length < 2) {
				return;
			}
					
			int count = Integer.valueOf(wordsPlusCount[1]);		
			if(count < threashold) {
				return;
			}

			String[] words = wordsPlusCount[0].split("\\s+");

			StringBuilder inputPhrase = new StringBuilder();
			for(int i = 0; i < words.length-1; i++) {
				inputPhrase.append(words[i]).append(" ");
			}
			String outputKey = inputPhrase.toString().trim();
			String outputValue = words[words.length - 1] + "=" + count;
			
			if(!((outputKey == null) || (outputKey.length() <1))) {
				context.write(new Text(outputKey), new Text(outputValue));
			}
		}
	}

	public static class Reduce extends Reducer<Text, Text, DBOutputWritable, NullWritable> {
		//find the n largest possible folloing word and put them into database
		int n;
		// get the n parameter from the configuration
		@Override
		public void setup(Context context) {
			Configuration conf = context.getConfiguration();
			n = conf.getInt("n", 5);
		}

		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			//inputKey = inputPhrase:I love
			//inputValue = <data=80, girls=120, boys=60>
			//select topK -> treeMap<key=frequency, value = List<words>> -> iterate treeMap -> number of words >=  topK -> stop
			//write it to database (inputKey word frequency)

			//create TreeMap
			TreeMap<Integer, List<String>> tm = new TreeMap<Integer, List<String>>(Collections.reverseOrder());
			for(Text val: values) {
				String curValue = val.toString().trim();
				String word = curValue.split("=")[0].trim();
				int count = Integer.parseInt(curValue.split("=")[1].trim());
				if(tm.containsKey(count)) {
					tm.get(count).add(word);
				}
				else {
					List<String> list = new ArrayList<String>();
					list.add(word);
					tm.put(count, list);
				}
			}
			//tm:<50, <girl, bird>>, <60, <boy...>>
			//choose top n string
			Iterator<Integer> iter = tm.keySet().iterator();
			for(int j=0; iter.hasNext() && j<n;) {
				int keyCount = iter.next();
				List<String> words = tm.get(keyCount);
				for(String curWord: words) {
					//write to database using DBOutputWritable. NullWritable.get() acts like pair. Since everything is 
					// key-value pair in map-reduce. 
					context.write(new DBOutputWritable(key.toString(), curWord, keyCount),NullWritable.get());
					j++;
				}
			}
		}
	}
}

























