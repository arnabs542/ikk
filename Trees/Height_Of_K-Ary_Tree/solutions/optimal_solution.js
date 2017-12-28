process.stdin.resume();
process.stdin.setEncoding('utf-8');

var __input_stdin = "";
var __input_stdin_array = "";
var __input_currentline = 0;

process.stdin.on('data', function(data) {
    __input_stdin += data;
});

function TreeNode()
{
    //this.val = 0;														// To find height of tree, value stored in nodes does not matter. So in input also we are not given this field. 
    this.children = [];
}

//--------------------------------START------------------------------------

/*
 * Complete the function below.
 */

/*
    For your reference:
    
    function TreeNode()
	{
	    this.children = [];
	}
*/

function find_height(k, root) 
{
    if (root.children.length == 0)									// Handle base case when root is a leaf node.
    {
        return 0;
    }
    var h = 0;
    for (var i = 0; i < root.children.length; i++)
    {
        var temp = root.children[i];
        h = Math.max(h, find_height(k, temp));				       // Find height of each children and store the maximum height of children. 
    }
    return h + 1;	
}

//--------------------------------STOP------------------------------------

var address = {};

function build_tree(from, to) {
    var N = from.length + 1;
    address = {};												    // Clear the global variable. 
    for (var i = 1; i <= N; i++)
    {
        address[i] = new TreeNode();								// Create N nodes. 
    }
    for (var i = 0; i < N - 1; i++)								  
    {
        address[from[i]].children.push(address[to[i]]);		        // Link the nodes. (Build the k-ary tree.)
    }
    return address[1];	
}

var fs = require('fs');
var wstream = fs.createWriteStream(process.env.OUTPUT_PATH);

process.stdin.on('end', function() {
    __input_stdin_array = __input_stdin.split("\n");
    var k;
    var k = parseInt(__input_stdin_array[__input_currentline].trim(), 10);
    __input_currentline += 1;

    var from;
    var from_size = 0;
    from_size = parseInt(__input_stdin_array[__input_currentline].trim(), 10);
    __input_currentline += 1;

    var from = [];
    var from_item;
    for (var from_i = 0; from_i < from_size; from_i++) {
        var from_item = parseInt(__input_stdin_array[__input_currentline].trim(), 10);
        __input_currentline += 1;
        from.push(from_item);
    }

    var to;
    var to_size = 0;
    to_size = parseInt(__input_stdin_array[__input_currentline].trim(), 10);
    __input_currentline += 1;

    var to = [];
    var to_item;
    for (var to_i = 0; to_i < to_size; to_i++) {
        var to_item = parseInt(__input_stdin_array[__input_currentline].trim(), 10);
        __input_currentline += 1;
        to.push(to_item);
    }

    var root = build_tree(from, to);

    res = find_height(k, root);
    wstream.write(res + "\n");

    wstream.end();
});