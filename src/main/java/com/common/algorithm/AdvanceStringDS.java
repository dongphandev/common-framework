package com.common.algorithm;

import java.util.*;
public class AdvanceStringDS {
	
	
	
	public static int[] suffixArray(CharSequence S) {
		int n = S.length();
		Integer[] order = new Integer[n];
		for (int i = 0; i < n; i++)
			order[i] = n - 1 - i;
		Arrays.sort(order, (a, b) -> Character.compare(S.charAt(a), S.charAt(b)));
		int[] sa = new int[n];
		int[] rank = new int[n];
		for (int i = 0; i < n; i++) {
			sa[i] = order[i];
			rank[i] = S.charAt(i);
		}
		for (int len = 1; len < n; len *= 2) {
			int[] c = rank.clone();
			for (int i = 0; i < n; i++) {
				rank[sa[i]] = i > 0 && c[sa[i - 1]] == c[sa[i]] && sa[i - 1] + len < n
						&& c[sa[i - 1] + len / 2] == c[sa[i] + len / 2] ? rank[sa[i - 1]] : i;
			}
			int[] cnt = new int[n];
			for (int i = 0; i < n; i++)
				cnt[i] = i;
			int[] s = sa.clone();
			for (int i = 0; i < n; i++) {
				int s1 = s[i] - len;
				if (s1 >= 0)
					sa[cnt[rank[s1]]++] = s1;
			}
		}
		return sa;
	}
	public static int[] lcp(int[] sa, CharSequence s) {
		int n = sa.length;
		int[] rank = new int[n];
		for (int i = 0; i < n; i++)
			rank[sa[i]] = i;
		int[] lcp = new int[n - 1];
		for (int i = 0, h = 0; i < n; i++) {
			if (rank[i] < n - 1) {
				for (int j = sa[rank[i] + 1]; Math.max(i, j) + h < s.length() && s.charAt(i + h) == s.charAt(j + h); ++h);
					lcp[rank[i]] = h;
				if (h > 0)
					--h;
			}
		}
		return lcp;
	}
	/**
	 * Build z table
	 * @param s
	 * @return
	 */
	public static int[] zFucntion(String s) {
		int n = s.length();
		int z[] = new int[n];
		int R = 0;
		int L = 0;

		for (int i = 1; i < n; i++) {
			z[i] = 0;
			if (R > i) {
				z[i] = Math.min(R - i, z[i - L]);
			}
			while (i + z[i] < n && s.charAt(i + z[i]) == s.charAt(z[i])) {
				z[i]++;
			}
			if (i + z[i] > R) {
				L = i;
				R = i + z[i];
			}
		}
		z[0] = n;
		return z;
	}
	
	
	private static List<String> findAllSuffix(String s, int miniminLength) {
		List<String> result = new ArrayList<>();
		for (int i = miniminLength+1; i < s.length()+1; i++) {
			result.add(s.substring(0, i));
		}
		return result;
	}

	// Usage example
	public static void main(String[] args) {
		String s1 = "b";
		int[] sa1 = suffixArray(s1);
		int[] lcp = lcp(sa1, s1);

		// print suffixes in lexicographic order
		for (int p : sa1)
			System.out.println(s1.substring(p));

		int total = s1.substring(sa1[0]).length();
		for (int i = 1; i < sa1.length; i++) {
			total = total + (s1.substring(sa1[i]).length() - lcp[i - 1]);
		}
		System.out.println(total);

		List<String> s = findAllSuffix(s1.substring(sa1[0]), 0);
		for (int i = 1; i < sa1.length; i++) {
			s.addAll(findAllSuffix(s1.substring(sa1[i]), lcp[i - 1]));
		}
		System.out.println(s);
		
		int[] re = zFucntion("ab");
		System.out.println(Arrays.asList(re).size());

	}
}