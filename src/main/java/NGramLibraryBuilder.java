
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class NGramLibraryBuilder {
	public static class NGramMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

		int noGram;
		@Override
		public void setup(Context context) {
			Configuration conf = context.getConfiguration();
			noGram = conf.getInt("noGram", 5);
		}

		// map method
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			//inputKey: offset, don't use
			//inputValue: line->sentence, did by configuration in driver.java
			//read sentence -> split into 2gram， 3gram,...ngram
			//if n = 3, i love, ilove big, love big, love big data, big data
			//outputKey = 2gram,...ngram
			//outputValue = 1
			String line = value.toString();
			
			line = line.trim().toLowerCase();
			line = line.replaceAll("[^a-z]", " ");// match anything that is NOT an lowercase letter.
			
			String[] words = line.split("\\s+"); //split by ' ', '\t'...ect
			
			if(words.length<2) {
				return;
			}
			
			//I love big data
			StringBuilder ngram;
			for(int i = 0; i < words.length-1; i++) {
				ngram = new StringBuilder();
				ngram.append(words[i]);
				for(int j=1; i+j<words.length && j<noGram; j++) {
					ngram.append(" ");
					ngram.append(words[i+j]);
					context.write(new Text(ngram.toString().trim()), new IntWritable(1));
				}
			}
		}
	}

	public static class NGramReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		// reduce method
		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			//inputKey = ngram, outputkey of mapper
			//inputValue = 相同ngram对应的value的组合<1,1,...1>
			//outputKey = ngram
			//outputValue = sum
			int sum = 0;
			for(IntWritable value: values) {
				sum += value.get();
			}
			context.write(key, new IntWritable(sum));
			//big data\t1051, \t is the default thing between key and value
		}
	}

}



















