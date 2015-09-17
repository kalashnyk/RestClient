package ru.kao.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class RestClient {

	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	public void printTop10(String query, int totalItems) throws MalformedURLException, ParseException, IOException,
			InterruptedException, net.minidev.json.parser.ParseException {

		int page = 1;
		List<Project> listProject = new ArrayList<Project>();
		
		if(totalItems > 300){
			totalItems = 300;
			System.out.println("Maximum number of required elements " + totalItems);
		}

		while (totalItems > 0) {
			JSONObject variants = RestService.getJSON(String.format("/search/repositories?q=%s&page=%s", query, page));

			Long totalCount = (Long) variants.get("total_count");
			
			JSONArray items = (JSONArray) variants.get("items");

			for (Object itemObject : items) {
				JSONObject jItemObject = (JSONObject) itemObject;
				
				JSONObject owner = (JSONObject) jItemObject.get("owner");
				Long ownerId = (Long) owner.get("id");
				String fullName = (String) jItemObject.get("full_name");
				Long forks = (Long) jItemObject.get("forks");
				Long watchers = (Long) jItemObject.get("watchers");
				Long openIssues = (Long) jItemObject.get("open_issues");
				Long size = (Long) jItemObject.get("size");
				String language = (String) jItemObject.get("language");
				Date updatedAt = df.parse((String) jItemObject.get("updated_at"));

				Project project = new Project(ownerId, fullName, forks, watchers, openIssues, size, language,
						updatedAt);

				listProject.add(project);
				
				if(--totalItems == 0){
					break;
				}
			}
			if (totalCount <= listProject.size()) {
				break;
			}
			page++;
		}

		Collections.sort(listProject, new Comparator<Project>() {
			public int compare(Project prj1, Project prj2) {
				return prj2.getCost().compareTo(prj1.getCost());
			}
		});

		int count = Math.min(10, listProject.size());
		for(int i = 0; i < count; i++){
			System.out.println(listProject.get(i));
		}
	}

	public static void main(String[] args) {
		RestClient restClient = new RestClient();

		try {
			if (args.length == 2) {
				restClient.printTop10(args[0], Integer.valueOf(args[1]));
			} else if (args.length == 1) {
				restClient.printTop10(args[0], 300);
			} else {
				restClient.printTop10("tetris", 300);
			}
		} catch (NumberFormatException e) {
			System.err.println(e.getMessage());
		} catch (MalformedURLException e) {
			System.err.println(e.getMessage());
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		} catch (net.minidev.json.parser.ParseException e) {
			System.err.println(e.getMessage());
		}
	}
}
