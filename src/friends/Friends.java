package friends;

import structures.Queue;
import structures.Stack;

import java.util.*;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		/** COMPLETE THIS METHOD **/
		
		//initialize distances, comesFrom, visited, and fringe
		int[] distances = new int[g.members.length];
		for (int i = 0; i<distances.length; i++) {
			distances[i] = -1;
		}
		distances[g.map.get(p1)] = 0;
		String[] comesFrom = new String[g.members.length]; //default all null
		boolean[] visited = new boolean[g.members.length]; //default all false
		
		
		Queue<Person> fringe = new Queue<Person>();
		fringe.enqueue(g.members[g.map.get(p1)]);
		
		//do Dijkstra's algorithmn until find p2 or fringe is empty
		Person visiting;
		Person friend;
		while (!fringe.isEmpty()) {
			visiting = fringe.dequeue();
			visited[g.map.get(visiting.name)] = true;
			
			for (Friend frnd = visiting.first; frnd != null; frnd = frnd.next) {
				friend = g.members[frnd.fnum];
				
				if (visited[g.map.get(friend.name)]) {
					continue;
				}
				
				fringe.enqueue(friend);
				
				//if the distance of the friend is greater than the distance through visiting
				if (distances[g.map.get(friend.name)] == -1 || distances[g.map.get(friend.name)] > distances[g.map.get(visiting.name)] + 1) {
					distances[g.map.get(friend.name)] = distances[g.map.get(visiting.name)] + 1;
					comesFrom[g.map.get(friend.name)] = visiting.name;
				}
				
				if (friend.name.equals(p2)) {
					return buildPath(g, comesFrom, p1, p2);
				}
				
				
			}
		}
		
		return null; //didn't find a path if ended up here
	}
	
	private static ArrayList<String> buildPath(Graph g, String[] comesFrom, String p1, String p2) {
		ArrayList<String> reversedPath = new ArrayList<String>();
		Person person2 = g.members[g.map.get(p2)];
		Person crntPerson = person2;
		
		//insert all the vertices in the path, starting from p2 and tracing through comesFrom until reach p1
		while(crntPerson != g.members[g.map.get(p1)]) { 
			reversedPath.add(crntPerson.name);
			crntPerson = g.members[g.map.get(comesFrom[g.map.get(crntPerson.name)])]; 
		} 
		reversedPath.add(crntPerson.name);
		
		//reverse the order to be p1 to p2
		ArrayList<String> path = new ArrayList<String>();
		for (int i = reversedPath.size()-1; i>=0; i--) {
			path.add(reversedPath.get(i));
		}
		
		
		return path;
	}
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		/** COMPLETE THIS METHOD **/
		school = school.toLowerCase();
		
		ArrayList<ArrayList<String>> cliques = new ArrayList<ArrayList<String>>();
		
		boolean[] visited = new boolean[g.members.length];
		Person startingStudent = getAStudent(g, school);
		
		if (startingStudent == null) {
			return null;
		}
		
		while (startingStudent != null) {
			cliques.add(clique(g, startingStudent, visited, school));
			startingStudent = getUnvisitedStudent(g, visited, school);
		}
		
		return cliques;
		
	}
	
	//always guarenteed to return something, no null
	private static  ArrayList<String> clique(Graph g, Person student, boolean[] visited, String school) {
		
		ArrayList<String> clique = new ArrayList<String>();
		
		Queue<Person> fringe = new Queue<Person>();
		Person visiting;
		Person friend;
		
		fringe.enqueue(g.members[g.map.get(student.name)]);
		
		while (!fringe.isEmpty()) {
			visiting = fringe.dequeue();
			visited[g.map.get(visiting.name)] = true;
			clique.add(visiting.name);
			
			for (Friend frnd = visiting.first; frnd != null; frnd = frnd.next) {
				friend = g.members[frnd.fnum];
				
				if (!friend.student || !friend.school.equals(school) || visited[g.map.get(friend.name)]) {
					continue;
				}
				
				fringe.enqueue(friend);
				
			}
		}
		
		return clique;
	}
	
	//returns null if there is no such student
	private static Person getAStudent(Graph g, String school) {
		
		for (int i = 0; i< g.members.length; i++) {
			if (g.members[i].student && g.members[i].school.equals(school)) {
				return g.members[i];
			}
		}
		
		return null;
	}
	
	//returns null if there are no more unvisited students
	private static Person getUnvisitedStudent(Graph g, boolean[] visited, String school) {
		
		for (int i = 0; i< g.members.length; i++) {
			if (g.members[i].student && !visited[i] && g.members[i].school.equals(school)) {
				return g.members[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of al  l connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		/** COMPLETE THIS METHOD **/
		
		ArrayList<String> connectors = new ArrayList<String>();
		
		int[] dfsnum = new int[g.members.length];
		int[] back = new int[g.members.length];
		boolean[] visited = new boolean[g.members.length];
		boolean[] added = new boolean[g.members.length];
		Friend[] crntFriend = new Friend[g.members.length];
		populateCrntFriend(g, crntFriend);
		
		Person start = g.members[0];
		Person visiting;
		Person toBePushed;
		int i;
		
		Stack<Person> stack = new Stack<Person>();
		
		while(start != null) {
			//System.out.println("start: " + start.name);
			
			i = 1;
			visiting = start;
			stack.clear(); //just in case
			stack.push(visiting);
			
			if (crntFriend[g.map.get(visiting.name)] == null) { //if start doesn't connect to anything
				visited[g.map.get(visiting.name)] = true;
				start =  getUnvisitedPerson(g, visited);
				continue;
			}
			toBePushed = g.members[crntFriend[g.map.get(visiting.name)].fnum];
		
			visited[g.map.get(visiting.name)] = true;
			dfsnum[g.map.get(visiting.name)] = i;
			back[g.map.get(visiting.name)] = i;
			i++;
		
			while (!stack.isEmpty()) {
			
				while (toBePushed != null) {
				
					if (visited[g.map.get(toBePushed.name)]) {
						back[g.map.get(visiting.name)] = Math.min(back[g.map.get(visiting.name)], dfsnum[g.map.get(toBePushed.name)]); 
						
						crntFriend[g.map.get(visiting.name)] = crntFriend[g.map.get(visiting.name)].next; 
						
						if (crntFriend[g.map.get(visiting.name)] == null) {
							toBePushed = null;
						}
						else {
							toBePushed = g.members[crntFriend[g.map.get(visiting.name)].fnum];
							
							if (visiting == start && isAConnector(g, visiting, crntFriend, visited) && !added[g.map.get(visiting.name)]) {
								connectors.add(visiting.name);
								added[g.map.get(visiting.name)] = true;
							}
						}
						continue;
					}
				
					stack.push(toBePushed);
					dfsnum[g.map.get(toBePushed.name)] = i;
					back[g.map.get(toBePushed.name)] = i;
					visited[g.map.get(toBePushed.name)] = true;
				
					//possible nullpointer exception
					crntFriend[g.map.get(visiting.name)] = crntFriend[g.map.get(visiting.name)].next;
					visiting = toBePushed;
					if (crntFriend[g.map.get(visiting.name)] == null) {
						toBePushed = null;
					}
					else {
						toBePushed = g.members[crntFriend[g.map.get(visiting.name)].fnum];
					}
				
					i++;
				
				}
			
				Person backUpFrom = stack.pop();
				if(stack.isEmpty()) { //avoid NoSuchElementException
					break;
				}
				Person backUpTo = stack.peek(); //never null at this point
			
				if (dfsnum[g.map.get(backUpTo.name)] > back[g.map.get(backUpFrom.name)]) {
					back[g.map.get(backUpTo.name)] = Math.min(back[g.map.get(backUpTo.name)], back[g.map.get(backUpFrom.name)]);
				}
				else {
					if ( ( backUpTo != start || isAConnector(g, backUpTo, crntFriend, visited)) && !added[g.map.get(backUpTo.name)] ) {
						connectors.add(backUpTo.name);
						added[g.map.get(backUpTo.name)] = true;
					}
				}
			
				visiting = backUpTo;
				if (crntFriend[g.map.get(visiting.name)] == null) {
					toBePushed = null;
				}
				else {
					toBePushed = g.members[crntFriend[g.map.get(visiting.name)].fnum];
				}
			
			}
			
			start =  getUnvisitedPerson(g, visited);
		
		}
		
		
		return connectors;
		
	}
	
	private static void populateCrntFriend(Graph g, Friend[] crntFriend) {
		
		for (int i = 0; i< g.members.length; i++) {
			crntFriend[i] = g.members[i].first;
		}

	}
	
	private static boolean isAConnector(Graph g, Person start, Friend[] crntFriend, boolean[] visited) {
		
		if (crntFriend[g.map.get(start.name)] == null) {
			return false;
		}
		
		//if the next toBePushed has been visited, that means it was visited through the previous path, 
		//which means it's not an independent path
		if (visited[crntFriend[g.map.get(start.name)].fnum]) {
			return false;
		}
		
		return true;
	}
	
	//returns null if there is no unvisited person left
	private static Person getUnvisitedPerson(Graph g, boolean[] visited) {
		
		for (int i = 0; i< g.members.length; i++) {
			if (!visited[i]) {
				return g.members[i];
			}
		}
		
		return null;
	}
	
}

