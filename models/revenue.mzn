% number of clients 
int: m;

% number of servers 
int: n;

% number of resources
int: o;

% set of clients 
set of int: I = 1..m;

% set of servers 
set of int: J = 1..n;

% set of resources 
set of int: K = 1..o;

% space consumed in allocating resource j to server i [kB]
array[K] of int: b; 

% requests intensity [rps] of client i for resource k
array[I, K] of int: d; 

% profit from delivering resource k 
array[K] of int: r; 

% disk capacity of server j 
array[J] of int: s;

% cost of accessing server j by client i [number of redirects]
array[I, J] of int: c; 


% x[i,j,k] = 1 means resource k requested by client i is assigned to server j 
array[I, J, K] of var 0..1: x;

array[J, K] of var 0..1: z;

%
% objective
%
    
var int: obj = sum(i in I, j in J, k in K) (  (r[k] * d[i, j] * x[i, j, k]) - (b[k] * d[i, k] * c[i, j] * x[i, j, k]));
solve :: int_search(
        [x[i,j,k] | i in I, j in J, k in K] ++ [z[j,k] | j in J, k in K] ++ [obj],
        first_fail, indomain_min, complete)
     maximize obj;

%
% constraints
%

constraint

  obj >= 0
  /\
  
  % total amount of resources consumed by all jobs assigned to agent i
  %  must not exceed the agent's capacity 
  forall(j in J) ( sum(k in K) (b[k] * z[j,k]) <= s[j])
  /\
  
  forall(i in I, k in K) ( sum (j in J) ( x[i,j,k] ) = 1 )
  /\
  
  forall(i in I, j in J, k in K) ( x[i,j,k] <= z[j,k]  )
  
  ;



output [
  "\nobj: ", show(obj), 
 
] ++
[
  if j = 1 then "\n" else " " endif ++
    show(x[i,j,k]) 
  | i in I, j in J, k in K
] ++ ["\n"]++
[
  if j = 1 then "\n" else " " endif ++
    show(z[j,k]) 
  | j in J, k in K
] ++ ["\n"];


m = 3;
n = 6;
o = 12;

b = [1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 5];
d = array2d(I, K, [
  5, 5, 5, 5, 5, 5, 0, 0, 0, 1, 1, 1,
  0, 0, 0, 5, 5, 5, 0, 0, 0, 5, 5, 5,
  5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 1, 1]
);

r = [5, 5, 5, 8, 3, 3, 9, 9, 9, 3, 1, 1];

s = [6, 6, 6, 6, 5, 5];
c = array2d(I, J, [
  0, 1, 1, 2, 2, 3,
  3, 0, 1, 1, 2, 2,
  2, 3, 0, 1, 1, 2]
);
