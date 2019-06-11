package com.common.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringUtils {

	public static List<String> findAllSuffix(String s) {
		List<String> result = new ArrayList<>();
		for (int i = 0; i < s.length(); i++) {
			String substrng = s.substring(i, s.length());
			result.add(substrng);
		}
		 Collections.sort(result);
		 return result;
	}

	public int findSubString(String source, String pattern) {
		int hash = pattern.hashCode();
		for (int i = 0; i < source.length() - pattern.length(); i++) {
			String s = source.substring(i, pattern.length() - 1);
			if (s.hashCode() == hash) {
				return i;
			}
		}
		return -1;
	}

	public void findLongestRepeatedSubstring(String s) {
		Map<Character, List<Integer>> map = new HashMap<Character, List<Integer>>();
		for (int i = 0; i < s.length(); i++) {
			Character c = s.charAt(i);
			List<Integer> index = map.get(c);
			if (index == null) {
				index = new ArrayList<>();
				map.put(c, index);
			}
			index.add(i);
		}
		for (Character c : map.keySet()) {
			List<Integer> index = map.get(c);

		}
	}

	
	
	public static void main(String[] a) {
		String s = "aec";
		System.out.println(findAllSuffix(s));
	}
}
