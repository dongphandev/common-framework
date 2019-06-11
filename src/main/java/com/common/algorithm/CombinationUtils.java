package com.common.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CombinationUtils {

	public static void findCombination(Integer[] arr, List<List<Integer>> result, List<Integer> tem, int k) {
		if (tem.size() == k) {
			result.add(new ArrayList<>(tem));
		} else {
			for (int i = 0; i < arr.length; i++) {
				if (!tem.contains(arr[i])) {
					tem.add(arr[i]);
					findCombination(arr, result, tem, k);
					tem.remove(arr[i]);
				}

			}
		}
	}

	public static void findSubset(Integer[] arr, List<List<Integer>> result, List<Integer> tem, int k, int sum) {
		int temSum = 0;
		if (tem.size() == k) {
			for (Integer e : tem) {
				temSum = temSum + e;
			}
			if (temSum == sum) {
				result.add(new ArrayList<>(tem));
			}
		} else {
			for (int i = 0; i < arr.length; i++) {
				if (!tem.contains(arr[i])) {
					temSum = 0;
					for (Integer e : tem) {
						temSum = temSum + e;
					}
					if ((temSum + arr[i]) > sum) {
						continue;
					} else {
						tem.add(arr[i]);
						findSubset(arr, result, tem, k, sum);
						tem.remove(arr[i]);
					}

				}

			}
		}
	}

	public static void findKSet(Integer[] arr, List<List<Integer>> result, List<Integer> subset, Set<Integer> inResult,
			int sum, int k) {
		if ((inResult.size() == arr.length) && (result.size() ==k)) {
			System.out.println(result);
			
		}else  {
			int temSum = 0;
			for (Integer e : subset) {
				temSum = temSum + e;
			}
			if (temSum == sum) {
				result.add(new ArrayList<>(subset));
				subset.clear();
				
			}
			for (int i = 0; i < arr.length; i++) {
				if (!inResult.contains(arr[i])) {
					inResult.add(arr[i]);
					subset.add(arr[i]);
					findKSet(arr, result, subset, inResult, sum,k);
					subset.remove(arr[i]);
					inResult.remove(arr[i]);
				}
			}
		}
		
	}

}
