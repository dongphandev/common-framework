package com.common.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class DynamicProgramming {

	public static int minPath(int[][] a, int x, int y) {
		Point initialPoint = new Point();
		initialPoint.x = 0;
		initialPoint.y = 0;
		initialPoint.cost = a[0][0];
		Result result = new Result();
		findPathOfThreeWay(a, x, y, initialPoint, result);
		for (Point point : result.finalResult) {
			System.out.println("Point: (" + point.x + " ," + point.y + "), cost: " + point.cost);
		}
		return result.min;
	}

	private static void minPathSub(int[][] a, int endX, int endY, Point currentPoint, Result result) {
		if (currentPoint.x + 1 == endX && currentPoint.y + 1 == endY) {
			if (result.totalCost + currentPoint.cost < result.min) {
				// result.points.add(currentPoint);
				// result.totalCost = result.totalCost + currentPoint.cost;
				result.finalResult.clear();
				result.finalResult.addAll(result.points);
				result.finalResult.add(currentPoint);
				result.min = result.totalCost + currentPoint.cost;
			}
		} else {
			if (currentPoint.x + 1 < endX) {
				Point nextPoint = new Point();
				nextPoint.x = currentPoint.x + 1;
				nextPoint.y = currentPoint.y;
				nextPoint.cost = a[currentPoint.x + 1][currentPoint.y];
				result.points.add(currentPoint);
				result.totalCost = result.totalCost + currentPoint.cost;
				minPathSub(a, endX, endY, nextPoint, result);
				result.points.remove(currentPoint);
				result.totalCost = result.totalCost - currentPoint.cost;
			}
			if (currentPoint.y + 1 < endY) {
				Point nextPoint = new Point();
				result.points.add(currentPoint);
				result.totalCost = result.totalCost + currentPoint.cost;
				nextPoint.x = currentPoint.x;
				nextPoint.y = currentPoint.y + 1;
				nextPoint.cost = a[currentPoint.x][currentPoint.y + 1];
				minPathSub(a, endX, endY, nextPoint, result);
				result.points.remove(currentPoint);
				result.totalCost = result.totalCost - currentPoint.cost;
			}
		}
	}

	private static void findPathOfThreeWay(int[][] a, int endX, int endY, Point currentPoint, Result result) {
		if ((currentPoint.x == endX && currentPoint.y == endY)) {
			if (result.totalCost + currentPoint.cost < result.min) {
				// result.points.add(currentPoint);
				// result.totalCost = result.totalCost + currentPoint.cost;
				result.finalResult.clear();
				result.finalResult.addAll(result.points);
				result.finalResult.add(currentPoint);
				result.min = result.totalCost + currentPoint.cost;
			}
		} else {
			if (currentPoint.x + 1 <= endX) {
				Point nextPoint = new Point();
				nextPoint.x = currentPoint.x + 1;
				nextPoint.y = currentPoint.y;
				nextPoint.cost = a[currentPoint.x + 1][currentPoint.y];
				result.points.add(currentPoint);
				result.totalCost = result.totalCost + currentPoint.cost;
				findPathOfThreeWay(a, endX, endY, nextPoint, result);
				result.points.remove(currentPoint);
				result.totalCost = result.totalCost - currentPoint.cost;
			}
			if (currentPoint.y + 1 <= endY) {
				Point nextPoint = new Point();
				result.points.add(currentPoint);
				result.totalCost = result.totalCost + currentPoint.cost;
				nextPoint.x = currentPoint.x;
				nextPoint.y = currentPoint.y + 1;
				nextPoint.cost = a[currentPoint.x][currentPoint.y + 1];
				findPathOfThreeWay(a, endX, endY, nextPoint, result);
				result.points.remove(currentPoint);
				result.totalCost = result.totalCost - currentPoint.cost;
			}
			if (currentPoint.y + 1 <= endY && currentPoint.x + 1 <= endX) {
				Point nextPoint = new Point();
				result.points.add(currentPoint);
				result.totalCost = result.totalCost + currentPoint.cost;
				nextPoint.x = currentPoint.x + 1;
				nextPoint.y = currentPoint.y + 1;
				nextPoint.cost = a[currentPoint.x + 1][currentPoint.y + 1];
				findPathOfThreeWay(a, endX, endY, nextPoint, result);
				result.points.remove(currentPoint);
				result.totalCost = result.totalCost - currentPoint.cost;
			}
		}
	}

	public static int maxProfitSellStock(int[] stock) {
		if (stock.length == 1) {
			return 0;
		}
		int min = stock[0];

		int max = 0;
		for (int i = 1; i < stock.length; i++) {
			if (min > stock[i]) {
				min = stock[i];
			}
			if ((stock[i] - min) > max) {
				max = (stock[i] - min);
			}
		}
		return max;
	}

	public static void maxSubSequence(int[] stock) {
		if (stock.length == 1) {
			return;
		}

		int[] maxValueSofar = new int[stock.length];
		int[] maxNumberOfElementSoFar = new int[stock.length];
		maxValueSofar[0] = stock[0];
		maxNumberOfElementSoFar[0] = 1;
		for (int i = 1; i < stock.length; i++) {
			int current = stock[i];
			int j = i - 1;
			maxValueSofar[i] = current;
			maxNumberOfElementSoFar[i] = 1;
			while (j >= 0) {
				if (maxValueSofar[i] > maxValueSofar[j]) {
					if (maxNumberOfElementSoFar[j] >= maxNumberOfElementSoFar[i]) {
						maxNumberOfElementSoFar[i] = maxNumberOfElementSoFar[j] + 1;
					}
				}
				j--;
			}
			// maxValueSofar[i] = maxValueOfThisIndex;
			// maxNumberOfElementSoFar[i] = maxNumberOfThisIndex;

		}
		for (int i = 0; i < maxValueSofar.length; i++) {
			System.out.print(maxValueSofar[i] + " ");
		}
		System.out.println();
		for (int i = 0; i < maxNumberOfElementSoFar.length; i++) {
			System.out.print(maxNumberOfElementSoFar[i] + " ");
		}
		// return max;
	}

	public static List<Integer> minimumJump(int[] a) {

		List<Integer> result = new ArrayList<>();
		List<Integer> tem = new ArrayList<>();
		tem.add(0);
		minimumJumpHelp(a, 0, result, tem);
		return result;
	}

	private static void minimumJumpHelp(int[] a, int index, List<Integer> finalResult, List<Integer> tem) {
		if (index == (a.length-1 )) {
			
			if (finalResult.size() == 0) {
				
				for(int i : tem) {//work
					finalResult.add(i);
				}
				finalResult = new ArrayList(tem);//not work
				
			} else {
				if (tem.size() < finalResult.size()) {
					finalResult.clear();
					for(int i : tem) {
						finalResult.add(i);
					}
				}
			}
			
		} else if (index >= a.length) {
			return;
		} else {
			int maximumJum = a[index];
			for (int i = 1; i <= maximumJum; i++) {
				tem.add(new Integer(index + i));
				minimumJumpHelp(a, index + i, finalResult, tem);
				tem.remove(new Integer(index + i));

			}
		}

	}

	public static void main(String[] a) {
		int m[][] = { { 1, 7, 9, 2 }, { 8, 6, 3, 2 }, { 1, 6, 7, 8 }, { 2, 9, 8, 2 } };
		// int m[][] = { { 1, 7 },{ 8, 6, } };
		// System.out.println(minPath(m, 4, 4));

		int cost[][] = { { 1, 2, 3 }, { 4, 8, 2 }, { 1, 5, 3 } };
		int[] prices = { 1, 3, 5, 8, 9, 2, 6, 7, 6, 8, 9 };
	
		List<Integer> result  = minimumJump(prices);
		for(int i : result) {
			System.out.print(prices[i]+" ");
		}
		
	}
}
