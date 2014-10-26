package ticker.logic;

// Package Common
import ticker.common.Task;
import ticker.common.sortByTime;
// Package Java util
import java.util.Collections;
import java.util.Vector;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.ChapmanLengthDeviation;
import uk.ac.shef.wit.simmetrics.similaritymetrics.ChapmanMatchingSoundex;
import uk.ac.shef.wit.simmetrics.similaritymetrics.ChapmanMeanLength;
import uk.ac.shef.wit.simmetrics.similaritymetrics.EuclideanDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import uk.ac.shef.wit.simmetrics.similaritymetrics.NeedlemanWunch;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotoh;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotohWindowedAffine;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Soundex;

// TODO: check description by end of project
/*
 * Class: SearchManager
 * Description: Calculates the level of similarity between the search key and the existing tasks in
 * the task list. Returns only the tasks with appointed level of similarity.
 */

public class SearchManager {
	Vector<StringMatch> matchList;
	Vector<Task> taskList;
	String key; 
	
	public SearchManager() {

	}
	
	public Vector<Task> search(Vector<Task> taskList, String key) {
		matchList = new Vector<StringMatch>();
		this.taskList = taskList;
		this.key = key;
		
		int i = 0;
		
		for (Task task: taskList) {
			float score = getMatchLikelyhood(key, task.toString());
			System.out.println(score);
			matchList.add(new StringMatch(i, score));
			i++;
		}
		
		Collections.sort(matchList, new StringMatchComparator());
		
		Vector<Task>searchResults = new Vector<Task>();
		
		for (StringMatch sm : matchList) {
			if (sm.getSimilarityScore() < 40.0) {
				continue;
			}
			searchResults.add(taskList.get(sm.getIndex()));
		}
		
		return searchResults;
		
	}

	private static float getMatchLikelyhood(final String str1, final String str2) {
		AbstractStringMetric metric;
		float avg = 0F, result = 0F;
		metric = new SmithWaterman();
		result = metric.getSimilarity(str1, str2);
		avg += result;
		metric = new SmithWatermanGotoh();
		result = metric.getSimilarity(str1, str2);
		avg += result;
		metric = new SmithWatermanGotohWindowedAffine();
		result = metric.getSimilarity(str1, str2);
		avg += result;
		metric = new MongeElkan();
		result = metric.getSimilarity(str1, str2);
		avg += result;
		return (avg / 4.0F) * 100.0F;
	}
}
