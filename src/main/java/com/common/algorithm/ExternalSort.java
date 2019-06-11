package com.common.algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class ExternalSort {

	private static final String TEM_FILE_PATH = "/Users/dongphan/Documents/";
	
	private static final String FILE_SORT = "/Users/dongphan/Documents/text.txt";

	private static final String TEM_FILE_NAME = "sorted";

	public static int sortParttion(String fileName) throws IOException {
		int i = 0;
		Set<Integer> set = new TreeSet<Integer>();
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		String s = br.readLine();
		while (s != null) {
			set.add(Integer.parseInt(s));
			if (set.size() == 10) {
				System.out.println(set);
				FileWriter fw = new FileWriter(new File(TEM_FILE_PATH + TEM_FILE_NAME + i + ".txt"));
				BufferedWriter bw = new BufferedWriter(fw);
				for (Integer line : set) {
					bw.write(line);
					bw.newLine();
				}
				bw.close();
				fw.close();
				i++;
				set = new TreeSet<Integer>();
			}
			s = br.readLine();
		}
		FileWriter fw = new FileWriter(new File(TEM_FILE_PATH + TEM_FILE_NAME + i + ".txt"));
		BufferedWriter bw = new BufferedWriter(fw);
		for (Integer line : set) {
			bw.write(line);
			bw.newLine();
		}
		bw.close();
		fw.close();
		i++;
		br.close();
		return i;
	}
	public int mergeSort(String fileName) throws IOException {

		int i = 0;
		Set<Integer> set = new TreeSet<Integer>();
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		String s = br.readLine();
		while (s != null) {
			set.add(Integer.parseInt(s));
			if (set.size() == 10000) {
				FileWriter fw = new FileWriter(new File(TEM_FILE_PATH + TEM_FILE_NAME + i + ".txt"));
				BufferedWriter bw = new BufferedWriter(fw);
				for (Integer line : set) {
					bw.write(line);
					bw.newLine();
				}
				fw.close();
				i++;
				set = new TreeSet<Integer>();
			}
			s = br.readLine();
		}
		br.close();
		return i;
	}
	public  static void main(String[] args) {
		//writeFile();
		Random r = new Random();
		List<Integer> ss =new ArrayList<>();
		for (int i = 0; i < 1000000; i++) {
			Integer value = r.nextInt(100000);
			ss.add(value);
			
		}
		long t1 = System.currentTimeMillis();
		Collections.sort(ss);
		long t2 = System.currentTimeMillis();
		System.out.println(t2-t1);
		
	}
	
	private static void writeFile() {
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			fw = new FileWriter(FILE_SORT);
			bw = new BufferedWriter(fw);
			Random r = new Random();
			List<Integer> s =new ArrayList<>();
			for (int i = 0; i < 1000000; i++) {
				Integer value = r.nextInt(100000);
				s.add(value);
				
			}
			//bw.write(value.toString());
			bw.newLine();
			bw.close();
			System.out.println("Done");

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

	}

}
