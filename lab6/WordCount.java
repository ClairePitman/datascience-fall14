package org.myorg;

	import java.io.IOException;
	import java.io.*;
	import java.util.*;
	
	import org.apache.hadoop.fs.Path;
	import org.apache.hadoop.conf.*;
	import org.apache.hadoop.io.*;
	import org.apache.hadoop.mapred.*;
	import org.apache.hadoop.util.*;
	
	public class WordCount {
	
        public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
            private final static IntWritable one = new IntWritable(1);
            private Text word = new Text();
	    private Text preword = new Text();
	    private Text space = new Text(" ");

            public void map(LongWritable key, Text value, OutputCollector output, Reporter reporter) throws IOException {
                String line = value.toString();
                StringTokenizer tokenizer = new StringTokenizer(line);
                while (tokenizer.hasMoreTokens()) {
		    preword.set(word);
                    word.set(tokenizer.nextToken());
		    preword.append(space.getBytes(),0,space.getLength());
		    preword.append(word.getBytes(),0,word.getLength());
                    output.collect(preword, one);
                }
            }
        }
	
	
	
        public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
            public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
                int sum = 0;
                while (values.hasNext()) {
                    sum += values.next().get();
                }
                output.collect(key, new IntWritable(sum));
            }
        }
	
	public static class Map2 extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
            private final static IntWritable one = new IntWritable(1);
            private Text word1 = new Text();
	    private Text word2 = new Text();
	    private String count ="0";

            public void map(LongWritable key, Text value, OutputCollector output, Reporter reporter) throws IOException {
                String line = value.toString();
                StringTokenizer tokenizer = new StringTokenizer(line);
		if(tokenizer.hasMoreTokens())
		word1.set(tokenizer.nextToken());
		if(tokenizer.hasMoreTokens())
		word2.set(tokenizer.nextToken());
		if(tokenizer.hasMoreTokens())
		count=tokenizer.nextToken();
		String gram=word1.toString()+" "+word2.toString();
               	output.collect(word1,new Text(count+" "+gram));
		output.collect(word2,new Text(count+" "+gram));
		//output.collect(word1,new IntWritable(Integer.parseInt(count)));
		//output.collect(word2,new IntWritable(Integer.parseInt(count)));
            }
        }
	
        public static class Reduce2 extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
            public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
                
		int max = 0;
		Text maxbi=null;
		Text temp;
                while (values.hasNext()) {
                    temp= values.next();
		    StringTokenizer tokenizer = new StringTokenizer(temp.toString());
		    if(tokenizer.hasMoreTokens()){
		    int i=Integer.parseInt(tokenizer.nextToken());
		    if (i>max){
		    max=i;
		    maxbi=temp;
		    }
		    
		    }
                }
		if(maxbi!=null)
                output.collect(key, maxbi);
            }
        }
	
	   public static void main(String[] args) throws Exception {
	     JobConf conf = new JobConf(WordCount.class);
	     conf.setJobName("wordcount");
	
	     conf.setOutputKeyClass(Text.class);
	     conf.setOutputValueClass(IntWritable.class);
	
	     conf.setMapperClass(Map.class);
	     conf.setCombinerClass(Reduce.class);
	     conf.setReducerClass(Reduce.class);
	
	     conf.setInputFormat(TextInputFormat.class);
	     conf.setOutputFormat(TextOutputFormat.class);
	
	     FileInputFormat.setInputPaths(conf, new Path(args[0]));
	     FileOutputFormat.setOutputPath(conf, new Path("wordcount/tmp"));
	
	     JobClient.runJob(conf);
	     
	     
	     JobConf conf2 = new JobConf(WordCount.class);
	     conf2.setJobName("wordcountp2");
	
	     conf2.setOutputKeyClass(Text.class);
	     conf2.setOutputValueClass(Text.class);
	
	     conf2.setMapperClass(Map2.class);
	     conf2.setCombinerClass(Reduce2.class);
	     conf2.setReducerClass(Reduce2.class);
	
	     conf2.setInputFormat(TextInputFormat.class);
	     conf2.setOutputFormat(TextOutputFormat.class);
	
	     FileInputFormat.setInputPaths(conf2, new Path("wordcount/tmp"));
	     FileOutputFormat.setOutputPath(conf2, new Path(args[1]));
	     JobClient.runJob(conf2);
	   }
	}
	

	
