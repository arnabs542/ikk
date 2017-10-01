#include<bits/stdc++.h>

using namespace std;

//--------------------------------START------------------------------------

const int MAX_N = 100000;

struct node
{
	//int val;														// To find height of tree, value stored in nodes does not matter. So in input also we are not given this field. 
	vector<node*> children;											// Address of children nodes. 
	node()
	{
		children.clear();
	}
};

int find_max_height(node* root)
{
	if (root->children.size() == 0)									// Handle base case when root is a leaf node.
	{
		return 0;
	}
	int h = 0;
	for (int i = 0; i < root->children.size(); i++)
	{
		h = max(h, find_max_height(root->children[i]));				// Find height of each children and store the maximum height of children. 
	}
	return h + 1;			
}

unordered_map<int, node*> address; 									// To build k-ary tree use this to speed up the insertion process. 

int find_height(int k, vector<int> from, vector<int> to)
{
	int N = from.size() + 1;
	address.clear();												// Clear the global variable. 
	for (int i = 1; i <= N; i++)
	{
		address[i] = new node();									// Create N nodes. 
	}
	for (int i = 0; i < N - 1; i++)								  
	{
		address[from[i]]->children.push_back(address[to[i]]);		// Link the nodes. (Build the k-ary tree.)
	}
	return find_max_height(address[1]);									
}

//--------------------------------STOP------------------------------------

int main()
{
	//freopen("..//test_cases//sample_test_cases_input.txt", "r", stdin);
	//freopen("..//test_cases//sample_test_cases_output.txt", "w", stdout);
	//freopen("..//test_cases//handmade_test_cases_input.txt", "r", stdin);
	//freopen("..//test_cases//handmade_test_cases_output.txt", "w", stdout);
	//freopen("..//test_cases//small_test_cases_input.txt", "r", stdin);
	//freopen("..//test_cases//small_test_cases_output.txt", "w", stdout);
	freopen("..//test_cases//big_test_cases_input.txt", "r", stdin);
	freopen("..//test_cases//big_test_cases_output.txt", "w", stdout);
	//freopen("..//test_cases//ignore.txt", "w", stdout);

	int test_cases;
	cin >> test_cases;
	while (test_cases--)
	{
		int k;
		cin >> k;

		int edges;
		cin >> edges;
		
		int N = edges + 1;
		assert(1 <= N);
		assert(N <= MAX_N);
		assert(0 <= k);
		assert(k <= N - 1);
		
		vector<int> from(edges, 0);
		for (int i = 0; i < edges; i++)
		{
			cin >> from[i];
			assert(1 <= from[i]);
			assert(from[i] <= N);
		}
		
		cin >> edges;
		assert(edges == N - 1);
		
		vector<int> to(edges, 0);
		for (int i = 0; i < edges; i++)
		{
			cin >> to[i];
			assert(1 <= to[i]);
			assert(to[i] <= N);
		}

		unordered_map<int, int> no_of_in_edges;
		for (int i = 0; i < edges; i++)
		{
			no_of_in_edges[to[i]]++;
		} 
		assert(no_of_in_edges.size() == N - 1);

		for (auto it = no_of_in_edges.begin(); it != no_of_in_edges.end(); it++)
		{
			assert(it->second == 1);
		}

		int h = find_height(k, from, to);
		assert(0 <= h);
		assert(h <= N - 1);
		cout << h << endl;
	}

	return 0;
}