package com.common.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;


public class ArrayUtils {

	public List<Integer> minimumSubArraySumEqualToX(List<Integer> arr, int sum) {
		
		for(int i =1;i<arr.size(); i++) {
			for(int j=0;j<= arr.size();j++) {
				for (int k=j; k<=j+i;k++) {
					
				}
			}
		}
		
		
		
		int minSize = arr.size();
		int start  =0;
		int end =0;
		int currentSum =arr.get(start);
		while(end>0) {
			if (currentSum > sum) {
				while((start<=end)) {
					if((currentSum - arr.get(start)) >= sum) {
						start = start +1;
					}
				}
				//record
			} else {
				end++;
				currentSum = currentSum + arr.get(end);
			}
		}
		return arr;
		
	}
	
	public static List<List<Integer>> findSubArraySumTo0(List<Integer> arr, int sum) {
		List<List<Integer>> result = new ArrayList<>();
		int[] sumAccumulation = new int[arr.size()];
		sumAccumulation[0] = arr.get(0);
		for (int i =1; i <arr.size(); i++) {
			sumAccumulation[i] = sumAccumulation[i-1] + arr.get(i);
			if (sumAccumulation[i] == sum) {
				result.add(arr.subList(0, i+1));
			}
		}
		
		for (int i =1; i <arr.size(); i++) {
			
			for (int j =i+1; j <arr.size(); j++) {
				int checkij = sumAccumulation[j] - sumAccumulation[i-1];
				if (checkij == sum) {
					result.add(arr.subList(i, j+1));
				}
			}
			
		}
		
		
		return result;
		
	}
	
	private static int maximumDiffernce(int[] arr) {
		TreeMap<Integer, Integer> set = new TreeMap();
		for(int i =0; i< arr.length; i++) {
			set.put(arr[i], i);
		}
		Integer lowestKey = set.firstKey();
		int minIndex = set.get(lowestKey);
		int maximum=0;
		do{
			lowestKey = set.higherKey(lowestKey);
			if (lowestKey ==null) 
				break;
			
			Integer index = set.get(lowestKey);
			if(index == null) {
				break;
			}
			if (index < minIndex) {
				minIndex = index;
			} else {
				int tem = index -minIndex;
				if (tem > maximum) {
					maximum = tem;
				}
			}
		} while((lowestKey !=null) );
		return maximum;
		
	}
	
	
	public static int binarySearch(List<Integer> a, int key) {
		
		if (a== null || a.size() ==0) {
			return -1;
		}
		int start = 0;
		int end = a.size()-1;
		
		int middle = -1;
		while(start <=end) {
			middle = (end+start)/2;
			if(key == a.get(middle)){
				return middle;
			} else if(key <a.get(middle)) {
				end = middle-1;
			} else {
				start = middle+1;
			}
		}
		return middle;
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int[] x = {6, 5, 4, 3, 2, 1};
		Random r = new Random();
		int[] ss = new int[10000000];
		for (int i = 0; i < 10000000; i++) {
			Integer value = r.nextInt(100000);
			 ss[i]=value;
			
		}
		long t1 = System.currentTimeMillis();
		System.out.println(maximumDiffernce(ss));
		long t2 = System.currentTimeMillis();
		System.out.println(t2-t1);
		
	}
}
